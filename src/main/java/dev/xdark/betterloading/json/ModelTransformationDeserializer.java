package dev.xdark.betterloading.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.Transformation;

import java.io.IOException;

public final class ModelTransformationDeserializer extends TypeAdapter<ModelTransformation> {

  public static final ModelTransformationDeserializer INSTANCE =
      new ModelTransformationDeserializer();

  private ModelTransformationDeserializer() {}

  @Override
  public void write(JsonWriter out, ModelTransformation value) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public ModelTransformation read(JsonReader in) throws IOException {
    in.beginObject();
    ModelTransformation result = implRead(in);
    in.endObject();
    return result;
  }

  public static ModelTransformation implRead(JsonReader in) throws IOException {
    TransformationDeserializer deserializer = TransformationDeserializer.INSTANCE;
    Transformation thirdperson_righthand = Transformation.IDENTITY;
    Transformation thirdperson_lefthand = Transformation.IDENTITY;
    Transformation firstperson_righthand = Transformation.IDENTITY;
    Transformation firstperson_lefthand = Transformation.IDENTITY;
    Transformation head = Transformation.IDENTITY;
    Transformation gui = Transformation.IDENTITY;
    Transformation ground = Transformation.IDENTITY;
    Transformation fixed = Transformation.IDENTITY;
    while (in.hasNext()) {
      switch(in.nextName()) {
        case "thirdperson_righthand" -> thirdperson_righthand = deserializer.read(in);
        case "thirdperson_lefthand" -> thirdperson_lefthand = deserializer.read(in);
        case "firstperson_righthand" -> firstperson_righthand = deserializer.read(in);
        case "firstperson_lefthand" -> firstperson_lefthand = deserializer.read(in);
        case "head" -> head = deserializer.read(in);
        case "gui" -> gui = deserializer.read(in);
        case "ground" -> ground = deserializer.read(in);
        case "fixed" -> fixed = deserializer.read(in);
        default -> in.skipValue();
      }
    }
    if (thirdperson_lefthand == Transformation.IDENTITY) {
      thirdperson_lefthand = thirdperson_righthand;
    }
    if (firstperson_lefthand == Transformation.IDENTITY) {
      firstperson_lefthand = firstperson_righthand;
    }
    return new ModelTransformation(
        thirdperson_lefthand,
        thirdperson_righthand,
        firstperson_lefthand,
        firstperson_righthand,
        head,
        gui,
        ground,
        fixed);
  }
}
