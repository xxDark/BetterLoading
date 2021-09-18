package dev.xdark.betterloading.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public final class IdentifierDeserializer extends TypeAdapter<Identifier> {

  public static final IdentifierDeserializer INSTANCE = new IdentifierDeserializer();

  private IdentifierDeserializer() {}

  @Override
  public void write(JsonWriter out, Identifier value) throws IOException {
    throw new UnsupportedEncodingException();
  }

  @Override
  public Identifier read(JsonReader in) throws IOException {
    return new Identifier(in.nextString());
  }
}
