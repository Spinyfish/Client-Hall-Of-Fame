package gay.sukumi.irc;

import de.datasecs.hydra.client.Client;
import de.datasecs.hydra.client.HydraClient;
import de.datasecs.hydra.shared.handler.Session;
import de.datasecs.hydra.shared.handler.listener.HydraSessionListener;
import de.datasecs.hydra.shared.protocol.packets.Packet;
import gay.sukumi.irc.listener.ConnectionListener;
import gay.sukumi.irc.listener.LoginListener;
import gay.sukumi.irc.listener.MessageListener;
import gay.sukumi.irc.listener.UserProfileListener;
import gay.sukumi.irc.packet.packet.impl.login.LoginRequestPacket;
import gay.sukumi.irc.packet.packet.impl.profile.CProfilePacket;
import gay.sukumi.irc.packet.protocol.Protocol;
import gay.sukumi.irc.profile.UserProfile;
import io.netty.channel.ChannelOption;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * The main class of the internet relay cht.
 * @author kittyuwu
 **/
public class ChatClient {

    /* ~~ Chat client instance && connected users arraylist  ~~ */
    public static ChatClient INSTANCE = new ChatClient();
    private final List<UserProfile> connectedUsers = new ArrayList<>();

    /* ~~ Hydra client ~~ */
    private HydraClient hClient;

    /* ~~ Listeners ~~ */
    private ConnectionListener connectionListener;
    private UserProfileListener userProfileListener;
    private MessageListener messageListener;
    private LoginListener loginListener;

    /* ~~ Profile things ~~ */
    private UserProfile profile;

    /**
     * Connect to the chat server.
     * @param socketAddress The ip address & port of the internet relay chat
     * @param username      Username for authentication
     * @param password      Password for authentication
     * @param client        Client type
     */
    public void connect(InetSocketAddress socketAddress, String username, String password, String mcUsername, UserProfile.Client client) {
        /* Connect to the chat server */
        hClient = new Client.Builder(
                socketAddress.getAddress().getHostName(), socketAddress.getPort(),
                new Protocol())
                .workerThreads(4)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .addSessionListener(new HydraSessionListener() {
                    @Override
                    public void onConnected(Session session) {
                        if(getConnectionListener() != null) getConnectionListener().onConnected(socketAddress.getHostName(), session);
                    }

                    @Override
                    public void onDisconnected(Session session) {
                        getConnectedUsers().clear();
                        if(getConnectionListener() != null) getConnectionListener().onDisconnected(socketAddress.getHostString(), session);
                    }
                }).build();

        /* Send authentication packet */
        if (isConnected())
            sendPacket(new LoginRequestPacket(username, password, mcUsername, client));

        Runtime.getRuntime().addShutdownHook(new Thread(ChatClient.INSTANCE::disconnect));
    }

    /**
     * Send a packet to the server.
     * @param packet Packet that will be sent to the server
     */
    public void sendPacket(Packet packet) {
        hClient.send(packet);
    }

    /**
     * Checks if the chat client is connected to the chat server.
     * @return is client connected
     */
    public boolean isConnected() {
        return hClient.isConnected();
    }

    /**
     * Disconnect from the chat server.
     */
    public void disconnect() {
        hClient.close();
    }

    /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

    public void addListener(ConnectionListener listener) {
        this.connectionListener = listener;
    }

    public void addListener(UserProfileListener listener) {
        this.userProfileListener = listener;
    }

    public void addListener(MessageListener listener) {
        this.messageListener = listener;
    }

    public void addListener(LoginListener listener) {
        this.loginListener = listener;
    }

    /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

    public ConnectionListener getConnectionListener() {
        return connectionListener;
    }

    public UserProfileListener getUserProfileListener() {
        return userProfileListener;
    }

    public MessageListener getMessageListener() {
        return messageListener;
    }

    public LoginListener getLoginListener() {
        return loginListener;
    }

    public UserProfile getProfile() {
        return profile;
    }

    public UserProfile getUser(String username) {
        for (UserProfile connectedUser : connectedUsers) {
            if(connectedUser.getUsername().equalsIgnoreCase(username)) return connectedUser;
        }
        return null;
    }

    public UserProfile getUserByMcName(String username) {
        for (UserProfile connectedUser : connectedUsers) {
            if(connectedUser.getMcUsername().equalsIgnoreCase(username)) return connectedUser;
        }
        return null;
    }


    public void setProfile(UserProfile profile) {
        this.profile = profile;
    }

    public void updateUsername(String name) {
        UserProfile userProfile = getProfile();
        userProfile.setMcUsername(name);
        sendPacket(new CProfilePacket(CProfilePacket.Action.CHANGE_NAME, userProfile));
        setProfile(userProfile);
    }

    public void updateServer(String name) {
        UserProfile userProfile = getProfile();
        userProfile.setCurrentServer(name);
        sendPacket(new CProfilePacket(CProfilePacket.Action.CHANGE_SERVER, userProfile));
        setProfile(userProfile);
    }

    public List<UserProfile> getConnectedUsers() {
        return connectedUsers;
    }
}
