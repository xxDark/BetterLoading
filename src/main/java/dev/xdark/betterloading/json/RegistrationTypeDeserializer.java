package dev.xdark.betterloading.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.client.sound.Sound;

import java.io.IOException;

public final class RegistrationTypeDeserializer extends TypeAdapter<Sound.RegistrationType> {

  public static final RegistrationTypeDeserializer INSTANCE = new RegistrationTypeDeserializer();

  private RegistrationTypeDeserializer() {}

  @Override
  public void write(JsonWriter out, Sound.RegistrationType value) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public Sound.RegistrationType read(JsonReader in) throws IOException {
    return Sound.RegistrationType.getByName(in.nextString());
  }
}
