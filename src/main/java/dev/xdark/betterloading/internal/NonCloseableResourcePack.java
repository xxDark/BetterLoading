package dev.xdark.betterloading.internal;

import dev.xdark.betterloading.cache.NativeImageHolder;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;

public final class NonCloseableResourcePack implements ResourcePack, ResourcePackExt {

  private final ResourcePack delegate;

  public NonCloseableResourcePack(ResourcePack resourcePack) {
    this.delegate = resourcePack;
  }

  @Override
  public InputStream tryOpen(ResourceType type, Identifier id) throws IOException {
    return ((ResourcePackExt) delegate).tryOpen(type, id);
  }

  @Override
  public NativeImageHolder tryLoadImage(ResourceType type, Identifier id) throws IOException {
    return ((ResourcePackExt) delegate).tryLoadImage(type, id);
  }

  @Override
  public NativeImageHolder loadImage(ResourceType type, Identifier id) throws IOException {
    return ((ResourcePackExt) delegate).loadImage(type, id);
  }

  @Override
  public JsonUnbakedModel tryLoadUnbakedModel(ResourceType type, Identifier id) throws IOException {
    return ((ResourcePackExt) delegate).tryLoadUnbakedModel(type, id);
  }

  @Override
  public JsonUnbakedModel loadUnbakedModel(ResourceType type, Identifier id) throws IOException {
    return ((ResourcePackExt) delegate).loadUnbakedModel(type, id);
  }

  @Nullable
  @Override
  public InputStream openRoot(String fileName) throws IOException {
    return delegate.openRoot(fileName);
  }

  @Override
  public InputStream open(ResourceType type, Identifier id) throws IOException {
    return delegate.open(type, id);
  }

  @Override
  public Collection<Identifier> findResources(
      ResourceType type,
      String namespace,
      String prefix,
      int maxDepth,
      Predicate<String> pathFilter) {
    return delegate.findResources(type, namespace, prefix, maxDepth, pathFilter);
  }

  @Override
  public boolean contains(ResourceType type, Identifier id) {
    return delegate.contains(type, id);
  }

  @Override
  public Set<String> getNamespaces(ResourceType type) {
    return delegate.getNamespaces(type);
  }

  @Nullable
  @Override
  public <T> T parseMetadata(ResourceMetadataReader<T> metaReader) throws IOException {
    return delegate.parseMetadata(metaReader);
  }

  @Override
  public String getName() {
    return delegate.getName();
  }

  @Override
  public void close() {}
}
