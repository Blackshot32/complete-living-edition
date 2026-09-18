/*
 * GNU GENERAL PUBLIC LICENSE Version 3
 */
package drzhark.mocreatures.entity.ai;

import drzhark.mocreatures.entity.IMoCEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * Coordinated herd panic behavior inspired by natural herd dynamics.
 * When one herd member is hurt or panicked, it alerts nearby members of the same species,
 * causing the herd to flee together away from the threat.
 */
public class EntityAIHerdPanic extends PanicGoal {

    private final PathfinderMob creature;
    private final double herdAlertRadius;

    public EntityAIHerdPanic(PathfinderMob creature, double speedModifier, double alertRadius) {
        super(creature, speedModifier);
        this.creature = creature;
        this.herdAlertRadius = alertRadius;
    }

    public EntityAIHerdPanic(PathfinderMob creature, double speedModifier) {
        this(creature, speedModifier, 16.0D);
    }

    @Override
    public boolean canUse() {
        if (this.creature.isVehicle() || this.creature.isPassenger()) {
            return false;
        }

        if (this.creature instanceof IMoCEntity moc && moc.isNotScared()) {
            return false;
        }

        // If this creature itself was harmed or in danger, alert nearby herd mates
        if (this.shouldPanic()) {
            this.alertHerd();
            return super.canUse();
        }

        // Otherwise check if any nearby herd mate is threatened or fleeing
        LivingEntity threat = this.findNearbyThreat();
        if (threat != null) {
            Vec3 fleePos = DefaultRandomPos.getPosAway(this.creature, 16, 7, threat.position());
            if (fleePos == null) {
                fleePos = DefaultRandomPos.getPos(this.creature, 10, 4);
            }
            if (fleePos != null) {
                this.posX = fleePos.x;
                this.posY = fleePos.y;
                this.posZ = fleePos.z;
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.creature.isVehicle() || this.creature.isPassenger()) {
            return false;
        }
        if (this.creature instanceof IMoCEntity moc && moc.isMovementCeased()) {
            return false;
        }
        return super.canContinueToUse();
    }

    /**
     * Alert all living members of the same species within the herd radius.
     */
    private void alertHerd() {
        LivingEntity attacker = this.creature.getLastHurtByMob();
        if (attacker == null) {
            return;
        }
        AABB searchBox = this.creature.getBoundingBox().inflate(this.herdAlertRadius, 6.0D, this.herdAlertRadius);
        List<? extends PathfinderMob> herd = this.creature.level().getEntitiesOfClass(this.creature.getClass(), searchBox);
        for (PathfinderMob mate : herd) {
            if (mate != this.creature && mate.isAlive() && mate.getLastHurtByMob() == null) {
                mate.setLastHurtByMob(attacker);
            }
        }
    }

    /**
     * Find if any herd member was attacked recently.
     */
    private LivingEntity findNearbyThreat() {
        AABB searchBox = this.creature.getBoundingBox().inflate(this.herdAlertRadius, 6.0D, this.herdAlertRadius);
        List<? extends PathfinderMob> herd = this.creature.level().getEntitiesOfClass(this.creature.getClass(), searchBox);
        double maxDistSq = this.herdAlertRadius * this.herdAlertRadius * 1.5D;

        for (PathfinderMob mate : herd) {
            if (mate != this.creature && mate.isAlive()) {
                LivingEntity attacker = mate.getLastHurtByMob();
                if (attacker != null && attacker.isAlive() && this.creature.distanceToSqr(attacker) < maxDistSq) {
                    return attacker;
                }
            }
        }
        return null;
    }
}
