package dev.xdark.betterloading.mixin;

import org.apache.commons.lang3.ObjectUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.BitSet;

@Mixin(targets = "com.mojang.datafixers.functions.PointFreeRule$CataFuseDifferent")
public abstract class CataFuseDifferentMixin {

  @Redirect(
      method = "doRewrite",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lorg/apache/commons/lang3/ObjectUtils;clone(Ljava/lang/Object;)Ljava/lang/Object;"))
  private static <T> T clone(T obj) {
    if (obj instanceof BitSet) {
      return (T) ((BitSet) obj).clone();
    }
    return ObjectUtils.clone(obj);
  }
}
