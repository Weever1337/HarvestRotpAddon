package net.weever.rotp_harvest.entity.ai.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.weever.rotp_harvest.capability.LivingUtilCap;
import net.weever.rotp_harvest.entity.stand.harvest.HarvestMainEntity;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class HarvestNearestAttackableTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
    private final HarvestMainEntity harvest;
    private final boolean requiredAttackingStatus;

    public HarvestNearestAttackableTargetGoal(HarvestMainEntity harvest, Class<T> targetClass, int targetChance, boolean mustSee, boolean mustReach, @Nullable Predicate<LivingEntity> targetPredicate, boolean requiredAttackingStatus) {
        super(harvest, targetClass, targetChance, mustSee, mustReach, targetPredicate);
        this.harvest = harvest;
        this.requiredAttackingStatus = requiredAttackingStatus;
    }

    @Override
    public boolean canUse() {
        if (requiredAttackingStatus) {
            return super.canUse() && harvest.getStatus() == LivingUtilCap.HarvestStatus.ATTACKING && harvest.getSecondOwner() == null;
        }
        return super.canUse() && harvest.getSecondOwner() == null;
    }

    @Override
    public void tick() {
        super.tick();
        if (target != null) {
            harvest.setStatus(LivingUtilCap.HarvestStatus.ATTACKING);
        }
    }
}

