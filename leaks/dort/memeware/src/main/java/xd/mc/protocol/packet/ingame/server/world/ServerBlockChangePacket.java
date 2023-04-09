package niggerlib.mc.protocol.packet.ingame.server.world;

import niggerlib.mc.protocol.data.game.world.block.BlockChangeRecord;
import niggerlib.mc.protocol.packet.MinecraftPacket;
import niggerlib.mc.protocol.util.NetUtil;
import niggerlib.packetlib.io.NetInput;
import niggerlib.packetlib.io.NetOutput;

import java.io.IOException;

public class ServerBlockChangePacket extends MinecraftPacket {
    private BlockChangeRecord record;

    @SuppressWarnings("unused")
    private ServerBlockChangePacket() {
    }

    public ServerBlockChangePacket(BlockChangeRecord record) {
        this.record = record;
    }

    public BlockChangeRecord getRecord() {
        return this.record;
    }

    @Override
    public void read(NetInput in) throws IOException {
        this.record = new BlockChangeRecord(NetUtil.readPosition(in), NetUtil.readBlockState(in));
    }

    @Override
    public void write(NetOutput out) throws IOException {
        NetUtil.writePosition(out, this.record.getPosition());
        NetUtil.writeBlockState(out, this.record.getBlock());
    }
}
