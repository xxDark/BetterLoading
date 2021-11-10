package dev.xdark.betterloading.resource;

import dev.xdark.betterloading.cache.CachedResourceImpl;
import dev.xdark.betterloading.internal.FabricInjector;
import dev.xdark.betterloading.internal.ResourcePackExt;
import net.fabricmc.fabric.impl.resource.loader.GroupResourcePack;
import net.fabricmc.fabric.mixin.resource.loader.NamespaceResourceManagerAccessor;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.NamespaceResourceManager;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public final class EnhancedNamespaceResourceManager extends NamespaceResourceManager {

  private static final boolean RESOURCE_LOADER_PRESENT = FabricInjector.RESOURCE_LOADER_PRESENT;

  public EnhancedNamespaceResourceManager(ResourceType type, String namespace) {
    super(type, namespace);
  }

  @Override
  public Resource getResource(Identifier id) throws IOException {
    this.validate(id);
    InputStream metadataStream = null;
    Identifier metadataPath = getMetadataPath(id);
    List<ResourcePack> packList = this.packList;
    ResourceType type = this.type;
    for (int i = packList.size() - 1; i >= 0; --i) {
      ResourcePack resourcePack = packList.get(i);
      ResourcePackExt helper = (ResourcePackExt) resourcePack;
      if (metadataStream == null) {
        metadataStream = helper.tryOpen(type, metadataPath);
      }
      InputStream in;
      if ((in = helper.tryOpen(type, id)) != null) {
        return new CachedResourceImpl(resourcePack, type, id, in, metadataStream);
      }
    }
    IOUtils.closeQuietly(metadataStream);
    throw new FileNotFoundException(id.toString());
  }

  @Override
  public List<Resource> getAllResources(Identifier id) throws IOException {
    this.validate(id);
    List<Resource> resources = new ArrayList<>();
    Identifier metadataPath = getMetadataPath(id);
    List<ResourcePack> packList = this.packList;
    ResourceType type = this.type;
    for (int i = 0, j = packList.size(); i < j; i++) {
      ResourcePack resourcePack = packList.get(i);
      if (RESOURCE_LOADER_PRESENT && resourcePack instanceof GroupResourcePack grp) {
        grp.appendResources((NamespaceResourceManagerAccessor) (Object) this, id, resources);
        continue;
      }
      ResourcePackExt helper = (ResourcePackExt) resourcePack;
      InputStream in;
      if ((in = helper.tryOpen(type, id)) != null) {
        InputStream metaStream = helper.tryOpen(type, metadataPath);
        resources.add(new CachedResourceImpl(resourcePack, type, id, in, metaStream));
      }
    }
    if (resources.isEmpty()) {
      throw new FileNotFoundException(id.toString());
    } else {
      return resources;
    }
  }
}
