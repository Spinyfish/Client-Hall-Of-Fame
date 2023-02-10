/**
 * @project Myth
 * @author CodeMan
 * @at 22.08.22, 17:49
 */
package dev.myth.features.player;

import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import dev.myth.api.event.EventState;
import dev.myth.api.feature.Feature;
import dev.myth.api.utils.MovementUtil;
import dev.myth.events.UpdateEvent;
import dev.myth.settings.EnumSetting;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;

@Feature.Info(
        name = "AntiVoid",
        description = "Prevents you from falling into the void",
        category = Feature.Category.PLAYER
)
public class AntiVoidFeature extends Feature {

    public final EnumSetting<Mode> mode = new EnumSetting<>("Mode", Mode.NORMAL);

    private float lastTimer = 1f;
    private boolean setTimer = false;

    @Handler
    public final Listener<UpdateEvent> updateEventListener = event -> {
        if(event.getState() != EventState.PRE) return;

        switch (mode.getValue()) {
            case NORMAL:
                if(getPlayer().fallDistance > 3 && !MovementUtil.isBlockUnder()) {
                    getPlayer().fallDistance = 0;
                    sendPacketUnlogged(new C03PacketPlayer.C04PacketPlayerPosition(getPlayer().posX, getPlayer().posY + getPlayer().fallDistance + 2, getPlayer().posZ, false));
                }
                break;
            case WATCHDOG:
                if(getPlayer().fallDistance > 2 && getPlayer().lastTickPosY - getPlayer().posY > 0.5 && getPlayer().motionY < 0 && !MovementUtil.isBlockUnder()) {
                    if(!setTimer) {
                        lastTimer = MC.timer.timerSpeed;
                        setTimer = true;
                    }
                    setTimer(0.25F);
                    getPlayer().setPosition(getPlayer().posX, getPlayer().posY + 1, getPlayer().posZ);
                } else if(setTimer){
                    setTimer(lastTimer);
                    setTimer = false;
                }
                break;
        }
    };

    @Override
    public void onEnable() {
        super.onEnable();
        lastTimer = MC.timer.timerSpeed;
        setTimer = false;
    }

    @Override
    public String getSuffix() {
        return mode.getValue().toString();
    }

    public enum Mode {
        NORMAL("Normal"),
        WATCHDOG("Watchdog");

        private final String name;

        Mode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

}