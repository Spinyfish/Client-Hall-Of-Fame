/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.Subscribe
 */
package lodomir.dev.modules.impl.other;

import com.google.common.eventbus.Subscribe;
import java.util.Objects;
import lodomir.dev.event.impl.network.EventGetPacket;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import lodomir.dev.ui.notification.Notification;
import lodomir.dev.ui.notification.NotificationManager;
import lodomir.dev.ui.notification.NotificationType;
import net.minecraft.network.play.server.S29PacketSoundEffect;

public class LightningDetector
extends Module {
    public LightningDetector() {
        super("LightningDetector", 0, Category.OTHER);
    }

    @Override
    @Subscribe
    public void onGetPacket(EventGetPacket event) {
        S29PacketSoundEffect packet;
        if (event.getPacket() instanceof S29PacketSoundEffect && Objects.equals((packet = (S29PacketSoundEffect)event.getPacket()).getSoundName(), "ambient.weather.thunder")) {
            NotificationManager.show(new Notification(NotificationType.WARNING, "Lightning Detected", String.format("Lightning struck at: %s, %s, %s", Math.round(packet.getX()), Math.round(packet.getY()), Math.round(packet.getZ())), 5));
        }
    }
}

