/*
 * Adapted from Naturalist (Starfish Studios) under MIT License & GNU GPLv3
 */
package drzhark.mocreatures.entity.ai;

import drzhark.mocreatures.entity.tameable.MoCEntityTameableAnimal;
import drzhark.mocreatures.util.MoCTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;

import java.util.function.Predicate;

/**
 * Intelligent hunting AI goal inspired by Naturalist's ecological food chain system.
 * Respects hunting cooldowns, targets designated prey, protects tamed pets,
 * and seamlessly hunts Vanilla, Naturalist, and common modded animals.
 */
public class EntityAIHunt<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {

    private final PathfinderMob hunter;

    public EntityAIHunt(PathfinderMob hunter, Class<T> targetClass, int interval, boolean checkSight, boolean onlyNearby, Predicate<LivingEntity> customPredicate) {
        super(hunter, targetClass, interval, checkSight, onlyNearby, createPredicate(hunter, customPredicate));
        this.hunter = hunter;
    }

    public EntityAIHunt(PathfinderMob hunter, Class<T> targetClass, boolean checkSight) {
        this(hunter, targetClass, 20, checkSight, false, null);
    }

    public EntityAIHunt(PathfinderMob hunter, Class<T> targetClass, boolean checkSight, boolean onlyNearby) {
        this(hunter, targetClass, 20, checkSight, onlyNearby, null);
    }

    private static Predicate<LivingEntity> createPredicate(PathfinderMob hunter, Predicate<LivingEntity> custom) {
        return target -> {
            if (target == null || !target.isAlive() || target == hunter) {
                return false;
            }

            // Do not target the same entity type
            if (target.getType() == hunter.getType()) {
                return false;
            }

            // Do not target tamed pets
            if (target instanceof TamableAnimal tamed && tamed.isTame()) {
                return false;
            }
            if (target instanceof MoCEntityTameableAnimal mocTamed && mocTamed.getIsTamed()) {
                return false;
            }

            // If custom predicate is provided, test it
            if (custom != null && !custom.test(target)) {
                return false;
            }

            // Check if target is in the predator prey tag or is a peaceful animal
            if (target.getType().is(MoCTags.EntityTypes.PREDATOR_PREY)) {
                return true;
            }

            if (target.getType().is(MoCTags.EntityTypes.COMMON_ANIMALS)) {
                return true;
            }

            // Default to general peaceful animals
            return target instanceof Animal;
        };
    }

    @Override
    public boolean canUse() {
        // Tamed hunters don't hunt independently
        if (this.hunter instanceof MoCEntityTameableAnimal tamed && tamed.getIsTamed()) {
            return false;
        }

        // Naturalist-style hunting cooldown: only hunt when hungry/ready
        if (this.hunter instanceof HuntingAnimal huntingAnimal && !huntingAnimal.canHunt()) {
            return false;
        }

        return super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        if (this.hunter instanceof HuntingAnimal huntingAnimal && !huntingAnimal.canHunt()) {
            return false;
        }
        return super.canContinueToUse();
    }

    @Override
    public void stop() {
        super.stop();
        // If the target is dead or no longer valid, apply cooldown
        if (this.target == null || !this.target.isAlive()) {
            if (this.hunter instanceof HuntingAnimal huntingAnimal) {
                huntingAnimal.startHuntingCooldown();
            }
        }
    }
}
