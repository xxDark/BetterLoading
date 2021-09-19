package dev.xdark.betterloading.mixin;

import dev.xdark.betterloading.IOUtil;
import dev.xdark.betterloading.cache.NativeImageHolder;
import dev.xdark.betterloading.internal.ResourceExt;
import dev.xdark.betterloading.json.JsonUnbakedModelDeserializer;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.Resource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Mixin(Resource.class)
public interface ResourceMixin extends ResourceExt {

  @Shadow
  InputStream getInputStream();

  @Override
  default NativeImageHolder readImage() throws IOException {
    return new NativeImageHolder(NativeImage.read(getInputStream()));
  }

  @Override
  default JsonUnbakedModel readUnbakedModel() throws IOException {
    return JsonUnbakedModelDeserializer.INSTANCE.read(
        IOUtil.toJsonReader(getInputStream(), StandardCharsets.UTF_8));
  }
}
