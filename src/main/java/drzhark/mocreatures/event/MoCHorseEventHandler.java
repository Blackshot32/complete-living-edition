package drzhark.mocreatures.event;

import drzhark.mocreatures.MoCConstants;
import drzhark.mocreatures.entity.passive.MoCEntityHorse;
import drzhark.mocreatures.init.MoCEntities;
import drzhark.mocreatures.init.MoCItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/**
 * Event handler that bridges Vanilla/Naturalist horses with Mo' Creatures mythical horses.
 * Allows players to transmute tamed vanilla horses using magical essences:
 * - Essence of Fire -> Nightmare (immune to lava/fire, leaves ember trails)
 * - Essence of Darkness -> Bat Horse (flying nocturnal mount)
 * - Essence of Undead -> Undead Horse
 * - Essence of Light -> Unicorn
 */
@EventBusSubscriber(modid = MoCConstants.MOD_ID)
public class MoCHorseEventHandler {

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof Horse vanillaHorse)) {
            return;
        }

        Player player = event.getEntity();
        ItemStack held = event.getItemStack();
        Level level = event.getLevel();

        if (held.isEmpty() || !vanillaHorse.isTamed()) {
            return;
        }

        int targetType = -1;

        if (held.is(MoCItems.ESSENCE_FIRE.get())) {
            targetType = 38; // Nightmare
        } else if (held.is(MoCItems.ESSENCE_DARKNESS.get())) {
            targetType = 32; // Bat Horse
        } else if (held.is(MoCItems.ESSENCE_UNDEAD.get())) {
            targetType = 23; // Undead Horse
        } else if (held.is(MoCItems.ESSENCE_LIGHT.get())) {
            targetType = 36; // Unicorn
        }

        if (targetType == -1) {
            return;
        }

        // Consume item unless in creative
        if (!player.getAbilities().instabuild) {
            held.shrink(1);
            if (held.isEmpty()) {
                player.setItemInHand(event.getHand(), new ItemStack(Items.GLASS_BOTTLE));
            } else {
                player.getInventory().add(new ItemStack(Items.GLASS_BOTTLE));
            }
        }

        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            MoCEntityHorse mythicalHorse = MoCEntities.WILDHORSE.get().create(serverLevel);
            if (mythicalHorse != null) {
                mythicalHorse.setPos(vanillaHorse.getX(), vanillaHorse.getY(), vanillaHorse.getZ());
                mythicalHorse.setYRot(vanillaHorse.getYRot());
                mythicalHorse.setXRot(vanillaHorse.getXRot());
                mythicalHorse.setAdult(true);
                mythicalHorse.setTamed(true);
                mythicalHorse.setOwnerId(player.getUUID());
                mythicalHorse.setTypeMoC(targetType);

                // Transfer saddle
                if (vanillaHorse.isSaddled()) {
                    mythicalHorse.setRideable(true);
                }

                // Transfer custom name
                if (vanillaHorse.hasCustomName()) {
                    mythicalHorse.setCustomName(vanillaHorse.getCustomName());
                }

                serverLevel.addFreshEntity(mythicalHorse);

                // Effects based on transformation type
                if (targetType == 38) { // Nightmare
                    serverLevel.sendParticles(ParticleTypes.FLAME, mythicalHorse.getX(), mythicalHorse.getY() + 1.0, mythicalHorse.getZ(), 40, 0.5, 0.5, 0.5, 0.05);
                    serverLevel.sendParticles(ParticleTypes.LAVA, mythicalHorse.getX(), mythicalHorse.getY() + 0.5, mythicalHorse.getZ(), 15, 0.3, 0.3, 0.3, 0.02);
                    serverLevel.playSound(null, mythicalHorse.blockPosition(), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 1.0F, 0.8F);
                } else if (targetType == 32) { // Bat Horse
                    serverLevel.sendParticles(ParticleTypes.SMOKE, mythicalHorse.getX(), mythicalHorse.getY() + 1.0, mythicalHorse.getZ(), 50, 0.5, 0.5, 0.5, 0.05);
                    serverLevel.sendParticles(ParticleTypes.PORTAL, mythicalHorse.getX(), mythicalHorse.getY() + 0.5, mythicalHorse.getZ(), 30, 0.5, 0.5, 0.5, 0.1);
                    serverLevel.playSound(null, mythicalHorse.blockPosition(), SoundEvents.BAT_TAKEOFF, SoundSource.PLAYERS, 1.0F, 0.8F);
                } else if (targetType == 23) { // Undead
                    serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, mythicalHorse.getX(), mythicalHorse.getY() + 1.0, mythicalHorse.getZ(), 30, 0.4, 0.4, 0.4, 0.03);
                    serverLevel.playSound(null, mythicalHorse.blockPosition(), SoundEvents.ZOMBIE_HORSE_DEATH, SoundSource.PLAYERS, 1.0F, 0.9F);
                } else if (targetType == 36) { // Unicorn
                    serverLevel.sendParticles(ParticleTypes.END_ROD, mythicalHorse.getX(), mythicalHorse.getY() + 1.0, mythicalHorse.getZ(), 40, 0.5, 0.5, 0.5, 0.05);
                    serverLevel.playSound(null, mythicalHorse.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.5F);
                }

                vanillaHorse.discard();
            }
        }

        event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide()));
        event.setCanceled(true);
    }
}
