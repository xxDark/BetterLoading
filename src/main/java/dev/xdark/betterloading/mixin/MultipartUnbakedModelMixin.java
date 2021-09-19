package dev.xdark.betterloading.mixin;

import dev.xdark.betterloading.internal.UnbakedModelExt;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.MultipartUnbakedModel;
import net.minecraft.client.render.model.json.MultipartModelComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(MultipartUnbakedModel.class)
public abstract class MultipartUnbakedModelMixin implements UnbakedModelExt {

  @Shadow
  public abstract List<MultipartModelComponent> getComponents();

  @Override
  public void putModels(ModelLoader modelLoader) {
    List<MultipartModelComponent> components = this.getComponents();
    for (int i = 0, j = components.size(); i < j; i++) {
      ((UnbakedModelExt) components.get(i).getModel()).putModels(modelLoader);
    }
  }
}
