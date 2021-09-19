package dev.xdark.betterloading.mixin;

import dev.xdark.betterloading.internal.ModelLoaderExt;
import dev.xdark.betterloading.internal.UnbakedModelExt;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Collection;

@Mixin(UnbakedModel.class)
public interface UnbakedModelMixin extends UnbakedModelExt {

  @Shadow
  Collection<Identifier> getModelDependencies();

  @Override
  default void putModels(ModelLoader modelLoader) {
    ((ModelLoaderExt) modelLoader).putModels(getModelDependencies());
  }
}
