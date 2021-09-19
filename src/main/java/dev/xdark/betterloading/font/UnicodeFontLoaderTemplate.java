package dev.xdark.betterloading.font;

import net.minecraft.util.Identifier;

public interface UnicodeFontLoaderTemplate {

	Identifier create(int codePoint);
}
