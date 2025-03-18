package net.weever.rotp_harvest.entity.ai.goal;

import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.weever.rotp_harvest.capability.LivingUtilCap;
import net.weever.rotp_harvest.entity.stand.harvest.HarvestMainEntity;

import java.util.EnumSet;

public class HarvestTeleportGoal extends Goal {
    private final HarvestMainEntity harvestEntity;

    public HarvestTeleportGoal(HarvestMainEntity harvestEntity) {
        this.harvestEntity = harvestEntity;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        return harvestEntity.getOriginalOwner() != null
                && harvestEntity.getStatus() != LivingUtilCap.HarvestStatus.ATTACKING
                && !harvestEntity.isGoingTo()
                && harvestEntity.getOwnerPower() != null;
    }

    @Override
    public void tick() {
        IStandPower power = harvestEntity.getOwnerPower();
        StandEntity stand = (StandEntity) power.getStandManifestation();
        LivingEntity owner = harvestEntity.getOriginalOwner();
        if (stand != null) { // Original part of code: https://github.com/MakutaZeml/Rotp-Bad-Company/blob/main/src/main/java/com/zeml/rotp_zbc/entity/BadCompanyUnitEntity.java#L84
            float range = harvestEntity.isCloser() ? 7F : (float) Math.min(stand.getMaxRange(), 50f);

            if (harvestEntity.distanceTo(owner) > range + 10f) {
                harvestEntity.teleportTo(
                        owner.getX() + 5 - 10F * Math.random(),
                        owner.getY(),
                        owner.getZ() + 5 - 10F * Math.random()
                );
            } else if (harvestEntity.distanceTo(owner) > range / 2){
                harvestEntity.getNavigation().moveTo(owner, 1.0f);
            }
        }
    }
}
