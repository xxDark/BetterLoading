package dev.xdark.betterloading.mixin;

import io.netty.util.concurrent.FastThreadLocalThread;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Mixin(Util.class)
public class UtilMixin {

  @Shadow @Final private static AtomicInteger NEXT_WORKER_ID;

  @Shadow
  private static void uncaughtExceptionHandler(Thread thread, Throwable t) {}

  /**
   * @author xDark
   * @reason use {@link FastThreadLocalThread}
   */
  @Overwrite
  private static ExecutorService createIoWorker() {
    return Executors.newCachedThreadPool(
        r -> {
          FastThreadLocalThread thread =
              new FastThreadLocalThread(r, "IO-Worker-" + NEXT_WORKER_ID.getAndIncrement());
          thread.setUncaughtExceptionHandler(UtilMixin::uncaughtExceptionHandler);
          return thread;
        });
  }
}
