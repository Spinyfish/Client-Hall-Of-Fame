package niggerlib.mc.protocol.packet.ingame.client.window;

import niggerlib.mc.protocol.packet.MinecraftPacket;
import niggerlib.packetlib.io.NetInput;
import niggerlib.packetlib.io.NetOutput;

import java.io.IOException;

public class ClientPrepareCraftingGridPacket extends MinecraftPacket {
    private int windowId;
    private int recipeId;
    private boolean makeAll;

    @SuppressWarnings("unused")
    private ClientPrepareCraftingGridPacket() {
    }

    public ClientPrepareCraftingGridPacket(int windowId, int recipeId, boolean makeAll) {
        this.windowId = windowId;
        this.recipeId = recipeId;
        this.makeAll = makeAll;
    }

    public int getWindowId() {
        return this.windowId;
    }

    public int getRecipeId() {
        return this.recipeId;
    }

    public boolean doesMakeAll() {
        return makeAll;
    }

    @Override
    public void read(NetInput in) throws IOException {
        this.windowId = in.readByte();
        this.recipeId = in.readVarInt();
        this.makeAll = in.readBoolean();
    }

    @Override
    public void write(NetOutput out) throws IOException {
        out.writeByte(this.windowId);
        out.writeVarInt(this.recipeId);
        out.writeBoolean(this.makeAll);
    }
}
