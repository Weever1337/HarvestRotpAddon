package net.weever.rotp_harvest.action.stand;

import com.github.standobyte.jojo.action.ActionConditionResult;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.stand.StandAction;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.util.mc.MCUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.weever.rotp_harvest.capability.LivingUtilCapProvider;
import net.weever.rotp_harvest.entity.stand.harvest.HarvestMainEntity;

import java.util.List;

public class CarryUp extends StandAction {
    public CarryUp(Builder builder) {
        super(builder);
    }

    @Override
    protected ActionConditionResult checkSpecificConditions(LivingEntity user, IStandPower power, ActionTarget target) {
        if (!(user instanceof PlayerEntity)) return ActionConditionResult.NEGATIVE;
        return ActionConditionResult.POSITIVE;
    }

    @Override
    protected void perform(World world, LivingEntity user, IStandPower power, ActionTarget target) {
        if (!world.isClientSide() && user instanceof PlayerEntity) {
            double range = 5;
            user.getCapability(LivingUtilCapProvider.CAPABILITY).ifPresent(cap -> {
                List<HarvestMainEntity> harvests = MCUtil.entitiesAround(HarvestMainEntity.class, user, range, false, harvest -> harvest.getOwner() == user);
                if (harvests.isEmpty()) {
                    cap.setCarryUpHarvest(null);
                    return;
                }
                HarvestMainEntity harvest = harvests.get(user.getRandom().nextInt(harvests.size()));
                cap.setCarryUpHarvest(cap.getCarryUpHarvest() == null ? harvest : null);
                if (cap.getCarryUpHarvest() != null) {
                    if (user.isPassenger()) {
                        user.stopRiding();
                    }
                    user.startRiding(harvest);
                } else {
                    if (user.isPassenger() && user.getVehicle() instanceof HarvestMainEntity) {
                        user.stopRiding();
                        user.setSwimming(false);
                    }
                }
                ((PlayerEntity) user).setForcedPose(Pose.SWIMMING);
            });
        }
    }

    @Override
    public boolean greenSelection(IStandPower power, ActionConditionResult conditionCheck) {
        return power.getUser().getCapability(LivingUtilCapProvider.CAPABILITY).map(cap -> cap.getCarryUpHarvest() != null).orElse(false);
    }
}
