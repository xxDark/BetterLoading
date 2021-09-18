package dev.xdark.betterloading.mixin;

import dev.xdark.betterloading.internal.IResourcePackMixin;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.io.IOException;
import java.io.InputStream;

@Mixin(ResourcePack.class)
public interface ResourcePackMixin extends IResourcePackMixin {

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
}
