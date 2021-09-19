package dev.xdark.betterloading;

import sun.misc.Unsafe;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;

final class UnsafeHelper {

  private static final Unsafe UNSAFE;
  private static final MethodHandles.Lookup LOOKUP;

  private UnsafeHelper() {}

  static <T> T getStaticValue(Field field) {
    try {
      LOOKUP.ensureInitialized(field.getDeclaringClass());
    } catch (IllegalAccessException ex) {
      throw new IllegalStateException("Could not ensure class initialization", ex);
    }
    Unsafe unsafe = UNSAFE;
    return (T) unsafe.getObject(unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field));
  }

  static void setStaticValue(Field field, Object value) {
    try {
      LOOKUP.ensureInitialized(field.getDeclaringClass());
    } catch (IllegalAccessException ex) {
      throw new IllegalStateException("Could not ensure class initialization", ex);
    }
    Unsafe unsafe = UNSAFE;
    unsafe.putObject(unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field), value);
  }

  static {
    try {
      Field field = Unsafe.class.getDeclaredField("theUnsafe");
      field.setAccessible(true);
      UNSAFE = (Unsafe) field.get(null);
    } catch (NoSuchFieldException | IllegalAccessException ex) {
      throw new ExceptionInInitializerError(ex);
    }
    LOOKUP = MethodHandles.lookup();
  }
}
