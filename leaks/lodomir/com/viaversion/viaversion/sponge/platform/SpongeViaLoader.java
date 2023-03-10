/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.spongepowered.api.Sponge
 *  org.spongepowered.api.event.EventManager
 */
package com.viaversion.viaversion.sponge.platform;

import com.viaversion.viaversion.SpongePlugin;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.platform.PlatformTask;
import com.viaversion.viaversion.api.platform.ViaPlatformLoader;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.HandItemProvider;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.MovementTransmitterProvider;
import com.viaversion.viaversion.sponge.listeners.UpdateListener;
import com.viaversion.viaversion.sponge.listeners.protocol1_9to1_8.BlockListener;
import com.viaversion.viaversion.sponge.listeners.protocol1_9to1_8.DeathListener;
import com.viaversion.viaversion.sponge.listeners.protocol1_9to1_8.HandItemCache;
import com.viaversion.viaversion.sponge.listeners.protocol1_9to1_8.sponge4.Sponge4ArmorListener;
import com.viaversion.viaversion.sponge.listeners.protocol1_9to1_8.sponge5.Sponge5ArmorListener;
import com.viaversion.viaversion.sponge.providers.SpongeViaMovementTransmitter;
import java.util.HashSet;
import java.util.Set;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.EventManager;

public class SpongeViaLoader
implements ViaPlatformLoader {
    private final SpongePlugin plugin;
    private final Set<Object> listeners = new HashSet<Object>();
    private final Set<PlatformTask> tasks = new HashSet<PlatformTask>();

    public SpongeViaLoader(SpongePlugin plugin) {
        this.plugin = plugin;
    }

    private void registerListener(Object listener) {
        Sponge.getEventManager().registerListeners((Object)this.plugin, this.storeListener(listener));
    }

    private <T> T storeListener(T listener) {
        this.listeners.add(listener);
        return listener;
    }

    @Override
    public void load() {
        this.registerListener(new UpdateListener());
        if (Via.getAPI().getServerVersion().lowestSupportedVersion() < ProtocolVersion.v1_9.getVersion()) {
            try {
                Class.forName("org.spongepowered.api.event.entity.DisplaceEntityEvent");
                this.storeListener(new Sponge4ArmorListener()).register();
            }
            catch (ClassNotFoundException e) {
                this.storeListener(new Sponge5ArmorListener(this.plugin)).register();
            }
            this.storeListener(new DeathListener(this.plugin)).register();
            this.storeListener(new BlockListener(this.plugin)).register();
            if (this.plugin.getConf().isItemCache()) {
                this.tasks.add(Via.getPlatform().runRepeatingSync(new HandItemCache(), 2L));
                HandItemCache.CACHE = true;
            }
        }
        if (Via.getAPI().getServerVersion().lowestSupportedVersion() < ProtocolVersion.v1_9.getVersion()) {
            Via.getManager().getProviders().use(MovementTransmitterProvider.class, new SpongeViaMovementTransmitter());
            Via.getManager().getProviders().use(HandItemProvider.class, new HandItemProvider(){

                @Override
                public Item getHandItem(UserConnection info) {
                    if (HandItemCache.CACHE) {
                        return HandItemCache.getHandItem(info.getProtocolInfo().getUuid());
                    }
                    return super.getHandItem(info);
                }
            });
        }
    }

    @Override
    public void unload() {
        this.listeners.forEach(arg_0 -> ((EventManager)Sponge.getEventManager()).unregisterListeners(arg_0));
        this.listeners.clear();
        this.tasks.forEach(PlatformTask::cancel);
        this.tasks.clear();
    }
}

