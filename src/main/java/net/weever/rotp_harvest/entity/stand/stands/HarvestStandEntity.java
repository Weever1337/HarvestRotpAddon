package net.weever.rotp_harvest.entity.stand.stands;

import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.entity.stand.StandEntityType;
import com.github.standobyte.jojo.entity.stand.StandRelativeOffset;
import com.github.standobyte.jojo.init.ModStatusEffects;

import net.minecraft.potion.EffectInstance;
import net.minecraft.world.World;

public class HarvestStandEntity extends StandEntity {
    private final StandRelativeOffset offsetDefault = StandRelativeOffset.withYOffset(0, 1, 0);

    public HarvestStandEntity(StandEntityType<HarvestStandEntity> type, World world) {
        super(type, world);
        unsummonOffset = getDefaultOffsetFromUser().copy();
    }

    @Override
    public void tick() {
        super.tick();
        if (!level.isClientSide()) {
            this.addEffect(new EffectInstance(ModStatusEffects.FULL_INVISIBILITY.get(), 666, 1, false, false, true));
        }
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    public StandRelativeOffset getDefaultOffsetFromUser() {
        return offsetDefault;
    }
}
