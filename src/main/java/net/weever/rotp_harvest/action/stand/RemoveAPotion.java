package net.weever.rotp_harvest.action.stand;

import com.github.standobyte.jojo.action.ActionConditionResult;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.stand.StandAction;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;

import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import net.weever.rotp_harvest.entity.stand.harvest.HarvestMainEntity;
import net.weever.rotp_harvest.entity.stand.stands.HarvestStandEntity;

public class RemoveAPotion extends StandAction {
    public RemoveAPotion(Builder builder) {
        super(builder);
    }

    @Override
    public ActionConditionResult checkRangeAndTarget(ActionTarget target, LivingEntity user, IStandPower power) {
        if (power.getStandManifestation() instanceof HarvestStandEntity) {
            if (target.getEntity() instanceof HarvestMainEntity 
            		&& !((HarvestMainEntity) target.getEntity()).getPotionsToStab().isEmpty() 
            		&& ((HarvestMainEntity) target.getEntity()).getOwner() == user) {
                return ActionConditionResult.POSITIVE;
            }
        }
        return ActionConditionResult.NEGATIVE;
    }

//    @Override
//    public ActionConditionResult checkConditions(LivingEntity user, IStandPower power, ActionTarget target) {
//        if (HarvestUtil.getItemInHand(user, Items.GLASS_BOTTLE).itemStack != null) {
//            return ActionConditionResult.POSITIVE;
//        }
//        return ActionConditionResult.NEGATIVE;
//    }

    @Override
    protected void perform(World world, LivingEntity user, IStandPower power, ActionTarget target) {
        if (!world.isClientSide()) {
            if (target.getEntity() instanceof HarvestMainEntity) {
                HarvestMainEntity harvest = (HarvestMainEntity) target.getEntity();
//                ItemInHandData itemData = HarvestUtil.getItemInHand(user, Items.GLASS_BOTTLE);
//                ItemStack itemStack = itemData.itemStack;
//                Hand hand = itemData.hand;
//                if (itemStack.hasTag()) {
//                	
//                } // TODO maybe via ItemCapability? Because i dont want to do it via NBT tags...
                harvest.clearPotionsToStab();
            }
        }
    }

    @Override
    public TargetRequirement getTargetRequirement() {
        return TargetRequirement.ENTITY;
    }
}
