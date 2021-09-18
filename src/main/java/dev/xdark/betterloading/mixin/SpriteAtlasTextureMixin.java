package dev.xdark.betterloading.mixin;

import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(SpriteAtlasTexture.class)
public abstract class SpriteAtlasTextureMixin {

  /**
   * @author xDark
   * @reason remove call to {@link String#format(String, Object...)}
   */
  @Overwrite
  private Identifier getTexturePath(Identifier id) {
    return new Identifier(id.getNamespace(), "textures/" + id.getPath() + ".png");
  }
}
