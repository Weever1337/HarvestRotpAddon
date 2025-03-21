package net.weever.rotp_harvest.client.render;

import com.github.standobyte.jojo.client.ClientUtil;
import com.github.standobyte.jojo.client.standskin.StandSkinsManager;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.power.impl.stand.StandInstance;
import com.github.standobyte.jojo.power.impl.stand.type.StandType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.weever.rotp_harvest.HarvestAddon;
import net.weever.rotp_harvest.entity.stand.harvest.HarvestMainEntity;
import net.weever.rotp_harvest.init.InitStands;
import org.jetbrains.annotations.NotNull;

public class HarvestMainRenderer extends MobRenderer<HarvestMainEntity, HarvestMainModel> {

    protected static final ResourceLocation TEXTURE = new ResourceLocation(HarvestAddon.MOD_ID, "textures/entity/harvest.png");
    protected static final ResourceLocation VOID = new ResourceLocation(HarvestAddon.MOD_ID, "textures/entity/void.png");
    protected static ResourceLocation SHEER = TEXTURE;

    public HarvestMainRenderer(EntityRendererManager rendererManager) {
        super(rendererManager, new HarvestMainModel(), 0F);
        // this.addLayer(new HarvestHeldItemLayer<HarvestMainModel>(this));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(HarvestMainEntity entity) {
        if (entity.getOwner() != null) {
            IStandPower.getStandPowerOptional(entity.getOwner()).ifPresent(power -> {
                StandType<?> standType = InitStands.HARVEST.getStandType();
                if (power.getType() == standType && power.getStandInstance().isPresent()) {
                    SHEER = getRemappedTexture(power.getStandInstance().get());
                } else {
                    SHEER = TEXTURE;
                }
            });
        }
        return ClientUtil.canSeeStands() ? SHEER : VOID;
    }

    private ResourceLocation getRemappedTexture(StandInstance standInstance) {
        return StandSkinsManager.getInstance() != null
            ? StandSkinsManager.getInstance().getRemappedResPath(manager -> manager.getStandSkin(standInstance), TEXTURE)
            : TEXTURE;
    }
}
