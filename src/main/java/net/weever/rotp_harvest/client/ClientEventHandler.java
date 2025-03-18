package net.weever.rotp_harvest.client;

import com.github.standobyte.jojo.capability.entity.EntityUtilCapProvider;
import com.github.standobyte.jojo.client.ui.actionshud.ActionsOverlayGui;
import com.github.standobyte.jojo.init.ModStatusEffects;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.util.mc.MCUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.weever.rotp_harvest.HarvestAddon;
import net.weever.rotp_harvest.capability.LivingUtilCapProvider;
import net.weever.rotp_harvest.client.render.vfx.HarvestShader;
import net.weever.rotp_harvest.entity.stand.harvest.HarvestMainEntity;
import net.weever.rotp_harvest.init.InitStands;
import net.weever.rotp_harvest.util.HarvestUtil;

import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.Random;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = HarvestAddon.MOD_ID, value = Dist.CLIENT)
public class ClientEventHandler {
    private static final Minecraft mc = Minecraft.getInstance();

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        PlayerEntity player = event.player;
        if (player.level.isClientSide()) {
            List<HarvestMainEntity> harvests = MCUtil.entitiesAround(HarvestMainEntity.class, player, 20, false, harvest -> harvest.getOwner() == player || Objects.equals(harvest.getOwner(), player));

            if (player == mc.player && IStandPower.getStandPowerOptional(player).isPresent()) {
                IStandPower.getStandPowerOptional(player).ifPresent(power -> {
                    if (power.getType() != InitStands.HARVEST.getStandType() || !power.isActive())
                        return;

                    harvests.forEach(harvest -> {
                        mc.player.getCapability(LivingUtilCapProvider.CAPABILITY).ifPresent(cap -> {
                            if (cap.getControllingHarvests().contains(harvest)) {
                                int color = OptionalInt.of(ActionsOverlayGui.getPowerUiColor(power)).orElse(0);
                                harvest.getCapability(EntityUtilCapProvider.CAPABILITY).ifPresent(
                                        capability -> capability.setClGlowingColor(OptionalInt.of(color), 80));
                            } else {
                                if (harvest.getPotionsToStab().isEmpty() || player.tickCount % 20 != 0) return;
                                
                                int i = harvest.getPotionsToStab().size();
                                Random random = new Random();
                                EffectInstance effectInstance = harvest.getPotionsToStab().get(random.nextInt(i));
                                
                                if (effectInstance != null) {
                                	int color = effectInstance.getEffect().getColor();
	                                harvest.getCapability(EntityUtilCapProvider.CAPABILITY).ifPresent(
	                                        capability -> capability.setClGlowingColor(OptionalInt.of(color), 25));
                                }
                            }
                        });
                    });
                });
            }
            if (mc.getCameraEntity() instanceof HarvestMainEntity) {
                if (((HarvestMainEntity) mc.getCameraEntity()).isDeadOrDying() || !mc.getCameraEntity().isAlive()) {
                    mc.setCameraEntity(player);
                } else {
                    player.displayClientMessage(new TranslationTextComponent("message.rotp_harvest.harvesting"), true);
                    HarvestShader.enableShader(HarvestUtil.getShader(IStandPower.getStandPowerOptional(player).orElse(null)), 2);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onInput(InputEvent event) {
        if (mc.player != null && mc.player.isShiftKeyDown() && mc.getCameraEntity() instanceof HarvestMainEntity) {
            mc.setCameraEntity(mc.player);
        }
    }

    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent event) {
        if (mc.player != null && mc.getCameraEntity() instanceof HarvestMainEntity) {
            event.setCanceled(true);
        }
    }

    private static boolean wasStunned = false;
    private static double prevSensitivity = 0.5;
    private static final double ZERO_SENSITIVITY = -1.0 / 3.0;
    @SubscribeEvent
    public static void setMouseSensitivity(TickEvent.ClientTickEvent event) {
        if (mc.player == null) {
            if (mc.options.sensitivity <= ZERO_SENSITIVITY) {
                mc.options.sensitivity = prevSensitivity;
            }
            return;
        }

        if (mc.getCameraEntity() instanceof HarvestMainEntity && !ModStatusEffects.isStunned(mc.player)) {
            if (!wasStunned) {
                prevSensitivity = mc.options.sensitivity;
                wasStunned = true;
            }
            mc.options.sensitivity = ZERO_SENSITIVITY;
        } else if (wasStunned) {
            mc.options.sensitivity = prevSensitivity;
            wasStunned = false;
        }
    }
}
