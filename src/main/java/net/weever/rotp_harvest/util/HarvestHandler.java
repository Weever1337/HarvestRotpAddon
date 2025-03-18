package net.weever.rotp_harvest.util;

import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.weever.rotp_harvest.HarvestAddon;
import net.weever.rotp_harvest.entity.stand.harvest.HarvestMainEntity;

@Mod.EventBusSubscriber(modid = HarvestAddon.MOD_ID)
public class HarvestHandler {
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onHarvestDeath(LivingDeathEvent event) {
        if (!event.getEntity().level.isClientSide && event.getEntity() instanceof HarvestMainEntity) {
            HarvestMainEntity harvest = (HarvestMainEntity) event.getEntity();
            if (harvest.getOwner() != null) {
                LivingEntity user = harvest.getOwner();
                IStandPower.getStandPowerOptional(user).ifPresent(power -> {
                    if (power.getStamina() >= 300 && power.isActive()) {
                        HarvestMainEntity newHarvest = new HarvestMainEntity(harvest.level);
                        newHarvest.setOwnerUUID(power.getUser().getUUID());
                        newHarvest.teleportTo(user.getX() + 5 - 10F * Math.random(), user.getY(), user.getZ() + 5 - 10F * Math.random());
                        harvest.level.addFreshEntity(newHarvest);
                        power.consumeStamina(300);
                    }
                });
            }
        }
    }
}
