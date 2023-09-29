package me.jinthium.straight.impl.modules.ghost;


import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.impl.event.game.PlayerAttackEvent;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.event.network.PacketEvent;
import me.jinthium.straight.impl.settings.ModeSetting;
import me.jinthium.straight.impl.settings.NumberSetting;
import me.jinthium.straight.impl.utils.network.PacketUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0BPacketEntityAction;

public class WTap extends Module {

    private final ModeSetting modeSetting = new ModeSetting("Mode", "Legit", "Legit", "Silent");
    private final NumberSetting chance = new NumberSetting("WTap Chance", 100, 0, 100, 1);
    private boolean unSprint, wTap, sprinting;
    private int ticks;

    public WTap(){
        super("WTap", Category.GHOST);
        this.addSettings(modeSetting, chance);
    }

    @Callback
    final EventCallback<PlayerAttackEvent> playerAttackEventEventCallback = event -> {
        ticks = 0;
        if(modeSetting.is("Legit")){
            wTap = Math.random() * 100 < chance.getValue();

            if (!wTap)
                return;

            if (mc.thePlayer.isSprinting() || mc.gameSettings.keyBindSprint.isKeyDown()) {
                mc.gameSettings.keyBindSprint.pressed = true;
                unSprint = true;
            }
        }
    };

    @Callback
    final EventCallback<PacketEvent> packetEventEventCallback = event -> {
        if(event.getPacketState() == PacketEvent.PacketState.SENDING){
            final Packet<?> packet = event.getPacket();

            if (packet instanceof C0BPacketEntityAction wrapper) {
                switch (wrapper.getAction()) {
                    case START_SPRINTING -> sprinting = true;
                    case STOP_SPRINTING -> sprinting = false;
                }
            }
        }
    };

    @Callback
    final EventCallback<PlayerUpdateEvent> playerUpdateEventCallback = event ->  {
        if(event.isPre()){
            switch(modeSetting.getMode()){
                case "Legit" -> {
                    if (!wTap)
                        return;

                    if (unSprint) {
                        mc.gameSettings.keyBindSprint.pressed = false;
                        unSprint = false;
                    }
                }
                case "Silent" -> {
                    ticks++;

                    switch (ticks) {
                        case 1 -> {
                            wTap = Math.random() * 100 < chance.getValue();
                            PacketUtil.sendPacket(new C0BPacketEntityAction(mc.thePlayer,
                                    sprinting ? C0BPacketEntityAction.Action.STOP_SPRINTING : C0BPacketEntityAction.Action.START_SPRINTING));
                        }
                        case 2 -> {
                            if (!sprinting)
                                PacketUtil.sendPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
                        }
                    }
                }
            }
        }
    };
}
