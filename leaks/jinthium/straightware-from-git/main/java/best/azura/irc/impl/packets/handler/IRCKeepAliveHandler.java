package best.azura.irc.impl.packets.handler;

import best.azura.irc.impl.IRCConnector;
import best.azura.irc.impl.packets.client.C3KeepAlivePacket;

/**
 * Class for the KeepAlive-Handler used to
 * keep the connection alive.
 */
public class IRCKeepAliveHandler {

    // Instance of the IRC-Connector.
    private final IRCConnector ircConnector;

    // Thread to handle KeepAlive-Packets.
    private Thread keepAliveHandlerThread;

    /**
     * Constructor for the IRC-KeepAliveHandler.
     *
     * @param ircConnector an instance of the IRC-Connector.
     */
    public IRCKeepAliveHandler(IRCConnector ircConnector) {
        this.ircConnector = ircConnector;
    }

    /**
     * Start the KeepAlive-Thread.
     */
    public void startHandler() {
        keepAliveHandlerThread = new Thread(() -> {

            // Check if there is a connection.
            if (ircConnector.getClientSocket().isClosed()) return;

            while (ircConnector.getClientSocket().isConnected()) {

                // Send KeepAlive Packet.
                getIrcConnector().sendPacket(new C3KeepAlivePacket());

                // Sleep for 30 min.
                try {
                    Thread.sleep(30 * 1000);
                } catch (Exception ignore) {
                }
            }
        });

        // Start actual Thread.
        keepAliveHandlerThread.start();
    }

    /**
     * Stop the Handler.
     */
    public void stopHandler() {

        // Interrupt the Thread.
        if (getKeepAliveHandlerThread() != null) getKeepAliveHandlerThread().interrupt();
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
     * Retrieve the Thread of the KeepAliveHandler.
     *
     * @return the KeepAliveHandler Thread.
     */
    public Thread getKeepAliveHandlerThread() {
        return keepAliveHandlerThread;
    }
}
