package dev.xdark.betterloading.mixin;

import com.google.common.collect.Collections2;
import dev.xdark.betterloading.internal.ModelLoaderExt;
import dev.xdark.betterloading.internal.UnbakedModelExt;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(JsonUnbakedModel.class)
public abstract class JsonUnbakedModelMixin implements UnbakedModelExt {

  @Shadow @Final private List<ModelOverride> overrides;
  @Shadow @Nullable public Identifier parentId;
  @Shadow @Final public List<ModelElement> elements;
  @Shadow @Nullable public JsonUnbakedModel parent;

  @Override
  public void putModels(ModelLoader modelLoader) {
    Identifier parentId = this.parentId;
    if (parentId != null) {
      List<ModelOverride> overrides = this.overrides;
      ((ModelLoaderExt) modelLoader)
          .putModels(Collections2.transform(overrides, ModelOverride::getModelId));
      ((ModelLoaderExt) modelLoader).putModel(parentId);
    }
  }

  /**
   * @author xDark
   * @reason avoid recursive calls
   */
  @Overwrite
  public List<ModelElement> getElements() {
    var elements = this.elements;
    JsonUnbakedModel parent;
    if (!elements.isEmpty() || (parent = this.parent) == null) {
      return elements;
    }
    JsonUnbakedModel model = parent;
    while (true) {
      if ((elements = model.elements).isEmpty()) {
        if ((model = model.parent) != null) {
          continue;
        }
      }
      return elements;
    }
  }

  /**
   * @author xDark
   * @reason avoid recursive calls
   */
  @Overwrite
  public boolean useAmbientOcclusion() {
    var model = (JsonUnbakedModel) (Object) this;
    JsonUnbakedModel result;
    do {
      result = model;
    } while ((model = model.parent) != null);
    return result.ambientOcclusion;
  }

  /**
   * @author xDark
   * @reason avoid recursive calls
   */
  @Overwrite
  public JsonUnbakedModel.GuiLight getGuiLight() {
    JsonUnbakedModel.GuiLight guiLight;
    var model = (JsonUnbakedModel) (Object) this;
    while (model != null) {
      guiLight = model.guiLight;
      if (guiLight != null) return guiLight;
      model = model.parent;
    }
    return JsonUnbakedModel.GuiLight.BLOCK;
  }

  /**
   * @author xDark
   * @reason avoid recursive calls
   */
  @Overwrite
  public boolean needsResolution() {
    var model = (JsonUnbakedModel) (Object) this;
    while (model != null) {
      if (model.parentId == null) return true;
      model = model.parent;
    }
    return false;
  }
}
