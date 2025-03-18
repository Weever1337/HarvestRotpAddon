package net.weever.rotp_harvest.action.stand;

import com.github.standobyte.jojo.action.ActionConditionResult;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.stand.StandAction;
import com.github.standobyte.jojo.client.standskin.StandSkinsManager;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.util.general.LazySupplier;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.weever.rotp_harvest.capability.LivingUtilCap;
import net.weever.rotp_harvest.capability.LivingUtilCapProvider;
import org.jetbrains.annotations.Nullable;

public class BeCloser extends StandAction {
    private final LazySupplier<ResourceLocation> bentText =
            new LazySupplier<>(() -> makeIconVariant(this, ".not"));

    public BeCloser(Builder builder) {
        super(builder);
    }

    @Override
    protected void perform(World world, LivingEntity user, IStandPower power, ActionTarget target) {
        if (!world.isClientSide()) {
            user.getCapability(LivingUtilCapProvider.CAPABILITY).ifPresent(cap -> {
                cap.setCloser(!cap.isCloser());
            });
        }
    }

    @Override
    public IFormattableTextComponent getTranslatedName(IStandPower power, String key) {
        if (power != null) {
            boolean isCloser = power.getUser().getCapability(LivingUtilCapProvider.CAPABILITY).map(LivingUtilCap::isCloser).orElse(false);
            return new TranslationTextComponent(isCloser ? key + ".not" : key);
        } else {
            return new TranslationTextComponent(key);
        }
    }

    @Override
    public boolean greenSelection(IStandPower power, ActionConditionResult conditionCheck) {
        return power.getUser().getCapability(LivingUtilCapProvider.CAPABILITY).map(LivingUtilCap::isCloser).orElse(false);
    }

    @Override
    public ResourceLocation getIconTexture(@Nullable IStandPower power) {
        if (power != null) {
            boolean isCloser = power.getUser().getCapability(LivingUtilCapProvider.CAPABILITY).map(LivingUtilCap::isCloser).orElse(false);
            return isCloser ? StandSkinsManager.getInstance() != null ? (StandSkinsManager.getInstance().getRemappedResPath(manager -> manager
                            .getStandSkin(power.getStandInstance().get()), bentText.get())) : bentText.get() : super.getIconTexture(power);
        } else {
            return super.getIconTexture(power);
        }
    }
}
