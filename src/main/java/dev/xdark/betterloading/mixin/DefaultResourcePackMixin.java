package dev.xdark.betterloading.mixin;

import dev.xdark.betterloading.cache.NativeImageHolder;
import dev.xdark.betterloading.cache.ResourceCache;
import dev.xdark.betterloading.internal.ResourcePackExt;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.io.InputStream;

@Mixin(DefaultResourcePack.class)
public abstract class DefaultResourcePackMixin implements ResourcePackExt {

  @Shadow
  @Nullable
  protected abstract InputStream findInputStream(ResourceType type, Identifier id);

  private ResourceCache cache;

  @Inject(method = "<init>", at = @At("RETURN"))
  private void init(PackResourceMetadata metadata, String[] namespaces, CallbackInfo ci) {
    cache = new ResourceCache((ResourcePack) this);
  }

  @Override
  public InputStream tryOpen(ResourceType type, Identifier id) throws IOException {
    return findInputStream(type, id);
  }

  @Override
  public NativeImageHolder tryLoadImage(ResourceType type, Identifier identifier) throws IOException {
    return cache.loadNativeImage(type, identifier);
  }

  @Override
  public JsonUnbakedModel tryLoadUnbakedModel(ResourceType type, Identifier identifier) throws IOException {
    return cache.loadUnbakedJsonModel(type, identifier);
  }
}
