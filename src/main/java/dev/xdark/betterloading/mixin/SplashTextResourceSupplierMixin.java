package dev.xdark.betterloading.mixin;

import dev.xdark.betterloading.IOUtil;
import net.minecraft.client.resource.SplashTextResourceSupplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.BufferedReader;
import java.io.Reader;

@Mixin(SplashTextResourceSupplier.class)
public abstract class SplashTextResourceSupplierMixin {

  @Redirect(
      method = "prepare",
      at = @At(value = "NEW", target = "Ljava/io/BufferedReader;<init>(Ljava/io/Reader;)V"))
  private static BufferedReader bufferReader(Reader reader) {
    return IOUtil.toBufferedReader(reader);
  }
}
