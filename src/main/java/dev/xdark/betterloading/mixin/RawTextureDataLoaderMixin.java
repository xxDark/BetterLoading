package dev.xdark.betterloading.mixin;

import dev.xdark.betterloading.cache.NativeImageHolder;
import dev.xdark.betterloading.internal.ResourceFactoryExt;
import net.minecraft.client.util.RawTextureDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.io.IOException;

@Mixin(RawTextureDataLoader.class)
public class RawTextureDataLoaderMixin {

  /**
   * @author xDark
   * @reason use image cache if possible
   */
  @Deprecated
  @Overwrite
  public static int[] loadRawTextureData(ResourceManager resourceManager, Identifier id)
      throws IOException {
    NativeImageHolder imageHolder = ((ResourceFactoryExt) resourceManager).getNativeImage(id);
    return imageHolder.makePixelArray();
  }
}
