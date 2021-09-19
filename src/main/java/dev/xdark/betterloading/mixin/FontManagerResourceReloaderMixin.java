package dev.xdark.betterloading.mixin;

import dev.xdark.betterloading.IOUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.BufferedReader;
import java.io.Reader;

@Mixin(targets = "net/minecraft/client/font/FontManager$1")
public abstract class FontManagerResourceReloaderMixin {

  @Redirect(
      method = "prepare",
      at = @At(value = "NEW", target = "(Ljava/io/Reader;)Ljava/io/BufferedReader;"))
  private static BufferedReader bufferReader(Reader reader) {
    return IOUtil.toBufferedReader(reader);
  }
}
