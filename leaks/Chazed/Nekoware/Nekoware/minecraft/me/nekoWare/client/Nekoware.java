
package me.nekoWare.client;

import me.rpc;
import me.hippo.api.lwjeb.bus.PubSub;
import me.hippo.api.lwjeb.configuration.BusConfigurations;
import me.hippo.api.lwjeb.configuration.config.impl.BusPubSubConfiguration;
import me.hippo.api.lwjeb.message.publish.impl.StandardMessagePublisher;
import me.hippo.api.lwjeb.message.scan.impl.FieldBasedMessageScanner;
import me.hippo.api.lwjeb.subscribe.impl.StrongReferencedListenerSubscriber;
import me.nekoWare.client.event.Event;
import me.nekoWare.client.module.Module;
import me.nekoWare.client.module.ModuleManager;
import me.nekoWare.client.module.misc.Commands;
import me.nekoWare.client.option.OptionManager;
import me.nekoWare.proxymanager.ProxyFileManager;
import net.minecraft.client.main.Main;
import org.lwjgl.opengl.Display;

import java.net.Proxy;

public enum Nekoware {
    INSTANCE(String.format("1", Main.build_type), Commands.clientName, Commands.editionName);
    public Proxy proxy = Proxy.NO_PROXY;
    private OptionManager settingsManager;
    private String buildNumber;
    private ModuleManager moduleManager;
    private final PubSub<Event> EVENT_PUBSUB = new PubSub<>(
            new BusConfigurations.Builder().setConfiguration(BusPubSubConfiguration.class, () -> {
                BusPubSubConfiguration busPubSubConfiguration = new BusPubSubConfiguration();
                busPubSubConfiguration.setPublisher(new StandardMessagePublisher<>());
                busPubSubConfiguration.setSubscriber(new StrongReferencedListenerSubscriber<>());
                busPubSubConfiguration.setScanner(new FieldBasedMessageScanner<>());
                return busPubSubConfiguration;
            }).build());

    Nekoware(String buildNumber, String clientName, String edition) {
        this.buildNumber = buildNumber;
        Commands.editionName = edition;
    }

    public String getEdition() {
        return Commands.editionName;
    }

    public String getClientName() {
        return Commands.clientName;
    }

    public ModuleManager getModuleManager() {
        return this.moduleManager;
    }

    public String getVersion() {
        return this.buildNumber;
    }

    public PubSub<Event> getEventBus() {
        return EVENT_PUBSUB;
    }

    public void handleKeyPress(final int var1) {
        for (final Module mod : this.getModuleManager().getModules()) {
            if (mod.getKey() != var1 || mod.getKey() == 0) {
                continue;
            }
            mod.toggle();
        }
    }

    public void init() {
        Display.setTitle(this.getClientName() + " " + this.getVersion());
        if (this.moduleManager == null) {
            this.settingsManager = new OptionManager();
            this.moduleManager = new ModuleManager();
        }
    }

    public OptionManager getSettingsManager() {
        return settingsManager;
    }

    private ProxyFileManager fileMgr = new ProxyFileManager();

    public ProxyFileManager getFileMgr() {
        return fileMgr;
    }
}
