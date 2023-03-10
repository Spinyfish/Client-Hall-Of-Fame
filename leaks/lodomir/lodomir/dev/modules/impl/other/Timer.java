/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.Subscribe
 */
package lodomir.dev.modules.impl.other;

import com.google.common.eventbus.Subscribe;
import java.awt.Color;
import lodomir.dev.November;
import lodomir.dev.event.EventUpdate;
import lodomir.dev.event.impl.network.EventGetPacket;
import lodomir.dev.event.impl.player.EventPreMotion;
import lodomir.dev.event.impl.render.EventRender2D;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import lodomir.dev.settings.impl.BooleanSetting;
import lodomir.dev.settings.impl.NumberSetting;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.login.server.S00PacketDisconnect;

public class Timer
extends Module {
    public NumberSetting speed = new NumberSetting("Speed", 0.1, 10.0, 2.0, 0.1);
    public BooleanSetting balance = new BooleanSetting("Balance", false);
    public BooleanSetting show = new BooleanSetting("Show Balance", true);
    private long value;
    private long motion;

    public Timer() {
        super("Timer", 0, Category.OTHER);
        this.addSettings(this.speed, this.balance, this.show);
    }

    @Override
    @Subscribe
    public void onGuiUpdate(EventUpdate e) {
        if (this.balance.isEnabled()) {
            this.show.setVisible(true);
        } else {
            this.show.setVisible(false);
        }
    }

    @Subscribe
    public void onUpdate(lodomir.dev.event.impl.game.EventUpdate e) {
        if (!this.balance.isEnabled()) {
            this.setSuffix(String.valueOf(this.speed.getValue()));
            Timer.mc.timer.timerSpeed = this.speed.getValueFloat();
        }
    }

    @Override
    @Subscribe
    public void onPreMotion(EventPreMotion event) {
        if (this.balance.isEnabled()) {
            long lastPreMotion = this.motion;
            this.motion = System.currentTimeMillis();
            if (lastPreMotion != 0L) {
                long difference = System.currentTimeMillis() - lastPreMotion;
                this.value += 50L;
                this.value -= difference;
            }
            Timer.mc.timer.timerSpeed = this.value < -1000L ? (float)this.speed.getValue() : 1.0f;
            if (this.speed.getValue() < 1.0) {
                Timer.mc.timer.timerSpeed = (float)this.speed.getValue();
            }
        }
    }

    @Override
    public void onDisable() {
        Timer.mc.timer.timerSpeed = 1.0f;
        this.value = 0L;
        super.onDisable();
    }

    @Override
    @Subscribe
    public void onGetPacket(EventGetPacket event) {
        if (event.getPacket() instanceof S00PacketDisconnect) {
            this.value = 0L;
            this.motion = 0L;
        }
    }

    @Override
    @Subscribe
    public void on2D(EventRender2D event) {
        ScaledResolution sr = new ScaledResolution(mc);
        if (this.balance.isEnabled() && this.show.isEnabled()) {
            November.INSTANCE.fm.getFont("SFUI BOLD 20").drawCenteredString("Balance: " + this.value, sr.getScaledWidth() / 2, sr.getScaledHeight() - 80, new Color(255, 255, 255, 140).getRGB());
        }
    }
}

