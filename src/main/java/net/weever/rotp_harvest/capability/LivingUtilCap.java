package net.weever.rotp_harvest.capability;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.weever.rotp_harvest.entity.stand.harvest.HarvestMainEntity;
import net.weever.rotp_harvest.network.AddonPackets;
import net.weever.rotp_harvest.network.s2c.TrSyncBooleanValues;
import net.weever.rotp_harvest.network.s2c.TrSyncIntValues;
import net.weever.rotp_harvest.network.s2c.TrSyncListValues;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class LivingUtilCap {
    private final LivingEntity livingEntity;
    private final Map<UUID, UUID> items = new HashMap<>();
    private final List<HarvestMainEntity> controllingHarvests = new LinkedList<>();
    private HarvestStatus status = HarvestStatus.STAYING;
    private boolean closer = false;
    @Nullable private HarvestMainEntity carryUpHarvest;
    private BlockPos goToThisPlace = BlockPos.ZERO;
    private UUID stayWith;

    public LivingUtilCap(LivingEntity livingEntity) {
        this.livingEntity = livingEntity;
    }

    public Map<UUID, UUID> getItems() {
        return items;
    }

    public List<HarvestMainEntity> getControllingHarvests() {
        return controllingHarvests;
    }

    public void setControllingHarvests(List<HarvestMainEntity> controllingHarvests) {
        this.controllingHarvests.clear();
        this.controllingHarvests.addAll(controllingHarvests);
        if (!livingEntity.level.isClientSide()) {
            AddonPackets.sendToClientsTrackingAndSelf(TrSyncListValues.setControlledHarvests(livingEntity.getId(), controllingHarvests), livingEntity);
        }
    }

    public HarvestStatus getStatus() {
        return status;
    }

    public void setStatus(HarvestStatus status) {
        this.status = status;
    }

    public BlockPos getGoToThisPlace() {
        return goToThisPlace;
    }

    public void setGoToThisPlace(BlockPos blockPos) {
        this.goToThisPlace = blockPos;
    }

    public boolean isCloser() {
        return closer;
    }

    public void setCloser(boolean closer) {
        this.closer = closer;
        if (!livingEntity.level.isClientSide()) {
            AddonPackets.sendToClientsTrackingAndSelf(TrSyncBooleanValues.setCloser(livingEntity.getId(), closer), livingEntity);
        }
    }

    @Nullable
    public HarvestMainEntity getCarryUpHarvest() {
        return carryUpHarvest;
    }

    public void setCarryUpHarvest(HarvestMainEntity carryUpHarvest) {
        this.carryUpHarvest = carryUpHarvest;
        if (!livingEntity.level.isClientSide()) {
            AddonPackets.sendToClientsTrackingAndSelf(TrSyncIntValues.setCarryUpHarvest(livingEntity.getId(), carryUpHarvest == null ? -1 : carryUpHarvest.getId()), livingEntity);
        }
    }

    @Nullable
    public UUID getStayWith() {
        return stayWith;
    }

    public void setStayWith(UUID stayWithUUID) {
        this.stayWith = stayWithUUID;
    }

    public void syncWithAnyPlayer(ServerPlayerEntity playerEntity) {
        AddonPackets.sendToClientsTrackingAndSelf(TrSyncBooleanValues.setCloser(playerEntity.getId(), closer), playerEntity);
        AddonPackets.sendToClientsTrackingAndSelf(TrSyncIntValues.setCarryUpHarvest(playerEntity.getId(), carryUpHarvest == null ? -1 : carryUpHarvest.getId()), playerEntity);
    }
    
    public CompoundNBT toNBT() {
        CompoundNBT nbt = new CompoundNBT();
        return nbt;
    }

    public void fromNBT(CompoundNBT nbt) {
    }

    public enum HarvestStatus {
        STAYING,
        ATTACKING,
        FINDING_ITEM
    }
}
