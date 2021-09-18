package dev.xdark.betterloading.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.client.render.model.MultipartUnbakedModel;
import net.minecraft.client.render.model.json.ModelVariantMap;
import net.minecraft.client.render.model.json.MultipartModelComponent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class MultipartUnbakedModelDeserializer extends TypeAdapter<MultipartUnbakedModel> {

  private final ModelVariantMap.DeserializationContext ctx;

  public MultipartUnbakedModelDeserializer(ModelVariantMap.DeserializationContext ctx) {
    this.ctx = ctx;
  }

  @Override
  public void write(JsonWriter out, MultipartUnbakedModel value) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public MultipartUnbakedModel read(JsonReader in) throws IOException {
    in.beginArray();
    MultipartUnbakedModel result = implRead(in);
    in.endArray();
    return result;
  }

  public MultipartUnbakedModel implRead(JsonReader in) throws IOException {
    List<MultipartModelComponent> components = new ArrayList<>();
    while (in.hasNext()) {
      components.add(MultipartModelComponentDeserializer.INSTANCE.read(in));
    }
    return new MultipartUnbakedModel(ctx.getStateFactory(), components);
  }
}
