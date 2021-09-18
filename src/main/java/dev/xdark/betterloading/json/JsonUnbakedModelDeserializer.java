package dev.xdark.betterloading.json;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class JsonUnbakedModelDeserializer extends TypeAdapter<JsonUnbakedModel> {

  public static final JsonUnbakedModelDeserializer INSTANCE = new JsonUnbakedModelDeserializer();

  private JsonUnbakedModelDeserializer() {}

  @Override
  public void write(JsonWriter out, JsonUnbakedModel value) throws IOException {
    throw new UnsupportedEncodingException();
  }

  @Override
  public JsonUnbakedModel read(JsonReader in) throws IOException {
    in.beginObject();
    JsonUnbakedModel result = implRead(in);
    in.endObject();
    return result;
  }

  public static JsonUnbakedModel implRead(JsonReader in) throws IOException {
    List<ModelElement> elements = new ArrayList<>();
    String parent = null;
    boolean ambientocclusion = true;
    Map<String, Either<SpriteIdentifier, String>> textures = new HashMap<>();
    List<ModelOverride> overrides = new ArrayList<>();
    ModelTransformation display = ModelTransformation.NONE;
    JsonUnbakedModel.GuiLight guiLight = null;
    while (in.hasNext()) {
      switch(in.nextName()) {
        case "elements" -> {
          in.beginArray();
          while(in.hasNext()) {
            elements.add(ModelElementDeserializer.INSTANCE.read(in));
          }
          in.endArray();
        }
        case "parent" -> {
          parent = in.nextString();
          if (parent.isEmpty()) {
            parent = null;
          }
        }
        case "ambientocclusion" -> ambientocclusion = in.nextBoolean();
        case "textures" -> {
          in.beginObject();
          while(in.hasNext()) {
            String key = in.nextName();
            String value = in.nextString();
            textures.put(key, resolveReference(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, value));
          }
          in.endObject();
        }
        case "display" -> display = ModelTransformationDeserializer.INSTANCE.read(in);
        case "gui_light" -> guiLight = GuiLightDeserializer.INSTANCE.read(in);
        case "overrides" -> {
          in.beginArray();
          while(in.hasNext()) {
            overrides.add(ModelOverrideDeserializer.INSTANCE.read(in));
          }
          in.endArray();
        }
        default -> in.skipValue();
      }
    }
    return new JsonUnbakedModel(
        parent == null ? null : new Identifier(parent),
        elements,
        textures,
        ambientocclusion,
        guiLight,
        display,
        overrides);
  }

  private static boolean isTextureReference(String reference) {
    return reference.charAt(0) == '#';
  }

  private static Either<SpriteIdentifier, String> resolveReference(Identifier id, String name) {
    if (isTextureReference(name)) {
      return Either.right(name.substring(1));
    } else {
      Identifier identifier = Identifier.tryParse(name);
      if (identifier == null) {
        throw new JsonParseException(name + " is not valid resource location");
      } else {
        return Either.left(new SpriteIdentifier(id, identifier));
      }
    }
  }
}
