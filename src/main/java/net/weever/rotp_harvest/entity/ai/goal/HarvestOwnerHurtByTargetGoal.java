package net.weever.rotp_harvest.entity.ai.goal;

import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.weever.rotp_harvest.entity.stand.harvest.HarvestMainEntity;

import java.util.EnumSet;

public class HarvestOwnerHurtByTargetGoal extends TargetGoal {
    private final HarvestMainEntity harvest;
    private LivingEntity attacker;
    private int timestamp;

    public HarvestOwnerHurtByTargetGoal(HarvestMainEntity harvest) {
        super(harvest, false);
        this.harvest = harvest;
        this.setFlags(EnumSet.of(Goal.Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        LivingEntity owner = harvest.getOriginalOwner();
        if (owner == null) {
            return false;
        } else {
            attacker = owner.getLastHurtByMob();
            int i = owner.getLastHurtByMobTimestamp();
            return i != timestamp && canAttack(attacker, EntityPredicate.DEFAULT) && harvest.wantsToAttack(attacker, owner);
        }
    }


    @Override
    public void start() {
        mob.setTarget(attacker);
        LivingEntity owner = harvest.getOriginalOwner();
        if (owner != null) {
            timestamp = owner.getLastHurtByMobTimestamp();
        }
        super.start();
    }
}