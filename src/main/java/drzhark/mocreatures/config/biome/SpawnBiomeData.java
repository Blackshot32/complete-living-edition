/*
 * GNU LESSER GENERAL PUBLIC LICENSE
 * Citadel: sbom_xela
 */
package drzhark.mocreatures.config.biome;

import com.google.gson.*;

import drzhark.mocreatures.MoCreatures;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.biome.Biome;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpawnBiomeData {

    private List<List<SpawnBiomeEntry>> biomes = new ArrayList<>();

    public SpawnBiomeData() {
    }

    private SpawnBiomeData(SpawnBiomeEntry[][] biomesRead) {
        biomes = new ArrayList<>();
        for (SpawnBiomeEntry[] innerArray : biomesRead) {
            biomes.add(Arrays.asList(innerArray));
        }
    }

    public SpawnBiomeData addBiomeEntry(BiomeEntryType type, boolean negate, String value, int pool) {
        if (biomes.isEmpty() || biomes.size() < pool + 1) {
            biomes.add(new ArrayList<>());
        }
        biomes.get(pool).add(new SpawnBiomeEntry(type, negate, value));
        return this;
    }

    public boolean matches(@Nullable Holder<Biome> biomeHolder, ResourceLocation registryName) {
        for (List<SpawnBiomeEntry> all : biomes) {
            boolean overall = true;
            for (SpawnBiomeEntry cond : all) {
                if (!cond.matches(biomeHolder, registryName)) {
                    overall = false;
                }
            }
            if (overall) {
                return true;
            }
        }
        return false;
    }

    public static class Deserializer implements JsonDeserializer<SpawnBiomeData>, JsonSerializer<SpawnBiomeData> {

        @Override
        public SpawnBiomeData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonobject = json.getAsJsonObject();
            SpawnBiomeEntry[][] biomesRead = GsonHelper.getAsObject(jsonobject, "biomes", new SpawnBiomeEntry[0][0], context, SpawnBiomeEntry[][].class);
            return new SpawnBiomeData(biomesRead);
        }

        @Override
        public JsonElement serialize(SpawnBiomeData src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonobject = new JsonObject();
            jsonobject.add("biomes", context.serialize(src.biomes));
            return jsonobject;
        }
    }

    private class SpawnBiomeEntry {
        BiomeEntryType type;
        boolean negate;
        String value;

        public SpawnBiomeEntry(BiomeEntryType type, boolean remove, String value) {
            this.type = type;
            this.negate = remove;
            this.value = value;
        }

        public boolean matches(@Nullable Holder<Biome> biomeHolder, ResourceLocation registryName) {
            if(type.isDepreciated()){
                MoCreatures.LOGGER.warn("biome config: BIOME_DICT and BIOME_CATEGORY are no longer valid in 1.19+. Please use BIOME_TAG instead.");
                return false;
            }else{
                if(type == BiomeEntryType.BIOME_TAG){
                    String altValue = null;
                    if (value.startsWith("c:is_")) {
                        altValue = "minecraft:is_" + value.substring(5);
                    } else if (value.startsWith("minecraft:is_")) {
                        altValue = "c:is_" + value.substring(13);
                    }
                    final String alt = altValue;

                    boolean tagMatched = biomeHolder != null && biomeHolder.tags().anyMatch(biomeTagKey -> {
                        if (biomeTagKey.location() == null) return false;
                        String locStr = biomeTagKey.location().toString();
                        return locStr.equals(value) || (alt != null && locStr.equals(alt));
                    });

                    if (!tagMatched && registryName != null) {
                        String path = registryName.getPath().toLowerCase();
                        if (value.contains("plains") && (path.contains("plains") || path.contains("meadow") || path.contains("prairie"))) {
                            tagMatched = true;
                        } else if (value.contains("forest") && (path.contains("forest") || path.contains("woods") || path.contains("grove"))) {
                            tagMatched = true;
                        } else if (value.contains("taiga") && (path.contains("taiga") || path.contains("spruce") || path.contains("boreal"))) {
                            tagMatched = true;
                        } else if (value.contains("savanna") && (path.contains("savanna") || path.contains("steppe"))) {
                            tagMatched = true;
                        } else if (value.contains("jungle") && (path.contains("jungle") || path.contains("rainforest"))) {
                            tagMatched = true;
                        } else if (value.contains("swamp") && (path.contains("swamp") || path.contains("marsh") || path.contains("bog") || path.contains("mangrove"))) {
                            tagMatched = true;
                        } else if ((value.contains("sandy") || value.contains("desert")) && (path.contains("desert") || path.contains("dune") || path.contains("sand"))) {
                            tagMatched = true;
                        } else if (value.contains("snowy") && (path.contains("snow") || path.contains("ice") || path.contains("frozen") || path.contains("glacier"))) {
                            tagMatched = true;
                        } else if (value.contains("mountain") && (path.contains("mountain") || path.contains("peak") || path.contains("cliff") || path.contains("hill"))) {
                            tagMatched = true;
                        } else if (value.contains("ocean") && (path.contains("ocean") || path.contains("sea") || path.contains("abyss"))) {
                            tagMatched = true;
                        } else if (value.contains("river") && (path.contains("river") || path.contains("stream") || path.contains("canal"))) {
                            tagMatched = true;
                        } else if (value.contains("beach") && (path.contains("beach") || path.contains("shore"))) {
                            tagMatched = true;
                        } else if (value.contains("badlands") && (path.contains("badlands") || path.contains("mesa") || path.contains("canyon"))) {
                            tagMatched = true;
                        }
                    }

                    if (tagMatched) {
                        return !negate;
                    }
                    return negate;
                } else {
                    if (registryName.toString().equals(value)) {
                        return !negate;
                    }
                    return negate;
                }
            }
        }
    }
}