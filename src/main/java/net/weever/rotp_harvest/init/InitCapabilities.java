package net.weever.rotp_harvest.init;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.weever.rotp_harvest.HarvestAddon;
import net.weever.rotp_harvest.capability.LivingUtilCap;
import net.weever.rotp_harvest.capability.LivingUtilCapProvider;
import net.weever.rotp_harvest.capability.LivingUtilCapStorage;

@Mod.EventBusSubscriber(modid = HarvestAddon.MOD_ID)
public class InitCapabilities {
    private static final ResourceLocation LIVING_UTIL = new ResourceLocation(HarvestAddon.MOD_ID, "living_util");

    @SubscribeEvent
    public static void onAttachCapabilitiesEntity(AttachCapabilitiesEvent<Entity> event) {
        Entity entity = event.getObject();
        if (entity instanceof LivingEntity) {
            event.addCapability(LIVING_UTIL, new LivingUtilCapProvider((LivingEntity) entity));
        }
    }

    public static void registerCapabilities() {
        CapabilityManager.INSTANCE.register(LivingUtilCap.class, new LivingUtilCapStorage(), () -> new LivingUtilCap(null));
    }

    @SubscribeEvent
    public static void syncWithNewPlayer(PlayerEvent.StartTracking event) {
        Entity entityTracked = event.getTarget();
        ServerPlayerEntity trackingPlayer = (ServerPlayerEntity) event.getPlayer();
        if (entityTracked instanceof LivingEntity) {
            entityTracked.getCapability(LivingUtilCapProvider.CAPABILITY).ifPresent(livingData -> {
                livingData.syncWithAnyPlayer(trackingPlayer);
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        syncAttachedData(event.getPlayer());
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        syncAttachedData(event.getPlayer());
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        syncAttachedData(event.getPlayer());
    }

    private static void syncAttachedData(PlayerEntity player) {
        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
        player.getCapability(LivingUtilCapProvider.CAPABILITY).ifPresent(data -> {
            data.syncWithAnyPlayer(serverPlayer);
        });
    }
}
