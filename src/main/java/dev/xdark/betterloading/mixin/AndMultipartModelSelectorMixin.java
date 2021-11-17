package dev.xdark.betterloading.mixin;

import com.google.common.collect.Lists;
import dev.xdark.betterloading.internal.GameHelper;
import dev.xdark.betterloading.internal.Predicates;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.json.AndMultipartModelSelector;
import net.minecraft.client.render.model.json.MultipartModelSelector;
import net.minecraft.state.StateManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.function.Predicate;

@Mixin(AndMultipartModelSelector.class)
public abstract class AndMultipartModelSelectorMixin {

  @Shadow @Final private Iterable<? extends MultipartModelSelector> selectors;

  // TODO I probably shouldn't overwrite this.
  // TODO(fixme)
  /** @author xDark */
  @Overwrite
  public Predicate<BlockState> getPredicate(StateManager<Block, BlockState> stateManager) {
    List<? extends MultipartModelSelector> list = GameHelper.ensureArrayList(selectors);
    try {
      return Predicates.and(Lists.transform(list, input -> input.getPredicate(stateManager)));
    } finally {
      GameHelper.recycle(list);
    }
  }
}
