package dev.xdark.betterloading.mixin;

import dev.xdark.betterloading.IOUtil;
import net.minecraft.client.resource.ResourceIndex;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

@Mixin(ResourceIndex.class)
public abstract class ResourceIndexMixin {

  @Redirect(
      method = "<init>(Ljava/io/File;Ljava/lang/String;)V",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lcom/google/common/io/Files;newReader(Ljava/io/File;Ljava/nio/charset/Charset;)Ljava/io/BufferedReader;"))
  private BufferedReader openFileRead(File file, Charset charset) throws IOException {
      return IOUtil.toBufferedReader(file, charset);
  }
}
