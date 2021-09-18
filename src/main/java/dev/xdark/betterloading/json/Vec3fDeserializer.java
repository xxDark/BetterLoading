package dev.xdark.betterloading.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.util.math.Vec3f;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public final class Vec3fDeserializer extends TypeAdapter<Vec3f> {

  public static final Vec3fDeserializer INSTANCE = new Vec3fDeserializer();

  private Vec3fDeserializer() {}

  @Override
  public void write(JsonWriter out, Vec3f value) throws IOException {
    throw new UnsupportedEncodingException();
  }

  @Override
  public Vec3f read(JsonReader in) throws IOException {
    in.beginArray();
    Vec3f result = implRead(in);
    in.endArray();
    return result;
  }

  public static Vec3f implRead(JsonReader in) throws IOException {
    return new Vec3f((float) in.nextDouble(), (float) in.nextDouble(), (float) in.nextDouble());
  }
}
