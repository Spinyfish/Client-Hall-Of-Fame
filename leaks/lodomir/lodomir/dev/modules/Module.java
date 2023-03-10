/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.modules;

import java.util.ArrayList;
import java.util.List;
import lodomir.dev.November;
import lodomir.dev.event.EventUpdate;
import lodomir.dev.event.impl.game.EventRenderWorld;
import lodomir.dev.event.impl.game.EventWorldChange;
import lodomir.dev.event.impl.network.EventGetPacket;
import lodomir.dev.event.impl.network.EventSendPacket;
import lodomir.dev.event.impl.player.EventCollideBlock;
import lodomir.dev.event.impl.player.EventMove;
import lodomir.dev.event.impl.player.EventPostMotion;
import lodomir.dev.event.impl.player.EventPreMotion;
import lodomir.dev.event.impl.player.EventStrafe;
import lodomir.dev.event.impl.render.EventRender2D;
import lodomir.dev.event.impl.render.EventRender3D;
import lodomir.dev.modules.Category;
import lodomir.dev.settings.Setting;
import lodomir.dev.ui.clickgui.Clickgui;
import lodomir.dev.ui.notification.Notification;
import lodomir.dev.ui.notification.NotificationManager;
import lodomir.dev.ui.notification.NotificationType;
import lodomir.dev.utils.file.FileHandler;
import lodomir.dev.utils.render.animations.Animate;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;

public class Module {
    public String name;
    public int key;
    public boolean enabled;
    public boolean toggled;
    private Animate animatorX;
    private Animate animatorY;
    public boolean visible;
    public Category category;
    public static Minecraft mc = Minecraft.getMinecraft();
    public boolean expanded;
    public int index;
    public boolean progress;
    public List<Setting> settings = new ArrayList<Setting>();
    private FileHandler file = new FileHandler();
    public String suffix;

    public Module(String name, int key, Category category) {
        this.name = name;
        this.key = key;
        this.category = category;
        this.animatorX = new Animate().setMax(1.0f).setMin(0.0f);
        this.animatorY = new Animate().setMax(1.0f).setMin(0.0f);
    }

    public List<Setting> getSettings() {
        return this.settings;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            this.enabled = enabled;
            if (enabled) {
                this.onEnable();
            } else {
                this.onDisable();
            }
        }
    }

    public void toggle() {
        boolean bl = this.enabled = !this.enabled;
        if (this.enabled) {
            this.onEnable();
        } else {
            this.onDisable();
        }
    }

    public void onEnable() {
        November.INSTANCE.getBus().register((Object)this);
        if (November.INSTANCE.getModuleManager().getModule("Notifications").isEnabled()) {
            NotificationManager.show(new Notification(NotificationType.INFO, "Enabled", this.name, 1));
        }
        this.file.save();
    }

    public void onDisable() {
        November.INSTANCE.getBus().unregister((Object)this);
        if (November.INSTANCE.getModuleManager().getModule("Notifications").isEnabled()) {
            NotificationManager.show(new Notification(NotificationType.INFO, "Disabled", this.name, 1));
        }
        this.file.save();
    }

    public void addSetting(Setting setting) {
        this.settings.add(setting);
    }

    public void addSettings(Setting ... settings) {
        for (Setting setting : settings) {
            this.addSetting(setting);
        }
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void sendPacket(Packet p) {
        mc.getNetHandler().addToSendQueue(p);
    }

    public void sendPacketSilent(Packet p) {
        mc.getNetHandler().addToSendQueueSilent(p);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getKey() {
        return this.key;
    }

    public void setKey(int key) {
        this.key = key;
        this.file.save();
    }

    public Category getCategory() {
        return this.category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getDisplayName() {
        return this.getName() + (this.suffix != null ? " \u00a77" + this.suffix : "");
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public void onUpdate(EventUpdate event) {
    }

    public void onGuiUpdate(EventUpdate e) {
        if (this.enabled) {
            this.onUpdate(e);
        }
        if (Module.mc.currentScreen instanceof lodomir.dev.ui.click.Clickgui || Module.mc.currentScreen instanceof Clickgui) {
            this.onGuiUpdate(e);
        }
        this.onUpdateAlways();
    }

    public void onUpdateAlways() {
    }

    public void onMove(EventMove event) {
    }

    public void onStrafe(EventStrafe event) {
    }

    public void onWorldChange(EventWorldChange event) {
    }

    public void onRenderWorld(EventRenderWorld event) {
    }

    public void onBlockCollide(EventCollideBlock event) {
    }

    public void onSendPacket(EventSendPacket event) {
    }

    public void onGetPacket(EventGetPacket event) {
    }

    public void onPreMotion(EventPreMotion event) {
    }

    public void onPostMotion(EventPostMotion event) {
    }

    public void on3D(EventRender3D event) {
    }

    public void on2D(EventRender2D event) {
    }
}

