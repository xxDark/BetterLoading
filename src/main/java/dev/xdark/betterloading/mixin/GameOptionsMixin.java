package dev.xdark.betterloading.mixin;

import dev.xdark.betterloading.IOUtil;
import net.minecraft.client.option.GameOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

@Mixin(GameOptions.class)
public abstract class GameOptionsMixin {

  @Redirect(
      method = "load",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lcom/google/common/io/Files;newReader(Ljava/io/File;Ljava/nio/charset/Charset;)Ljava/io/BufferedReader;"))
  private BufferedReader openFileRead(File file, Charset charset) throws IOException {
    return IOUtil.toBufferedReader(file, charset);
  }
}
