package dev.xdark.betterloading.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.minecraft.client.render.model.json.ModelVariant;
import net.minecraft.client.render.model.json.WeightedUnbakedModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class WeightedUnbakedModelDeserializer extends TypeAdapter<WeightedUnbakedModel> {

  public static final WeightedUnbakedModelDeserializer INSTANCE =
      new WeightedUnbakedModelDeserializer();

  private WeightedUnbakedModelDeserializer() {}

  @Override
  public void write(JsonWriter out, WeightedUnbakedModel value) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public WeightedUnbakedModel read(JsonReader in) throws IOException {
    List<ModelVariant> variants = new ArrayList<>();
    if (in.peek() == JsonToken.BEGIN_ARRAY) {
      in.beginArray();
      while(in.hasNext()) {
        variants.add(ModelVariantDeserializer.INSTANCE.read(in));
      }
      in.endArray();
    } else {
      variants.add(ModelVariantDeserializer.INSTANCE.read(in));
    }
    return new WeightedUnbakedModel(variants);
  }
}
