package net.weever.rotp_harvest.network.s2c;

import com.github.standobyte.jojo.client.ClientUtil;
import com.github.standobyte.jojo.network.packets.IModPacketHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.weever.rotp_harvest.capability.LivingUtilCapProvider;
import net.weever.rotp_harvest.entity.stand.harvest.HarvestMainEntity;

import java.util.function.Supplier;

public class TrSyncIntValues {
    private final PacketField packetField;
    private final int entityId;
    private final int intValue;

    public TrSyncIntValues(PacketField packetField, int entityId, int intValue) {
        this.packetField = packetField;
        this.entityId = entityId;
        this.intValue = intValue;
    }

    public static TrSyncIntValues setCarryUpHarvest(int entityId, int intValue) {
        return new TrSyncIntValues(PacketField.CARRY_UP, entityId, intValue);
    }

    public enum PacketField {
        CARRY_UP
    }

    public static class Handler implements IModPacketHandler<TrSyncIntValues> {
        @Override
        public void encode(TrSyncIntValues msg, PacketBuffer buf) {
            buf.writeEnum(msg.packetField);
            buf.writeInt(msg.entityId);
            buf.writeInt(msg.intValue);
        }

        @Override
        public TrSyncIntValues decode(PacketBuffer buf) {
            PacketField field = buf.readEnum(PacketField.class);
            int entityId = buf.readInt();
            int intValue = buf.readInt();
            return new TrSyncIntValues(field, entityId, intValue);
        }

        @Override
        public void handle(TrSyncIntValues msg, Supplier<NetworkEvent.Context> ctx) {
            Entity entity = ClientUtil.getEntityById(msg.entityId);
            if (entity != null) {
                entity.getCapability(LivingUtilCapProvider.CAPABILITY).ifPresent(cap -> {
                    switch (msg.packetField) {
                        case CARRY_UP:
                            Entity harvest = ClientUtil.getEntityById(msg.intValue);
                            if (harvest instanceof HarvestMainEntity) {
                                cap.setCarryUpHarvest((HarvestMainEntity) harvest);
                            }
                            break;
                    }
                });
            }
        }

        @Override
        public Class<TrSyncIntValues> getPacketClass() {
            return TrSyncIntValues.class;
        }
    }
}
