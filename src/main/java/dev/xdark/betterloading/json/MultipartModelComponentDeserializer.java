package dev.xdark.betterloading.json;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.minecraft.client.render.model.json.AndMultipartModelSelector;
import net.minecraft.client.render.model.json.MultipartModelComponent;
import net.minecraft.client.render.model.json.MultipartModelSelector;
import net.minecraft.client.render.model.json.OrMultipartModelSelector;
import net.minecraft.client.render.model.json.SimpleMultipartModelSelector;
import net.minecraft.client.render.model.json.WeightedUnbakedModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class MultipartModelComponentDeserializer
    extends TypeAdapter<MultipartModelComponent> {

  public static final MultipartModelComponentDeserializer INSTANCE =
      new MultipartModelComponentDeserializer();

  private MultipartModelComponentDeserializer() {}

  @Override
  public void write(JsonWriter out, MultipartModelComponent value) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public MultipartModelComponent read(JsonReader in) throws IOException {
    in.beginObject();
    MultipartModelComponent result = implRead(in);
    in.endObject();
    return result;
  }

  public static MultipartModelComponent implRead(JsonReader in) throws IOException {
    MultipartModelSelector selector = MultipartModelSelector.TRUE;
    WeightedUnbakedModel apply = null;
    while (in.hasNext()) {
      switch(in.nextName()) {
        case "when" -> selector = readSelector(in);
        case "apply" -> apply = WeightedUnbakedModelDeserializer.INSTANCE.read(in);
        default -> in.skipValue();
      }
    }
    if (apply == null) {
      throw new JsonParseException("Missing apply on MultipartModelComponent");
    }
    return new MultipartModelComponent(selector, apply);
  }

  private static MultipartModelSelector readSelector(JsonReader in) throws IOException {
    in.beginObject();
    if (in.peek() == JsonToken.END_OBJECT) {
      throw new JsonParseException("No elements found in selector");
    }
    MultipartModelSelector result;
    String name = in.nextName();
    if (in.peek() == JsonToken.BEGIN_ARRAY) {
      List<MultipartModelSelector> selectors = new ArrayList<>();
      in.beginArray();
      while (in.hasNext()) {
        selectors.add(readSelector(in));
      }
      in.endArray();
      if ("OR".equals(name)) {
        result = new OrMultipartModelSelector(selectors);
      } else if ("AND".equals(name)) {
        result = new AndMultipartModelSelector(selectors);
      } else {
        throw new JsonParseException("Unexpected token: " + name);
      }
    } else if (in.peek() != JsonToken.STRING) {
      throw new JsonParseException("Expected selector value");
    } else {
      result = new SimpleMultipartModelSelector(name, in.nextString());
      if (in.hasNext()) {
        List<MultipartModelSelector> selectors = new ArrayList<>();
        selectors.add(result);
        selectors.add(new SimpleMultipartModelSelector(in.nextName(), in.nextString()));
        while (in.hasNext()) {
          selectors.add(new SimpleMultipartModelSelector(in.nextName(), in.nextString()));
        }
        result = new AndMultipartModelSelector(selectors);
      }
    }
    in.endObject();
    return result;
  }
}
