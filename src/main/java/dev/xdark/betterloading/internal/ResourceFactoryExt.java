package dev.xdark.betterloading.internal;

import dev.xdark.betterloading.cache.NativeImageHolder;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.util.Identifier;

import java.io.IOException;

public interface ResourceFactoryExt {

	JsonUnbakedModel tryGetJsonUnbakedModel(Identifier id) throws IOException;

	JsonUnbakedModel getJsonUnbakedModel(Identifier id) throws IOException;

	NativeImageHolder tryGetNativeImage(Identifier id) throws IOException;

	NativeImageHolder getNativeImage(Identifier id) throws IOException;
}
