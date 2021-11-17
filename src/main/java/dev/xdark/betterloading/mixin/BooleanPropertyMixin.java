package dev.xdark.betterloading.mixin;

import net.minecraft.state.property.BooleanProperty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Optional;

@Mixin(BooleanProperty.class)
public final class BooleanPropertyMixin {

  private static final Optional<Boolean> TRUE = Optional.of(true);
  private static final Optional<Boolean> FALSE = Optional.of(false);

  /**
   * @author xDark
   * @reason reuse optionals
   */
  @Overwrite
  public Optional<Boolean> parse(String name) {
    if ("true".equals(name)) return TRUE;
    if ("false".equals(name)) return FALSE;
    return Optional.empty();
  }
}
