package dev.xdark.betterloading.internal;

import dev.xdark.betterloading.RuntimeHelper;
import io.netty.util.concurrent.FastThreadLocalThread;
import io.netty.util.internal.RecyclableArrayList;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.Bootstrap;
import net.minecraft.datafixer.Schemas;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipFile;

public final class GameHelper {

  public static final boolean RESOURCE_LOADER_PRESENT =
      FabricLoader.getInstance().isModLoaded("fabric-resource-loader-v0");
  private static final FileSystem DEFAULT = FileSystems.getDefault();
  private static final Class<?> ZIP_PATH;
  private static final MethodHandle ZIPFS_IS_DIR;
  private static final MethodHandle ZIPFS_EXISTS;
  private static final MethodHandle ZIP_PATH_RESOLVE;
  private static Thread bootstrapThread;
  private static Throwable bootstrapThrowable;

  private GameHelper() {}

  public static void bootstrap() {
    (bootstrapThread =
            new FastThreadLocalThread(
                () -> {
                  try {
                    long now = System.currentTimeMillis();
                    Bootstrap.initialize();
                    System.out.println(
                        "Finished bootstrapping in " + (System.currentTimeMillis() - now));
                  } catch (Throwable t) {
                    bootstrapThrowable = t;
                  }
                },
                "Bootstrap Thread"))
        .start();
    Schemas.getFixer();
  }

  public static void finishBootstrap() throws InterruptedException {
    System.out.println("Waiting for bootstrap to finish...");
    bootstrapThread.join();
    Throwable bootstrapThrowable = GameHelper.bootstrapThrowable;
    if (bootstrapThrowable != null) {
      RuntimeHelper._throw(bootstrapThrowable);
    }
    Bootstrap.logMissing();
  }

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
      RuntimeHelper._throw(t);
      return false;
    }
  }

  private static boolean zipExists(FileSystem fs, byte[] path) {
    try {
      return (boolean) ZIPFS_EXISTS.invoke(fs, path);
    } catch (Throwable t) {
      RuntimeHelper._throw(t);
      return false;
    }
  }

  private static byte[] resolveZipPath(Path path) {
    try {
      return (byte[]) ZIP_PATH_RESOLVE.invoke(path);
    } catch (Throwable t) {
      RuntimeHelper._throw(t);
      return null;
    }
  }

  public static <E> List<E> ensureArrayList(Iterable<? extends E> elements) {
    if (elements instanceof ArrayList) return (ArrayList<E>) elements;
    int size;
    if (elements instanceof Collection) {
      size = ((Collection<? extends E>) elements).size();
    } else {
      size = 16;
    }
    RecyclableArrayList list = RecyclableArrayList.newInstance(size);
    for (E element : elements) list.add(element);
    return (List<E>) list;
  }

  public static void recycle(Object o) {
    if (o instanceof RecyclableArrayList) ((RecyclableArrayList) o).recycle();
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
