package dev.xdark.betterloading.internal;

import dev.xdark.betterloading.RuntimeHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.ResourcePack;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipFile;

public final class FabricInjector {

  public static final boolean RESOURCE_LOADER_PRESENT =
      FabricLoader.getInstance().isModLoaded("fabric-resource-loader-v0");
  private static final FileSystem DEFAULT = FileSystems.getDefault();
  private static final Class<?> ZIP_PATH;
  private static final MethodHandle ZIPFS_IS_DIR;
  private static final MethodHandle ZIPFS_EXISTS;
  private static final MethodHandle ZIP_PATH_RESOLVE;

  private FabricInjector() {}

  public static Field getDefaultDelegatingPackField() {
    if (!RESOURCE_LOADER_PRESENT) return null;
    for (Field field : DefaultResourcePack.class.getDeclaredFields()) {
      if (field.getType() == ResourcePack.class && "fabric_mcJarPack".equals(field.getName())) {
        return field;
      }
    }
    throw new IllegalStateException("Could not locate delegating resource pack field");
  }

  public static boolean isZipPath(Path path) {
    return ZIP_PATH.isInstance(path);
  }

  public static ZipFile openZipFile(FileSystem fs) throws IOException {
    File file = new File(fs.toString());
    if (file.isFile()) {
      return new ZipFile(file);
    }
    return null;
  }

  public static boolean isFile(Path path) {
    FileSystem fs = path.getFileSystem();
    if (isZipPath(path)) {
      byte[] raw = resolveZipPath(path);
      return zipExists(fs, raw) && !zipIsDirectory(fs, raw);
    }
    if (DEFAULT == fs) return path.toFile().isFile();
    return Files.isRegularFile(path);
  }

  public static boolean isDirectory(Path path) {
    FileSystem fs = path.getFileSystem();
    if (isZipPath(path)) return zipIsDirectory(fs, resolveZipPath(path));
    if (DEFAULT == fs) return path.toFile().isDirectory();
    return Files.isDirectory(path);
  }

  public static boolean exists(Path path) {
    FileSystem fs = path.getFileSystem();
    if (isZipPath(path)) return zipExists(fs, resolveZipPath(path));
    if (DEFAULT == fs) return path.toFile().exists();
    return Files.exists(path);
  }

  private static boolean zipIsDirectory(FileSystem fs, byte[] path) {
    try {
      return (boolean) ZIPFS_IS_DIR.invoke(fs, path);
    } catch (Throwable t) {
      sneaky(t);
      return false;
    }
  }

  private static boolean zipExists(FileSystem fs, byte[] path) {
    try {
      return (boolean) ZIPFS_EXISTS.invoke(fs, path);
    } catch (Throwable t) {
      sneaky(t);
      return false;
    }
  }

  private static byte[] resolveZipPath(Path path) {
    try {
      return (byte[]) ZIP_PATH_RESOLVE.invoke(path);
    } catch (Throwable t) {
      sneaky(t);
      return null;
    }
  }

  private static <T extends Throwable> void sneaky(Throwable r) throws T {
    throw (T) r;
  }

  static {
    try {
      ZIP_PATH = Class.forName("jdk.nio.zipfs.ZipPath");
      Class<?> zipfs = Class.forName("jdk.nio.zipfs.ZipFileSystem");
      ZIPFS_IS_DIR =
          RuntimeHelper.findVirtual(
              zipfs, "isDirectory", MethodType.methodType(Boolean.TYPE, byte[].class));
      ZIPFS_EXISTS =
          RuntimeHelper.findVirtual(
              zipfs, "exists", MethodType.methodType(Boolean.TYPE, byte[].class));
      ZIP_PATH_RESOLVE =
          RuntimeHelper.findVirtual(
              ZIP_PATH, "getResolvedPath", MethodType.methodType(byte[].class));
      if (RESOURCE_LOADER_PRESENT) {
        System.out.println("detected Fabric's resource loader");
      }
    } catch (ClassNotFoundException ex) {
      throw new ExceptionInInitializerError(ex);
    }
  }
}
