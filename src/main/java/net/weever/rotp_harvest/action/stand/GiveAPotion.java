package net.weever.rotp_harvest.action.stand;

import com.github.standobyte.jojo.action.ActionConditionResult;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.stand.StandAction;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.weever.rotp_harvest.capability.LivingUtilCapProvider;
import net.weever.rotp_harvest.entity.stand.harvest.HarvestMainEntity;
import net.weever.rotp_harvest.entity.stand.stands.HarvestStandEntity;
import net.weever.rotp_harvest.util.HarvestUtil;
import net.weever.rotp_harvest.util.HarvestUtil.ItemInHandData;

public class GiveAPotion extends StandAction {
    public GiveAPotion(Builder builder) {
        super(builder);
    }

    @Override
    public ActionConditionResult checkRangeAndTarget(ActionTarget target, LivingEntity user, IStandPower power) {
        if (power.getStandManifestation() instanceof HarvestStandEntity) {
            if (target.getEntity() instanceof HarvestMainEntity 
//            		&& ((HarvestMainEntity) target.getEntity()).getPotionsToStab().isEmpty() 
            		&& ((HarvestMainEntity) target.getEntity()).getOwner() == user) {
                return ActionConditionResult.POSITIVE;
            }
        }
        return ActionConditionResult.NEGATIVE;
    }

    @Override
    public ActionConditionResult checkConditions(LivingEntity user, IStandPower power, ActionTarget target) {
        if (HarvestUtil.getItemInHand(user, Items.POTION).itemStack != null) {
            return ActionConditionResult.POSITIVE;
        }
        return ActionConditionResult.NEGATIVE;
    }

    @Override
    protected void perform(World world, LivingEntity user, IStandPower power, ActionTarget target) {
        if (!world.isClientSide()) {
            ItemInHandData itemData = HarvestUtil.getItemInHand(user, Items.POTION);
            ItemStack itemStack = itemData.itemStack;
            Hand hand = itemData.hand;
            if (target.getEntity() instanceof HarvestMainEntity && itemStack != null && hand != null) {
                user.getCapability(LivingUtilCapProvider.CAPABILITY).ifPresent(cap -> {
                    HarvestMainEntity harvest = (HarvestMainEntity) target.getEntity();
                    harvest.addAllPotionsToStab(PotionUtils.getPotion(itemStack).getEffects());
                    if (itemStack.hasTag()) {
                    	int alreadyUsed = itemStack.getTag().getInt("potionUsedAlready");
                    	
                    	itemStack.getOrCreateTag().putInt("potionUsedAlready", alreadyUsed++);
                    	if (itemStack.getTag().getInt("potionUsedAlready") >= 4) {
                    		user.setItemInHand(hand, Items.GLASS_BOTTLE.getDefaultInstance());
                    	}
                    }
                });
            }
        }
    }

    @Override
    public TargetRequirement getTargetRequirement() {
        return TargetRequirement.ENTITY;
    }
}
