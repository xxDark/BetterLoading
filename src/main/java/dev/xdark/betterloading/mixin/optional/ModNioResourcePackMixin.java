package dev.xdark.betterloading.mixin.optional;

import dev.xdark.betterloading.cache.NativeImageHolder;
import dev.xdark.betterloading.cache.ResourceCache;
import dev.xdark.betterloading.internal.FabricInjector;
import dev.xdark.betterloading.internal.FileResourcePackExt;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.fabric.impl.resource.loader.ModNioResourcePack;
import net.fabricmc.fabric.impl.resource.loader.ModResourcePackUtil;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Mixin(ModNioResourcePack.class)
public abstract class ModNioResourcePackMixin implements FileResourcePackExt {

  @Shadow
  protected abstract Path getPath(String filename);

  @Shadow @Final private ModMetadata modInfo;
  @Shadow @Final private ResourceType type;

  @Shadow
  public abstract Collection<Identifier> findResources(
      ResourceType type, String type2, String namespace, int prefix, Predicate<String> maxDepth);

  private ResourceCache cache;

  private String zipPathPrefix;
  private ZipFile fileSystemOverride;

  @Inject(method = "<init>", at = @At("RETURN"))
  private void init(
      ModMetadata modInfo,
      Path path,
      ResourceType type,
      AutoCloseable closer,
      ResourcePackActivationType activationType,
      CallbackInfo ci) {
    cache = new ResourceCache((ResourcePack) (Object) this);
    if (FabricInjector.isZipPath(path)) {
      try {
        if ((fileSystemOverride = FabricInjector.openZipFile(path.getFileSystem())) != null) {
          String zipPathPrefix = path.toAbsolutePath().normalize().toString();
          if (zipPathPrefix.charAt(0) == '/') zipPathPrefix = zipPathPrefix.substring(1);
          this.zipPathPrefix = zipPathPrefix + '/';
        }
      } catch (IOException ex) {
        System.err.println("Unable to open ZIP archive: ");
        ex.printStackTrace();
      }
    }
  }

  @Override
  public InputStream tryOpenFile(String name) throws IOException {
    ZipFile zf = fileSystemOverride;
    ZipEntry entry;
    if (zf != null && (entry = zf.getEntry(zipPathPrefix + name)) != null)
      return zf.getInputStream(entry);
    Path path = getPath(name);

    // RSPEC-3725
    if (path != null && /* Files.isRegularFile(path)*/ FabricInjector.isFile(path)) {
      return Files.newInputStream(path);
    }

    return ModResourcePackUtil.openDefault(this.modInfo, this.type, name);
  }

  @Redirect(
      method = "*",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Ljava/nio/file/Files;exists(Ljava/nio/file/Path;[Ljava/nio/file/LinkOption;)Z"))
  private boolean existsCheck(Path path, LinkOption[] x) {
    // assert x.length == 0;
    // RSPEC-3725
    return FabricInjector.exists(path);
  }

  @Redirect(
      method = "*",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Ljava/nio/file/Files;isDirectory(Ljava/nio/file/Path;[Ljava/nio/file/LinkOption;)Z"))
  private boolean isDirectoryCheck(Path path, LinkOption[] x) {
    // assert x.length == 0;
    // RSPEC-3725
    return FabricInjector.isDirectory(path);
  }

  /**
   * @author xDark
   * @reason use tryOpen
   */
  @Overwrite
  protected InputStream openFile(String filename) throws IOException {
    InputStream in = tryOpenFile(filename);
    if (in == null) {
      throw new FileNotFoundException(
          '\"' + filename + "\" in Fabric mod \"" + modInfo.getId() + "\"");
    }
    return in;
  }

  @Override
  public NativeImageHolder tryLoadImage(ResourceType type, Identifier id) throws IOException {
    return cache.loadNativeImage(type, id);
  }

  @Override
  public JsonUnbakedModel tryLoadUnbakedModel(ResourceType type, Identifier id) throws IOException {
    return cache.loadUnbakedJsonModel(type, id);
  }
}
