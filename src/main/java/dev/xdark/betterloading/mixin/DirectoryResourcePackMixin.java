package dev.xdark.betterloading.mixin;

import dev.xdark.betterloading.internal.FileResourcePackExt;
import net.minecraft.resource.DirectoryResourcePack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Mixin(DirectoryResourcePack.class)
public abstract class DirectoryResourcePackMixin implements FileResourcePackExt {

  @Shadow
  @Nullable
  protected abstract File getFile(String name);

  @Override
  public InputStream tryOpenFile(String name) throws IOException {
    File file = getFile(name);
    if (file == null) {
      return null;
    }
    return new FileInputStream(file);
  }
}
