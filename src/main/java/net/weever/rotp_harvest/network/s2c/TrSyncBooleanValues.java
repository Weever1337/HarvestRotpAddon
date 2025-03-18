package net.weever.rotp_harvest.network.s2c;

import com.github.standobyte.jojo.client.ClientUtil;
import com.github.standobyte.jojo.network.packets.IModPacketHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.weever.rotp_harvest.capability.LivingUtilCapProvider;

import java.util.Objects;
import java.util.function.Supplier;

public class TrSyncBooleanValues {
    private final PacketField packetField;
    private final int entityId;
    private final boolean booleanValue;

    public TrSyncBooleanValues(PacketField packetField, int entityId, boolean booleanValue) {
        this.packetField = packetField;
        this.entityId = entityId;
        this.booleanValue = booleanValue;
    }

    public static TrSyncBooleanValues setCloser(int entityId, boolean isCloser) {
        return new TrSyncBooleanValues(PacketField.CLOSER, entityId, isCloser);
    }

    public enum PacketField {
        CLOSER
    }

    public static class Handler implements IModPacketHandler<TrSyncBooleanValues> {
        @Override
        public void encode(TrSyncBooleanValues msg, PacketBuffer buf) {
            buf.writeEnum(msg.packetField);
            buf.writeInt(msg.entityId);
            buf.writeBoolean(msg.booleanValue);
        }

        @Override
        public TrSyncBooleanValues decode(PacketBuffer buf) {
            PacketField field = buf.readEnum(PacketField.class);
            int entityId = buf.readInt();
            boolean booleanValue = buf.readBoolean();
            return new TrSyncBooleanValues(field, entityId, booleanValue);
        }

        @Override
        public void handle(TrSyncBooleanValues msg, Supplier<NetworkEvent.Context> ctx) {
            Entity entity = ClientUtil.getEntityById(msg.entityId);
            if (entity != null) {
                entity.getCapability(LivingUtilCapProvider.CAPABILITY).ifPresent(cap -> {
                    if (Objects.requireNonNull(msg.packetField) == PacketField.CLOSER) {
                        cap.setCloser(msg.booleanValue);
                    }
                });
            }
        }

        @Override
        public Class<TrSyncBooleanValues> getPacketClass() {
            return TrSyncBooleanValues.class;
        }
    }
}
