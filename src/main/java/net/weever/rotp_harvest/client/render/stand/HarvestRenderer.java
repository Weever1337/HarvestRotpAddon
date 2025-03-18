package net.weever.rotp_harvest.client.render.stand;

import com.github.standobyte.jojo.client.render.entity.model.stand.StandEntityModel;
import com.github.standobyte.jojo.client.render.entity.model.stand.StandModelRegistry;
import com.github.standobyte.jojo.client.render.entity.renderer.stand.StandEntityRenderer;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.weever.rotp_harvest.HarvestAddon;
import net.weever.rotp_harvest.entity.stand.stands.HarvestStandEntity;

public class HarvestRenderer extends StandEntityRenderer<HarvestStandEntity, StandEntityModel<HarvestStandEntity>> {
    public HarvestRenderer(EntityRendererManager renderManager) {
        super(renderManager,
                StandModelRegistry.registerModel(new ResourceLocation(HarvestAddon.MOD_ID, "harvest_stand"), HarvestModel::new),
                new ResourceLocation(HarvestAddon.MOD_ID, "textures/entity/harvest.png"), 0);
    }
}