package dev.xdark.betterloading.mixin.optional;

import dev.xdark.betterloading.internal.ResourcePackExt;
import net.fabricmc.fabric.api.resource.ModResourcePack;
import net.fabricmc.fabric.impl.resource.loader.GroupResourcePack;
import net.fabricmc.fabric.impl.resource.loader.client.pack.ProgrammerArtResourcePack;
import net.minecraft.resource.AbstractFileResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Mixin(ProgrammerArtResourcePack.class)
public abstract class ProgrammerArtResourcePackMixin extends GroupResourcePack {

  @Shadow @Final private AbstractFileResourcePack originalResourcePack;

  public ProgrammerArtResourcePackMixin(ResourceType type, List<ModResourcePack> packs) {
    super(type, packs);
  }

  /**
   * @author xDark
   * @reason use tryOpen
   */
  @Overwrite
  public InputStream open(ResourceType type, Identifier id) throws IOException {
    InputStream in;
    if ((in = ((ResourcePackExt) originalResourcePack).tryOpen(type, id)) != null) return in;
    return super.open(type, id);
  }
}
