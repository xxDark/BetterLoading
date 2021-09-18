package dev.xdark.betterloading.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.client.render.model.json.JsonUnbakedModel;

import java.io.IOException;

public final class GuiLightDeserializer extends TypeAdapter<JsonUnbakedModel.GuiLight> {

  public static final GuiLightDeserializer INSTANCE = new GuiLightDeserializer();

  private GuiLightDeserializer() {}

  @Override
  public void write(JsonWriter out, JsonUnbakedModel.GuiLight value) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public JsonUnbakedModel.GuiLight read(JsonReader in) throws IOException {
    return JsonUnbakedModel.GuiLight.byName(in.nextString());
  }
}
