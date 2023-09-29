package me.jinthium.straight.impl.modules.visual;

import best.azura.irc.utils.Wrapper;
import me.jinthium.straight.api.irc.IRCClient;
import me.jinthium.straight.api.module.Module;

public class IRC extends Module {

    public IRC(){
        super("IRC", Category.MISC);
    }

    @Override
    public void onEnable() {
        IRCClient.INSTANCE.connect();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        IRCClient.INSTANCE.disconnect();
        super.onDisable();
    }
}
