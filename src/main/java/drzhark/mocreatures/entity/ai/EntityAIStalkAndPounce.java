/*
 * GNU GENERAL PUBLIC LICENSE Version 3
 */
package drzhark.mocreatures.entity.ai;

import drzhark.mocreatures.entity.IMoCEntity;
import drzhark.mocreatures.entity.hunter.MoCEntityBigCat;
import drzhark.mocreatures.init.MoCSoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * Intelligent predatory stalk-and-pounce AI inspired by feline hunting dynamics in Alex's Mobs.
 * 
 * - When target is 7 to 18 blocks away: Stalks carefully at reduced speed (0.75x) to sneak up.
 * - When within striking range (3.5 to 8 blocks): Gathers momentum, launches an explosive pounce leap,
 *   roars and strikes the target on impact.
 * - When within close melee range (< 3.5 blocks): Engages in direct melee strikes.
 * - If target runs far (> 18 blocks): Breaks stealth and sprints at top speed (1.35x) to chase.
 */
public class EntityAIStalkAndPounce extends Goal {

    private final MoCEntityBigCat cat;
    private LivingEntity target;
    private final double stalkSpeed;
    private final double chaseSpeed;
    private int pounceCooldown;
    private int pathRecalcTime;
    private int attackCooldown;
    private boolean isPouncing;

    public EntityAIStalkAndPounce(MoCEntityBigCat cat, double stalkSpeed, double chaseSpeed) {
        this.cat = cat;
        this.stalkSpeed = stalkSpeed;
        this.chaseSpeed = chaseSpeed;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    public EntityAIStalkAndPounce(MoCEntityBigCat cat) {
        this(cat, 0.8D, 1.35D);
    }

    @Override
    public boolean canUse() {
        LivingEntity potentialTarget = this.cat.getTarget();
        if (potentialTarget == null || !potentialTarget.isAlive()) {
            return false;
        }
        if (this.cat instanceof IMoCEntity moc && moc.isMovementCeased()) {
            return false;
        }
        if (this.cat.isPassenger() || this.cat.getIsSitting()) {
            return false;
        }
        this.target = potentialTarget;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.target == null || !this.target.isAlive()) {
            return false;
        }
        if (this.cat instanceof IMoCEntity moc && moc.isMovementCeased()) {
            return false;
        }
        if (this.cat.isPassenger() || this.cat.getIsSitting()) {
            return false;
        }
        return true;
    }

    @Override
    public void start() {
        this.pathRecalcTime = 0;
        this.attackCooldown = 0;
        this.isPouncing = false;
    }

    @Override
    public void stop() {
        this.target = null;
        this.isPouncing = false;
        this.cat.setShiftKeyDown(false);
    }

    @Override
    public void tick() {
        if (this.target == null) {
            return;
        }

        this.cat.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
        double distSq = this.cat.distanceToSqr(this.target);

        if (this.pounceCooldown > 0) {
            this.pounceCooldown--;
        }
        if (this.attackCooldown > 0) {
            this.attackCooldown--;
        }

        // Pouncing airborne check
        if (this.isPouncing) {
            if (this.cat.onGround()) {
                this.isPouncing = false;
            } else {
                // If airborne and close enough, impact attack!
                if (distSq <= 6.0D && this.attackCooldown <= 0) {
                    this.cat.doHurtTarget(this.target);
                    this.cat.swing(InteractionHand.MAIN_HAND);
                    this.attackCooldown = 20;
                }
                return;
            }
        }

        // Tactical Stalking / Pouncing / Sprinting logic:
        if (distSq > 18.0D * 18.0D) {
            // Far away: Sprint chase
            this.cat.setShiftKeyDown(false);
            if (--this.pathRecalcTime <= 0) {
                this.pathRecalcTime = 10;
                this.cat.getNavigation().moveTo(this.target, this.chaseSpeed);
            }
        } else if (distSq >= 3.5D * 3.5D && distSq <= 8.0D * 8.0D) {
            // In pounce zone!
            if (this.pounceCooldown <= 0 && this.cat.onGround() && this.cat.hasLineOfSight(this.target)) {
                // Launch explosive pounce!
                Vec3 dir = new Vec3(this.target.getX() - this.cat.getX(), 0, this.target.getZ() - this.cat.getZ()).normalize();
                this.cat.setDeltaMovement(dir.x * 1.15D, 0.38D, dir.z * 1.15D);
                this.cat.hasImpulse = true;
                this.isPouncing = true;
                this.pounceCooldown = 60; // cooldown between pounces
                this.cat.openMouth();
                this.cat.performAnimation(0); // twitch tail
                this.cat.playSound(MoCSoundEvents.ENTITY_LION_AMBIENT.get(), 1.0F, this.cat.getVoicePitch());
                return;
            }

            // Stalk carefully towards the target
            this.cat.setShiftKeyDown(true);
            if (this.cat.getRandom().nextInt(20) == 0) {
                this.cat.performAnimation(0); // twitch tail in anticipation
            }
            if (--this.pathRecalcTime <= 0) {
                this.pathRecalcTime = 8;
                this.cat.getNavigation().moveTo(this.target, this.stalkSpeed);
            }
        } else if (distSq > 8.0D * 8.0D) {
            // Mid distance: stealth approach
            this.cat.setShiftKeyDown(true);
            if (--this.pathRecalcTime <= 0) {
                this.pathRecalcTime = 12;
                this.cat.getNavigation().moveTo(this.target, this.stalkSpeed);
            }
        } else {
            // Close melee range (< 3.5 blocks)
            this.cat.setShiftKeyDown(false);
            if (--this.pathRecalcTime <= 0) {
                this.pathRecalcTime = 6;
                this.cat.getNavigation().moveTo(this.target, 1.0D);
            }

            // Melee strike
            double attackReachSq = (this.cat.getBbWidth() * 2.0F * this.cat.getBbWidth() * 2.0F) + this.target.getBbWidth();
            if (distSq <= attackReachSq && this.attackCooldown <= 0) {
                this.attackCooldown = 20;
                this.cat.swing(InteractionHand.MAIN_HAND);
                this.cat.doHurtTarget(this.target);
                this.cat.openMouth();
            }
        }
    }
}
