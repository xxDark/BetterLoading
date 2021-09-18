package dev.xdark.betterloading.json;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.render.model.json.ModelRotation;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.EnumMap;
import java.util.Map;

public final class ModelElementDeserializer extends TypeAdapter<ModelElement> {

  public static final ModelElementDeserializer INSTANCE = new ModelElementDeserializer();

  private ModelElementDeserializer() {}

  @Override
  public void write(JsonWriter out, ModelElement value) throws IOException {
    throw new UnsupportedEncodingException();
  }

  @Override
  public ModelElement read(JsonReader in) throws IOException {
    in.beginObject();
    ModelElement result = implRead(in);
    in.endObject();
    return result;
  }

  public static ModelElement implRead(JsonReader in) throws IOException {
    Vec3f from = null, to = null;
    ModelRotation rotation = null;
    Map<Direction, ModelElementFace> faceMap = null;
    boolean shade = true;
    while (in.hasNext()) {
      switch(in.nextName()) {
        case "from" -> from = verify("from", Vec3fDeserializer.INSTANCE.read(in));
        case "to" -> to = verify("to", Vec3fDeserializer.INSTANCE.read(in));
        case "rotation" -> rotation = ModelRotationDeserializer.INSTANCE.read(in);
        case "faces" -> {
          faceMap = new EnumMap<>(Direction.class);
          ModelElementFaceDeserializer deserializer = ModelElementFaceDeserializer.INSTANCE;
          in.beginObject();
          while(in.hasNext()) {
            String key = in.nextName();
            Direction direction = Direction.byName(key);
            if (direction == null) {
              throw new JsonParseException("Unknown facing: " + key);
            }
            ModelElementFace element = deserializer.read(in);
            faceMap.put(direction, element);
          }
          in.endObject();
          if (faceMap.isEmpty()) {
            throw new JsonParseException("Expected between 1 and 6 unique faces, got 0");
          }
        }
        case "shade" -> {
          if (in.peek() != JsonToken.BOOLEAN) {
            throw new JsonParseException("Expected shade to be a Boolean");
          }
          shade = in.nextBoolean();
        }
        default -> in.skipValue();
      }
    }
    if (from == null) {
      throw new JsonParseException("Missing from on ModelElement");
    }
    if (to == null) {
      throw new JsonParseException("Missing to on ModelElement");
    }
    return new ModelElement(from, to, faceMap, rotation, shade);
  }

  private static Vec3f verify(String name, Vec3f vec3f) {
    float x, y, z;
    if (!((x = vec3f.getX()) < -16.0F)
        && !((y = vec3f.getY()) < -16.0F)
        && !((z = vec3f.getZ()) < -16.0F)
        && !(x > 32.0F)
        && !(y > 32.0F)
        && !(z > 32.0F)) {
      return vec3f;
    }
    throw new JsonParseException(
        '\'' + name + "' specifier exceeds the allowed boundaries: " + vec3f);
  }
}
