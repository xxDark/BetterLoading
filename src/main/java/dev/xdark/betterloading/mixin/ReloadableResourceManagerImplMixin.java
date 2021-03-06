package dev.xdark.betterloading.mixin;

import dev.xdark.betterloading.cache.NativeImageHolder;
import dev.xdark.betterloading.internal.ResourceFactoryExt;
import dev.xdark.betterloading.resource.EnhancedNamespaceResourceManager;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.resource.NamespaceResourceManager;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

@Mixin(ReloadableResourceManagerImpl.class)
public abstract class ReloadableResourceManagerImplMixin implements ResourceFactoryExt {

  @Shadow @Final private Map<String, NamespaceResourceManager> namespaceManagers;

  @Redirect(
      method = "addPack",
      at =
          @At(
              value = "NEW",
              target =
                  "(Lnet/minecraft/resource/ResourceType;Ljava/lang/String;)Lnet/minecraft/resource/NamespaceResourceManager;"))
  private NamespaceResourceManager onResourceManagerCreate(ResourceType type, String namespace) {
    return new EnhancedNamespaceResourceManager(type, namespace);
  }

  @Override
  public JsonUnbakedModel tryGetJsonUnbakedModel(Identifier id) throws IOException {
    ResourceManager resourceManager = this.namespaceManagers.get(id.getNamespace());
    if (resourceManager != null) {
      return ((ResourceFactoryExt) resourceManager).tryGetJsonUnbakedModel(id);
    }
    return null;
  }

  @Override
  public JsonUnbakedModel getJsonUnbakedModel(Identifier id) throws IOException {
    ResourceManager resourceManager = this.namespaceManagers.get(id.getNamespace());
    if (resourceManager != null) {
      return ((ResourceFactoryExt) resourceManager).getJsonUnbakedModel(id);
    } else {
      throw new FileNotFoundException(id.toString());
    }
  }

  @Override
  public NativeImageHolder tryGetNativeImage(Identifier id) throws IOException {
    ResourceManager resourceManager = this.namespaceManagers.get(id.getNamespace());
    if (resourceManager != null) {
      return ((ResourceFactoryExt) resourceManager).tryGetNativeImage(id);
    }
    return null;
  }

  @Override
  public NativeImageHolder getNativeImage(Identifier id) throws IOException {
    ResourceManager resourceManager = this.namespaceManagers.get(id.getNamespace());
    if (resourceManager != null) {
      return ((ResourceFactoryExt) resourceManager).getNativeImage(id);
    } else {
      throw new FileNotFoundException(id.toString());
    }
  }
}
