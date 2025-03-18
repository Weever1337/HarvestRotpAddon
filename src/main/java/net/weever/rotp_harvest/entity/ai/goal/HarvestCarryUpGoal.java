package net.weever.rotp_harvest.entity.ai.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.weever.rotp_harvest.capability.LivingUtilCap;
import net.weever.rotp_harvest.capability.LivingUtilCapProvider;
import net.weever.rotp_harvest.entity.stand.harvest.HarvestMainEntity;

public class HarvestCarryUpGoal extends Goal {
    private final HarvestMainEntity harvest;

    public HarvestCarryUpGoal(HarvestMainEntity harvest) {
        super();
        this.harvest = harvest;
    }

    @Override
    public boolean canUse() {
        return harvest.getStatus() != LivingUtilCap.HarvestStatus.ATTACKING && harvest.getStatus() != LivingUtilCap.HarvestStatus.FINDING_ITEM && harvest.getOwner() != null;
    }

    @Override
    public void tick() {
        LivingEntity owner = harvest.getOwner();
        if (owner == null) {
            return;
        }
        owner.getCapability(LivingUtilCapProvider.CAPABILITY).ifPresent(cap -> {
            if (cap.getCarryUpHarvest() != null && !cap.getCarryUpHarvest().equals(harvest)) {
                if (harvest.distanceTo(owner) <= 5) {
                    harvest.getNavigation().moveTo(owner, 1);
                } else {
                    harvest.teleportTo(owner.getX(), owner.getY(), owner.getZ());
                }
                harvest.setCarryingUp(true);
            } else harvest.setCarryingUp(cap.getCarryUpHarvest() == harvest);
        });
    }
}