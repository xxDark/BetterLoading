package dev.xdark.betterloading.mixin;

import dev.xdark.betterloading.internal.IResourcePackMixin;
import net.minecraft.resource.NamespaceResourceManager;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceImpl;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Mixin(NamespaceResourceManager.class)
public abstract class NamespaceResourceManagerMixin {

  @Shadow
  protected abstract void validate(Identifier id) throws IOException;

  @Shadow
  static Identifier getMetadataPath(Identifier id) {
    return null;
  }

  @Shadow @Final protected List<ResourcePack> packList;
  @Shadow @Final private ResourceType type;

  /**
   * @author xDark
   * @reason optimize I/O usage
   */
  @Overwrite
  public Resource getResource(Identifier id) throws IOException {
    this.validate(id);
    InputStream metadataStream = null;
    Identifier metadataPath = getMetadataPath(id);
    List<ResourcePack> packList = this.packList;
    ResourceType type = this.type;
    for (int i = packList.size() - 1; i >= 0; --i) {
      ResourcePack resourcePack = packList.get(i);
      IResourcePackMixin helper = (IResourcePackMixin) resourcePack;
      if (metadataStream == null) {
        metadataStream = helper.tryOpen(type, metadataPath);
      }
      InputStream in;
      if ((in = helper.tryOpen(type, id)) != null) {
        return new ResourceImpl(
            resourcePack.getName(), id, instrument(in), instrument(metadataStream));
      }
    }
    IOUtils.closeQuietly(metadataStream);
    throw new FileNotFoundException(id.toString());
  }

  /**
   * @author xDark
   * @reason optimize I/O usage
   */
  @Overwrite
  public List<Resource> getAllResources(Identifier id) throws IOException {
    this.validate(id);
    List<Resource> resources = new ArrayList<>();
    Identifier metadataPath = getMetadataPath(id);
    List<ResourcePack> packList = this.packList;
    ResourceType type = this.type;
    for (int i = 0, j = packList.size(); i < j; i++) {
      ResourcePack resourcePack = packList.get(i);
      IResourcePackMixin helper = (IResourcePackMixin) resourcePack;
      InputStream in;
      if ((in = helper.tryOpen(type, id)) != null) {
        InputStream metaStream = helper.tryOpen(type, metadataPath);
        resources.add(new ResourceImpl(resourcePack.getName(), id, instrument(in), instrument(metaStream)));
      }
    }
    if (resources.isEmpty()) {
      throw new FileNotFoundException(id.toString());
    } else {
      return resources;
    }
  }

  private static InputStream instrument(InputStream in) {
    // TODO mirror vanilla logic
    return in;
  }
}
