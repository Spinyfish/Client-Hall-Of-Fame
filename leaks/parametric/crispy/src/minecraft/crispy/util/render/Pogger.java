package crispy.util.render;


import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.OldServerPinger;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Pogger {

    public boolean done;
    public ServerData serverData;
    private boolean failed;

    public Pogger(String address) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    OldServerPinger oldServerPinger = new OldServerPinger();
                    ServerData data = new ServerData("Grief", address + ":25565");
                    serverData = data;
                    oldServerPinger.ping(data);
                    System.out.println("Ping successful: " + address + ":" + "25565");
                    GuiServerFinder.foundServers++;
                    GuiMultiplayer.savedServerList.addServerData(serverData);
                    GuiMultiplayer.savedServerList.saveServerList();
                    GuiMultiplayer.serverListSelector.setSelectedSlotIndex(-1);
                    GuiMultiplayer.serverListSelector.func_148195_a(GuiMultiplayer.savedServerList);
                    done = true;
                } catch (UnknownHostException e) {

                    System.out.println("Unknown host: " + address + ":" + "25565");
                    failed = true;

                } catch (Exception e2) {
                    System.out.println("Ping failed: " + address + ":" + "25565");
                    failed = true;
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();

    }

    public static byte[] createHandshakeMessage(String host, int port) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        DataOutputStream handshake = new DataOutputStream(buffer);
        handshake.writeByte(0x00); //packet id for handshake
        writeVarInt(handshake, 4); //protocol version
        writeString(handshake, host, StandardCharsets.UTF_8);
        handshake.writeShort(port); //port
        writeVarInt(handshake, 1); //state (1 for handshake)

        return buffer.toByteArray();
    }

    public static void writeString(DataOutputStream out, String string, Charset charset) throws IOException {
        byte[] bytes = string.getBytes(charset);
        writeVarInt(out, bytes.length);
        out.write(bytes);
    }

    public static void writeVarInt(DataOutputStream out, int paramInt) throws IOException {
        while (true) {
            if ((paramInt & 0xFFFFFF80) == 0) {
                out.writeByte(paramInt);
                return;
            }

            out.writeByte(paramInt & 0x7F | 0x80);
            paramInt >>>= 7;
        }
    }

    public static int readVarInt(DataInputStream in) throws IOException {
        int i = 0;
        int j = 0;
        while (true) {
            int k = in.readByte();
            i |= (k & 0x7F) << j++ * 7;
            if (j > 5) throw new RuntimeException("VarInt too big");
            if ((k & 0x80) != 128) break;
        }
        return i;
    }

    public boolean isStillPinging() {
        return !done;
    }

    public boolean isWorking() {
        return !failed;
    }


}
