package dev.xdark.betterloading.mixin;

import com.google.gson.stream.JsonReader;
import dev.xdark.betterloading.IOUtil;
import dev.xdark.betterloading.cache.NativeImageHolder;
import dev.xdark.betterloading.internal.ResourcePackExt;
import dev.xdark.betterloading.json.JsonUnbakedModelDeserializer;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Mixin(ResourcePack.class)
public interface ResourcePackMixin extends ResourcePackExt {

  @Shadow
  boolean contains(ResourceType type, Identifier id);

  @Shadow
  InputStream open(ResourceType type, Identifier id) throws IOException;

  @Override
  default InputStream tryOpen(ResourceType type, Identifier id) throws IOException {
    if (contains(type, id)) {
      return open(type, id);
    }
    return null;
  }

  @Override
  default NativeImageHolder tryLoadImage(ResourceType type, Identifier id)
      throws IOException {
    try (InputStream in = tryOpen(type, id)) {
      if (in == null) {
        return null;
      }
      return new NativeImageHolder(NativeImage.read(in));
    }
  }

  @Override
  default NativeImageHolder loadImage(ResourceType type, Identifier id) throws IOException {
    NativeImageHolder image = tryLoadImage(type, id);
    if (image == null) {
      throw new FileNotFoundException(id.toString());
    }
    return image;
  }

  @Override
  default JsonUnbakedModel tryLoadUnbakedModel(ResourceType type, Identifier id)
      throws IOException {
    try (InputStream in = tryOpen(type, id)) {
      if (in == null) {
        return null;
      }
      return JsonUnbakedModelDeserializer.INSTANCE.read(
          new JsonReader(IOUtil.toBufferedReader(in, StandardCharsets.UTF_8)));
    }
  }

  @Override
  default JsonUnbakedModel loadUnbakedModel(ResourceType type, Identifier id)
      throws IOException {
    JsonUnbakedModel model = tryLoadUnbakedModel(type, id);
    if (model == null) {
      throw new FileNotFoundException(id.toString());
    }
    return model;
  }
}
