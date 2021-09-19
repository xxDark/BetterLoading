package dev.xdark.betterloading.mixin;

import dev.xdark.betterloading.cache.NativeImageHolder;
import dev.xdark.betterloading.internal.ResourceExt;
import dev.xdark.betterloading.internal.ResourceFactoryExt;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.io.FileNotFoundException;
import java.io.IOException;

@Mixin(ResourceFactory.class)
public interface ResourceFactoryMixin extends ResourceFactoryExt {

  @Shadow
  Resource getResource(Identifier id) throws IOException;

  @Override
  default JsonUnbakedModel tryGetJsonUnbakedModel(Identifier id) throws IOException {
    try (Resource resource = getResource(id)) {
      return ((ResourceExt) resource).readUnbakedModel();
    } catch (FileNotFoundException ex) {
      return null;
    }
  }

  @Override
  default JsonUnbakedModel getJsonUnbakedModel(Identifier id) throws IOException {
    try (Resource resource = getResource(id)) {
      return ((ResourceExt) resource).readUnbakedModel();
    }
  }

  @Override
  default NativeImageHolder tryGetNativeImage(Identifier id) throws IOException {
    try (Resource resource = getResource(id)) {
      return ((ResourceExt) resource).readImage();
    } catch (FileNotFoundException ex) {
      return null;
    }
  }

  @Override
  default NativeImageHolder getNativeImage(Identifier id) throws IOException {
    try (Resource resource = getResource(id)) {
      return ((ResourceExt) resource).readImage();
    }
  }
}
