package dev.xdark.betterloading.mixin;

import dev.xdark.betterloading.IOUtil;
import net.minecraft.client.resource.VideoWarningManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.BufferedReader;
import java.io.Reader;

@Mixin(VideoWarningManager.class)
public abstract class VideoWarningManagerMixin {

  @Redirect(
      method = "loadWarnlist",
      at = @At(value = "NEW", target = "(Ljava/io/Reader;)Ljava/io/BufferedReader;"))
  private static BufferedReader bufferReader(Reader reader) {
    return IOUtil.toBufferedReader(reader);
  }
}
