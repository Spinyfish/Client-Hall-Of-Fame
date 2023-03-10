/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.Subscribe
 */
package lodomir.dev.modules.impl.other;

import com.google.common.eventbus.Subscribe;
import lodomir.dev.November;
import lodomir.dev.event.impl.EventGetPackets;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import lodomir.dev.ui.notification.Notification;
import lodomir.dev.ui.notification.NotificationManager;
import lodomir.dev.ui.notification.NotificationType;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class AntiLag
extends Module {
    public AntiLag() {
        super("AntiLag", 0, Category.OTHER);
    }

    @Subscribe
    public void onGetPackets(EventGetPackets event) {
        if (November.INSTANCE.getModuleManager().getModule("Speed").isEnabled() || November.INSTANCE.getModuleManager().getModule("Fly").isEnabled()) {
            if (event.getPackets() instanceof S08PacketPlayerPosLook) {
                November.INSTANCE.getModuleManager().getModule("Speed").setEnabled(false);
                November.INSTANCE.getModuleManager().getModule("Fly").setEnabled(false);
            }
        } else {
            NotificationManager.show(new Notification(NotificationType.WARNING, "LagBack Detected", "Disabled Speed and Fly", 2));
        }
    }
}

