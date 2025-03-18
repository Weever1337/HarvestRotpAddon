package net.weever.rotp_harvest.entity.ai.goal;

import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.util.DamageSource;
import net.weever.rotp_harvest.entity.stand.harvest.HarvestMainEntity;

import java.util.EnumSet;

public class HarvestOwnerHurtTargetGoal extends TargetGoal {
    private final HarvestMainEntity harvest;
    private LivingEntity attacked;
    private int timestamp;

    public HarvestOwnerHurtTargetGoal(HarvestMainEntity harvest) {
        super(harvest, false);
        this.harvest = harvest;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        LivingEntity owner = harvest.getOriginalOwner();
        if (owner == null) {
            return false;
        } else {
            this.attacked = owner.getLastHurtMob();
            int i = owner.getLastHurtMobTimestamp();
            DamageSource attackedBy = attacked != null ? attacked.getLastDamageSource() : null;
            return i != timestamp && !(attackedBy != null && canAttack(attacked, EntityPredicate.DEFAULT)
                    && harvest.wantsToAttack(attacked, owner));
        }
    }

    @Override
    public void start() {
        mob.setTarget(attacked);
        LivingEntity owner = harvest.getOriginalOwner();
        if (owner != null) {
            timestamp = owner.getLastHurtMobTimestamp();
        }
        super.start();
    }
}