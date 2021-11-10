package dev.xdark.betterloading.mixin;

import dev.xdark.betterloading.internal.DefaultResourcePackExt;
import dev.xdark.betterloading.internal.ResourcePackExt;
import net.minecraft.client.resource.DefaultClientResourcePack;
import net.minecraft.client.resource.ResourceIndex;
import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

@Mixin(DefaultClientResourcePack.class)
public abstract class DefaultClientResourcePackMixin extends DefaultResourcePack
    implements ResourcePackExt {

  @Shadow @Final private ResourceIndex index;

  public DefaultClientResourcePackMixin(PackResourceMetadata metadata, String... namespaces) {
    super(metadata, namespaces);
  }

  @Override
  public InputStream tryOpen(ResourceType type, Identifier id) throws IOException {
    if (type == ResourceType.CLIENT_RESOURCES) {
      File file = index.getResource(id);
      if (file != null && file.isFile()) {
        try {
          return new FileInputStream(file);
        } catch (FileNotFoundException ignored) {
        }
      }
    }
    return ((DefaultResourcePackExt) this).superTryOpen(type, id);
  }
}
