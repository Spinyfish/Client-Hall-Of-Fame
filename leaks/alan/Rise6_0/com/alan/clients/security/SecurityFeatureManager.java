package com.alan.clients.security;

import com.alan.clients.Client;
import com.alan.clients.module.impl.other.SecurityFeatures;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.packet.PacketReceiveEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import org.atteo.classindex.ClassIndex;

import java.util.ArrayList;

public final class SecurityFeatureManager extends ArrayList<SecurityFeature> {

    private SecurityFeatures features;

    public SecurityFeatureManager() {
        super();
    }

    public void init() {
        Client.INSTANCE.getEventBus().register(this);

        this.features = Client.INSTANCE.getModuleManager().get(SecurityFeatures.class);

        if (this.features == null) return;

        ClassIndex.getSubclasses(SecurityFeature.class, SecurityFeature.class.getClassLoader()).forEach(clazz -> {
            try {
                this.add(clazz.getConstructor().newInstance());
            } catch (final Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    public boolean isInsecure(final Packet<?> packet) {
        // Notification
        return this.features != null && this.features.isEnabled()
                && !Minecraft.getMinecraft().isSingleplayer()
                && this.stream().anyMatch(feature -> feature.handle(packet));
    }

    @EventLink()
    public final Listener<PacketReceiveEvent> onPacketReceive = event -> {
        event.setCancelled(isInsecure(event.getPacket()));
    };
}
