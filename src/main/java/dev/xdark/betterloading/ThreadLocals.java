package dev.xdark.betterloading;

import io.netty.util.concurrent.FastThreadLocal;

public final class ThreadLocals {

  private static final FastThreadLocal<Buffer> BUFFER_LOCAL =
      new FastThreadLocal<>() {
        @Override
        protected Buffer initialValue() throws Exception {
          return new Buffer();
        }
      };

  private ThreadLocals() {}

  public static byte[] byteBuffer() {
    return BUFFER_LOCAL.get().bbuffer;
  }

  public static char[] charBuffer() {
    return BUFFER_LOCAL.get().cbuffer;
  }

  private static final class Buffer {

    private static final int BUFFER_SIZE = 8192;

    final char[] cbuffer;
    final byte[] bbuffer;

    private Buffer() {
      this.cbuffer = new char[BUFFER_SIZE];
      this.bbuffer = new byte[BUFFER_SIZE];
    }
  }
}
