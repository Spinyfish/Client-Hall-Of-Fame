package net.minecraft.network.play.server;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

import java.io.IOException;

public class S2EPacketCloseWindow implements Packet<INetHandlerPlayClient>
{
    private int windowId;

    public S2EPacketCloseWindow()
    {
    }

    public S2EPacketCloseWindow(int windowIdIn)
    {
        this.windowId = windowIdIn;
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler)
    {
        if (!(Minecraft.getMinecraft().currentScreen instanceof GuiChat))
            handler.handleCloseWindow(this);
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.windowId = buf.readUnsignedByte();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeByte(this.windowId);
    }


    @Override
    public int getID() {
        return 28;
    }

}
