package com.alan.clients.component.impl.statistics;

import com.alan.clients.Client;
import com.alan.clients.component.Component;
import com.alan.clients.component.impl.player.LastConnectionComponent;
import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.impl.exploit.LightningTracker;
import com.alan.clients.module.impl.exploit.StaffDetector;
import com.alan.clients.module.impl.other.*;
import com.alan.clients.module.impl.player.InventorySync;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.newevent.impl.other.ModuleToggleEvent;
import com.alan.clients.newevent.impl.other.ServerJoinEvent;
import com.alan.clients.newevent.impl.other.ServerKickEvent;
import com.alan.clients.value.Value;
import com.alan.clients.value.impl.ModeValue;
import packet.impl.client.data.ClientModuleData;
import util.time.StopWatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class StatisticsCollectionComponent extends Component {
    private final StopWatch serverTime = new StopWatch();
    private boolean saved;
    private final ArrayList<Module> modules = new ArrayList<>();

    private final Class<?>[] exempted = {
            RichPresence.class,
            SecurityFeatures.class,
            LightningTracker.class,
            StaffDetector.class,
            AntiAFK.class,
            CheatDetector.class,
            ClickSounds.class,
            Debugger.class,
            MurderMystery.class,
            PlayerNotifier.class,
            Spotify.class,
            Translator.class,
            InventorySync.class,
    };

    @EventLink()
    public final Listener<ServerJoinEvent> onServerJoin = event -> {
        saved = false;
        serverTime.reset();
        modules.clear();

        Client.INSTANCE.getModuleManager().getAll().forEach(module -> {
            if (module.isEnabled()) modules.add(module);
        });
    };

    @EventLink()
    public final Listener<ModuleToggleEvent> onModuleToggle = event -> {
        if (modules.stream().noneMatch(module -> Objects.equals(module.getDisplayName(), event.getModule().getDisplayName()))) {
            modules.add(event.getModule());
        }
    };

    @EventLink()
    public final Listener<ServerKickEvent> onServerKick = event -> {
        if (event.message.stream().anyMatch(line -> line.toLowerCase().contains("banned") ||
                line.toLowerCase().contains("kicked") || line.toLowerCase().contains("cheating"))) {

            saveData(true);
        }
    };

    @EventLink()
    public final Listener<PreMotionEvent> onPreMotion = event -> {
        if (mc.thePlayer.ticksExisted > 50 && serverTime.finished(50 * 20 * 60 * 30)) {
            saveData(false);
        }
    };

    public void saveData(boolean banned) {
        if (saved) return;
        saved = true;

        List<String> list = new ArrayList<>();

        for (Module module : modules) {
            if (module.getModuleInfo().category() == Category.RENDER || Arrays.stream(exempted).anyMatch(clazz -> clazz.equals(module.getClass()))) {
                continue;
            }

            String moduleData = module.getDisplayName();
            String valueData = "";

            for (Value<?> value : module.getAllValues()) {
                if (value instanceof ModeValue && (value.hideIf == null || !value.hideIf.getAsBoolean())) {
                    valueData += value.getName() + ": " + ((ModeValue) value).getValue().getName() + " ";
                }
            }

            list.add(moduleData + " " + valueData);
        }

        Client.INSTANCE.getNetworkManager().getCommunication().write(new ClientModuleData(banned, list, LastConnectionComponent.ip));

    }

}
