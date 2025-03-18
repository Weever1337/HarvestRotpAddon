package net.weever.rotp_harvest.init;

import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.weever.rotp_harvest.HarvestAddon;
import net.weever.rotp_harvest.entity.stand.harvest.HarvestMainEntity;

@Mod.EventBusSubscriber(modid = HarvestAddon.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {

    @SubscribeEvent
    public static void addEntityAttribues(EntityAttributeCreationEvent event) {
        event.put(InitEntities.HARVEST.get(), HarvestMainEntity.createAttributes().build());
    }
}
