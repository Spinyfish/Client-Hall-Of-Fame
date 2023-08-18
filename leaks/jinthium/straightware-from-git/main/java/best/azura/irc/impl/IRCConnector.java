package best.azura.irc.impl;

import best.azura.irc.core.packet.Packet;
import best.azura.irc.core.packet.PacketManager;
import best.azura.irc.impl.packets.client.C7HandshakeRequest;
import best.azura.irc.impl.packets.handler.IRCKeepAliveHandler;
import best.azura.irc.impl.packets.handler.IRCMessageHandler;
import best.azura.irc.utils.IRCCache;
import best.azura.irc.utils.IRCData;
import best.azura.irc.utils.Wrapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.PrintStream;
import java.net.Socket;
import java.util.Base64;

public class IRCConnector {

    // Temporal Data. TODO remove.
    public String username, password;

    // GSON for serialize.
    private final Gson gson;

    // PacketManager for Packets.
    private final PacketManager packetManager;

    // The connection between Server and Client.
    private Socket clientSocket;

    // The output Stream of the Client.
    private PrintStream clientOutput;

    // The Server host address.
    private final String host;

    // The Server port.
    private final int port;

    // Handler for the Data.
    private final IRCMessageHandler ircMessageHandler;
    private final IRCKeepAliveHandler ircKeepAliveHandler;
    private final IRCData ircData;

    // Instance of the IRC-Cache.
    private final IRCCache ircCache;

    /**
     * Constructor for the Connector.
     * @param host the Host-Address of the Server.
     * @param port the port of the Server.
     */
    public IRCConnector(String host, int port) {
        this.host = host;
        this.port = port;

        gson = new GsonBuilder().create();

        packetManager = new PacketManager();

        ircCache = new IRCCache();
        ircData = new IRCData();
        ircMessageHandler = new IRCMessageHandler(this);
        ircKeepAliveHandler = new IRCKeepAliveHandler(this);
    }

    /**
     * Start the IRC-Connector.
     */
    public void startConnection() {

        new Thread(() -> {
            try {
                clientSocket = new Socket(host, port);
                clientSocket.setSoTimeout(5000);
                clientOutput = new PrintStream(clientSocket.getOutputStream());
            } catch (Exception ignored) {
                Wrapper.shutdown();
            }

            // Check if the ClientSocket is null.
            if (getClientSocket() == null || getClientSocket().isClosed()) return;

            C7HandshakeRequest c7HandshakeRequest = new C7HandshakeRequest();

            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("public", Base64.getEncoder().encodeToString(ircData.keyPair.getPublic().getEncoded()));

            c7HandshakeRequest.setContent(jsonObject);

            sendPacket(c7HandshakeRequest);


            // Check if the ClientSocket is null (maybe disconnect because AuthFailed).
            if (getClientSocket() == null || getClientSocket().isClosed()) Wrapper.shutdown();

            // Start Message Handler.
            getIrcMessageHandler().startHandler();
        }).start();
    }

    /**
     * Send an IRC-Packet to the Server.
     * @param packet the Packet.
     */
    public void sendPacket(Packet packet) {

        // Don't do anything if the Socket is closed.
        if (getClientSocket() == null || getClientSocket().isClosed()) {
            startConnection();
            return;
        }

        // Don't do anything if the PrintStream is null.
        if (clientOutput == null) return;

        packetManager.sendPacket(this, packet);
    }

    /**
     * Stop the IRC-Connector.
     */
    public void stopConnection() {
        try {
            if (clientOutput != null) clientOutput.close();
            if (clientSocket != null) clientSocket.close();
        } catch (Exception ignore) {}

        getIrcCache().getIrcUserHashMap().clear();

        getIrcMessageHandler().stopHandler();
        getIrcKeepAliveHandler().stopHandler();
    }

    /**
     * Retrieve the current ClientSocket.
     * @return the Client Socket.
     */
    public Socket getClientSocket() {
        return clientSocket;
    }

    /**
     * Change the current ClientSocket.
     * @param clientSocket the new ClientSocket.
     */
    @Deprecated
    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    /**
     * Retrieve Server Host Address.
     * @return Host Address.
     */
    public String getHost() {
        return host;
    }

    /**
     * Retrieve Server port.
     * @return Server Port.
     */
    public int getPort() {
        return port;
    }

    /**
     * Retrieve an Instance of the IRC-MessageHandler.
     * @return instance of the IRC-MessageHandler.
     */
    public IRCMessageHandler getIrcMessageHandler() {
        return ircMessageHandler;
    }

    /**
     * Retrieve an Instance of the IRC-KeepAliveHandler.
     * @return instance of the IRC-KeepAliveHandler.
     */
    public IRCKeepAliveHandler getIrcKeepAliveHandler() {
        return ircKeepAliveHandler;
    }

    /**
     * Retrieve the instance of the Client PrintStream.
     * @return instance of the Client PrintStream.
     */
    public PrintStream getClientOutput() { return clientOutput; }

    /**
     * Retrieve the instance of the PacketManager.
     * @return instance of the PacketManager.
     */
    public PacketManager getPacketManager() { return packetManager; }

    /**
     * Retrieve the instance of the built GSON.
     * @return instance of the built GSON.
     */
    public Gson getGson() { return gson; }

    /**
     * Retrieve an Instance of the IRC-Cache.
     * @return instance of the IRC-Cache.
     */
    public IRCCache getIrcCache() {
        return ircCache;
    }

    /**
     * Retrieve an Instance of the IRC-Data.
     * @return instance of the IRC-Data.
     */
    public IRCData getIrcData() {
        return ircData;
    }
}
