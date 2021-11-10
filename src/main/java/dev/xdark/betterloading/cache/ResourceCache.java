package dev.xdark.betterloading.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.stream.JsonReader;
import dev.xdark.betterloading.IOUtil;
import dev.xdark.betterloading.internal.ResourcePackExt;
import dev.xdark.betterloading.json.JsonUnbakedModelDeserializer;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public final class ResourceCache {

  // private static final Cleaner CLEANER = Cleaner.create();
  private static final int WORKER_THREADS =
      MathHelper.clamp(Runtime.getRuntime().availableProcessors() - 1, 1, 7) + 1;
  private static final NativeImageHolder MISSING_IMAGE = new NativeImageHolder(null);
  private static final JsonUnbakedModel MISSING_UNBAKED_MODEL =
      new JsonUnbakedModel(null, null, null, false, null, null, null);

  private final Cache<ResourceKey, NativeImageHolder> imageCache =
      newCache()
          .<ResourceKey, NativeImageHolder>removalListener(
              notification -> {
                NativeImageHolder holder = notification.getValue();
                if (holder != MISSING_IMAGE) {
                  /*
                  CachedNativeImage image = (CachedNativeImage) holder.getImage();
                  CLEANER.register(image, image::doClose);
                   */
                  ((CachedNativeImage) holder.getImage()).doClose();
                }
              })
          .build();
  private final Cache<ResourceKey, JsonUnbakedModel> jsonUnbakedModelCache = newCache().build();
  private final ResourcePack resourcePack;

  public ResourceCache(ResourcePack resourcePack) {
    this.resourcePack = resourcePack;
  }

  public NativeImageHolder loadNativeImage(ResourceType type, Identifier identifier)
      throws IOException {
    return loadFromCache(
        imageCache,
        new ResourceKey(type, identifier),
        (rp, key) -> {
          try (InputStream in =
              ((ResourcePackExt) rp).tryOpen(key.resourceType(), key.getIdentifier())) {
            return in == null ? null : new NativeImageHolder(CachedNativeImage.read(in));
          }
        },
        MISSING_IMAGE);
  }

  public JsonUnbakedModel loadUnbakedJsonModel(ResourceType type, Identifier identifier)
      throws IOException {
    return loadFromCache(
        jsonUnbakedModelCache,
        new ResourceKey(type, identifier),
        (rp, key) -> {
          InputStream in = ((ResourcePackExt) rp).tryOpen(key.resourceType(), key.getIdentifier());
          if (in == null) {
            return null;
          }
          try (JsonReader reader = IOUtil.toJsonReader(in, StandardCharsets.UTF_8)) {
            return JsonUnbakedModelDeserializer.INSTANCE.read(reader);
          }
        },
        MISSING_UNBAKED_MODEL);
  }

  private <K, V> V loadFromCache(Cache<K, V> cache, K key, ValueLoader<K, V> loader, V missing)
      throws IOException {
    V value = cache.getIfPresent(key);
    if (value != null) {
      if (value == missing) {
        return null;
      }
      return value;
    }
    if ((value = loader.load(resourcePack, key)) == null) {
      cache.put(key, missing);
    } else {
      cache.put(key, value);
    }
    return value;
  }

  @SuppressWarnings("unchecked")
  private static <K, V> CacheBuilder<K, V> newCache() {
    return (CacheBuilder<K, V>)
        CacheBuilder.newBuilder()
            .expireAfterAccess(1L, TimeUnit.HOURS)
            .softValues()
            .concurrencyLevel(WORKER_THREADS);
  }

  private interface ValueLoader<K, V> {

    V load(ResourcePack rp, K key) throws IOException;
  }
}
