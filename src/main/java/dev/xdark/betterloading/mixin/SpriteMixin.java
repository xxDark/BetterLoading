package dev.xdark.betterloading.mixin;

import net.minecraft.client.texture.Sprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.stream.IntStream;

@Mixin(Sprite.class)
public abstract class SpriteMixin {

  @Redirect(
      method = "createAnimation",
      at =
          @At(
              value = "INVOKE",
              target = "Ljava/util/stream/IntStream;range(II)Ljava/util/stream/IntStream;"))
  private IntStream onUnusedCheck(int startInclusive, int endExclusive) {
    return IntStream.empty();
  }
}
