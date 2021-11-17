package dev.xdark.betterloading;

import sun.misc.Unsafe;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;

public final class RuntimeHelper {

  private static final Unsafe UNSAFE;
  private static final MethodHandles.Lookup LOOKUP;

  private RuntimeHelper() {}

  static <T> T getStaticValue(Field field) {
    try {
      LOOKUP.ensureInitialized(field.getDeclaringClass());
    } catch (IllegalAccessException ex) {
      throw new IllegalStateException("Could not ensure class initialization", ex);
    }
    Unsafe unsafe = UNSAFE;
    return (T) unsafe.getObject(unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field));
  }

  public static <T> T getObjectValue(Object instance, Field field) {
    try {
      LOOKUP.ensureInitialized(field.getDeclaringClass());
    } catch (IllegalAccessException ex) {
      throw new IllegalStateException("Could not ensure class initialization", ex);
    }
    Unsafe unsafe = UNSAFE;
    return (T) unsafe.getObject(instance, unsafe.objectFieldOffset(field));
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

  public static MethodHandle findVirtual(Class<?> declaringClass, String name, MethodType type) {
    try {
      return LOOKUP.findVirtual(declaringClass, name, type);
    } catch (NoSuchMethodException | IllegalAccessException ex) {
      throw new RuntimeException(
          "Unable to acquire method "
              + declaringClass.getName()
              + '.'
              + name
              + type.toMethodDescriptorString(),
          ex);
    }
  }

  public static <T extends Throwable> void _throw(Throwable t) throws T {
    throw (T) t;
  }

  static {
    try {
      Field field = Unsafe.class.getDeclaredField("theUnsafe");
      field.setAccessible(true);
      Unsafe unsafe = UNSAFE = (Unsafe) field.get(null);
      field = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
      MethodHandles.lookup();
      LOOKUP =
          (MethodHandles.Lookup)
              unsafe.getObject(unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field));
    } catch (NoSuchFieldException | IllegalAccessException ex) {
      throw new ExceptionInInitializerError(ex);
    }
  }
}
