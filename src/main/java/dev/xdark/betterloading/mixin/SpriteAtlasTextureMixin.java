package dev.xdark.betterloading.mixin;

import dev.xdark.betterloading.internal.ResourceFactoryExt;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.io.IOException;

@Mixin(SpriteAtlasTexture.class)
public abstract class SpriteAtlasTextureMixin {

  @Shadow @Final private static Logger LOGGER;

  /**
   * @author xDark
   * @reason remove call to {@link String#format(String, Object...)}
   */
  @Overwrite
  public Identifier getTexturePath(Identifier id) {
    return new Identifier(id.getNamespace(), "textures/" + id.getPath() + ".png");
  }

  /**
   * @author xDark
   * @reason use image cache if possible
   */
  @Overwrite
  public @Nullable Sprite loadSprite(
      ResourceManager container,
      Sprite.Info info,
      int atlasWidth,
      int atlasHeight,
      int maxLevel,
      int x,
      int y) {
    Identifier identifier = this.getTexturePath(info.getId());

    try {
      NativeImage image = ((ResourceFactoryExt) container).getNativeImage(identifier).getImage();
      return new Sprite(
          (SpriteAtlasTexture) (Object) this, info, maxLevel, atlasWidth, atlasHeight, x, y, image);
    } catch (RuntimeException ex) {
      LOGGER.error("Unable to parse metadata from {}", identifier, ex);
      return null;
    } catch (IOException ex) {
      LOGGER.error("Using missing texture, unable to load {}", identifier, ex);
      return null;
    }
  }
}
