package dev.xdark.betterloading.mixin;

import io.netty.util.concurrent.FastThreadLocalThread;
import net.minecraft.client.main.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Main.class)
public abstract class MainMixin {

  @Inject(method = "main", at = @At("HEAD"), cancellable = true)
  private static void assistLaunch(String[] args, CallbackInfo ci) {
    Thread t;
    if (!((t = Thread.currentThread()) instanceof FastThreadLocalThread)) {
      System.out.println("Re-entering into the entrypoint in fast thread local thread");
      FastThreadLocalThread thread = new FastThreadLocalThread(() -> Main.main(args));
      thread.setContextClassLoader(t.getContextClassLoader());
      thread.start();
      ci.cancel();
    }
  }
}
