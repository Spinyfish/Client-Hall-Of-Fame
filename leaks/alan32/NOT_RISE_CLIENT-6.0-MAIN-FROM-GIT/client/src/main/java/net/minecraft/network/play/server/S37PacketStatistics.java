package net.minecraft.network.play.server;

import com.google.common.collect.Maps;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

public class S37PacketStatistics implements Packet<INetHandlerPlayClient> {
    private Map<StatBase, Integer> field_148976_a;

    public S37PacketStatistics() {
    }

    public S37PacketStatistics(final Map<StatBase, Integer> p_i45173_1_) {
        this.field_148976_a = p_i45173_1_;
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(final INetHandlerPlayClient handler) {
        handler.handleStatistics(this);
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(final PacketBuffer buf) throws IOException {
        final int i = buf.readVarIntFromBuffer();
        this.field_148976_a = Maps.newHashMap();

        for (int j = 0; j < i; ++j) {
            final StatBase statbase = StatList.getOneShotStat(buf.readStringFromBuffer(32767));
            final int k = buf.readVarIntFromBuffer();

            if (statbase != null) {
                this.field_148976_a.put(statbase, Integer.valueOf(k));
            }
        }
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(final PacketBuffer buf) throws IOException {
        buf.writeVarIntToBuffer(this.field_148976_a.size());

        for (final Entry<StatBase, Integer> entry : this.field_148976_a.entrySet()) {
            buf.writeString(entry.getKey().statId);
            buf.writeVarIntToBuffer(entry.getValue().intValue());
        }
    }

    public Map<StatBase, Integer> func_148974_c() {
        return this.field_148976_a;
    }
}
