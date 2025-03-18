package net.weever.rotp_harvest.action.stand;

import com.github.standobyte.jojo.action.ActionConditionResult;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.stand.StandAction;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.weever.rotp_harvest.entity.stand.harvest.HarvestMainEntity;
import net.weever.rotp_harvest.entity.stand.stands.HarvestStandEntity;
import net.weever.rotp_harvest.util.HarvestUtil;

public class ControlHarvest extends StandAction {
    public ControlHarvest(Builder builder) {
        super(builder);
    }

    @Override
    protected ActionConditionResult checkSpecificConditions(LivingEntity user, IStandPower power, ActionTarget target) {
        if (!(user instanceof PlayerEntity)) return ActionConditionResult.NEGATIVE;
        if (power.getStandManifestation() instanceof HarvestStandEntity) {
            boolean isClient = user.level.isClientSide();
            boolean isCameraEntityHarvest = Minecraft.getInstance().getCameraEntity() instanceof HarvestMainEntity;
            boolean isTargetEntityHarvest = target.getType() == ActionTarget.TargetType.ENTITY &&
                    target.getEntity() instanceof HarvestMainEntity &&
                    ((HarvestMainEntity) target.getEntity()).getOwner() == user;
            return (isClient && (isCameraEntityHarvest || isTargetEntityHarvest)) ?
                    ActionConditionResult.POSITIVE : ActionConditionResult.NEGATIVE; // AHH FUCK STUPID "LAMBDA" 🐹🐹🐹
        }
        return ActionConditionResult.NEGATIVE;
    }

    @Override
    protected void perform(World world, LivingEntity user, IStandPower power, ActionTarget target) {
        HarvestUtil.controlHarvest(user, (HarvestMainEntity) target.getEntity());
    }
}
