package dev.xdark.betterloading.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.util.math.Direction;

import java.io.IOException;

public final class AxisDeserializer extends TypeAdapter<Direction.Axis> {

  public static final AxisDeserializer INSTANCE = new AxisDeserializer();

  private AxisDeserializer() {}

  @Override
  public void write(JsonWriter out, Direction.Axis value) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public Direction.Axis read(JsonReader in) throws IOException {
    return Direction.Axis.fromName(in.nextString());
  }
}
