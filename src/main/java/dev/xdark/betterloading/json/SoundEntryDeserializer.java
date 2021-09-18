package dev.xdark.betterloading.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundEntry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class SoundEntryDeserializer extends TypeAdapter<SoundEntry> {

  public static final SoundEntryDeserializer INSTANCE = new SoundEntryDeserializer();

  private SoundEntryDeserializer() {}

  @Override
  public void write(JsonWriter out, SoundEntry value) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public SoundEntry read(JsonReader in) throws IOException {
    in.beginObject();
    SoundEntry entry = implRead(in);
    in.endObject();
    return entry;
  }

  public static SoundEntry implRead(JsonReader in) throws IOException {
    boolean replace = false;
    String subtitle = null;
    List<Sound> sounds = new ArrayList<>();
    while (in.hasNext()) {
      switch(in.nextName()) {
        case "replace" -> replace = in.nextBoolean();
        case "subtitle" -> subtitle = in.nextString();
        case "sounds" -> {
          in.beginArray();
          while(in.hasNext()) {
            sounds.add(SoundDeserializer.INSTANCE.read(in));
          }
          in.endArray();
        }
        default -> in.skipValue();
      }
    }
    return new SoundEntry(sounds, replace, subtitle);
  }
}
