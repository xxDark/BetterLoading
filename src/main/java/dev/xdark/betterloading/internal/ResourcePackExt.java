package dev.xdark.betterloading.internal;

import dev.xdark.betterloading.cache.NativeImageHolder;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStream;

public interface ResourcePackExt {

  InputStream tryOpen(ResourceType type, Identifier id) throws IOException;

  NativeImageHolder tryLoadImage(ResourceType type, Identifier id) throws IOException;

  NativeImageHolder loadImage(ResourceType type, Identifier id) throws IOException;

  JsonUnbakedModel tryLoadUnbakedModel(ResourceType type, Identifier id) throws IOException;

  JsonUnbakedModel loadUnbakedModel(ResourceType type, Identifier id) throws IOException;
}
