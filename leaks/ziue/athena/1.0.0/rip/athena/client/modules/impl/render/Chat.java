package rip.athena.client.modules.impl.render;

import rip.athena.client.config.*;
import rip.athena.client.modules.*;

public class Chat extends Module
{
    @ConfigValue.Boolean(name = "Infinite Chat")
    public boolean infiniteChat;
    
    public Chat() {
        super("Chat", Category.RENDER, "Athena/gui/mods/chat.png");
        this.infiniteChat = false;
    }
    
    @Override
    public void onEnable() {
        super.onEnable();
    }
    
    @Override
    public void onDisable() {
        super.onDisable();
    }
}
