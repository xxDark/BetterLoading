package dev.xdark.betterloading.mixin;

import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StateManager.Builder.class)
public abstract class StateManagerBuilderMixin {

  @Inject(method = "validate", at = @At("HEAD"), cancellable = true)
  private <T extends Comparable<T>> void validate(Property<T> property, CallbackInfo ci) {
    ci.cancel();
  }
}
