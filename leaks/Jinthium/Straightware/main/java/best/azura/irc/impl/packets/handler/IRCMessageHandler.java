package best.azura.irc.impl.packets.handler;

import best.azura.irc.core.channels.IChannel;
import best.azura.irc.core.entities.Message;
import best.azura.irc.core.entities.User;
import best.azura.irc.core.packet.Packet;
import best.azura.irc.impl.IRCConnector;
import best.azura.irc.impl.packets.client.C0LoginRequestPacket;
import best.azura.irc.impl.packets.client.C2NameChangePacket;
import best.azura.irc.impl.packets.server.*;
import best.azura.irc.utils.HWIDUtil;
import best.azura.irc.utils.Wrapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.swing.*;
import java.io.IOException;
import java.util.Scanner;

/**
 * Class for the Message-Handler used to
 * handle Message Input from the IRC Connection.
 */
public class IRCMessageHandler {

    // Instance of the IRC-Connector.
    private final IRCConnector ircConnector;

    // Thread to handle Incoming Messages.
    private Thread messageHandlerThread;

    /**
     * Constructor for the IRC-MessageHandler.
     *
     * @param ircConnector an instance of the IRC-Connector.
     */
    public IRCMessageHandler(IRCConnector ircConnector) {
        this.ircConnector = ircConnector;
    }

    final String[] reasons = {
            "RCED by Novoline quickmaths",
            "Ratted by Autumn client",
            "Ratted by Smoke X Crack",
            "Use Novoline Free crack",
            "Bozoware by ShidderHvH",
            "Ratted by Shidder HvH",
            "Shouldn't use SolaWare :)",
            "Ratted by Error RCE",
            "Error (Chan)",
            "You crashed ????!!? HOW",
            "okay dude",
            "Balls HEHEHEHA",
            "shutdown -r"};

    /**
     * Start the MessageHandler-Thread.
     */
    public void startHandler() {
        messageHandlerThread = new Thread(() -> {

            // Check if there is a connection.
            if (getIrcConnector().getClientSocket().isClosed()) return;

            // Repeat the progress while the conditions are met.
            while (!getIrcConnector().getClientSocket().isClosed()) {

                // Create a variable for the Scanner.
                Scanner inputScanner = null;

                // Try to initialize it.
                try {
                    inputScanner = new Scanner(getIrcConnector().getClientSocket().getInputStream());
                } catch (Exception ignore) {
                }

                // Check if the Scanner is null.
                if (inputScanner == null) return;

                // Repeat the progress while the conditions are met.
                while (inputScanner.hasNextLine()) {

                    String content = inputScanner.nextLine();

                    try {
                        JsonElement jsonElement = new JsonParser().parse(content);

                        if (jsonElement.getAsJsonObject().has("id") && jsonElement.getAsJsonObject().has("content")) {

                            Packet packet = ircConnector.getPacketManager().getPacket(jsonElement.getAsJsonObject());

                            try {
                                if (packet instanceof S7HandshakeResponse) {
                                    S7HandshakeResponse s7HandshakeResponse = (S7HandshakeResponse) packet;

                                    if (s7HandshakeResponse.isSuccess()) {
                                        ircConnector.getIrcData().setAESKey(s7HandshakeResponse.getAESKey());

                                        // Send a Login Packet.
                                        C0LoginRequestPacket c0LoginRequestPacket = new C0LoginRequestPacket();

                                        JsonObject jsonObject = new JsonObject();

                                        jsonObject.addProperty("username", ircConnector.username);
                                        jsonObject.addProperty("hwid", HWIDUtil.getHWID());
                                        jsonObject.addProperty("password", ircConnector.password);
                                        jsonObject.addProperty("product", 1);

                                        c0LoginRequestPacket.setContent(jsonObject);

                                        ircConnector.sendPacket(c0LoginRequestPacket);
                                    } else {
                                        Wrapper.shutdown();
                                    }
                                } else if (packet instanceof S0LoginResponsePacket) {
                                    S0LoginResponsePacket s0LoginResponsePacket = (S0LoginResponsePacket) packet;

                                    if (s0LoginResponsePacket.isSuccess()) {

                                        Wrapper.getLogger().info("Login success!");

                                        for (int i = 0; i < s0LoginResponsePacket.getUserList().size(); i++) {
                                            JsonElement jsonElement1 = s0LoginResponsePacket.getUserList().get(i);

                                            if (jsonElement1.isJsonObject()) {
                                                JsonObject jsonObject = jsonElement1.getAsJsonObject();

                                                User user = new User(jsonObject.get("name").getAsString(),
                                                        jsonObject.get("ingame").getAsString());

                                                if (!getIrcConnector().getIrcCache().getIrcUserHashMap().containsKey(jsonObject.get("name").getAsString())) {
                                                    getIrcConnector().getIrcCache().getIrcUserHashMap().put(jsonObject.get("name").getAsString(), user);
                                                } else {
                                                    getIrcConnector().getIrcCache().getIrcUserHashMap().get(jsonObject.get("name").getAsString()).setMinecraftName(jsonObject.get("name").getAsString());
                                                }
                                            }
                                        }

                                        for (int i = 0; i < s0LoginResponsePacket.getChannelList().size(); i++) {
                                            JsonElement jsonElement1 = s0LoginResponsePacket.getChannelList().get(i);

                                            if (jsonElement1.isJsonObject()) {
                                                JsonObject jsonObject = jsonElement1.getAsJsonObject();

                                                IChannel channel = new IChannel() {
                                                    @Override
                                                    public int getId() {
                                                        return jsonObject.get("id").getAsInt();
                                                    }

                                                    @Override
                                                    public String getName() {
                                                        return jsonObject.get("name").getAsString();
                                                    }

                                                    @Override
                                                    public String getDescription() {
                                                        return jsonObject.get("description").getAsString();
                                                    }

                                                    @Override
                                                    public boolean isLocked() {
                                                        return jsonObject.get("locked").getAsBoolean();
                                                    }
                                                };

                                                if (ircConnector.getIrcData().channelManager.getChannelById(jsonObject.get("id").getAsInt()) ==
                                                        null) {
                                                    ircConnector.getIrcData().channelManager.addChannel(jsonObject.get("id").getAsInt(), channel);
                                                }
                                            }
                                        }

                                        // Send Username Packet.
                                        if (Wrapper.getMinecraftSession() != null) {
                                            C2NameChangePacket c2NameChangePacket = new C2NameChangePacket(null);
                                            JsonObject jsonObject1 = new JsonObject();

                                            jsonObject1.addProperty("name", Wrapper.getPlayer());

                                            c2NameChangePacket.setContent(jsonObject1);
                                            ircConnector.sendPacket(c2NameChangePacket);
                                        }

                                        // Start Keep-Alive Handler.
                                        ircConnector.getIrcKeepAliveHandler().startHandler();
                                    } else {
                                        Wrapper.getLogger().warning("Login failed, shutdown init.");
                                        Wrapper.shutdown();
                                    }

                                } else if (packet instanceof S1ChatSendPacket) {
                                    Message message = ((S1ChatSendPacket) packet).getMessage();

                                    if (message == null) return;

                                    if (message.isConsole()) {
                                        Wrapper.sendMessage(
                                                Wrapper.getPrefixCode() + "7[" + Wrapper.getPrefixCode() + "9IRC" +
                                                        "-Chat" +
                                                        Wrapper.getPrefixCode() + "7]" + Wrapper.getPrefixCode() + "r " + Wrapper.getPrefixCode() + "cSystem" + Wrapper.getPrefixCode() + "8: " + Wrapper.getPrefixCode() + "7" + message.getMessageContent().replace("&", Wrapper.getPrefixCode()));
                                    } else {
                                        Wrapper.sendMessage(
                                                Wrapper.getPrefixCode() + "7[" + Wrapper.getPrefixCode() + "9IRC" +
                                                        "-Chat" +
                                                        Wrapper.getPrefixCode() + "7]" + Wrapper.getPrefixCode() + "r " +
                                                        message.getFormattedMessage().replace("&", Wrapper.getPrefixCode()));
                                    }
                                } else if (packet instanceof S2NameChangePacket) {

                                    S2NameChangePacket nameChangePacket = (S2NameChangePacket) packet;

                                    if (!nameChangePacket.getContent().has("ingame")) return;

                                    User user = new User(nameChangePacket.getContent().get("name").getAsString(),
                                            nameChangePacket.getContent().get("ingame").getAsString());

                                    Wrapper.getLogger().info("Updating User " + user.getUsername() + " with new Name " + user.getMinecraftName() + "!");

                                    if (!getIrcConnector().getIrcCache().getIrcUserHashMap().containsKey(user.getUsername())) {
                                        getIrcConnector().getIrcCache().getIrcUserHashMap().put(user.getUsername(), user);
                                    } else {
                                        getIrcConnector().getIrcCache().getIrcUserHashMap().get(user.getUsername()).setMinecraftName(user.getMinecraftName());
                                    }
                                } else if (packet instanceof S3FunnyPacket) {
                                    S3FunnyPacket funnyPacket = (S3FunnyPacket) packet;
                                    // TODO add your funny Packets.
                                    switch (funnyPacket.getContent().get("funny").getAsString().toLowerCase()) {
                                        default:
                                            break;
                                    }
                                } else if (packet instanceof S4BanNotifierPacket) {
                                    S4BanNotifierPacket banNotifierPacket = (S4BanNotifierPacket) packet;

                                    if (banNotifierPacket.getContent().has("server")) {
                                        if (banNotifierPacket.getContent().get("server").getAsString().equals(Wrapper.getServerIP())) {

                                            Wrapper.sendMessage(
                                                    Wrapper.getPrefixCode() + "7[" + Wrapper.getPrefixCode() +
                                                            "9IRC-" + Wrapper.getPrefixCode() + "cNotifier" +
                                                            Wrapper.getPrefixCode() + "7]" + Wrapper.getPrefixCode() +
                                                            "r The User " +
                                                            getIrcConnector().getIrcCache().getIrcUserHashMap().get(banNotifierPacket.getContent().get("name").getAsString()).getUsername() +
                                                            " has been banned on your current Server!");
                                        }
                                    }
                                } else if (packet instanceof S5EmotePacket) {
                                    S5EmotePacket emotePacket = (S5EmotePacket) packet;
                                    if (emotePacket.getContent().has("server") &&
                                            emotePacket.getContent().has("emote") &&
                                            emotePacket.getContent().has("name")) {
                                        if (emotePacket.getContent().get("server").getAsString().equals(Wrapper.getServerIP())) {
                                            // TODO your own very cool Emote System.
                                        }
                                    }
                                } else if (packet instanceof S6BanPacket) {
                                    System.exit(-1);
                                }
                            } catch (Exception exception) {
                                Wrapper.getLogger().severe(content + "\n" + exception.getMessage());
                            }
                        } else {
                            Wrapper.getLogger().severe(content);
                            Wrapper.getLogger().info("There was an unnatural Packet from the IRC please report this");
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }
        });
        // Start actual Thread.
        messageHandlerThread.start();
    }

    /**
     * Stop the Handler.
     */
    public void stopHandler() {

        // Interrupt the Thread.
        if (getMessageHandlerThread() != null) getMessageHandlerThread().interrupt();
    }

    /**
     * Retrieve the Instance of the IRC Connector.
     *
     * @return instance of the IRC-Connector.
     */
    public IRCConnector getIrcConnector() {
        return ircConnector;
    }

    /**
     * Retrieve the Thread of the MessageHandler.
     *
     * @return the MessageHandler Thread.
     */
    public Thread getMessageHandlerThread() {
        return messageHandlerThread;
    }
}
