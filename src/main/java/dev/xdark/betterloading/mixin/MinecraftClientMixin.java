package dev.xdark.betterloading.mixin;

import net.minecraft.client.MinecraftClient;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

  /*
  @Shadow @Final private static Logger LOGGER;

  @Inject(method = "reloadResources(Z)Ljava/util/concurrent/CompletableFuture;", at = @At("RETURN"))
  private void onReload(boolean force, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
    long l = System.currentTimeMillis();
    cir.getReturnValue()
        .thenRun(
            () -> {
              LOGGER.info("Resource reload took {}ms", System.currentTimeMillis() - l);
            });
  }
     */
}
