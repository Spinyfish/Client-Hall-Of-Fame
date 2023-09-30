/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.json.JSONArray
 *  org.json.JSONObject
 */
package wtf.monsoon.misc.server;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;
import wtf.monsoon.Wrapper;
import wtf.monsoon.misc.server.packet.MPacket;
import wtf.monsoon.misc.server.packet.impl.MPacketLogin;

public class MonsoonServerConnection {
    private String address;
    private int port;
    private Socket socket;
    private String loginToken;
    private Map<String, String> onlineMonsoonUsers;
    private String motd;

    public MonsoonServerConnection(String address, int port) {
        this.setAddress(address);
        this.setPort(port);
    }

    public void login() throws Exception {
        Socket loginSocket = new Socket(this.address, this.port);
        Wrapper.getLogger().info("Connecting to Monsoon servers...");
        while (!loginSocket.isConnected()) {
        }
        if (loginSocket.isConnected()) {
            Wrapper.getLogger().info("Connected to Monsoon servers!");
            this.sendPacket(new MPacketLogin(), loginSocket);
            Scanner scanner = new Scanner(loginSocket.getInputStream());
            while (scanner.hasNext()) {
                String nextLine = scanner.nextLine();
                Wrapper.getLogger().debug("got packet: " + nextLine);
                JSONObject packet = new JSONObject(nextLine);
                if (packet.isNull("response") || !packet.getString("response").equalsIgnoreCase("ok")) continue;
                Wrapper.getLogger().debug("Got response, connecting to new server...");
                this.setLoginToken(packet.getJSONObject("data").getString("token"));
                loginSocket.close();
                this.useServer(packet.getJSONObject("data").getString("ip"), packet.getJSONObject("data").getInt("port"));
            }
        }
    }

    public void useServer(String ip, int port) throws Exception {
        Wrapper.getLogger().info("Connecting to client server...");
        this.onlineMonsoonUsers = new HashMap<String, String>();
        this.socket = new Socket(ip, port);
        this.sendPacket(new MPacketLogin(this.getLoginToken()));
        Scanner scanner = new Scanner(this.socket.getInputStream());
        block8: while (scanner.hasNext()) {
            String packetId;
            String nextLine = scanner.nextLine();
            Wrapper.getLogger().debug("got packet: " + nextLine);
            JSONObject packet = new JSONObject(nextLine);
            switch (packetId = packet.getString("id")) {
                case "s2": {
                    double clientVersion;
                    if (!packet.getString("response").equalsIgnoreCase("ok")) break;
                    JSONObject data = packet.getJSONObject("data");
                    this.setMotd(data.getString("motd"));
                    double serverVersion = Double.parseDouble(data.getString("clientVersion"));
                    if (serverVersion == (clientVersion = Double.parseDouble(Wrapper.getMonsoon().getVersion()))) continue block8;
                    Wrapper.getLogger().error("You are currently running a" + (serverVersion > clientVersion ? "n older" : "newer") + " version of Monsoon than our servers! Use this at your own risk.");
                    break;
                }
                case "s3": {
                    JSONArray onlineUsers = packet.getJSONArray("onlineUsers");
                    this.onlineMonsoonUsers.clear();
                    for (int i = 0; i < onlineUsers.length(); ++i) {
                        String onlineUser = onlineUsers.getString(i);
                        this.onlineMonsoonUsers.put(onlineUser.split(":")[0], onlineUser.split(":")[1]);
                    }
                    break;
                }
            }
        }
    }

    public void sendPacket(MPacket packet) {
        try {
            this.sendMessageToServer(packet.getPacketData().toString(), this.socket);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void sendPacket(MPacket packet, Socket socket) throws Exception {
        this.sendMessageToServer(packet.getPacketData().toString(), socket);
    }

    private void sendMessageToServer(String message, Socket socket) throws Exception {
        if (socket == null) {
            return;
        }
        if (!socket.isConnected()) {
            return;
        }
        Wrapper.getLogger().debug("sent packet: " + message);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        writer.write(message + "\n");
        writer.flush();
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Socket getSocket() {
        return this.socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public String getLoginToken() {
        return this.loginToken;
    }

    public void setLoginToken(String loginToken) {
        this.loginToken = loginToken;
    }

    public Map<String, String> getOnlineMonsoonUsers() {
        return this.onlineMonsoonUsers;
    }

    public String getMotd() {
        return this.motd;
    }

    public void setMotd(String motd) {
        this.motd = motd;
    }
}

