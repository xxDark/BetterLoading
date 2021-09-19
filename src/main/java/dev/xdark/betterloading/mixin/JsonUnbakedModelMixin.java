package dev.xdark.betterloading.mixin;

import com.google.common.collect.Collections2;
import dev.xdark.betterloading.internal.ModelLoaderExt;
import dev.xdark.betterloading.internal.UnbakedModelExt;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(JsonUnbakedModel.class)
public abstract class JsonUnbakedModelMixin implements UnbakedModelExt {

  @Shadow @Final private List<ModelOverride> overrides;
  @Shadow @Nullable protected Identifier parentId;

  @Override
  public void putModels(ModelLoader modelLoader) {
    List<ModelOverride> overrides = this.overrides;
    ((ModelLoaderExt) modelLoader)
        .putModels(Collections2.transform(overrides, ModelOverride::getModelId));
    Identifier parentId = this.parentId;
    if (parentId != null) {
      ((ModelLoaderExt)modelLoader).putModel(parentId);
    }
  }
}
