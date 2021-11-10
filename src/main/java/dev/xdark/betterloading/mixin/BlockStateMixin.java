package dev.xdark.betterloading.mixin;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import dev.xdark.betterloading.internal.BlockStateExt;
import io.netty.util.internal.InternalThreadLocalMap;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.state.property.Property;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Iterator;
import java.util.Map;

@Mixin(BlockState.class)
public abstract class BlockStateMixin extends AbstractBlock.AbstractBlockState
    implements BlockStateExt {

  private BakedModel model;
  private String propertyMapAsString;

  private BlockStateMixin(
      Block block,
      ImmutableMap<Property<?>, Comparable<?>> propertyMap,
      MapCodec<BlockState> codec) {
    super(block, propertyMap, codec);
  }

  @Override
  public BakedModel getModel() {
    return model;
  }

  @Override
  public void setModel(BakedModel model) {
    this.model = model;
  }

  @Override
  public String propertyMapToString() {
    String propertyMapAsString = this.propertyMapAsString;
    if (propertyMapAsString == null) {
      StringBuilder builder = InternalThreadLocalMap.get().stringBuilder();
      Iterator<Map.Entry<Property<?>, Comparable<?>>> iterator = getEntries().entrySet().iterator();
      if (iterator.hasNext()) {
        Map.Entry<Property<?>, Comparable<?>> entry = iterator.next();
        appendProperty(builder, entry.getKey(), entry.getValue());
        if (iterator.hasNext()) {
          builder.append(',');
          do {
            appendProperty(builder, (entry = iterator.next()).getKey(), entry.getValue());
          } while (iterator.hasNext());
        }
      }
      return this.propertyMapAsString = builder.toString();
    }

    return propertyMapAsString;
  }

  private static void appendProperty(
      StringBuilder builder, Property<?> property, Comparable<?> value) {
    builder.append(property.getName()).append('=').append(propertyValueToString(property, value));
  }

  private static String propertyValueToString(Property property, Comparable<?> value) {
    return property.name(value);
  }
}
