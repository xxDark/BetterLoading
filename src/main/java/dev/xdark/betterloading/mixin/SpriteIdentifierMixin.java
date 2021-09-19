package dev.xdark.betterloading.mixin;

import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SpriteIdentifier.class)
public abstract class SpriteIdentifierMixin {

	@Shadow @Final private Identifier texture;
	@Shadow @Final private Identifier atlas;

	/**
	 * @author xDark
	 * @reason remove array allocation
	 */
	@Overwrite
	public int hashCode() {
		return 31 * atlas.hashCode() + texture.hashCode();
	}
}
