package me.tecnio.backend.handler;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import me.tecnio.backend.Backend;
import me.tecnio.backend.network.Client;
import me.tecnio.util.RequestUtil;
import packet.handler.impl.IClientPacketHandler;
import packet.impl.client.community.ClientCommunityMessageSend;
import packet.impl.client.community.ClientCommunityPopulateRequest;
import packet.impl.client.data.ClientModuleData;
import packet.impl.client.general.ClientKeepAlive;
import packet.impl.client.login.ClientLoginPacket;
import packet.impl.client.protection.ClientConstantsPacket;
import packet.impl.client.protection.lIlIlIlIIIlllIIlIlIIIlllIIlllIlIIIlllIlI;
import packet.impl.client.protection.lllIIllIlIIlIlllIllIlIIIIIlIlIIl;
import packet.impl.client.store.lIIlIIIIIllIlllllllIIlllIlIllIlI;
import packet.impl.client.store.lllllIIlIIIlIIIIIIIlIlllIlIlIIlI;
import packet.impl.server.general.ServerKeepAlive;
import packet.impl.server.login.ServerLoginPacket;
import packet.impl.server.protection.ServerConstantResult;
import packet.impl.server.protection.lIllIlIlIlIIIlllIIIlllIIIIlllI;
import util.time.StopWatch;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;

@RequiredArgsConstructor
public final class ClientPacketHandler implements IClientPacketHandler {

    private final Client client;

    // Used to get more details about the request.
    private final boolean debug = true;
    private final StopWatch lastDataSave = new StopWatch();


    @Override
    public void handle(ClientLoginPacket packet) {
        try {

            handle:
            {
                if (Backend.OFFLINE)
                    break handle;

                HashMap data = getData(packet.getEmail(), packet.getHardwareID());

                System.out.println(packet.getEmail() + " " + packet.getHardwareID());


                if (data == null) {
                    this.respond(false, "No data");
                    return;
                }

                String response = (String) data.get("status");

                if (!response.equals("OK")) {
                    this.respond(false, response);
                    return;
                }

                System.out.println(data);
            }

            Backend.INSTANCE.getServer().getClients().removeIf(client1 -> client1 != client && client1.timeSinceKeepAlive.finished(1000 * 30));

            debug("Say hi to " + packet.getSystemName() + "@" + packet.getOsName());
            debug("Online users: " + Backend.INSTANCE.getServer().getClients().size());

            client.setUsername(packet.getEmail());
            client.setUserId(String.valueOf(0));

            client.setPcName(packet.getSystemName());
            client.setLoggedIn(true);

            System.out.println("<------Logging in------>");
            System.out.println("ID : " + packet.getClientID());
            System.out.println("User: " + packet.getEmail());
            System.out.println("IP: " + client.getIp());
            System.out.println("<---------------------->");

            this.respond(true, "");

            System.gc();
        } catch (Exception e) {
            debug("Something went wrong while handling a packet from a client! - " + e.getMessage());
        }
    }

    @Override
    public void handle(ClientConstantsPacket packet) {
        if (!client.loggedIn) return;
        debug("Handling request for constants");
        System.gc();
        client.getCommunication().write(new ServerConstantResult(Math.PI, Math.PI * 2, 90.0F, 180));
    }

    @Override
    public void handle(ClientCommunityMessageSend packet) {
        if (!client.loggedIn) return;
        Backend.INSTANCE.getCommunityManager().packet(packet, client);
    }

    @Override
    public void handle(lIlIlIlIIIlllIIlIlIIIlllIIlllIlIIIlllIlI packet) {
        if (!client.loggedIn) return;
        debug("Received and sent");
        System.gc();
        client.getCommunication().write(new lIllIlIlIlIIIlllIIIlllIIIIlllI(packet.getLIlIllIlIIIlllIIlIIIlllIIIlllIlIIIlllIlI(), packet.getLIllIlIlIlIIIlllIIIlllIIIIllIlIIIlllIllI()));
    }

    @Override
    public void handle(ClientCommunityPopulateRequest packet) {
        if (!client.loggedIn) return;
        Backend.INSTANCE.getCommunityManager().packet(packet, client);
    }

    @Override
    public void handle(lllllIIlIIIlIIIIIIIlIlllIlIlIIlI packet) {
        if (!client.loggedIn) return;
    }

    @Override
    public void handle(lIIlIIIIIllIlllllllIIlllIlIllIlI packet) {
        if (!client.loggedIn) return;
    }

    @Override
    public void handle(lllIIllIlIIlIlllIllIlIIIIIlIlIIl packet) {
        if (!client.loggedIn) return;
        Backend.INSTANCE.getProtectionManager().packet(packet, client);
    }

    @Override
    public void handle(ClientKeepAlive packet) {
        if (!client.loggedIn) return;
        client.timeSinceKeepAlive.reset();
        client.getCommunication().write(new ServerKeepAlive());
    }

    @Override
    public void handle(ClientModuleData packet) {
        try {
            if (!lastDataSave.finished(5000) ||
                    packet.modules.size() > 500 ||
                    packet.modules.stream().anyMatch(module -> module.length() > 300) ||
                    client.getUsername().length() > 50) {
                return;
            }

            lastDataSave.reset();

            Date date = new Date();

            // Use JSON next time please to parse data
            String savePath = "Ban Data";
            String path = savePath + File.separator + packet.ip.replace(".", "");
            String file = (packet.banned ? "BANNED" : "BYPASSING") + "__" +
                    (client.getUsername() + "__" + date.getYear() + "_" +
                            date.getMonth() + "_" + date.getDay() + "_" +
                            date.getHours() + "_" + date.getMinutes() + "_" +
                            date.getSeconds()).replace(".", "").
                            replace("/", "").replace("\\", "") + ".txt";

            if (!Files.exists(Paths.get(savePath))) Files.createDirectory(Paths.get(savePath));
            if (!Files.exists(Paths.get(path))) Files.createDirectory(Paths.get(path));
            Files.createFile(Paths.get(path + File.separator + file));

            FileWriter fileWriter = new FileWriter(path + File.separator + file);

            fileWriter.write("Bypassing: " + !packet.banned + "\n");

            for (String module : packet.modules) {
                fileWriter.write(module + "\n");
            }

            fileWriter.close();
            System.out.println("Saved Data");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to save data");
        }
    }

    public void respond(boolean response, String message) {
        client.getCommunication().write(new ServerLoginPacket(response, message));
        debug("Sent Response");

        if (!response) {
            Backend.INSTANCE.getServer().getClients().remove(client);
            debug("Removed client from list");
        }
    }

    private void debug(String msg) {
        if (debug) {
            System.out.println("[Backend Debug] > " + msg);
        }
    }

    private static final Gson g = new Gson();

    public HashMap getData(String username, String hwid) throws IOException {
        HashMap<String, String> data = new HashMap<String, String>() {{
            put("username", username);
            put("hwid", hwid);
            put("access_key", "shrd!bmhdou!rtbjr!rise"); // Use sys.getenv
        }};
        String str = RequestUtil.postRequest("https://vantageclients.com/api/rise/validate", g.toJson(data, HashMap.class));

        HashMap res = g.fromJson(str, HashMap.class);

        return res;
    }
}
