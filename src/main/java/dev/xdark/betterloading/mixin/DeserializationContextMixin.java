package dev.xdark.betterloading.mixin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.xdark.betterloading.json.ModelVariantDeserializer;
import dev.xdark.betterloading.json.ModelVariantMapDeserializer;
import dev.xdark.betterloading.json.MultipartModelComponentDeserializer;
import dev.xdark.betterloading.json.MultipartUnbakedModelDeserializer;
import dev.xdark.betterloading.json.WeightedUnbakedModelDeserializer;
import net.minecraft.client.render.model.MultipartUnbakedModel;
import net.minecraft.client.render.model.json.ModelVariant;
import net.minecraft.client.render.model.json.ModelVariantMap;
import net.minecraft.client.render.model.json.MultipartModelComponent;
import net.minecraft.client.render.model.json.WeightedUnbakedModel;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelVariantMap.DeserializationContext.class)
public abstract class DeserializationContextMixin {

  @Mutable @Shadow @Final protected Gson gson;

  @Inject(method = "<init>", at = @At("RETURN"))
  private void onInit(CallbackInfo ci) {
    MultipartUnbakedModelDeserializer multipartUnbakedModelDeserializer =
        new MultipartUnbakedModelDeserializer(
            (ModelVariantMap.DeserializationContext) (Object) this);
    gson =
        (new GsonBuilder())
            .registerTypeAdapter(
                ModelVariantMap.class,
                new ModelVariantMapDeserializer(multipartUnbakedModelDeserializer))
            .registerTypeAdapter(ModelVariant.class, ModelVariantDeserializer.INSTANCE)
            .registerTypeAdapter(
                WeightedUnbakedModel.class, WeightedUnbakedModelDeserializer.INSTANCE)
            .registerTypeAdapter(
                MultipartModelComponent.class, MultipartModelComponentDeserializer.INSTANCE)
            .registerTypeAdapter(MultipartUnbakedModel.class, multipartUnbakedModelDeserializer)
            .create();
  }
}
