package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class S20PacketEntityProperties implements Packet<INetHandlerPlayClient> {
    private int entityId;
    private final List<S20PacketEntityProperties.Snapshot> field_149444_b = Lists.newArrayList();

    public S20PacketEntityProperties() {
    }

    public S20PacketEntityProperties(final int entityIdIn, final Collection<IAttributeInstance> p_i45236_2_) {
        this.entityId = entityIdIn;

        for (final IAttributeInstance iattributeinstance : p_i45236_2_) {
            this.field_149444_b.add(new S20PacketEntityProperties.Snapshot(iattributeinstance.getAttribute().getAttributeUnlocalizedName(), iattributeinstance.getBaseValue(), iattributeinstance.func_111122_c()));
        }
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(final PacketBuffer buf) throws IOException {
        this.entityId = buf.readVarIntFromBuffer();
        final int i = buf.readInt();

        for (int j = 0; j < i; ++j) {
            final String s = buf.readStringFromBuffer(64);
            final double d0 = buf.readDouble();
            final List<AttributeModifier> list = Lists.newArrayList();
            final int k = buf.readVarIntFromBuffer();

            for (int l = 0; l < k; ++l) {
                final UUID uuid = buf.readUuid();
                list.add(new AttributeModifier(uuid, "Unknown synced attribute modifier", buf.readDouble(), buf.readByte()));
            }

            this.field_149444_b.add(new S20PacketEntityProperties.Snapshot(s, d0, list));
        }
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(final PacketBuffer buf) throws IOException {
        buf.writeVarIntToBuffer(this.entityId);
        buf.writeInt(this.field_149444_b.size());

        for (final S20PacketEntityProperties.Snapshot s20packetentityproperties$snapshot : this.field_149444_b) {
            buf.writeString(s20packetentityproperties$snapshot.func_151409_a());
            buf.writeDouble(s20packetentityproperties$snapshot.func_151410_b());
            buf.writeVarIntToBuffer(s20packetentityproperties$snapshot.func_151408_c().size());

            for (final AttributeModifier attributemodifier : s20packetentityproperties$snapshot.func_151408_c()) {
                buf.writeUuid(attributemodifier.getID());
                buf.writeDouble(attributemodifier.getAmount());
                buf.writeByte(attributemodifier.getOperation());
            }
        }
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(final INetHandlerPlayClient handler) {
        handler.handleEntityProperties(this);
    }

    public int getEntityId() {
        return this.entityId;
    }

    public List<S20PacketEntityProperties.Snapshot> func_149441_d() {
        return this.field_149444_b;
    }

    public class Snapshot {
        private final String field_151412_b;
        private final double field_151413_c;
        private final Collection<AttributeModifier> field_151411_d;

        public Snapshot(final String p_i45235_2_, final double p_i45235_3_, final Collection<AttributeModifier> p_i45235_5_) {
            this.field_151412_b = p_i45235_2_;
            this.field_151413_c = p_i45235_3_;
            this.field_151411_d = p_i45235_5_;
        }

        public String func_151409_a() {
            return this.field_151412_b;
        }

        public double func_151410_b() {
            return this.field_151413_c;
        }

        public Collection<AttributeModifier> func_151408_c() {
            return this.field_151411_d;
        }
    }
}
