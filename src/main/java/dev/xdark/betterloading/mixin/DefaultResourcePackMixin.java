package dev.xdark.betterloading.mixin;

import dev.xdark.betterloading.internal.IResourcePackMixin;
import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.io.IOException;
import java.io.InputStream;

@Mixin(DefaultResourcePack.class)
public abstract class DefaultResourcePackMixin implements IResourcePackMixin {

  @Shadow
  @Nullable
  protected abstract InputStream findInputStream(ResourceType type, Identifier id);

  @Override
  public InputStream tryOpen(ResourceType type, Identifier id) throws IOException {
    return findInputStream(type, id);
  }
}
