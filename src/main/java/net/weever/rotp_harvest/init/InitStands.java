package net.weever.rotp_harvest.init;

import com.github.standobyte.jojo.action.Action;
import com.github.standobyte.jojo.action.stand.StandAction;
import com.github.standobyte.jojo.entity.stand.StandEntityType;
import com.github.standobyte.jojo.init.power.stand.EntityStandRegistryObject;
import com.github.standobyte.jojo.power.impl.stand.stats.StandStats;
import com.github.standobyte.jojo.power.impl.stand.type.StandType;
import com.github.standobyte.jojo.util.mod.StoryPart;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.weever.rotp_harvest.HarvestAddon;
import net.weever.rotp_harvest.action.stand.*;
import net.weever.rotp_harvest.entity.stand.stands.HarvestStandEntity;
import net.weever.rotp_harvest.power.impl.stand.type.HarvestStandType;

public class InitStands {
    @SuppressWarnings("unchecked")
    public static final DeferredRegister<Action<?>> ACTIONS = DeferredRegister.create(
            (Class<Action<?>>) ((Class<?>) Action.class), HarvestAddon.MOD_ID);
    @SuppressWarnings("unchecked")
    public static final DeferredRegister<StandType<?>> STANDS = DeferredRegister.create(
            (Class<StandType<?>>) ((Class<?>) StandType.class), HarvestAddon.MOD_ID);

    // ======================================== Harvest ========================================
    public static final RegistryObject<StandAction> SET_TARGET = ACTIONS.register("set_target",
            () -> new SetTarget(new StandAction.Builder()
                    .autoSummonStand()
            ));

    public static final RegistryObject<StandAction> FORGET_TARGET = ACTIONS.register("forget_target",
            () -> new ForgetTarget(new StandAction.Builder()
                    .autoSummonStand()
            ));

    public static final RegistryObject<StandAction> GIVE_A_POTION = ACTIONS.register("give_a_potion",
            () -> new GiveAPotion(new StandAction.Builder()
                    .holdToFire(25, false)
            ));

    public static final RegistryObject<StandAction> REMOVE_A_POTION = ACTIONS.register("remove_a_potion",
            () -> new RemoveAPotion(new StandAction.Builder()
                    .shiftVariationOf(GIVE_A_POTION)
                    .holdToFire(25, false)
            ));

    public static final RegistryObject<StandAction> GO_TO_THIS_PLACE = ACTIONS.register("go_to_this_place",
            () -> new GoToThisPlace(new StandAction.Builder()
                    .autoSummonStand()
            ));

    public static final RegistryObject<StandAction> SEARCH = ACTIONS.register("search",
            () -> new Search(new StandAction.Builder()
                    .holdToFire(25, false)
                    .autoSummonStand()
            ));

    public static final RegistryObject<StandAction> STAY_WITH = ACTIONS.register("stay_with",
            () -> new StayWith(new StandAction.Builder()
                    .autoSummonStand()
            ));

    public static final RegistryObject<StandAction> BE_CLOSER = ACTIONS.register("be_closer",
            () -> new BeCloser(new StandAction.Builder()
                    .autoSummonStand()
            ));

    public static final RegistryObject<StandAction> CONTROL_HARVEST = ACTIONS.register("control_harvest",
            () -> new ControlHarvest(new StandAction.Builder()
                    .autoSummonStand()
            ));

    public static final RegistryObject<StandAction> CARRY_UP = ACTIONS.register("carry_up",
            () -> new CarryUp(new StandAction.Builder()
                    .holdToFire(2, false)
                    .autoSummonStand()
            ));

    public static final EntityStandRegistryObject<HarvestStandType<StandStats>, StandEntityType<HarvestStandEntity>> HARVEST =
            new EntityStandRegistryObject<>("harvest_stand",
                    STANDS,
                    () -> new HarvestStandType.Builder<>()
                            .color(0xEACC74)
                            .storyPartName(StoryPart.DIAMOND_IS_UNBREAKABLE.getName())
                            .leftClickHotbar(
                                    SET_TARGET.get(),
                                    FORGET_TARGET.get(),
                                    GIVE_A_POTION.get()
                            )
                            .rightClickHotbar(
                                    GO_TO_THIS_PLACE.get(),
                                    STAY_WITH.get(),
                                    SEARCH.get(),
                                    BE_CLOSER.get(),
                                    CONTROL_HARVEST.get()
//                                    CARRY_UP.get()
                            )
                            .defaultStats(StandStats.class, new StandStats.Builder()
                                    .power(2.0)
                                    .speed(12.0)
                                    .range(150, 200)
                                    .durability(14)
                                    .precision(2)
                                    .randomWeight(2f)
                                    .build()
                            )
                            .disableStandLeap()
                            .build(),
                    InitEntities.ENTITIES,
                    () -> new StandEntityType<>(HarvestStandEntity::new, 0F, 0F)
                        // .summonSound(InitSounds.SUMMON)
                        // .unsummonSound(InitSounds.UNSUMMON)
                        )
                    .withDefaultStandAttributes();

}
