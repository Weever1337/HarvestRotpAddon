package net.weever.rotp_harvest.config;

import net.weever.rotp_harvest.HarvestConfig;

public class SettingsConfig {
    public static int getHarvestSummonInt(boolean isClientSide) {
        return HarvestConfig.getCommonConfigInstance(isClientSide).HarvestSummonInt.get();
    }
}
