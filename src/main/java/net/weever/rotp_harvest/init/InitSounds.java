package net.weever.rotp_harvest.init;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.weever.rotp_harvest.HarvestAddon;

public class InitSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(
            ForgeRegistries.SOUND_EVENTS, HarvestAddon.MOD_ID);

    public static final RegistryObject<SoundEvent> SUMMON = SOUNDS.register("hv_summon",
            () -> new SoundEvent(new ResourceLocation(HarvestAddon.MOD_ID, "hv_summon"))
    );

    public static final RegistryObject<SoundEvent> UNSUMMON = SOUNDS.register("hv_unsummon",
            () -> new SoundEvent(new ResourceLocation(HarvestAddon.MOD_ID, "hv_unsummon"))
    );

    public static final RegistryObject<SoundEvent> HARVEST_STEP = SOUNDS.register("hv_step",
            () -> new SoundEvent(new ResourceLocation(HarvestAddon.MOD_ID, "hv_step"))
    );
}
