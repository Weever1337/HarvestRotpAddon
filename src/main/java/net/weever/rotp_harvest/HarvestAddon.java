package net.weever.rotp_harvest;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.weever.rotp_harvest.init.InitCapabilities;
import net.weever.rotp_harvest.init.InitEntities;
import net.weever.rotp_harvest.init.InitSounds;
import net.weever.rotp_harvest.init.InitStands;
import net.weever.rotp_harvest.network.AddonPackets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(HarvestAddon.MOD_ID)
public class HarvestAddon {
    public static final String MOD_ID = "rotp_harvest";
    public static final Logger LOGGER = LogManager.getLogger();

    public HarvestAddon() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, HarvestConfig.commonSpec);
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        InitEntities.ENTITIES.register(modEventBus);
        InitSounds.SOUNDS.register(modEventBus);
        InitStands.ACTIONS.register(modEventBus);
        InitStands.STANDS.register(modEventBus);
        modEventBus.addListener(this::onFMLCommonSetup);
    }

    private void onFMLCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(InitCapabilities::registerCapabilities);
        AddonPackets.init();
    }
}