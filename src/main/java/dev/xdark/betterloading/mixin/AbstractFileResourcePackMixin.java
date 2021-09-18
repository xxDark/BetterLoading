package dev.xdark.betterloading.mixin;

import dev.xdark.betterloading.internal.IFileResourcePackMixin;
import net.minecraft.resource.AbstractFileResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.io.IOException;
import java.io.InputStream;

@Mixin(AbstractFileResourcePack.class)
public abstract class AbstractFileResourcePackMixin implements IFileResourcePackMixin {

  @Shadow
  protected abstract boolean containsFile(String name);

  @Shadow
  protected abstract InputStream openFile(String name) throws IOException;

  /**
   * @author xDark
   * @reason remove {@link String#format(String, Object...)} call
   */
  @Overwrite
  private static String getFilename(ResourceType type, Identifier id) {
    return type.getDirectory() + '/' + id.getNamespace() + '/' + id.getPath();
  }

  @Override
  public InputStream tryOpenFile(String name) throws IOException {
    if (containsFile(name)) {
      return openFile(name);
    }
    return null;
  }

  @Override
  public InputStream tryOpen(ResourceType type, Identifier id) throws IOException {
    return tryOpenFile(getFilename(type, id));
  }
}
