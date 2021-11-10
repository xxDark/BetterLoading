package dev.xdark.betterloading.mixin.optional;

import dev.xdark.betterloading.cache.NativeImageHolder;
import dev.xdark.betterloading.cache.ResourceCache;
import dev.xdark.betterloading.internal.ResourcePackExt;
import net.fabricmc.fabric.api.resource.ModResourcePack;
import net.fabricmc.fabric.impl.resource.loader.GroupResourcePack;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.resource.ResourceNotFoundException;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Mixin(GroupResourcePack.class)
public abstract class GroupResourcePackMixin implements ResourcePackExt {

  @Shadow @Final protected Map<String, List<ModResourcePack>> namespacedPacks;
  private ResourceCache cache;

  @Inject(method = "<init>", at = @At("RETURN"))
  private void init(ResourceType type, List packs, CallbackInfo ci) {
    cache = new ResourceCache((ResourcePack) (Object) this);
  }

  /**
   * @author xDark
   * @reason use tryOpen
   */
  @Overwrite
  public InputStream open(ResourceType type, Identifier id) throws IOException {
    InputStream in = tryOpen(type, id);
    if (in == null) {
      throw new ResourceNotFoundException(
          null, type.getDirectory() + '/' + id.getNamespace() + '/' + id.getPath());
    }
    return in;
  }

  @Override
  public InputStream tryOpen(ResourceType type, Identifier id) throws IOException {
    List<ModResourcePack> packs = this.namespacedPacks.get(id.getNamespace());

    if (packs != null) {
      for (int i = packs.size() - 1; i >= 0; i--) {
        ResourcePackExt pack = (ResourcePackExt) packs.get(i);
        InputStream in;
        if ((in = pack.tryOpen(type, id)) != null) return in;
      }
    }
    return null;
  }

  @Override
  public NativeImageHolder tryLoadImage(ResourceType type, Identifier id) throws IOException {
    return cache.loadNativeImage(type, id);
  }

  @Override
  public JsonUnbakedModel tryLoadUnbakedModel(ResourceType type, Identifier id) throws IOException {
    return cache.loadUnbakedJsonModel(type, id);
  }
}
