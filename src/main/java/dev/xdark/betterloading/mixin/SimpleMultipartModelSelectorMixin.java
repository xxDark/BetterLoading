package dev.xdark.betterloading.mixin;

import com.google.common.base.Splitter;
import dev.xdark.betterloading.internal.Predicates;
import io.netty.util.internal.RecyclableArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.json.SimpleMultipartModelSelector;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Mixin(SimpleMultipartModelSelector.class)
public abstract class SimpleMultipartModelSelectorMixin {

  @Shadow @Final private String key;
  @Shadow @Final private String valueString;
  @Shadow @Final private static Splitter VALUE_SPLITTER;

  // TODO I probably shouldn't overwrite this.
  // TODO(fixme)
  /**
   * @author xDark
   * @reason use better path for predicates
   */
  @Overwrite
  public Predicate<BlockState> getPredicate(StateManager<Block, BlockState> stateManager) {
    Property<?> property = stateManager.getProperty(key);
    if (property == null) {
      throw new RuntimeException(
          String.format("Unknown property '%s' on '%s'", key, stateManager.getOwner()));
    } else {
      String string = valueString;
      boolean negate = !string.isEmpty() && string.charAt(0) == '!';
      if (negate) {
        string = string.substring(1);
      }

      List<String> values = VALUE_SPLITTER.splitToList(string);
      if (values.isEmpty()) {
        throw new RuntimeException(
            String.format(
                "Empty value '%s' for property '%s' on '%s'",
                valueString, key, stateManager.getOwner()));
      } else {
        Predicate<BlockState> predicate;
        int j = values.size();
        if (j == 1) {
          predicate = createPredicate(stateManager, property, string);
        } else {
          RecyclableArrayList list2 = RecyclableArrayList.newInstance(j);
          for (int i = 0; i < j; i++) {
            list2.add(createPredicate(stateManager, property, values.get(i)));
          }
          //noinspection unchecked
          predicate = Predicates.or((List) list2);
          list2.recycle();
        }
        return negate ? predicate.negate() : predicate;
      }
    }
  }

  /** @author xDark */
  @Overwrite
  private Predicate<BlockState> createPredicate(
      StateManager<Block, BlockState> stateFactory, Property<?> property, String valueString) {
    Optional<?> optional = property.parse(valueString);
    if (optional.isEmpty()) {
      throw new RuntimeException(
          String.format(
              "Unknown value '%s' for property '%s' on '%s' in '%s'",
              valueString, key, stateFactory.getOwner(), valueString));
    } else {
      Object value = optional.get();
      return (state) -> state.get(property).equals(value);
    }
  }
}
