package net.weever.rotp_harvest.entity.ai.goal;

import java.util.EnumSet;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.MathHelper;
import net.weever.rotp_harvest.entity.stand.harvest.HarvestMainEntity;
import net.weever.rotp_harvest.util.HarvestUtil;

public class HarvestReworkMeleeCombat extends Goal {
    private final HarvestMainEntity harvest;
    private final double speedModifier;
    private int attackInterval;
    private int attackCooldown;

    public HarvestReworkMeleeCombat(HarvestMainEntity harvest, double speedModifier, int attackInterval) {
        this.harvest = harvest;
        this.speedModifier = speedModifier;
        this.attackInterval = attackInterval;
        this.attackCooldown = 0;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.harvest.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public void start() {
        super.start();
        this.harvest.getNavigation().moveTo(this.harvest.getTarget(), this.speedModifier);
    }

    @Override
    public void tick() {
        LivingEntity target = this.harvest.getTarget();
        if (target == null || !target.isAlive()) {
            resetAttackCooldown();
            return;
        }

        double distanceToTarget = this.harvest.distanceToSqr(target.getX(), target.getY(), target.getZ());

        if (distanceToTarget < MathHelper.square(2.0f)) {
            attack(target);
        } else {
            this.harvest.getNavigation().moveTo(target, this.speedModifier);
        }

        if (this.attackCooldown > 0) {
            this.attackCooldown--;
        }

        this.harvest.getLookControl().setLookAt(target, 30.0F, 30.0F);
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.harvest.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public void stop() {
        super.stop();
        this.harvest.getNavigation().stop();
        this.harvest.setTarget(null);
    }
    
    protected void attack(LivingEntity targetEntity) {
    	if (this.attackCooldown <= 0) {
    		if (harvest.actions.canBite()) {
                HarvestUtil.biteEntity(harvest, targetEntity);
                harvest.actions.resetBiteCooldown();
    		} else if (harvest.actions.canStab()) {
                HarvestUtil.stabEntity(harvest, targetEntity);
                harvest.actions.resetStabCooldown();
            } else if (harvest.actions.canPunch()) {
                HarvestUtil.punchEntity(harvest, targetEntity);
                harvest.actions.resetPunchCooldown();
            }
            resetAttackCooldown();
        }
    }
    
    protected void resetAttackCooldown() {
        this.attackCooldown = this.attackInterval;
    }
}
