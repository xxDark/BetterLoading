package dev.xdark.betterloading.mixin;

import dev.xdark.betterloading.internal.BlockStateExt;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@Mixin(BlockModels.class)
public abstract class BlockModelsMixin {

  @Shadow @Final private BakedModelManager modelManager;

  @Shadow
  public static ModelIdentifier getModelId(BlockState state) {
    return null;
  }

  @Shadow @Final private Map<BlockState, BakedModel> models;

  @Redirect(
      method = "getModel",
      at =
          @At(
              value = "INVOKE",
              target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"))
  private Object getModel(Map instance, Object o) {
    return ((BlockStateExt) o).getModel();
  }

  @Inject(
      method = "reload",
      at = @At(value = "INVOKE", target = "Ljava/util/Map;clear()V", shift = At.Shift.AFTER),
      cancellable = true)
  private void onReload(CallbackInfo ci) {
    ci.cancel();
    // TODO actually put in map if someone injects here
    BakedModelManager modelManager = this.modelManager;
    Map<BlockState, BakedModel> models = this.models;
    for (Block block : Registry.BLOCK) {
      List<BlockState> states = block.getStateManager().getStates();
      for (int i = 0, j = states.size(); i < j; i++) {
        BlockState state = states.get(i);
        BakedModel model = modelManager.getModel(getModelId(state));
        models.put(state, model);
        ((BlockStateExt) state).setModel(model);
      }
    }
  }

  /**
   * @author xDark
   * @reason use optimized version of propertyMapToString
   */
  @Overwrite
  public static ModelIdentifier getModelId(Identifier id, BlockState state) {
    return new ModelIdentifier(id, ((BlockStateExt) state).propertyMapToString());
  }
}
