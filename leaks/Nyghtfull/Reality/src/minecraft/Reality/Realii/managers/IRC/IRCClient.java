package Reality.Realii.managers.IRC;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import Reality.Realii.Client;
import Reality.Realii.login.login12;
import Reality.Realii.managers.ModuleManager;
import Reality.Realii.mods.modules.misc.IRC;
import Reality.Realii.utils.cheats.player.Helper;
import Reality.server.packets.client.C00PacketConnect;
import Realyy.encryption.EncryptionUtils;
import checkerv2.idk12;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.*;

import Moses.server.packets12.ClientPacket;
import Moses.server.packets12.PacketType;
import Moses.server.packets12.PacketUtil;
import Moses.server.packets12.ServerPacket;
import Nyghtfull.serverdate.User;
import Realioty.server.utile.MyBufferedReader;
import Realioty.server.utile.MyPrintWriter;

import java.io.*;
import java.net.Socket;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class IRCClient {
    public static User user;
    static Socket socket;
    private static InputStream input;
    public static MyBufferedReader reader;
    public static MyPrintWriter writer;
    private static final char[] DIGITS_HEX = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static String toHex(String str) {
        byte[] data = str.getBytes();
        int outLength = data.length;
        char[] out = new char[outLength << 1];
        for (int i = 0, j = 0; i < outLength; i++) {
            out[j++] = DIGITS_HEX[(0xF0 & data[i]) >>> 4];
            out[j++] = DIGITS_HEX[0x0F & data[i]];
        }
        return new String(out);
    }

    public static String genKey() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd:HH:mm");
        String date = sdf.format(new Date());
        return toHex(date);
    }

    public static String decode(String content) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, UnsupportedEncodingException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        return EncryptionUtils.decryptByHexString(content, genKey()).trim();
    }

    public static void Start(String username, String password, String hwid, String ip, int port) {
        try {
            socket = new Socket(ip, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            input = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        writer = null;
        try {
            writer = new MyPrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            reader = new MyBufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        addPacket(writer, new C00PacketConnect(new User(username, password, hwid)));

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        handle();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public static void addPacket(PrintWriter writer, ClientPacket sp) {
        String s = PacketUtil.getClientJson(sp);
        String encode = EncryptionUtils.encryptIntoHexString(s, genKey());
        writer.println(encode);
        writer.flush();
    }

    public static void handle() throws IOException {
        String msg = reader.readLine();
        if (msg != null) {
            ServerPacket sp = null;
            try {
                sp = PacketUtil.parsePacketServer(decode(msg), ServerPacket.class);
            }catch (Exception e){
                System.out.println("yes " + e.getMessage());
                return;
            }

            if (sp.packetType.equals(PacketType.S03))
                if (ModuleManager.getModuleByClass(IRC.class).isEnabled()) {
                    Helper.sendMessageWithoutPrefix(sp.content);
                }
            if (sp.packetType.name().equals("S02")) {
                System.exit(0);
                JOptionPane.showMessageDialog(null, sp.content);
            } else if (sp.packetType.equals(PacketType.S01)) {
                user = new User(Client.username, Client.password, idk12.getHWID());
                if (Minecraft.getMinecraft().currentScreen instanceof login12) {
                    Minecraft.getMinecraft().displayGuiScreen(new GuiMainMenu());
                }
            }
        }
    }
}
