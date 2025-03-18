package net.weever.rotp_harvest.power.impl.stand.type;

import java.util.Random;
import java.util.function.Consumer;

import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.power.impl.stand.StandUtil;
import com.github.standobyte.jojo.power.impl.stand.stats.StandStats;
import com.github.standobyte.jojo.power.impl.stand.type.EntityStandType;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.weever.rotp_harvest.capability.LivingUtilCap;
import net.weever.rotp_harvest.capability.LivingUtilCapProvider;
import net.weever.rotp_harvest.config.SettingsConfig;
import net.weever.rotp_harvest.entity.stand.harvest.HarvestMainEntity;
import net.weever.rotp_harvest.entity.stand.stands.HarvestStandEntity;
import net.weever.rotp_harvest.util.HarvestUtil;

public class HarvestStandType<T extends StandStats> extends EntityStandType<T> {
    protected HarvestStandType(EntityStandType.AbstractBuilder<?, T> builder) {
        super(builder);
    }

    @Override
    public boolean summon(LivingEntity user, IStandPower standPower, Consumer<StandEntity> beforeTheSummon, boolean withoutNameVoiceLine, boolean addToWorld) {
        if (!user.level.isClientSide()) {
            user.getCapability(LivingUtilCapProvider.CAPABILITY).ifPresent(cap -> {
                int numEntities = SettingsConfig.getHarvestSummonInt(user.level.isClientSide());
                double radius = 3.0;
                Random random = new Random();
                for (int i = 0; i < numEntities; i++) {
                    double offsetX = (random.nextDouble() * 2 - 1) * radius;
                    double offsetZ = (random.nextDouble() * 2 - 1) * radius;
                    spawnHarvest(user, offsetX, offsetZ);
                }
            });
        }
        return super.summon(user, standPower, beforeTheSummon, withoutNameVoiceLine, addToWorld);
    }
    
    private void spawnHarvest(LivingEntity user, double offsetX, double offsetZ) {
        HarvestMainEntity harvest = new HarvestMainEntity(user.level);
        double entityX = user.getX() + offsetX;
        double entityZ = user.getZ() + offsetZ;
        harvest.teleportTo(entityX, user.getY(), entityZ);
        harvest.setOwnerUUID(user.getUUID());
        user.level.addFreshEntity(harvest);
    }

    @Override
    public void unsummon(LivingEntity user, IStandPower standPower) {
        super.unsummon(user, standPower);
        unsummonStand(user);
    }

    @Override
    public void forceUnsummon(LivingEntity user, IStandPower standPower) {
        super.forceUnsummon(user, standPower);
        unsummonStand(user);
    }

    private void unsummonStand(LivingEntity user) {
        if (!user.level.isClientSide) {
            user.getCapability(LivingUtilCapProvider.CAPABILITY).ifPresent(cap -> {
                cap.setStatus(LivingUtilCap.HarvestStatus.STAYING);
            });
        }
    }

    @Override
    public void tickUser(LivingEntity user, IStandPower power) {
        super.tickUser(user, power);
        if (power.getStandManifestation() instanceof HarvestStandEntity && user instanceof PlayerEntity) {
            StandEntity standEntity = ((StandEntity) power.getStandManifestation());
            if (standEntity.isManuallyControlled()) {
                StandUtil.setManualControl((PlayerEntity) user, false, true);
                HarvestMainEntity nearestHarvest = HarvestUtil.findNearestHarvest(user, (int) standEntity.getMaxRange());
                HarvestUtil.controlHarvest(user, nearestHarvest);
            }
        }
    }

    public static class Builder<T extends StandStats> extends EntityStandType.AbstractBuilder<Builder<T>, T> {
        @Override
        protected Builder<T> getThis() {
            return this;
        }

        @Override
        public HarvestStandType<T> build() {
            return new HarvestStandType<>(this);
        }
    }
}
