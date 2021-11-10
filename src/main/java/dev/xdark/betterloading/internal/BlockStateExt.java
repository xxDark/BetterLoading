package dev.xdark.betterloading.internal;

import net.minecraft.client.render.model.BakedModel;

public interface BlockStateExt {

	BakedModel getModel();

	void setModel(BakedModel model);

	String propertyMapToString();
}
