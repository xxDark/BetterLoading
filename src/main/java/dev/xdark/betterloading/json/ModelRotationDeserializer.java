package dev.xdark.betterloading.json;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.client.render.model.json.ModelRotation;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public final class ModelRotationDeserializer extends TypeAdapter<ModelRotation> {

  public static final ModelRotationDeserializer INSTANCE = new ModelRotationDeserializer();

  private ModelRotationDeserializer() {}

  @Override
  public void write(JsonWriter out, ModelRotation value) throws IOException {
    throw new UnsupportedEncodingException();
  }

  @Override
  public ModelRotation read(JsonReader in) throws IOException {
    in.beginObject();
    ModelRotation result = implRead(in);
    in.endObject();
    return result;
  }

  public static ModelRotation implRead(JsonReader in) throws IOException {
    Vec3f origin = null;
    Direction.Axis axis = null;
    float angle = Float.NaN;
    boolean rescale = false;
    while (in.hasNext()) {
      switch (in.nextName()) {
        case "origin":
          origin = Vec3fDeserializer.INSTANCE.read(in);
          origin.scale(0.0625F);
          break;
        case "axis":
          if ((axis = AxisDeserializer.INSTANCE.read(in)) == null) {
            throw new JsonParseException("Invalid rotation axis");
          }
          break;
        case "angle":
          float abs;
          if ((angle = (float) in.nextDouble()) != 0.0F
              && (abs = MathHelper.abs(angle)) != 22.5F
              && abs != 45.0F) {
            throw new JsonParseException(
                "Invalid rotation " + angle + " found, only -45/-22.5/0/22.5/45 allowed");
          }
          break;
        case "rescale":
          rescale = in.nextBoolean();
          break;
        default:
          in.skipValue();
      }
    }
    if (origin == null) {
      throw new JsonParseException("Missing origin on ModelRotation");
    }
    if (Float.isNaN(angle)) {
      throw new JsonParseException("Missing angle on ModelRotation");
    }
    return new ModelRotation(origin, axis, angle, rescale);
  }
}
