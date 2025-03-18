package net.weever.rotp_harvest.util;

import com.github.standobyte.jojo.client.standskin.StandSkinsManager;
import com.github.standobyte.jojo.init.ModStatusEffects;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.util.mc.MCUtil;
import com.github.standobyte.jojo.util.mod.JojoModUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.weever.rotp_harvest.HarvestAddon;
import net.weever.rotp_harvest.capability.LivingUtilCap;
import net.weever.rotp_harvest.capability.LivingUtilCapProvider;
import net.weever.rotp_harvest.entity.stand.harvest.HarvestMainEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.UUID;
import java.util.function.Predicate;

public class HarvestUtil {
    public static final DamageSource punchSource = new DamageSource("rotp_harvest_punch").bypassArmor();
    public static final DamageSource stabSource = new DamageSource("rotp_harvest_stab").bypassArmor();
    public static final DamageSource biteSource = new DamageSource("rotp_harvest_bite").bypassArmor();
    private static final ResourceLocation harvestShader = new ResourceLocation(HarvestAddon.MOD_ID, "shaders/post/harvest.json");

    public static void punchEntity(HarvestMainEntity harvest, LivingEntity livingEntity) {
        if (livingEntity.level.isClientSide()) return;
        
        livingEntity.hurt(punchSource, 0.2f);
        endOfAttack(harvest, livingEntity);
    }

    public static void stabEntity(HarvestMainEntity harvest, LivingEntity livingEntity) {
        if (livingEntity.level.isClientSide()) return;
        
        for (EffectInstance effect : harvest.getPotionsToStab()) {
            int duration = effect.getDuration();
            int amplifier = effect.getAmplifier();

            EffectInstance existingEffect = livingEntity.getEffect(effect.getEffect());
            if (existingEffect != null) {
                duration += existingEffect.getDuration();
                amplifier = Math.min(existingEffect.getAmplifier() + 1, 5);
            }
            livingEntity.addEffect(new EffectInstance(effect.getEffect(), duration, amplifier, false, false, true));
        }
        harvest.clearPotionsToStab();
        livingEntity.hurt(stabSource, 0.3f);

        endOfAttack(harvest, livingEntity);
    }

    public static void biteEntity(HarvestMainEntity harvest, LivingEntity livingEntity) {
        if (livingEntity.level.isClientSide()) return;
        
        livingEntity.hurt(biteSource, 0.5f);
        int duration = 60;
        int amplifier = 0;

        EffectInstance existingEffect = livingEntity.getEffect(ModStatusEffects.BLEEDING.get());
        if (existingEffect != null) {
            duration += existingEffect.getDuration();
            amplifier = Math.min(existingEffect.getAmplifier() + 1, 3);
        }
        livingEntity.addEffect(new EffectInstance(ModStatusEffects.BLEEDING.get(), duration, amplifier, false, false, true));
        endOfAttack(harvest, livingEntity);
    }

    private static void endOfAttack(HarvestMainEntity harvest, LivingEntity livingEntity) {
        if (harvest.getOwner() == null) return;

        LivingEntity owner = harvest.getOwner();
        IStandPower.getStandPowerOptional(owner).ifPresent(power -> {
            power.getResolveCounter().addResolveValue(5f, owner);
        });

        if (!livingEntity.isAlive() && !livingEntity.level.isClientSide()) {
            owner.getCapability(LivingUtilCapProvider.CAPABILITY).ifPresent(cap -> {
                cap.setStatus(LivingUtilCap.HarvestStatus.STAYING);
            });
        }
    }

    @Nullable
    // Original code from Bad Company: https://github.com/MakutaZeml/Rotp-Bad-Company/blob/main/src/main/java/com/zeml/rotp_zbc/util/AddonUtil.java#L24C5-L27C6
    public static LivingEntity getEntityFromUUID(UUID uuid, Entity entity) {
        return entity.level.getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(Double.MAX_VALUE), living -> living.getUUID() == uuid || living.getUUID().equals(uuid)).stream().findFirst().orElse(null);
    }

    public static LivingEntity getEntityByRayTrace(LivingEntity user, int maxDistance, @Nullable Predicate<LivingEntity> entityFilter) {
        RayTraceResult rayTraceResult = JojoModUtil.rayTrace(
                user.getEyePosition(1.0F),
                user.getLookAngle(),
                maxDistance,
                user.level,
                user,
                entity -> !entity.is(user),
                0,
                0
        );

        if (rayTraceResult.getType() != RayTraceResult.Type.ENTITY) {
            return user;
        }

        Entity entity = ((EntityRayTraceResult) rayTraceResult).getEntity();
        if (!(entity instanceof LivingEntity)) {
            return user;
        }

        LivingEntity targetEntity = (LivingEntity) entity;
        if (entityFilter == null || entityFilter.test(targetEntity)) {
            return targetEntity;
        }

        return user;
    }

    @Nullable
    public static BlockRayTraceResult getBlockByRayTrace(LivingEntity user, int maxDistance) {
        RayTraceResult rayTraceResult = JojoModUtil.rayTrace(
                user.getEyePosition(1.0F),
                user.getLookAngle(),
                maxDistance,
                user.level,
                user,
                entity -> !entity.is(user),
                0,
                0
        );

        if (rayTraceResult.getType() != RayTraceResult.Type.BLOCK) {
            return null;
        }

        return ((BlockRayTraceResult) rayTraceResult);
    }


    public static void controlHarvest(LivingEntity user, @Nullable HarvestMainEntity harvest) {
        if (user.level.isClientSide()) {
            Minecraft minecraft = Minecraft.getInstance();
            Entity cameraEntity = minecraft.getCameraEntity();
            if (harvest != null) {
                if (cameraEntity == user) {
                    minecraft.setCameraEntity(harvest);
                } else {
                    minecraft.setCameraEntity(user);
                }
            } else if (cameraEntity instanceof HarvestMainEntity) {
                minecraft.setCameraEntity(user);
            }
        }
    }

    @Nullable
    public static HarvestMainEntity findNearestHarvest(LivingEntity user, int range) {
        return MCUtil.entitiesAround(HarvestMainEntity.class, user, range, false, harvest -> harvest.getOwner() == user).stream().min(Comparator.comparingDouble(user::distanceToSqr)).orElse(null);
    }
    
    public static ItemInHandData getItemInHand(LivingEntity livingEntity, Item item) {
        ItemStack itemStack;
        Hand mainHand;
        if (livingEntity.getMainHandItem().getItem() == item) {
            itemStack = livingEntity.getMainHandItem();
            mainHand = Hand.MAIN_HAND;
        } else if (livingEntity.getOffhandItem().getItem() == item) {
            itemStack = livingEntity.getOffhandItem();
            mainHand = Hand.OFF_HAND;
        } else {
            itemStack = null;
            mainHand = null;
        }
        
        return new ItemInHandData(itemStack, mainHand);
    }
    
    public static class ItemInHandData {
    	public final ItemStack itemStack;
    	@Nullable public final Hand hand;

        private ItemInHandData(ItemStack itemStack, @Nullable Hand hand) {
    		this.itemStack = itemStack;
    		this.hand = hand;
    	}
    }

    public static ResourceLocation getShader(@Nullable IStandPower power) {
        if (power == null) return harvestShader;
        return StandSkinsManager.getInstance().getRemappedResPath(manager -> manager.getStandSkin(power.getStandInstance().get()), harvestShader);
    }
}
