package net.weever.rotp_harvest.init;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.weever.rotp_harvest.HarvestAddon;
import net.weever.rotp_harvest.entity.stand.harvest.HarvestMainEntity;

public class InitEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(
            ForgeRegistries.ENTITIES, HarvestAddon.MOD_ID);

    public static final RegistryObject<EntityType<HarvestMainEntity>> HARVEST = ENTITIES.register("harvest",
            () -> EntityType.Builder.<HarvestMainEntity>of(HarvestMainEntity::new, EntityClassification.MISC).sized(0.2F, 0.4F)
                    .setUpdateInterval(2).build((new ResourceLocation(HarvestAddon.MOD_ID, "harvest").toString())));
}
