package dev.xdark.betterloading.mixin;

import com.google.gson.stream.JsonReader;
import dev.xdark.betterloading.internal.ModelLoaderExt;
import dev.xdark.betterloading.internal.ResourceFactoryExt;
import dev.xdark.betterloading.internal.UnbakedModelExt;
import dev.xdark.betterloading.json.JsonUnbakedModelDeserializer;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

@Mixin(ModelLoader.class)
public abstract class ModelLoaderMixin implements ModelLoaderExt {

  @Shadow @Final public static JsonUnbakedModel GENERATION_MARKER;
  @Shadow @Final public static JsonUnbakedModel BLOCK_ENTITY_MARKER;
  @Shadow @Final private static Map<String, String> BUILTIN_MODEL_DEFINITIONS;
  @Shadow @Final private ResourceManager resourceManager;
  @Shadow @Final private Set<Identifier> modelsToLoad;
  @Shadow @Final private Map<Identifier, UnbakedModel> unbakedModels;

  @Override
  public void putModel(Identifier model) {
    this.modelsToLoad.add(model);
  }

  @Override
  public void putModels(Collection<Identifier> models) {
    this.modelsToLoad.addAll(models);
  }

  /**
   * @author xDark
   * @reason use model cache if possible
   */
  @Overwrite
  private JsonUnbakedModel loadModelFromJson(Identifier id) throws IOException {
    String path = id.getPath();
    if ("builtin/generated".equals(path)) {
      return GENERATION_MARKER;
    }
    if ("builtin/entity".equals(path)) {
      return BLOCK_ENTITY_MARKER;
    }

    JsonUnbakedModel model;
    if (path.startsWith("builtin/")) {
      String key = path.substring("builtin/".length());
      String definition = BUILTIN_MODEL_DEFINITIONS.get(key);
      if (definition == null) {
        throw new FileNotFoundException(id.toString());
      }

      model =
          JsonUnbakedModelDeserializer.INSTANCE.read(new JsonReader(new StringReader(definition)));
    } else {
      model =
          ((ResourceFactoryExt) this.resourceManager)
              .getJsonUnbakedModel(
                  new Identifier(id.getNamespace(), "models/" + id.getPath() + ".json"));
    }
    model.id = id.toString();
    return model;
  }

  /**
   * @author xDark
   * @reason use model specific versions
   */
  @Overwrite
  private void putModel(Identifier id, UnbakedModel unbakedModel) {
    this.unbakedModels.put(id, unbakedModel);
    ((UnbakedModelExt) unbakedModel).putModels((ModelLoader) (Object) this);
  }
}
