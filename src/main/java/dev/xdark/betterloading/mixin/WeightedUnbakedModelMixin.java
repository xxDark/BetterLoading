package dev.xdark.betterloading.mixin;

import com.google.common.collect.Collections2;
import dev.xdark.betterloading.internal.ModelLoaderExt;
import dev.xdark.betterloading.internal.UnbakedModelExt;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.json.ModelVariant;
import net.minecraft.client.render.model.json.WeightedUnbakedModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Collection;
import java.util.List;

@Mixin(WeightedUnbakedModel.class)
public abstract class WeightedUnbakedModelMixin implements UnbakedModelExt {

  @Shadow
  public abstract List<ModelVariant> getVariants();

  @Override
  public void putModels(ModelLoader modelLoader) {
    Collection<ModelVariant> variants = getVariants();
    if (!variants.isEmpty()) {
      ((ModelLoaderExt) modelLoader)
          .putModels(Collections2.transform(variants, ModelVariant::getLocation));
    }
  }
}
