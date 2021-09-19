package dev.xdark.betterloading.cache;

import com.mojang.blaze3d.platform.TextureUtil;
import net.minecraft.client.texture.NativeImage;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public final class CachedNativeImage extends NativeImage {

  public CachedNativeImage(int width, int height, boolean useStb) {
    super(width, height, useStb);
  }

  public CachedNativeImage(Format format, int width, int height, boolean useStb) {
    super(format, width, height, useStb);
  }

  public CachedNativeImage(Format format, int width, int height, boolean useStb, long pointer) {
    super(format, width, height, useStb, pointer);
  }

  @Override
  public void close() {}

  public void doClose() {
    super.close();
  }

  public static CachedNativeImage read(InputStream inputStream) throws IOException {
    return read(NativeImage.Format.ABGR, inputStream);
  }

  public static CachedNativeImage read(@Nullable NativeImage.Format format, InputStream inputStream)
      throws IOException {
    ByteBuffer buffer = null;

    CachedNativeImage image;
    try {
      buffer = TextureUtil.readResource(inputStream);
      buffer.rewind();
      image = read(format, buffer);
    } finally {
      MemoryUtil.memFree(buffer);
      IOUtils.closeQuietly(inputStream);
    }

    return image;
  }

  public static CachedNativeImage read(ByteBuffer buffer) throws IOException {
    return read(NativeImage.Format.ABGR, buffer);
  }

  public static CachedNativeImage read(@Nullable NativeImage.Format format, ByteBuffer buffer)
      throws IOException {
    if (format != null && !format.isWriteable()) {
      throw new UnsupportedOperationException("Don't know how to read format " + format);
    } else if (MemoryUtil.memAddress(buffer) == 0L) {
      throw new IllegalArgumentException("Invalid buffer");
    } else {

      try (MemoryStack memoryStack = MemoryStack.stackPush()) {
        IntBuffer x = memoryStack.mallocInt(1);
        IntBuffer y = memoryStack.mallocInt(1);
        IntBuffer channels = memoryStack.mallocInt(1);
        ByteBuffer byteBuffer2 =
            STBImage.stbi_load_from_memory(
                buffer, x, y, channels, format == null ? 0 : format.channelCount);
        if (byteBuffer2 == null) {
          throw new IOException("Could not load image: " + STBImage.stbi_failure_reason());
        }

        return new CachedNativeImage(
            format == null ? NativeImage.Format.getFormat(channels.get(0)) : format,
            x.get(0),
            y.get(0),
            true,
            MemoryUtil.memAddress(byteBuffer2));
      }
    }
  }
}
