package dev.xdark.betterloading;

import java.io.BufferedReader;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;

final class UnsafeIO {

  private static final MethodHandle MH_BR_SET_BUF;

  private UnsafeIO() {}

  static void setReaderBuffer(BufferedReader reader, char[] buffer) {
    try {
      MH_BR_SET_BUF.invokeExact(reader, buffer);
    } catch (Throwable t) {
      throw new IllegalStateException("Could not set buffer for a reader", t);
    }
  }

  static {
    try {
      Field field = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
      MethodHandles.Lookup lookup = UnsafeHelper.getStaticValue(field);
      MH_BR_SET_BUF = lookup.findSetter(BufferedReader.class, "cb", char[].class);
    } catch (NoSuchFieldException | IllegalAccessException ex) {
      throw new ExceptionInInitializerError(ex);
    }
  }
}
