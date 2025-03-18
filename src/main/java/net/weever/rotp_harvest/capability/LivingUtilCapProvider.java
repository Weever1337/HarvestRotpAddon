package net.weever.rotp_harvest.capability;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class LivingUtilCapProvider implements ICapabilitySerializable<INBT> {
    @CapabilityInject(LivingUtilCap.class)
    public static Capability<LivingUtilCap> CAPABILITY = null;
    private final LazyOptional<LivingUtilCap> instance;

    public LivingUtilCapProvider(LivingEntity livingEntity) {
        instance = LazyOptional.of(() -> new LivingUtilCap(livingEntity));
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        return CAPABILITY.orEmpty(cap, instance);
    }

    @Override
    public INBT serializeNBT() {
        return CAPABILITY.getStorage().writeNBT(CAPABILITY, instance.orElseThrow(
                () -> new IllegalArgumentException("Entity capability LazyOptional is not attached.")), null);
    }

    @Override
    public void deserializeNBT(INBT nbt) {
        CAPABILITY.getStorage().readNBT(CAPABILITY, instance.orElseThrow(
                () -> new IllegalArgumentException("Entity capability LazyOptional is not attached.")), null, nbt);
    }
}
