/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.Subscribe
 */
package lodomir.dev.modules.impl.render;

import com.google.common.eventbus.Subscribe;
import lodomir.dev.event.impl.game.EventUpdate;
import lodomir.dev.event.impl.network.EventGetPacket;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import lodomir.dev.settings.impl.NumberSetting;
import net.minecraft.network.play.server.S03PacketTimeUpdate;

public class Ambience
extends Module {
    public NumberSetting time = new NumberSetting("Time", 0.0, 24000.0, 12000.0, 1000.0);

    public Ambience() {
        super("Ambience", 0, Category.RENDER);
        this.addSettings(this.time);
    }

    @Subscribe
    public void onUpdate(EventUpdate event) {
        if (Ambience.mc.theWorld != null) {
            Ambience.mc.theWorld.setWorldTime(this.time.getValueInt());
        }
        super.onUpdate(event);
    }

    @Override
    @Subscribe
    public void onGetPacket(EventGetPacket event) {
        if (event.packet instanceof S03PacketTimeUpdate) {
            event.setCancelled(true);
            super.onGetPacket(event);
        }
    }
}

