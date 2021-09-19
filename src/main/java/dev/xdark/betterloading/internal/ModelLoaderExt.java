package dev.xdark.betterloading.internal;

import net.minecraft.util.Identifier;

import java.util.Collection;

public interface ModelLoaderExt {

  void putModel(Identifier model);

  void putModels(Collection<Identifier> models);
}
