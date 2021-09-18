package dev.xdark.betterloading.json;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.client.render.model.json.ModelElementTexture;

import java.io.IOException;

public final class ModelElementTextureDeserializer extends TypeAdapter<ModelElementTexture> {

  public static final ModelElementTextureDeserializer INSTANCE =
      new ModelElementTextureDeserializer();

  private ModelElementTextureDeserializer() {}

  @Override
  public void write(JsonWriter out, ModelElementTexture value) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public ModelElementTexture read(JsonReader in) throws IOException {
    in.beginObject();
    ModelElementTexture texture = implRead(in);
    in.endObject();
    return texture;
  }

  public static ModelElementTexture implRead(JsonReader in) throws IOException {
    float[] uv = null;
    int rotation = 0;
    while (in.hasNext()) {
      switch (in.nextName()) {
        case "rotation":
          if ((rotation = in.nextInt()) < 0 || rotation % 90 != 0 || rotation / 90 > 3) {
            throw new JsonParseException(
                "Invalid rotation " + rotation + " found, only 0/90/180/270 allowed");
          }
          break;
        case "uv":
          in.beginArray();
          uv =
              new float[] {
                (float) in.nextDouble(),
                (float) in.nextDouble(),
                (float) in.nextDouble(),
                (float) in.nextDouble()
              };
          in.endArray();
          break;
        default:
          in.skipValue();
      }
    }
    return new ModelElementTexture(uv, rotation);
  }
}
