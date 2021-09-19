package dev.xdark.betterloading.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.client.particle.ParticleTextureData;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ParticleTextureDataDeserializer extends TypeAdapter<ParticleTextureData> {

  public static final ParticleTextureDataDeserializer INSTANCE =
      new ParticleTextureDataDeserializer();

  private ParticleTextureDataDeserializer() {}

  @Override
  public void write(JsonWriter out, ParticleTextureData value) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public ParticleTextureData read(JsonReader in) throws IOException {
    in.beginObject();
    ParticleTextureData result = implRead(in);
    in.endObject();
    return result;
  }

  public static ParticleTextureData implRead(JsonReader in) throws IOException {
    List<Identifier> identifiers = null;
    while (in.hasNext()) {
      if ("textures".equals(in.nextName())) {
        in.beginArray();
        identifiers = new ArrayList<>();
        while (in.hasNext()) {
          identifiers.add(IdentifierDeserializer.INSTANCE.read(in));
        }
        in.endArray();
        while (in.hasNext()) {
          in.skipValue();
        }
        break;
      }
    }
    return new ParticleTextureData(identifiers);
  }
}
