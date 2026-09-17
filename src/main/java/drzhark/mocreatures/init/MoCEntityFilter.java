package drzhark.mocreatures.init;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Filter to eliminate redundant mundane animals that overlap with Vanilla, Naturalist, and Alex's Mobs.
 * Keeps all unique creatures, Big Cat hybrids, Elephants with Howdah, Ents, Ostriches,
 * unique aquatic/plague fauna, and ALL hostile/mythical/RPG monsters.
 */
public class MoCEntityFilter {

    /**
     * Master configuration flag. When false, redundant animal spawns and eggs are disabled.
     */
    public static boolean enableRedundantAnimals = false;

    private static final Set<String> REDUNDANT_ENTITIES = new HashSet<>();
    private static final Set<String> REDUNDANT_ITEMS = new HashSet<>();

    static {
        // Redundant Bears (Naturalist and Alex's Mobs have superior models, fishing, and sleeping)
        Collections.addAll(REDUNDANT_ENTITIES,
                "blackbear", "black_bear",
                "grizzlybear", "grizzly_bear",
                "wildpolarbear", "polar_bear",
                "pandabear", "panda_bear"
        );

        // Redundant Wildlife (Naturalist provides high quality animated variants with herds)
        Collections.addAll(REDUNDANT_ENTITIES,
                "deer",
                "boar",
                "bunny",
                "duck",
                "bird",
                "goat",
                "fox",
                "kitty",
                "mouse",
                "turkey"
        );

        // Redundant Reptiles & Amphibians
        Collections.addAll(REDUNDANT_ENTITIES,
                "crocodile",
                "snake",
                "turtle"
        );

        // Redundant Ambient Insects (Naturalist already includes GeckoLib versions)
        Collections.addAll(REDUNDANT_ENTITIES,
                "butterfly",
                "firefly",
                "snail",
                "dragonfly"
        );

        // Redundant Generic Fish (Vanilla and Naturalist have identical or richer fish)
        Collections.addAll(REDUNDANT_ENTITIES,
                "anchovy",
                "angelfish",
                "angler",
                "clownfish",
                "goldfish",
                "hippotang",
                "manderin",
                "bass",
                "cod",
                "salmon",
                "smallfish", "small_fish",
                "mediumfish", "medium_fish"
        );

        // Orphaned / Redundant Items belonging to pruned animals
        Collections.addAll(REDUNDANT_ITEMS,
                "turkeycooked", "turkeyraw",
                "duckcooked", "duckraw",
                "turtleraw", "turtlecooked", "turtlesoup",
                "venisonraw", "venisoncooked",
                "kittylitter", "woolball",
                "furhelmet", "furchest", "furlegs", "furboots"
        );
        String[] colors = {"white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray",
                "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black"};
        for (String c : colors) {
            REDUNDANT_ITEMS.add("kittybed_" + c);
        }
    }

    public static boolean isItemAllowed(String itemPath) {
        if (enableRedundantAnimals) {
            return true;
        }
        if (itemPath == null) {
            return true;
        }
        String normalized = itemPath.toLowerCase().replace("mocreatures:", "");
        return !REDUNDANT_ITEMS.contains(normalized);
    }

    public static boolean isRedundant(String entityName) {
        if (enableRedundantAnimals) {
            return false;
        }
        if (entityName == null) {
            return false;
        }
        String normalized = entityName.toLowerCase().replace("mocreatures:", "").replace("_spawn_egg", "");
        return REDUNDANT_ENTITIES.contains(normalized);
    }

    public static boolean isEggAllowed(String eggName) {
        return !isRedundant(eggName);
    }

    public static boolean shouldSpawn(String creatureName) {
        return !isRedundant(creatureName);
    }
}
