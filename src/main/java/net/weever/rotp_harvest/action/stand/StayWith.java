package net.weever.rotp_harvest.action.stand;

import java.util.List;

import com.github.standobyte.jojo.action.ActionConditionResult;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.stand.StandAction;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.util.mc.MCUtil;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.weever.rotp_harvest.capability.LivingUtilCap;
import net.weever.rotp_harvest.capability.LivingUtilCapProvider;
import net.weever.rotp_harvest.entity.stand.harvest.HarvestMainEntity;
import net.weever.rotp_harvest.entity.stand.stands.HarvestStandEntity;
import net.weever.rotp_harvest.util.HarvestUtil;

public class StayWith extends StandAction {
    public StayWith(Builder builder) {
        super(builder);
    }

    @Override
    protected ActionConditionResult checkSpecificConditions(LivingEntity user, IStandPower power, ActionTarget target) {
        if (!(user instanceof PlayerEntity)) return ActionConditionResult.NEGATIVE;
        if (power.getStandManifestation() instanceof HarvestStandEntity) {
            LivingEntity targetEntity = HarvestUtil.getEntityByRayTrace(power.getUser(), 7, entity -> !(entity instanceof HarvestMainEntity));
            if (!targetEntity.isAlive()) {
                return ActionConditionResult.NEGATIVE;
            }
            return ActionConditionResult.POSITIVE;
        }
        return ActionConditionResult.NEGATIVE;
    }

    @Override
    protected void perform(World world, LivingEntity user, IStandPower power, ActionTarget target) {
        if (!world.isClientSide()) {
            LivingEntity entityByTraceResult = HarvestUtil.getEntityByRayTrace(power.getUser(), 7, entity -> !(entity instanceof HarvestMainEntity));
            double range = power.getStandManifestation() instanceof HarvestStandEntity ? ((HarvestStandEntity) power.getStandManifestation()).getMaxRange() : 150D;
            user.getCapability(LivingUtilCapProvider.CAPABILITY).ifPresent(cap -> {
                cap.setStatus(LivingUtilCap.HarvestStatus.STAYING);
                if (!entityByTraceResult.is(user)) {
                	cap.setStayWith(entityByTraceResult.getUUID());
                } else {
                	cap.setStayWith(null);
                }
                List<HarvestMainEntity> harvests = MCUtil.entitiesAround(HarvestMainEntity.class, user, range, false, harvest -> harvest.getOwner() == user);
                harvests.forEach(harvest -> {
                    harvest.setTarget(null);
                });
            });
        }
    }

    @Override
    public IFormattableTextComponent getTranslatedName(IStandPower power, String key) {
        if (power != null) {
            Entity entityByTraceResult = HarvestUtil.getEntityByRayTrace(power.getUser(), 7, entity -> !(entity instanceof HarvestMainEntity));
            return new TranslationTextComponent(key, entityByTraceResult.getName().getString());
        } else {
            return new TranslationTextComponent(key);
        }
    }
}
