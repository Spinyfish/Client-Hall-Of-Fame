/**
 * @project Myth
 * @author CodeMan
 * @at 07.08.22, 23:18
 */
package dev.myth.features.player;

import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import dev.myth.api.event.EventState;
import dev.myth.api.feature.Feature;
import dev.myth.api.utils.MovementUtil;
import dev.myth.api.utils.PlayerUtil;
import dev.myth.events.PacketEvent;
import dev.myth.events.UpdateEvent;
import dev.myth.settings.EnumSetting;
import net.minecraft.network.play.client.C03PacketPlayer;

@Feature.Info(
        name = "NoFall",
        description = "Allows you to avoid fall damage",
        category = Feature.Category.PLAYER
)
public class NoFallFeature extends Feature {

    public final EnumSetting<Mode> mode = new EnumSetting<>("Mode", Mode.SPOOF);
    public final EnumSetting<VulcanMode> vulcanMode = new EnumSetting<>("Type", VulcanMode.FASTFALL).addDependency(() -> mode.is(Mode.VULCAN));

    public boolean can;

    @Handler
    public final Listener<UpdateEvent> updateEventListener = event -> {
        if(getPlayer() == null) return;
        if(!MovementUtil.isBlockUnder()) return;
        if(event.getState() == EventState.PRE) {
            double minFallDist = PlayerUtil.getMinFallDistForDamage();
            if(mode.is(Mode.SPOOF)) {
                if(getPlayer().fallDistance >= minFallDist) {
                    getPlayer().fallDistance = 0;
                    getPlayer().sendQueue.addToSendQueue(new C03PacketPlayer(true));
                }
            }
            if(mode.is(Mode.WATCHDOG)) {
                if(getPlayer().fallDistance >= minFallDist) {
                    getPlayer().fallDistance = 0;
                    event.setOnGround(true);
                }
            }
            if(mode.is(Mode.AAC) || mode.is(Mode.VULCAN) && vulcanMode.is(VulcanMode.FASTFALL)) {
                if(getPlayer().fallDistance >= minFallDist) {
                    getPlayer().fallDistance = 0;
                    getPlayer().motionY = -10;
                    getPlayer().motionX = 0;
                    getPlayer().motionZ = 0;
                    event.setOnGround(true);
                }
            }
            if (mode.is(Mode.COLLISON)) {
                if (getPlayer().fallDistance - getPlayer().motionY >= minFallDist && MovementUtil.isBlockUnder()) {
                    getPlayer().motionY = 0;
                    getPlayer().fallDistance = 0;
                    event.setOnGround(true);
                }
            }
            if (mode.is(Mode.COLLISON_SILENT)) {
                if (getPlayer().fallDistance > minFallDist) {
                    sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook((getPlayer().posX + getPlayer().lastTickPosX) / 2, (getPlayer().posY - (getPlayer().posY % (1 / 64.0))), (getPlayer().posZ + getPlayer().lastTickPosZ) / 2, getPlayer().rotationYaw, getPlayer().rotationPitch, true));
                    getPlayer().fallDistance = 0;
                }
            }
        }
    };

    @Handler
    public final Listener<PacketEvent> packetEventListener = event -> {
        if(getPlayer() == null) return;
        if(!MovementUtil.isBlockUnder()) return;
        if(event.getState() == EventState.SENDING) {
            if(event.getPacket() instanceof C03PacketPlayer) {
                C03PacketPlayer packet = event.getPacket();
                double minFallDist = PlayerUtil.getMinFallDistForDamage();
                if(mode.is(Mode.EDIT) && getPlayer().fallDistance >= minFallDist) {
                    packet.onGround = true;
                }
                
                if (mode.is(Mode.MATRIX) || mode.is(Mode.VULCAN) && vulcanMode.is(VulcanMode.NORMAL)) {
                    if (!can && !getPlayer().onGround && getPlayer().fallDistance >= minFallDist)  {
                        packet.setOnGround(false);
                        can = true;
                    }
                    if (can) {
                        packet.setOnGround(true);
                        getPlayer().fallDistance = 0;
                        can = false;
                    }
                }
            }
        }
    };

    @Override
    public String getSuffix() {
        return mode.getValue().toString();
    }

    public enum Mode {
        SPOOF("Spoof"),
        WATCHDOG("Watchdog"),
        EDIT("Edit"),
        AAC("AAC"),
        COLLISON("Collison"),
        COLLISON_SILENT("Collison Silent"),
        MATRIX("Matrix"),
        VULCAN("Vulcan");

        private final String name;

        Mode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public enum VulcanMode {
        FASTFALL("Fast Fall"),
        NORMAL("Basic");

        private final String name;

        VulcanMode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

}
