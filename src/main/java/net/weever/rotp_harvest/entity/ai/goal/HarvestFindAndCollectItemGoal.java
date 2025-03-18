package net.weever.rotp_harvest.entity.ai.goal;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.weever.rotp_harvest.capability.LivingUtilCap;
import net.weever.rotp_harvest.capability.LivingUtilCapProvider;
import net.weever.rotp_harvest.entity.stand.harvest.HarvestMainEntity;

public class HarvestFindAndCollectItemGoal extends Goal {
    private final HarvestMainEntity harvest;
    private ItemEntity targetItem = null;

    public HarvestFindAndCollectItemGoal(HarvestMainEntity harvest) {
        this.harvest = harvest;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return harvest.getStatus() == LivingUtilCap.HarvestStatus.FINDING_ITEM && harvest.getOwner() != null;
    }

    @Override
    public void tick() {
        if (!harvest.level.isClientSide()) {
            if (targetItem == null || !targetItem.isAlive() || isClaimedByOther(targetItem)) {
                targetItem = findNearestItem();
            }

            if (targetItem != null) {
                if (harvest.distanceTo(targetItem) > 2.0D) {
                    harvest.getNavigation().moveTo(targetItem, 1);
                } else {
                    claimItem(targetItem);
                    collectItem(targetItem);
                }
            } else if (!harvest.getMainHandItem().isEmpty()) {
                moveToOwnerAndDrop();
            }
        }
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    private ItemEntity findNearestItem() {
        List<ItemEntity> items = harvest.level.getEntitiesOfClass(ItemEntity.class,
                new AxisAlignedBB(harvest.blockPosition()).inflate(200.0D),
                item -> item.isAlive() && !isClaimedByOther(item)
        );
        List<ItemEntity> itemsToForget = new ArrayList<>();
        for (ItemEntity item : items) {
            LivingEntity owner = harvest.getOwner();
            owner.getCapability(LivingUtilCapProvider.CAPABILITY).ifPresent(cap -> {
                if (cap.getItems().containsValue(item.getUUID())) {
                    itemsToForget.add(item);
                }
            });
        }
        items.removeIf(itemsToForget::contains);
        return items.stream()
                .min((item1, item2) -> Double.compare(harvest.distanceToSqr(item1), harvest.distanceToSqr(item2)))
                .orElse(null);
    }

    private void collectItem(ItemEntity item) {
        item.remove();
        ItemStack stack = item.getItem();
        harvest.setItemInHand(Hand.MAIN_HAND, stack);

        targetItem = null;
    }

    private void moveToOwnerAndDrop() {
        if (harvest.getOwner() != null) {
            if (harvest.distanceTo(harvest.getOwner()) > 2.0D) {
                harvest.getNavigation().moveTo(harvest.getOwner(), 1);
            } else {
                ItemStack itemStack = harvest.getMainHandItem();
                if (!itemStack.isEmpty()) {
                    harvest.setItemInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
                    ItemEntity newEntity = new ItemEntity(harvest.level, harvest.getX(), harvest.getY() + 1f, harvest.getZ(), itemStack);
                    harvest.level.addFreshEntity(newEntity);
                    newEntity.getPersistentData().putString("harvest", "424242");
                }
                harvest.getOwner().getCapability(LivingUtilCapProvider.CAPABILITY).ifPresent(cap -> {
                    cap.getItems().remove(harvest.getUUID());
                });
            }
        }
    }

    private void claimItem(ItemEntity item) {
        LivingEntity owner = harvest.getOwner();
        if (owner == null) return;
        owner.getCapability(LivingUtilCapProvider.CAPABILITY).ifPresent(cap -> {
            cap.getItems().put(harvest.getUUID(), item.getUUID());
        });
    }

    private boolean isClaimedByOther(ItemEntity item) {
        LivingEntity owner = harvest.getOwner();
        if (owner == null) return true;

        AtomicBoolean claimed = new AtomicBoolean(true);
        owner.getCapability(LivingUtilCapProvider.CAPABILITY).ifPresent(cap -> {
            claimed.set(cap.getItems().containsValue(item.getUUID()));
        });
        return claimed.get() || item.getPersistentData().contains("harvest");
    }
}
