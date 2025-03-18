package net.weever.rotp_harvest.entity.ai.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.weever.rotp_harvest.entity.stand.harvest.HarvestMainEntity;
import net.weever.rotp_harvest.util.HarvestUtil;

@Deprecated
public class HarvestMeleeAttackGoal extends MeleeAttackGoal {
    private final HarvestMainEntity harvest;

    public HarvestMeleeAttackGoal(HarvestMainEntity harvest, double speedModifier, boolean followingTargetEvenIfNotSeen) {
        super(harvest, speedModifier, followingTargetEvenIfNotSeen);
        this.harvest = harvest;
    }

    @Override
    protected void checkAndPerformAttack(LivingEntity target, double p_190102_2_) {
        double d0 = this.getAttackReachSqr(target);
        if (p_190102_2_ <= d0 && this.getTicksUntilNextAttack() <= 0) {
            this.resetAttackCooldown();

            if (harvest.actions.canStab()) {
                HarvestUtil.stabEntity(harvest, target);
                harvest.actions.resetStabCooldown();
            } else if (harvest.actions.canBite()) {
                HarvestUtil.biteEntity(harvest, target);
                harvest.actions.resetBiteCooldown();
            }
        }
    }
    
    @Override
    protected int getAttackInterval() {
    	return 5;
    }
//    @Override
//    public boolean canUse() {
//        return super.canUse() && harvest.getStatus() == LivingUtilCap.HarvestStatus.ATTACKING;
//    }
}
