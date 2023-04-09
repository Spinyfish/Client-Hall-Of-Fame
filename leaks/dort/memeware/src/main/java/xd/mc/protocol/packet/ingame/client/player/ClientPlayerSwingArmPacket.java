package niggerlib.mc.protocol.packet.ingame.client.player;

import niggerlib.mc.protocol.data.MagicValues;
import niggerlib.mc.protocol.data.game.entity.player.Hand;
import niggerlib.mc.protocol.packet.MinecraftPacket;
import niggerlib.packetlib.io.NetInput;
import niggerlib.packetlib.io.NetOutput;

import java.io.IOException;

public class ClientPlayerSwingArmPacket extends MinecraftPacket {
    private Hand hand;

    @SuppressWarnings("unused")
    private ClientPlayerSwingArmPacket() {
    }

    public ClientPlayerSwingArmPacket(Hand hand) {
        this.hand = hand;
    }

    public Hand getHand() {
        return this.hand;
    }

    @Override
    public void read(NetInput in) throws IOException {
        this.hand = MagicValues.key(Hand.class, in.readVarInt());
    }

    @Override
    public void write(NetOutput out) throws IOException {
        out.writeVarInt(MagicValues.value(Integer.class, this.hand));
    }
}
