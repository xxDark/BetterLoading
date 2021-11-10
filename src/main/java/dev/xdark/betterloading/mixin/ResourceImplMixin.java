package dev.xdark.betterloading.mixin;

import dev.xdark.betterloading.IOUtil;
import net.minecraft.resource.ResourceImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.BufferedReader;
import java.io.Reader;

@Mixin(ResourceImpl.class)
public abstract class ResourceImplMixin {

  @Redirect(
      method = "getMetadata",
      at = @At(value = "NEW", target = "Ljava/io/BufferedReader;<init>(Ljava/io/Reader;)V"))
  private static BufferedReader bufferReader(Reader reader) {
    return IOUtil.toBufferedReader(reader);
  }
}
