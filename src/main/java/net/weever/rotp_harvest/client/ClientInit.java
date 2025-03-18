package net.weever.rotp_harvest.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.weever.rotp_harvest.HarvestAddon;
import net.weever.rotp_harvest.client.render.HarvestMainRenderer;
import net.weever.rotp_harvest.client.render.stand.HarvestRenderer;
import net.weever.rotp_harvest.init.InitEntities;
import net.weever.rotp_harvest.init.InitStands;

@EventBusSubscriber(modid = HarvestAddon.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientInit {

    @SubscribeEvent
    public static void onFMLClientSetup(FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(
                InitStands.HARVEST.getEntityType(), HarvestRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(
                InitEntities.HARVEST.get(), HarvestMainRenderer::new);
    }
}
