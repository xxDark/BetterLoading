package dev.xdark.betterloading.cache;

import net.minecraft.client.texture.NativeImage;

public final class NativeImageHolder {

  private NativeImage image;
  private int[] pixels;

  public NativeImageHolder(NativeImage image) {
    this.image = image;
  }

  public NativeImage getImage() {
    return image;
  }

  public void replaceImage(NativeImage image) {
    this.image = image;
    pixels = null;
  }

  public int[] makePixelArray() {
    int[] pixels = this.pixels;
    if (pixels == null) {
      return this.pixels = image.makePixelArray();
    }
    return pixels;
  }
}
