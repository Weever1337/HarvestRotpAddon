package net.weever.rotp_harvest.client.render.vfx;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.weever.rotp_harvest.HarvestAddon;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = HarvestAddon.MOD_ID, value = Dist.CLIENT)
public class HarvestShader {
    private static final Minecraft mc = Minecraft.getInstance();
    private static boolean isShaderLoaded = false;
    private static PointOfView oldF5Mode;
    private static int ticks;
    private static int duration = -1;
    @Nullable
    private static ResourceLocation shaderTexture;

    public static void enableShader(ResourceLocation shaderTextureToRender, int durationInTicks) {
        shaderTexture = shaderTextureToRender;
        setDuration(durationInTicks);
    }

    public static void shutdownShader() {
        mc.gameRenderer.shutdownEffect();
        isShaderLoaded = false;
        setDuration(-1);
        shaderTexture = null;
        ticks = 0;
    }

    public static void onShaderTick() {
        if (duration == 0) {
            shutdownShader();
        } else if (duration == -1) {
            ticks = 0;
            return;
        }
        ticks++;
        if (ticks >= duration) {
            duration--;
        }
        if (oldF5Mode != mc.options.getCameraType()) {
            setIsShaderLoaded(false);
        }
        oldF5Mode = mc.options.getCameraType();
        renderShaders();
    }

    private static void renderShaders() {
        if (!isShaderLoaded && shaderTexture != null) {
            RenderSystem.recordRenderCall(() -> mc.gameRenderer.loadEffect(shaderTexture));
            setIsShaderLoaded(true);
        }
    }

    public static void setDuration(int set) {
        duration = set;
    }

    public static void setIsShaderLoaded(boolean set) {
        isShaderLoaded = set;
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (mc.level != null) {
            onShaderTick();
        }
    }
}