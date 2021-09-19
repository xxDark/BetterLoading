package dev.xdark.betterloading.mixin;

import com.google.gson.stream.JsonReader;
import dev.xdark.betterloading.IOUtil;
import dev.xdark.betterloading.json.ParticleTextureDataDeserializer;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleTextureData;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mixin(ParticleManager.class)
public abstract class ParticleManagerMixin {

  @Shadow @Final private Map spriteAwareFactories;

  /**
   * @author xDark
   * @reason optimize I/O usage
   */
  @Overwrite
  private void loadTextureList(
      ResourceManager resourceManager, Identifier id, Map<Identifier, List<Identifier>> result) {
    Identifier identifier =
        new Identifier(id.getNamespace(), "particles/" + id.getPath() + ".json");

    try (Resource resource = resourceManager.getResource(identifier)) {
      ParticleTextureData textureData;
      try (JsonReader reader =
          IOUtil.toJsonReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
        textureData = ParticleTextureDataDeserializer.INSTANCE.read(reader);
      }
      List<Identifier> textureList = textureData.getTextureList();
      boolean isSpriteAware = this.spriteAwareFactories.containsKey(id);
      if (textureList == null) {
        if (isSpriteAware) {
          throw new IllegalStateException("Missing texture list for particle " + id);
        }
      } else {
        if (!isSpriteAware) {
          throw new IllegalStateException("Redundant texture list for particle " + id);
        }

        result.put(
            id,
            textureList.stream()
                .map(
                    (identifierx) ->
                        new Identifier(
                            identifierx.getNamespace(), "particle/" + identifierx.getPath()))
                .collect(Collectors.toList()));
      }
    } catch (IOException ex) {
      throw new IllegalStateException("Failed to load description for particle " + id, ex);
    }
  }
}
