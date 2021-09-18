package dev.xdark.betterloading.internal;

import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStream;

public interface IResourcePackMixin {

  InputStream tryOpen(ResourceType type, Identifier id) throws IOException;
}
