package tech.dort.dortware.impl.modules.combat;

import com.google.common.eventbus.Subscribe;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.api.property.impl.EnumValue;
import tech.dort.dortware.api.property.impl.interfaces.INameable;
import tech.dort.dortware.impl.events.PacketEvent;
import tech.dort.dortware.impl.events.UpdateEvent;
import tech.dort.dortware.impl.utils.world.EntityData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class AntiBot extends Module {

    private final HashMap<EntityPlayer, EntityData> trackedMovements = new HashMap<>();
    private final List<Integer> invalidEntityIDs = new ArrayList<>();

    private final EnumValue<Mode> enumValue = new EnumValue<>("Mode", this, AntiBot.Mode.values());

    public AntiBot(ModuleData moduleData) {
        super(moduleData);
        register(enumValue);
    }

    @Subscribe
    public void onPacket(PacketEvent packetEvent) {
        if (enumValue.getValue() == Mode.MINEPLEX) {
            if (packetEvent.getPacket() instanceof S0CPacketSpawnPlayer) {
                S0CPacketSpawnPlayer packetSpawnPlayer = packetEvent.getPacket();
                if (packetSpawnPlayer.func_148944_c().size() < 3) {
                    invalidEntityIDs.add(packetSpawnPlayer.getEntityID());
                }
            }
        }
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        Mode mode = enumValue.getValue();
        switch (mode) {
            case MINEPLEX: {
                try {
                    for (EntityPlayer player : mc.theWorld.playerEntities.stream().filter(player -> player != mc.thePlayer).collect(Collectors.toList())) {
                        player.setValid(!invalidEntityIDs.contains(player.getEntityId()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case HYPIXEL: {
                for (EntityPlayer player : mc.theWorld.playerEntities.stream().filter(player -> player != mc.thePlayer).collect(Collectors.toList())) {
                    NetworkPlayerInfo info = mc.getNetHandler().func_175102_a(player.getUniqueID());
                    player.setValid(info != null && info.getResponseTime() == 1);
                }
                break;
            }
        }
    }

    @Override
    public void onDisable() {
        for (EntityPlayer entityPlayer : mc.theWorld.playerEntities) {
            entityPlayer.setValid(true);
        }
        trackedMovements.clear();
        invalidEntityIDs.clear();
    }

    @Override
    public String getSuffix() {
        return " \2477" + enumValue.getValue().getDisplayName();
    }

    public enum Mode implements INameable {
        MINEPLEX("Mineplex"), HYPIXEL("Hypixel");
        private final String name;

        Mode(String name) {
            this.name = name;
        }

        @Override
        public String getDisplayName() {
            return name;
        }
    }
}