package dev.xdark.betterloading.internal;

import dev.xdark.betterloading.cache.NativeImageHolder;
import net.minecraft.client.render.model.json.JsonUnbakedModel;

import java.io.IOException;

public interface ResourceExt {

  NativeImageHolder readImage() throws IOException;

  JsonUnbakedModel readUnbakedModel() throws IOException;
}
