package net.weever.rotp_harvest.network.s2c;

import com.github.standobyte.jojo.client.ClientUtil;
import com.github.standobyte.jojo.network.packets.IModPacketHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.weever.rotp_harvest.capability.LivingUtilCapProvider;
import net.weever.rotp_harvest.entity.stand.harvest.HarvestMainEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class TrSyncListValues {
    private final PacketField packetField;
    private final int entityId;
    private final List<?> listValue;

    public TrSyncListValues(PacketField packetField, int entityId, List<?> listValue) {
        this.packetField = packetField;
        this.entityId = entityId;
        this.listValue = listValue;
    }

    public static TrSyncListValues setControlledHarvests(int entityId, List<HarvestMainEntity> controlledHarvests) {
        return new TrSyncListValues(PacketField.HARVESTS, entityId, controlledHarvests);
    }

    public enum PacketField {
        HARVESTS
    }

    public static class Handler implements IModPacketHandler<TrSyncListValues> {
        @Override
        public void encode(TrSyncListValues msg, PacketBuffer buf) {
            buf.writeEnum(msg.packetField);
            buf.writeInt(msg.entityId);
            buf.writeInt(msg.listValue.size());
            if (msg.packetField == PacketField.HARVESTS) {
                for (HarvestMainEntity harvest : (List<HarvestMainEntity>) msg.listValue) {
                    buf.writeInt(harvest.getId());
                }
            }
        }

        @Override
        public TrSyncListValues decode(PacketBuffer buf) {
            PacketField field = buf.readEnum(PacketField.class);
            int entityId = buf.readInt();
            int i = buf.readInt();
            List<Entity> listValue = new ArrayList<>();
            if (field == PacketField.HARVESTS) {
                for (int j = 0; j < i; j++) {
                    listValue.add(ClientUtil.getEntityById(buf.readInt()));
                }
            }
            return new TrSyncListValues(field, entityId, listValue);
        }

        @Override
        public void handle(TrSyncListValues msg, Supplier<NetworkEvent.Context> ctx) {
            Entity entity = ClientUtil.getEntityById(msg.entityId);
            if (entity != null) {
                entity.getCapability(LivingUtilCapProvider.CAPABILITY).ifPresent(cap -> {
                    switch (msg.packetField) {
                        case HARVESTS:
                            cap.setControllingHarvests((List<HarvestMainEntity>) msg.listValue);
                            break;
                    }
                });
            }
        }

        @Override
        public Class<TrSyncListValues> getPacketClass() {
            return TrSyncListValues.class;
        }
    }
}
