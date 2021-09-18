package dev.xdark.betterloading.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.client.render.model.MultipartUnbakedModel;
import net.minecraft.client.render.model.json.ModelVariantMap;
import net.minecraft.client.render.model.json.WeightedUnbakedModel;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class ModelVariantMapDeserializer extends TypeAdapter<ModelVariantMap> {

  private final MultipartUnbakedModelDeserializer multipartUnbakedModelDeserializer;

  public ModelVariantMapDeserializer(
      MultipartUnbakedModelDeserializer multipartUnbakedModelDeserializer) {
    this.multipartUnbakedModelDeserializer = multipartUnbakedModelDeserializer;
  }

  @Override
  public void write(JsonWriter out, ModelVariantMap value) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public ModelVariantMap read(JsonReader in) throws IOException {
    in.beginObject();
    ModelVariantMap result = implRead(in);
    in.endObject();
    return result;
  }

  public ModelVariantMap implRead(JsonReader in) throws IOException {
    Map<String, WeightedUnbakedModel> variants = new HashMap<>();
    MultipartUnbakedModel multipart = null;
    while (in.hasNext()) {
      switch(in.nextName()) {
        case "variants" -> {
          in.beginObject();
          while(in.hasNext()) {
            variants.put(in.nextName(), WeightedUnbakedModelDeserializer.INSTANCE.read(in));
          }
          in.endObject();
        }
        case "multipart" -> multipart = multipartUnbakedModelDeserializer.read(in);
        default -> in.skipValue();
      }
    }
    return new ModelVariantMap(variants, multipart);
  }
}
