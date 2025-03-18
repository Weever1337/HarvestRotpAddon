package net.weever.rotp_harvest.action.stand;

import com.github.standobyte.jojo.action.ActionConditionResult;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.stand.StandAction;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.util.mc.MCUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.weever.rotp_harvest.capability.LivingUtilCap;
import net.weever.rotp_harvest.capability.LivingUtilCapProvider;
import net.weever.rotp_harvest.entity.stand.harvest.HarvestMainEntity;
import net.weever.rotp_harvest.entity.stand.stands.HarvestStandEntity;

import java.util.List;

public class ForgetTarget extends StandAction {
    public ForgetTarget(Builder builder) {
        super(builder);
    }

    @Override
    protected ActionConditionResult checkSpecificConditions(LivingEntity user, IStandPower power, ActionTarget target) {
        if (!(user instanceof PlayerEntity)) return ActionConditionResult.NEGATIVE;
        return ActionConditionResult.POSITIVE;
    }

    @Override
    protected void perform(World world, LivingEntity user, IStandPower power, ActionTarget target) {
        if (!world.isClientSide()) {
            double range = power.getStandManifestation() instanceof HarvestStandEntity ? ((HarvestStandEntity) power.getStandManifestation()).getMaxRange() : 150D;
            user.getCapability(LivingUtilCapProvider.CAPABILITY).ifPresent(cap -> {
                cap.setStatus(LivingUtilCap.HarvestStatus.STAYING);
                List<HarvestMainEntity> harvests = MCUtil.entitiesAround(HarvestMainEntity.class, user, range, false, harvest -> harvest.getOwner() == user);
                harvests.forEach(harvest -> harvest.setTarget(null));
            });
        }
    }
}
