package dev.xdark.betterloading.json;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.client.render.model.ModelRotation;
import net.minecraft.client.render.model.json.ModelVariant;
import net.minecraft.util.Identifier;

import java.io.IOException;

public final class ModelVariantDeserializer extends TypeAdapter<ModelVariant> {

  public static final ModelVariantDeserializer INSTANCE = new ModelVariantDeserializer();

  private ModelVariantDeserializer() {}

  @Override
  public void write(JsonWriter out, ModelVariant value) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public ModelVariant read(JsonReader in) throws IOException {
    in.beginObject();
    ModelVariant result = implRead(in);
    in.endObject();
    return result;
  }

  public static ModelVariant implRead(JsonReader in) throws IOException {
    Identifier identifier = null;
    int rotX = 0, rotY = 0;
    boolean uvlock = false;
    int weight = 1;
    while (in.hasNext()) {
      switch (in.nextName()) {
        case "model":
          identifier = IdentifierDeserializer.INSTANCE.read(in);
          break;
        case "x":
          rotX = in.nextInt();
          break;
        case "y":
          rotY = in.nextInt();
          break;
        case "uvlock":
          uvlock = in.nextBoolean();
          break;
        case "weight":
          if ((weight = in.nextInt()) < 1) {
            throw new JsonParseException(
                "Invalid weight " + weight + " found, expected integer >= 1");
          }
          break;
        default:
          in.skipValue();
      }
    }
    if (identifier == null) {
      throw new JsonParseException("Missing model on ModelVariant");
    }
    ModelRotation rotation = ModelRotation.get(rotX, rotY);
    if (rotation == null) {
      throw new JsonParseException("Invalid BlockModelRotation x: " + rotX + ", y: " + rotY);
    }
    return new ModelVariant(identifier, rotation.getRotation(), uvlock, weight);
  }
}
