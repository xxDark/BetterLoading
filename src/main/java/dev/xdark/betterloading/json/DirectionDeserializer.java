package dev.xdark.betterloading.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.util.math.Direction;

import java.io.IOException;

public final class DirectionDeserializer extends TypeAdapter<Direction> {

  public static final DirectionDeserializer INSTANCE = new DirectionDeserializer();

  private DirectionDeserializer() {}

  @Override
  public void write(JsonWriter out, Direction value) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public Direction read(JsonReader in) throws IOException {
    return Direction.byName(in.nextString());
  }
}
