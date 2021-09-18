package dev.xdark.betterloading.json;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class ModelOverrideDeserializer extends TypeAdapter<ModelOverride> {

  public static final ModelOverrideDeserializer INSTANCE = new ModelOverrideDeserializer();

  private ModelOverrideDeserializer() {}

  @Override
  public void write(JsonWriter out, ModelOverride value) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public ModelOverride read(JsonReader in) throws IOException {
    in.beginObject();
    ModelOverride result = implRead(in);
    in.endObject();
    return result;
  }

  public static ModelOverride implRead(JsonReader in) throws IOException {
    Identifier model = null;
    List<ModelOverride.Condition> conditions = null;
    while (in.hasNext()) {
      switch(in.nextName()) {
        case "model" -> model = IdentifierDeserializer.INSTANCE.read(in);
        case "predicate" -> {
          conditions = new ArrayList<>();
          in.beginObject();
          while(in.hasNext()) {
            String key = in.nextName();
            float value = (float) in.nextDouble();
            conditions.add(new ModelOverride.Condition(new Identifier(key), value));
          }
          in.endObject();
        }
        default -> in.skipValue();
      }
    }
    if (model == null) {
      throw new JsonParseException("Missing origin on ModelOverride");
    }
    if (conditions == null) {
      throw new JsonParseException("Missing predicate on ModelOverride");
    }
    return new ModelOverride(model, conditions);
  }
}
