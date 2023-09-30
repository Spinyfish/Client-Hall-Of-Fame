package best.azura.irc.utils;

import best.azura.irc.impl.IRCConnector;
import best.azura.irc.impl.packets.client.C0LoginRequestPacket;
import best.azura.irc.impl.packets.client.C1ChatSendPacket;
import me.jinthium.straight.api.util.MinecraftInstance;
import net.minecraft.util.ChatComponentText;

import java.util.logging.Logger;

public class Wrapper implements MinecraftInstance {

    private static IRCConnector ircConnector;

    /**
     * Get the Prefix Code, please let this refer to the ChatFormatting class.
     * @return the Prefix Code.
     */
    public static String getPrefixCode() {
        return "§";
    }

    /**
     * Should call the Minecraft.getMinecraft().shutdown() method.
     */
    public static void shutdown() {
        // Call shutdown.
    }

    /**
     * Instance of the IRC-Connector.
     * @return an Instance of {@link IRCConnector}.
     */
    public static IRCConnector getIRCConnector() {
        return ircConnector == null ? ircConnector = new IRCConnector("45.128.232.213", 8000) :ircConnector;
    }

    /**
     * Get the Logger that should be used.
     * @return an Instance of the Global-{@link Logger}
     */
    public static Logger getLogger() {
        // Use your own Logger.
        return Logger.getGlobal();
    }

    /**
     * Get the current Minecraft Session.
     * @return the Minecraft Session.
     */
    public static Class<?> getMinecraftSession() {
        // TODO let this refer to the MinecraftSession.
        return mc.session.getClass();
    }

    /**
     * Get the current PlayerName.
     * @return current PlayerName.
     */
    public static String getPlayer() {
        // TODO let this refer to the MinecraftSession Username.
        return mc.session.getUsername();
    }

    /**
     * Method used to send a Message into the chat.
     * @param message the Message.
     */
    public static void sendMessage(String message) {
        // TODO call your send Message method.

//        Wrapper.getIRCConnector().sendPacket(new C1ChatSendPacket(0));
        mc.thePlayer.addChatComponentMessage(new ChatComponentText(message));
    }

    /**
     * Method used to get the last resolved Server IP.
     * @return last resolved Server IP
     */
    public static String getServerIP() {
        // TODO let this refer to your IP Resolver.
        return mc.getIntegratedServer().isSinglePlayer() ? "Singleplayer" : mc.getIntegratedServer().getName();
    }
}
