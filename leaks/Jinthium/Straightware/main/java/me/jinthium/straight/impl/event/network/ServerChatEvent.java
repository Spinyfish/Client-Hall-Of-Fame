package me.jinthium.straight.impl.event.network;


import me.jinthium.straight.api.event.Event;
import net.minecraft.util.IChatComponent;

public class ServerChatEvent extends Event {
    private IChatComponent chatComponent;

    public ServerChatEvent(IChatComponent chatComponent) {
        this.chatComponent = chatComponent;
    }

    public IChatComponent getChatComponent() {
        return chatComponent;
    }

    public void setChatComponent(IChatComponent chatComponent) {
        this.chatComponent = chatComponent;
    }
}
