package dev.xdark.betterloading.json;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.minecraft.client.sound.Sound;
import org.apache.commons.lang3.Validate;

import java.io.IOException;

public final class SoundDeserializer extends TypeAdapter<Sound> {

  public static final SoundDeserializer INSTANCE = new SoundDeserializer();

  private SoundDeserializer() {}

  @Override
  public void write(JsonWriter out, Sound value) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public Sound read(JsonReader in) throws IOException {
    Sound result;
    JsonToken token = in.peek();
    if (token == JsonToken.BEGIN_OBJECT) {
      in.beginObject();
      result = implRead(in);
      in.endObject();
    } else if (token == JsonToken.STRING) {
      result =
          new Sound(in.nextString(), 1.0F, 1.0F, 1, Sound.RegistrationType.FILE, false, false, 16);
    } else {
      throw new JsonParseException("Unexpected sound token");
    }
    return result;
  }

    public static Sound implRead(JsonReader in) throws IOException {
    String name = null;
    Sound.RegistrationType type = Sound.RegistrationType.FILE;
    float volume = 1.0F;
    float pitch = 1.0F;
    int weight = 1;
    boolean preload = false;
    boolean stream = false;
    int attenuation_distance = 16;
    while (in.hasNext()) {
      switch (in.nextName()) {
        case "name":
          name = in.nextString();
          break;
        case "type":
          if ((type = RegistrationTypeDeserializer.INSTANCE.read(in)) == null) {
            throw new JsonParseException("Unknown sound registration type");
          }
          break;
        case "volume":
          Validate.isTrue((volume = (float) in.nextDouble()) > 0.0F, "Invalid volume");
          break;
        case "pitch":
          Validate.isTrue((pitch = (float) in.nextDouble()) > 0.0F, "Invalid pitch");
          break;
        case "weight":
          Validate.isTrue((weight = in.nextInt()) > 0, "Invalid weight");
          break;
        case "preload":
          preload = in.nextBoolean();
          break;
        case "stream":
          stream = in.nextBoolean();
          break;
        case "attenuation_distance":
          attenuation_distance = in.nextInt();
          break;
        default:
          in.skipValue();
      }
    }
    if (name == null) {
      throw new JsonParseException("Missing name on Sound");
    }
    return new Sound(name, volume, pitch, weight, type, stream, preload, attenuation_distance);
  }
}
