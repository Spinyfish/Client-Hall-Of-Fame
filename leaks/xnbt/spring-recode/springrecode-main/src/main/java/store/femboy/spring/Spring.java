package store.femboy.spring;

import best.azura.eventbus.core.EventBus;
import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import de.datasecs.hydra.shared.handler.Session;
import gay.sukumi.irc.ChatClient;
import gay.sukumi.irc.listener.ConnectionListener;
import gay.sukumi.irc.listener.LoginListener;
import gay.sukumi.irc.listener.UserProfileListener;
import gay.sukumi.irc.profile.UserProfile;
import gay.sukumi.irc.utils.Errors;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.Display;
import store.femboy.spring.custommenus.LoginGUI;
import store.femboy.spring.impl.command.CommandManager;
import store.femboy.spring.impl.event.impl.EventChat;
import store.femboy.spring.impl.event.impl.EventKey;
import store.femboy.spring.impl.module.Module;
import store.femboy.spring.impl.module.ModuleManager;
import store.femboy.spring.impl.module.ReleaseType;
import store.femboy.spring.impl.notifications.Notification;
import store.femboy.spring.impl.notifications.NotificationType;
import store.femboy.spring.impl.util.AddChatMessage;

import java.net.InetSocketAddress;

public enum Spring {

    INSTANCE;

    public String name= "Spring", version="0.1", edition="Femboy edition";

    public EventBus bus;
    public ReleaseType releaseType = ReleaseType.DEV;

    public ModuleManager manager;
    public CommandManager commandManager = new CommandManager();

    public final Runnable init = () -> {
        Display.setTitle(name + " " + version);

        ChatClient.INSTANCE.addListener((message, type, profile) -> {
            switch (type) {
                case USER:
                    String rankColor = profile.getRank() == UserProfile.Rank.ADMIN ? "§4" : "§9";
                    AddChatMessage.print("§bIRC§7> " + String.format("%s%s§7: §f%s", rankColor, profile.getUsername(), message));
                    break;
                case RAW:
                    AddChatMessage.print("§bIRC§7> §f" + message);
                    break;
            }
        });

        ChatClient.INSTANCE.addListener(new ConnectionListener() {
            @Override
            public void onConnected(String host, Session session) {

            }

            @Override
            public void onDisconnected(String host, Session session) {
                ChatClient.INSTANCE.connect(new InetSocketAddress("193.142.146.35", 8888), LoginGUI.ircUser, LoginGUI.ircPass, Minecraft.getMinecraft().session.getUsername(), UserProfile.Client.SPRING);
            }
        });
        ChatClient.INSTANCE.addListener(new UserProfileListener() {
            @Override
            public void onConnected(UserProfile profile) {
                Notification.addNotification(new Notification(NotificationType.INFO, profile.getUsername() + " connected to IRC", false));
            }

            @Override
            public void onDisconnected(UserProfile profile) {
                Notification.addNotification(new Notification(NotificationType.INFO, profile.getUsername() + " disconnected from IRC", false));
            }

            @Override
            public void onNameChange(UserProfile profile) {

            }

            @Override
            public void onServerChange(UserProfile profile) {

            }

        });

        ChatClient.INSTANCE.addListener(new LoginListener() {

            @Override
            public void onSuccess(UserProfile userProfile, Session session) {

            }

            @Override
            public void onFail(Errors reason, Session session) {

            }
        });

        bus = new EventBus();
        bus.register(this);

        manager = new ModuleManager();
    };

    public void stop(){
        System.out.println("Stopping Spring");
        bus.unregister(this);

    }

    public EventBus getBus() {
        return bus;
    }

    public ModuleManager getManager() {
        return manager;
    }

    @EventHandler()
    public final Listener<EventKey> onKey = e ->
            getManager().getModules().stream().filter(m -> m.getKey() == e.getKey()).forEach(Module::toggle);

    @EventHandler()
    public final  Listener<EventChat> onChat = e ->{
        if (e instanceof EventChat) {
            commandManager.handleChat((EventChat)e);
        }
    };

}
