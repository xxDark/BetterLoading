package dev.xdark.betterloading.cache;

import dev.xdark.betterloading.internal.ResourceExt;
import dev.xdark.betterloading.internal.ResourcePackExt;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.resource.ResourceImpl;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;

public final class CachedResourceImpl extends ResourceImpl implements ResourceExt {

  private final ResourcePack resourcePack;
  private final ResourceType resourceType;

  public CachedResourceImpl(
      ResourcePack resourcePack,
      ResourceType resourceType,
      Identifier id,
      InputStream inputStream,
      @Nullable InputStream metaInputStream) {
    super(resourcePack.getName(), id, inputStream, metaInputStream);
    this.resourcePack = resourcePack;
    this.resourceType = resourceType;
  }

  @Override
  public NativeImageHolder readImage() throws IOException {
    return ((ResourcePackExt) resourcePack).loadImage(resourceType, getId());
  }

  @Override
  public JsonUnbakedModel readUnbakedModel() throws IOException {
    return ((ResourcePackExt) resourcePack).loadUnbakedModel(resourceType, getId());
  }
}
