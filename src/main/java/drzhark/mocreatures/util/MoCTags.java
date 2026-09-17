package drzhark.mocreatures.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class MoCTags {
    public static class Items {
        @SuppressWarnings("removal")
        public static final TagKey<Item> COOKED_FISHES = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "cooked_fishes"));
        @SuppressWarnings("removal")
        public static final TagKey<Item> RAW_FISHES = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "raw_fishes"));
    }

    public static class EntityTypes {
        public static final TagKey<net.minecraft.world.entity.EntityType<?>> PREDATOR_PREY = TagKey.create(
                Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath("mocreatures", "predator_prey"));
        public static final TagKey<net.minecraft.world.entity.EntityType<?>> NATURALIST_PREDATORS = TagKey.create(
                Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath("naturalist", "predators"));
        public static final TagKey<net.minecraft.world.entity.EntityType<?>> COMMON_ANIMALS = TagKey.create(
                Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath("c", "animals"));
    }
}
