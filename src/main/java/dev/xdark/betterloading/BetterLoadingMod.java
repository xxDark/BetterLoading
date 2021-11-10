package dev.xdark.betterloading;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.xdark.betterloading.json.JsonUnbakedModelDeserializer;
import dev.xdark.betterloading.json.ModelElementDeserializer;
import dev.xdark.betterloading.json.ModelElementFaceDeserializer;
import dev.xdark.betterloading.json.ModelElementTextureDeserializer;
import dev.xdark.betterloading.json.ModelOverrideDeserializer;
import dev.xdark.betterloading.json.ModelTransformationDeserializer;
import dev.xdark.betterloading.json.SoundEntryDeserializer;
import dev.xdark.betterloading.json.TransformationDeserializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.render.model.json.ModelElementTexture;
import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.client.sound.SoundEntry;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.text.Text;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public final class BetterLoadingMod implements ModInitializer {

  public static final boolean RESOURCE_LOADER_PRESENT =
      FabricLoader.getInstance().isModLoaded("fabric-resource-loader-v0");

  @Override
  public void onInitialize() {
    System.out.println("BetterLoading will now replace Gson instances...");
    RuntimeHelper.setStaticValue(
        searchForGsonField(JsonUnbakedModel.class),
        new GsonBuilder()
            .registerTypeAdapter(JsonUnbakedModel.class, JsonUnbakedModelDeserializer.INSTANCE)
            .registerTypeAdapter(ModelElement.class, ModelElementDeserializer.INSTANCE)
            .registerTypeAdapter(ModelElementFace.class, ModelElementFaceDeserializer.INSTANCE)
            .registerTypeAdapter(
                ModelElementTexture.class, ModelElementTextureDeserializer.INSTANCE)
            .registerTypeAdapter(Transformation.class, TransformationDeserializer.INSTANCE)
            .registerTypeAdapter(
                ModelTransformation.class, ModelTransformationDeserializer.INSTANCE)
            .registerTypeAdapter(ModelOverride.class, ModelOverrideDeserializer.INSTANCE)
            .create());
    RuntimeHelper.setStaticValue(
        searchForGsonField(SoundManager.class),
        new GsonBuilder()
            .registerTypeHierarchyAdapter(Text.class, new Text.Serializer())
            .registerTypeAdapter(SoundEntry.class, SoundEntryDeserializer.INSTANCE)
            .create());
    System.out.println("Replacement complete.");
  }

  private static Field searchForGsonField(Class<?> c) {
    for (Field f : c.getDeclaredFields()) {
      int m;
      if (f.getType() == Gson.class
          && Modifier.isStatic(m = f.getModifiers())
          && Modifier.isFinal(m)) {
        return f;
      }
    }
    throw new IllegalStateException("No GSON field in " + c);
  }
}
