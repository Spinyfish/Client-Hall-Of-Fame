package com.alan.clients.module.impl.player;

import com.alan.clients.Client;
import com.alan.clients.api.Rise;
import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.module.impl.player.flagdetector.Flight;
import com.alan.clients.module.impl.player.flagdetector.Friction;
import com.alan.clients.module.impl.player.flagdetector.MathGround;
import com.alan.clients.module.impl.player.flagdetector.Strafe;
import com.alan.clients.util.chat.ChatUtil;
import com.alan.clients.value.impl.BooleanValue;

@Rise
@ModuleInfo(name = "module.player.flagdetector.name", description = "module.player.flagdetector.description", category = Category.PLAYER)
public class FlagDetector extends Module {
    public BooleanValue strafe = new BooleanValue("Strafe (Watchdog)", this, false, new Strafe("", this));
    public BooleanValue friction = new BooleanValue("Friction", this, false, new Friction("", this));
    public BooleanValue flight = new BooleanValue("Flight", this, false, new Flight("", this));
    public BooleanValue mathGround = new BooleanValue("Math Ground", this, false, new MathGround("", this));

    @Override
    protected void onEnable() {
        if (!Client.DEVELOPMENT_SWITCH) {
            ChatUtil.display("This module is only enabled for developers or config makersconfig");

            toggle();
        }
    }

    public void fail(String check) {
        ChatUtil.display("§7failed " + Client.INSTANCE.getThemeManager().getTheme().getChatAccentColor() + check);
    }
}
