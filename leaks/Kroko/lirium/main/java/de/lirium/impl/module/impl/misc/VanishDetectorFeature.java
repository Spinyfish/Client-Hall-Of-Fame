package de.lirium.impl.module.impl.misc;

import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import de.lirium.base.helper.TimeHelper;
import de.lirium.base.setting.Dependency;
import de.lirium.base.setting.Value;
import de.lirium.base.setting.impl.CheckBox;
import de.lirium.base.setting.impl.ComboBox;
import de.lirium.base.setting.impl.SliderSetting;
import de.lirium.impl.events.PacketEvent;
import de.lirium.impl.events.UpdateEvent;
import de.lirium.impl.module.ModuleFeature;
import de.lirium.util.mojang.MojangUtil;
import god.buddy.aot.BCompiler;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketTabComplete;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.network.play.server.SPacketTabComplete;

import java.io.IOException;
import java.util.*;

@ModuleFeature.Info(name = "Vanish Detector", description = "Shows if invisible players are in the game", category = ModuleFeature.Category.MISCELLANEOUS)
public class VanishDetectorFeature extends ModuleFeature {
    @Value(name = "Mode")
    private final ComboBox<String> mode = new ComboBox<>("Latency", new String[]{"Gomme"});

    @Value(name = "Search Delay")
    private final SliderSetting<Long> searchDelay = new SliderSetting<>(60000L, 1000L, 100000L, new Dependency<>(mode, "Gomme"));

    @Value(name = "Send Message", visual = true)
    private final CheckBox sendMessage = new CheckBox(true);

    private final HashMap<UUID, String> names = new HashMap<>();

    private final List<String> vanished = new ArrayList<>();

    private final TimeHelper gommeTimeHelper = new TimeHelper();

    private boolean sentSearch = false;

    @EventHandler
    public Listener<PacketEvent> packetEventListener = e -> {
        final Packet<?> packet = e.packet;
        switch (mode.getValue()) {
            case "Latency":
                if (packet instanceof SPacketPlayerListItem) {
                    final SPacketPlayerListItem listItem = (SPacketPlayerListItem) packet;
                    if (listItem.getAction() == SPacketPlayerListItem.Action.UPDATE_LATENCY) {
                        vanished.clear();
                        final StringJoiner joiner = new StringJoiner(", ");
                        for (SPacketPlayerListItem.AddPlayerData data : listItem.getEntries()) {
                            if (mc.getConnection() == null) break;
                            final UUID uuid = data.getProfile().getId();
                            if (!mc.getConnection().playerInfoMap.containsKey(uuid)) {
                                if (!names.containsKey(uuid)) {
                                    try {
                                        names.put(uuid, MojangUtil.getName(uuid));
                                    } catch (IOException ex) {
                                        names.put(uuid, "unknown");
                                    }
                                }
                                joiner.add(names.get(uuid));
                                vanished.add(names.get(uuid));
                            }
                        }
                        if (!vanished.isEmpty()) {
                            if (sendMessage.getValue())
                                sendMessage("§cFound vanished players (" + vanished.size() + ")" + ": §f" + joiner);
                        }
                    }
                }
                break;
            case "Gomme":
               doGomme(packet);
                break;
        }
    };

    @EventHandler
    public Listener<UpdateEvent> updateEventListener = e -> {
        setSuffix(mode.getValue(), String.valueOf(vanished.size()));
        switch (mode.getValue()) {
            case "Gomme":
                doGomme();
                break;
        }
    };

    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    private void doGomme(Packet<?> packet) {
        if(packet instanceof SPacketTabComplete) {
            final SPacketTabComplete tabComplete = (SPacketTabComplete) packet;
            if(!sentSearch || mc.isSingleplayer()) return;
            vanished.clear();
            final StringJoiner joiner = new StringJoiner(", ");
            for(String match : tabComplete.getMatches()) {
                boolean isVanish = true;
                for(NetworkPlayerInfo info : getPlayer().connection.getPlayerInfoMap()) {
                    if(info.getGameProfile().getName().equalsIgnoreCase(match))
                        isVanish = false;
                }
                if(isVanish) {
                    joiner.add(match);
                    vanished.add(match);
                }
            }
            if(!vanished.isEmpty()) {
                if(sendMessage.getValue())
                    sendMessage("§cFound vanished players (" + vanished.size() + ")" + ": §f" + joiner);
            }
            sentSearch = false;
        }
    }

    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    private void doGomme() {
        if(gommeTimeHelper.hasReached(searchDelay.getValue())) {
            sendPacketUnlogged(new CPacketTabComplete("/stats ", getPlayer().getPosition(), false));
            sentSearch = true;
        }
    }

    @Override
    public void onEnable() {
        sentSearch = false;
        super.onEnable();
    }
}