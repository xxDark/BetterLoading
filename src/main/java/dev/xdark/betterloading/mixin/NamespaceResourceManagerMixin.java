package dev.xdark.betterloading.mixin;

import dev.xdark.betterloading.cache.NativeImageHolder;
import dev.xdark.betterloading.internal.ResourceFactoryExt;
import dev.xdark.betterloading.internal.ResourcePackExt;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.resource.NamespaceResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Mixin(NamespaceResourceManager.class)
public abstract class NamespaceResourceManagerMixin implements ResourceFactoryExt {

  @Shadow
  public abstract void validate(Identifier id) throws IOException;

  @Shadow @Final protected List<ResourcePack> packList;
  @Shadow @Final public ResourceType type;

  @Override
  public JsonUnbakedModel tryGetJsonUnbakedModel(Identifier id) throws IOException {
    this.validate(id);
    List<ResourcePack> packList = this.packList;
    ResourceType type = this.type;
    for (int i = packList.size() - 1; i >= 0; --i) {
      ResourcePack resourcePack = packList.get(i);
      JsonUnbakedModel model;
      if ((model = ((ResourcePackExt) resourcePack).tryLoadUnbakedModel(type, id)) != null) {
        return model;
      }
    }
    return null;
  }

  @Override
  public JsonUnbakedModel getJsonUnbakedModel(Identifier id) throws IOException {
    JsonUnbakedModel model = tryGetJsonUnbakedModel(id);
    if (model == null) {
      throw new FileNotFoundException(id.toString());
    }
    return model;
  }

  @Override
  public NativeImageHolder tryGetNativeImage(Identifier id) throws IOException {
    this.validate(id);
    List<ResourcePack> packList = this.packList;
    ResourceType type = this.type;
    for (int i = packList.size() - 1; i >= 0; --i) {
      ResourcePack resourcePack = packList.get(i);
      NativeImageHolder image;
      if ((image = ((ResourcePackExt) resourcePack).tryLoadImage(type, id)) != null) {
        return image;
      }
    }
    return null;
  }

  @Override
  public NativeImageHolder getNativeImage(Identifier id) throws IOException {
    NativeImageHolder image = tryGetNativeImage(id);
    if (image == null) {
      throw new FileNotFoundException(id.toString());
    }
    return image;
  }
}
