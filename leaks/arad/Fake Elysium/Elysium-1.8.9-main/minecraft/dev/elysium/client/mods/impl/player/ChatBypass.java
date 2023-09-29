package dev.elysium.client.mods.impl.player;

import dev.elysium.base.events.types.EventTarget;
import dev.elysium.base.mods.Category;
import dev.elysium.base.mods.Mod;
import dev.elysium.client.events.EventChat;

public class ChatBypass extends Mod {
    public ChatBypass() {
        super("ChatBypass","Bypass chat filter plugins", Category.PLAYER);
    }

    @EventTarget
    public void onEventChat(EventChat e) {
        if(e.getMessage().length() > 0 && e.getMessage().indexOf("/") == 0)
            return;
        String modified = "";

        for(char c : e.getMessage().toCharArray())
            modified += c + "\uD83D\uDC45";

        e.setMessage(modified);
    }
}
