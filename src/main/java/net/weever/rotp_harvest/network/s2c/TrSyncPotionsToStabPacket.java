package net.weever.rotp_harvest.network.s2c;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.github.standobyte.jojo.client.ClientUtil;
import com.github.standobyte.jojo.network.packets.IModPacketHandler;

import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraftforge.fml.network.NetworkEvent;
import net.weever.rotp_harvest.entity.stand.harvest.HarvestMainEntity;

public class TrSyncPotionsToStabPacket {
    private final int harvestId;
    private final List<?> listValue;

    public TrSyncPotionsToStabPacket(int harvestId, List<?> listValue) {
        this.harvestId = harvestId;
        this.listValue = listValue;
    }

    public static TrSyncPotionsToStabPacket setControlledHarvests(int harvestId, List<EffectInstance> effects) {
        return new TrSyncPotionsToStabPacket(harvestId, effects);
    }

    public static class Handler implements IModPacketHandler<TrSyncPotionsToStabPacket> {
        @Override
        public void encode(TrSyncPotionsToStabPacket msg, PacketBuffer buf) {
            buf.writeInt(msg.harvestId);
            buf.writeInt(msg.listValue.size());
            for (EffectInstance effectInstance : (List<EffectInstance>) msg.listValue) {
            	Effect effect = effectInstance.getEffect();
                buf.writeInt(Effect.getId(effect));
                buf.writeInt(effectInstance.getDuration());
                buf.writeInt(effectInstance.getAmplifier());
                buf.writeBoolean(effectInstance.isAmbient());
                buf.writeBoolean(effectInstance.isVisible());
                buf.writeBoolean(effect.isBeneficial());
            }
        }

        @Override
        public TrSyncPotionsToStabPacket decode(PacketBuffer buf) {
            int harvestId = buf.readInt();
            int i = buf.readInt();
            List<EffectInstance> listValue = new ArrayList<>();
            for (int j = 0; j < i; j++) {
            	Effect effect = Effect.byId(buf.readInt());
                int duration = buf.readInt();
                int amplifier = buf.readInt();
                boolean ambient = buf.readBoolean();
                boolean showParticles = buf.readBoolean();
                boolean beneficial = buf.readBoolean();
                listValue.add(new EffectInstance(effect, duration, amplifier, ambient, showParticles, beneficial));
            }
            return new TrSyncPotionsToStabPacket(harvestId, listValue);
        }

        @Override
        public void handle(TrSyncPotionsToStabPacket msg, Supplier<NetworkEvent.Context> ctx) {
            Entity entity = ClientUtil.getEntityById(msg.harvestId);
            if (entity instanceof HarvestMainEntity) {
            	((HarvestMainEntity) entity).setPotionsToStab((List<EffectInstance>) msg.listValue);
            }
        }

        @Override
        public Class<TrSyncPotionsToStabPacket> getPacketClass() {
            return TrSyncPotionsToStabPacket.class;
        }
    }
}
