package net.weever.rotp_harvest.action.stand;

import com.github.standobyte.jojo.action.ActionConditionResult;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.stand.StandAction;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.util.mc.MCUtil;
import net.minecraft.block.AirBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.weever.rotp_harvest.capability.LivingUtilCap;
import net.weever.rotp_harvest.capability.LivingUtilCapProvider;
import net.weever.rotp_harvest.entity.stand.harvest.HarvestMainEntity;
import net.weever.rotp_harvest.entity.stand.stands.HarvestStandEntity;
import net.weever.rotp_harvest.util.HarvestUtil;

import java.util.List;

public class GoToThisPlace extends StandAction {
    public GoToThisPlace(Builder builder) {
        super(builder);
    }

    @Override
    protected ActionConditionResult checkSpecificConditions(LivingEntity user, IStandPower power, ActionTarget target) {
        HarvestStandEntity harvestStand = (HarvestStandEntity) power.getStandManifestation();
        if (harvestStand == null) return ActionConditionResult.NEGATIVE;

        BlockRayTraceResult rayTrace = HarvestUtil.getBlockByRayTrace(user, 15);
        if (rayTrace == null) return ActionConditionResult.NEGATIVE;

        BlockPos blockPos = rayTrace.getBlockPos();
        BlockState blockState = user.level.getBlockState(blockPos);
        if (blockState.getBlock() instanceof AirBlock) return ActionConditionResult.NEGATIVE;

        return ActionConditionResult.POSITIVE;
    }

    @Override
    protected void perform(World world, LivingEntity user, IStandPower power, ActionTarget target) {
        if (!world.isClientSide()) {
            BlockRayTraceResult rayTrace = HarvestUtil.getBlockByRayTrace(power.getUser(), 15);
            if (rayTrace == null) return;
            BlockPos blockPos = rayTrace.getBlockPos();
            double range = power.getStandManifestation() instanceof HarvestStandEntity ? ((HarvestStandEntity) power.getStandManifestation()).getMaxRange() : 150D;
            user.getCapability(LivingUtilCapProvider.CAPABILITY).ifPresent(cap -> {
                cap.setStatus(LivingUtilCap.HarvestStatus.STAYING);
                cap.setGoToThisPlace(blockPos);
                List<HarvestMainEntity> harvests = MCUtil.entitiesAround(HarvestMainEntity.class, user, range, false, harvest -> harvest.getOwner() == user);
                harvests.forEach(harvest -> {
                    harvest.setTarget(null);
                    harvest.setGoingTo(true);
                });
            });
        }
    }

    @Override
    public IFormattableTextComponent getTranslatedName(IStandPower power, String key) {
        if (power != null) {
            BlockRayTraceResult blockRayTrace = HarvestUtil.getBlockByRayTrace(power.getUser(), 15);
            if (blockRayTrace == null) {
                return new TranslationTextComponent(key);
            }
            BlockPos blockPos = blockRayTrace.getBlockPos();
            BlockState blockState = power.getUser().level.getBlockState(blockPos);
            if (blockState.getBlock() instanceof AirBlock) return new TranslationTextComponent(key);

            return new TranslationTextComponent(key + ".additional", blockState.getBlock().getName().getString());
        }
        return new TranslationTextComponent(key);
    }
}
