package net.weever.rotp_harvest.entity.stand.harvest;

import com.github.standobyte.jojo.client.ClientUtil;
import com.github.standobyte.jojo.init.ModStatusEffects;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.power.impl.stand.StandUtil;
import com.github.standobyte.jojo.power.impl.stand.type.StandType;
import com.github.standobyte.jojo.util.mc.MCUtil;
import com.github.standobyte.jojo.util.mc.damage.IModdedDamageSource;
import com.github.standobyte.jojo.util.mc.damage.IStandDamageSource;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.ClimberPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.weever.rotp_harvest.capability.LivingUtilCap;
import net.weever.rotp_harvest.capability.LivingUtilCapProvider;
import net.weever.rotp_harvest.entity.ai.goal.*;
import net.weever.rotp_harvest.init.InitEntities;
import net.weever.rotp_harvest.init.InitSounds;
import net.weever.rotp_harvest.init.InitStands;
import net.weever.rotp_harvest.network.AddonPackets;
import net.weever.rotp_harvest.network.s2c.TrSyncPotionsToStabPacket;
import net.weever.rotp_harvest.util.HarvestUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class HarvestMainEntity extends TameableEntity implements IRideable {
    private static final DataParameter<Boolean> CLIMBING = EntityDataManager.defineId(HarvestMainEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<String> STATUS = EntityDataManager.defineId(HarvestMainEntity.class, DataSerializers.STRING);
    private static final DataParameter<Boolean> GOINGTO = EntityDataManager.defineId(HarvestMainEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> CLOSER = EntityDataManager.defineId(HarvestMainEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> CARRYING_UP = EntityDataManager.defineId(HarvestMainEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Optional<UUID>> STAY_WITH = EntityDataManager.defineId(HarvestMainEntity.class, DataSerializers.OPTIONAL_UUID);
    private static final DataParameter<Integer> DATA_BOOST_TIME = EntityDataManager.defineId(HarvestMainEntity.class, DataSerializers.INT);
    private static final DataParameter<Boolean> DATA_SADDLE_ID = EntityDataManager.defineId(HarvestMainEntity.class, DataSerializers.BOOLEAN);
    private final BoostHelper steering;

    public final HarvestActions actions = new HarvestActions(this);
    private List<EffectInstance> potionsToStab = new ArrayList<>();

    public HarvestMainEntity(EntityType<? extends TameableEntity> p_i48574_1_, World p_i48574_2_) {
        super(p_i48574_1_, p_i48574_2_);
        this.steering = new BoostHelper(this.getEntityData(), DATA_BOOST_TIME, DATA_SADDLE_ID);
    }

    public HarvestMainEntity(@NotNull World world) {
        super(InitEntities.HARVEST.get(), world);
        this.setNoGravity(false);
        this.steering = new BoostHelper(this.getEntityData(), DATA_BOOST_TIME, DATA_SADDLE_ID);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return TameableEntity.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 12.0D)
                .add(Attributes.ATTACK_DAMAGE, 0.3D)
                .add(Attributes.MOVEMENT_SPEED, 0.4D);
    }

    @Override
    public void onSyncedDataUpdated(@NotNull DataParameter<?> p_184206_1_) {
        if (DATA_BOOST_TIME.equals(p_184206_1_) && this.level.isClientSide) {
            this.steering.onSynced();
        }

        super.onSyncedDataUpdated(p_184206_1_);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CLIMBING, false);
        this.entityData.define(STATUS, LivingUtilCap.HarvestStatus.STAYING.name());
        this.entityData.define(GOINGTO, false);
        this.entityData.define(CLOSER, false);
        this.entityData.define(CARRYING_UP, false);
        this.entityData.define(STAY_WITH, Optional.ofNullable(getOwnerUUID()));
        this.entityData.define(DATA_SADDLE_ID, false);
        this.entityData.define(DATA_BOOST_TIME, 0);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundNBT nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putString("status", this.entityData.get(STATUS));
        nbt.putBoolean("goingto", this.entityData.get(GOINGTO));
        nbt.putBoolean("closer", this.entityData.get(CLOSER));
        nbt.putBoolean("carrying_up", this.entityData.get(CARRYING_UP));
        if (this.getStayWith().isPresent()) {
            nbt.putUUID("stay_with", this.entityData.get(STAY_WITH).get());
        }
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundNBT nbt) {
        super.readAdditionalSaveData(nbt);
        this.entityData.set(STATUS, nbt.getString("status"));
        this.entityData.set(GOINGTO, nbt.getBoolean("goingto"));
        this.entityData.set(CLOSER, nbt.getBoolean("closer"));
        this.entityData.set(CARRYING_UP, nbt.getBoolean("carrying_up"));
        if (nbt.hasUUID("stay_with")) {
            this.entityData.set(STAY_WITH, Optional.of(nbt.getUUID("stay_with")));
        }
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.addGoal(0, new HarvestFindAndCollectItemGoal(this));
        this.goalSelector.addGoal(0, new HarvestCarryUpGoal(this));
        this.goalSelector.addGoal(1, new HarvestTeleportGoal(this));
        this.goalSelector.addGoal(2, new HarvestReworkMeleeCombat(this, 1.0D, 10));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomWalkingGoal(this, .1F));
        this.goalSelector.addGoal(3, new SwimGoal(this));
        this.goalSelector.addGoal(3, new LookAtGoal(this, LivingEntity.class, 20F));
        this.goalSelector.addGoal(3, new LookRandomlyGoal(this));

        this.targetSelector.addGoal(1, new HarvestOwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(1, new HarvestOwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new HarvestNearestAttackableTargetGoal<>(this, MobEntity.class, 15, false, false, target -> target != getOwner() && target.isAlive() && target instanceof IMob, false));
//        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, MobEntity.class, 5, false, false, (p_234199_0_) -> p_234199_0_ != this.getOwner()));
        this.targetSelector.addGoal(2, new HarvestNearestAttackableTargetGoal<>(this, PlayerEntity.class, 15, false, false, entity -> entity.isAlive() && !entity.is(getOwner()), true));
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide()) {
            LivingEntity owner = this.getOwner();
            LivingEntity secondOwner = this.getSecondOwner();
            IStandPower power = getOwnerPower();
            StandType<?> standType = InitStands.HARVEST.getStandType();

            if (owner == null
                    || !owner.isAlive()
                    || HarvestUtil.getEntityFromUUID(owner.getUUID(), this) != null
                    || power == null
                    || power.getType() != standType
                    || !power.isActive()
            ) {
                this.remove();
                return;
            }

            if (secondOwner != null) {
                if (!secondOwner.isAlive() || HarvestUtil.getEntityFromUUID(secondOwner.getUUID(), this) != null) {
                    this.setStayWith(null); // because second owner died/leaved
                }
                if (getTarget() == secondOwner) {
                    this.setTarget(null);
                }
                if (secondOwner instanceof MobEntity) {
                    if (((MobEntity) secondOwner).getTarget() != null) {
                        this.setStatus(LivingUtilCap.HarvestStatus.ATTACKING);
                    } else {
                        this.setStatus(LivingUtilCap.HarvestStatus.STAYING);
                    }
                    if (this.getTarget() != ((MobEntity) secondOwner).getTarget() && ((MobEntity) secondOwner).getTarget() != owner) {
                        this.setTarget(((MobEntity) secondOwner).getTarget());
                    }
                }
            }

            if (getTarget() == getOwner())  {
                this.setTarget(null);
            }

            if (owner.getCapability(LivingUtilCapProvider.CAPABILITY).isPresent()) {
                LivingUtilCap cap = owner.getCapability(LivingUtilCapProvider.CAPABILITY).resolve().get();
                if (this.getStatus() != cap.getStatus() && (secondOwner == null || secondOwner == owner)) {
                    this.setStatus(cap.getStatus());
                }

                if (this.isCloser() != cap.isCloser()) {
                    this.setCloser(cap.isCloser());
                }

                if (cap.getStayWith() == null || !this.getStayWith().isPresent() || this.getStayWith().get() != cap.getStayWith()) {
                    this.setStayWith(cap.getStayWith());
                }

                if (cap.getCarryUpHarvest() != null) {
                    if (owner instanceof PlayerEntity && ((PlayerEntity) owner).getForcedPose() == null) {
                        ((PlayerEntity) owner).setForcedPose(Pose.SWIMMING);
                    }
                } else {
                    if (owner instanceof PlayerEntity && ((PlayerEntity) owner).getForcedPose() != null) {
                        ((PlayerEntity) owner).setForcedPose(null);
                    }
                }

                if (isGoingTo() && !cap.getGoToThisPlace().equals(BlockPos.ZERO) && this.getTarget() == null) {
                    BlockPos blockPos = cap.getGoToThisPlace();
                    this.navigation.moveTo(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1);
                    if (this.distanceToSqr(Vector3d.atCenterOf(blockPos)) < 3) {
                        setGoingTo(false);
                    }
                } else {
                    setGoingTo(false);
                }
            }
            this.setClimbing(this.horizontalCollision);
            actions.tick();
        }
    }


    @Override
    public boolean isInvulnerableTo(@NotNull DamageSource damageSrc) {
        if (damageSrc == DamageSource.OUT_OF_WORLD) {
            return false;
        }

        if (this.is(damageSrc.getEntity())
                || getOwner() != null && getOwner().is(damageSrc.getEntity())
                || getSecondOwner() != null && getSecondOwner().is(damageSrc.getEntity())
                || getOwner() instanceof PlayerEntity && ((PlayerEntity) getOwner()).abilities.invulnerable && !damageSrc.isBypassInvul()
                || damageSrc.isFire() && !level.getGameRules().getBoolean(GameRules.RULE_FIRE_DAMAGE)) {
            return true;
        }

        if (canTakeDamageFrom(damageSrc)) {
            return isInvulnerable();
        }

        return true;
    }

    public boolean canTakeDamageFrom(DamageSource damageSrc) {
        return damageSrc instanceof IStandDamageSource
                || damageSrc instanceof IModdedDamageSource && ((IModdedDamageSource) damageSrc).canHurtStands()
                || damageSrc.getMsgId().contains("stand")
                || damageSrc == DamageSource.ON_FIRE
                || (damageSrc.getEntity() instanceof LivingEntity && ((LivingEntity) damageSrc.getEntity()).hasEffect(ModStatusEffects.INTEGRATED_STAND.get()));
    }

    @Override
    public boolean isAlliedTo(@NotNull Entity entity) {
        if (this.getOwner() != null || this.getSecondOwner() != null) {
            LivingEntity firstOwner = this.getOwner();
            LivingEntity secondOwner = this.getSecondOwner();

            if (getStatus() == LivingUtilCap.HarvestStatus.FINDING_ITEM) {
                return true;
            }

            if (entity == secondOwner || entity == firstOwner) {
                return true;
            }

            if (firstOwner != null) {
                return entity.isAlliedTo(firstOwner);
            }
            if (secondOwner != null) {
                return entity.isAlliedTo(secondOwner);
            }
        }
        return super.isAlliedTo(entity);
    }

    protected @NotNull PathNavigator createNavigation(@NotNull World p_175447_1_) {
        return new ClimberPathNavigator(this, p_175447_1_);
    }

    @Override
    public boolean onClimbable() {
        return this.isClimbing();
    }

    @Override
    public boolean isInvisible() {
        return level.isClientSide() && !ClientUtil.canSeeStands() || super.isInvisible();
    }

    @Override
    public boolean isInvisibleTo(@NotNull PlayerEntity player) {
        return !StandUtil.clStandEntityVisibleTo(player)
                || !StandUtil.playerCanSeeStands(player);
    }

    @Override
    public boolean canBeAffected(EffectInstance p_70687_1_) {
        return false;
    }

    @Override
    public @Nullable AgeableEntity getBreedOffspring(@NotNull ServerWorld serverWorld, @NotNull AgeableEntity ageableEntity) {
        return null;
    }

    @Override
    public boolean canBeRiddenInWater(Entity rider) {
        return false;
    }

    @Override
    public boolean canBeControlledByRider() {
        return this.getControllingPassenger() instanceof PlayerEntity;
    }

    @Override
    public boolean canRide(@NotNull Entity rider) {
        return rider instanceof PlayerEntity;
    }

    @Override
    public boolean shouldRiderSit() {
        return false;
    }

    public boolean isGoingTo() {
        return this.entityData.get(GOINGTO);
    }

    public void setGoingTo(boolean goingTo) {
        this.entityData.set(GOINGTO, goingTo);
    }

    public boolean isCloser() {
        return this.entityData.get(CLOSER);
    }

    public void setCarryingUp(boolean carryingUp) {
        this.entityData.set(CARRYING_UP, carryingUp);
    }

    public boolean isCarryingUp() {
        return this.entityData.get(CARRYING_UP);
    }

    public void setCloser(boolean closer) {
        this.entityData.set(CLOSER, closer);
    }

    public Optional<UUID> getStayWith() {
        return this.entityData.get(STAY_WITH);
    }

    public void setStayWith(UUID uuid) {
        this.entityData.set(STAY_WITH, Optional.ofNullable(uuid));
    }

    public LivingUtilCap.HarvestStatus getStatus() {
        try {
            String statusInString = this.entityData.get(STATUS);
            return LivingUtilCap.HarvestStatus.valueOf(statusInString);
        } catch (IllegalArgumentException e) {
            return LivingUtilCap.HarvestStatus.STAYING;
        }
    }

    public void setStatus(LivingUtilCap.HarvestStatus status) {
        this.entityData.set(STATUS, status.name());
    }

    public boolean isClimbing() {
        return this.entityData.get(CLIMBING); // SpiderEntity ahhh moment
    }

    public void setClimbing(boolean set) {
        this.entityData.set(CLIMBING, set);
    }

    @Nullable
    public IStandPower getOwnerPower() {
        if (getOwner() == null) return null;

        return IStandPower.getStandPowerOptional(getOwner()).orElse(null);
    }

    @Nullable
    public LivingEntity getSecondOwner() {
        LivingEntity owner = null;
        if (this.getStayWith().isPresent()) {
            for (Entity entity : MCUtil.getAllEntities(level)) {
                if (entity instanceof LivingEntity) {
                    if (entity.getUUID() == this.getStayWith().get() || entity.getUUID().equals(this.getStayWith().get())) {
                        owner = (LivingEntity) entity;
                    }
                }
            }
        }
        return owner;
    }

    public LivingEntity getOriginalOwner() {
        return this.getSecondOwner() == null ? this.getOwner() : this.getSecondOwner();
    }
    
    public void addOnePotionToStab(EffectInstance effect) {
    	this.potionsToStab.add(effect);
    	if (!this.level.isClientSide()) {
    		AddonPackets.sendToClientsTracking(new TrSyncPotionsToStabPacket(this.getId(), this.potionsToStab), this);
    	}
    }
    
    public void addAllPotionsToStab(List<EffectInstance> effect) {
    	this.potionsToStab.addAll(effect);
    	if (!this.level.isClientSide()) {
    		AddonPackets.sendToClientsTracking(new TrSyncPotionsToStabPacket(this.getId(), this.potionsToStab), this);
    	}
    }
    
    public void clearPotionsToStab() {
    	this.potionsToStab.clear();
    	if (!this.level.isClientSide()) {
    		AddonPackets.sendToClientsTracking(new TrSyncPotionsToStabPacket(this.getId(), this.potionsToStab), this);
    	}
    }
    
    public void setPotionsToStab(List<EffectInstance> effects) {
    	this.potionsToStab = effects;
    	if (!this.level.isClientSide()) {
    		AddonPackets.sendToClientsTracking(new TrSyncPotionsToStabPacket(this.getId(), this.potionsToStab), this);
    	}
    }
    
    public List<EffectInstance> getPotionsToStab() {
    	return this.potionsToStab;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        if (!this.isUnderWater()) {
            this.playSound(InitSounds.HARVEST_STEP.get(), 0.5F, 1.0F);
        } else {
            super.playStepSound(pos, state);
        }
    }

    @Override
    public boolean canBeLeashed(PlayerEntity p_184652_1_) {
        return false;
    }

    @Override
    protected int getExperienceReward(PlayerEntity p_70693_1_) {
        return 0;
    }

    @Override
    protected boolean shouldDropLoot() {
        return false;
    }

    @Override
    public void travel(Vector3d p_213352_1_) {
        this.travel(this, this.steering, p_213352_1_);
    }

    @Override
    public boolean boost() {
        return true;
    }

    @Override
    public void travelWithInput(Vector3d vector3d) {
        super.travel(vector3d);
    }

    @Override
    public float getSteeringSpeed() {
        return 2;
    }

    public static class HarvestActions {
        private final HarvestMainEntity harvest;
        private int punchCooldown = 0;
        private int stabCooldown = 0;
        private int biteCooldown = 0;

        public HarvestActions(HarvestMainEntity harvest) {
            this.harvest = harvest;
        }

        public void tick() {
            if (punchCooldown > 0) punchCooldown--;
            if (stabCooldown > 0) stabCooldown--;
            if (biteCooldown > 0) biteCooldown--;
        }
        
        public boolean canPunch() {
            return punchCooldown <= 0;
        }

        public boolean canStab() {
            return stabCooldown <= 0 && !harvest.potionsToStab.isEmpty();
        }

        public boolean canBite() {
            return biteCooldown <= 0;
        }

        public void resetPunchCooldown() {
            punchCooldown = 15;
        }

        public void resetStabCooldown() {
            stabCooldown = 100;
        }

        public void resetBiteCooldown() {
            biteCooldown = 45;
        }
    }
}
