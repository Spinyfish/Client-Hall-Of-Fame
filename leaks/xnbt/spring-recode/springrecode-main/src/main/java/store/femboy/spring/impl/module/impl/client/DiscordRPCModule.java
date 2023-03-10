package store.femboy.spring.impl.module.impl.client;

import gay.sukumi.irc.ChatClient;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import org.lwjgl.input.Keyboard;
import store.femboy.spring.Spring;
import store.femboy.spring.impl.module.Category;
import store.femboy.spring.impl.module.Module;
import store.femboy.spring.impl.util.TimeUtil;

public class DiscordRPCModule extends Module {
    public DiscordRPCModule() {
        super("DiscordRPC", "Discord RPC", Keyboard.KEY_NONE, Category.CLIENT);
        this.setToggled(true);
    }

    private static String clientId = "1038124626351489044";
    private static DiscordRichPresence presence = new DiscordRichPresence();
    private static DiscordRPC discordRPC = new DiscordRPC();
    private TimeUtil timer = new TimeUtil();

    @Override
    public void onEnable() {
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        handlers.disconnected = ((var1, var2) -> System.out.println("Disconnected from Discord:, var1 " + var1+ " var2 " + var2));

        discordRPC.discordInitialize(clientId, handlers, true, null);
        presence.startTimestamp = System.currentTimeMillis() / 1000;
        presence.details = "Spring Client | Logged in as: " + ChatClient.INSTANCE.getProfile().getUsername();
        presence.largeImageKey = "logo";
        presence.largeImageText = "Spring Client " + Spring.INSTANCE.version;
        presence.state = null;
        discordRPC.discordUpdatePresence(presence);

        super.onEnable();
    }

    @Override
    public void onDisable() {
        discordRPC.discordShutdown();
        discordRPC.discordClearPresence();
        super.onDisable();
    }
}
