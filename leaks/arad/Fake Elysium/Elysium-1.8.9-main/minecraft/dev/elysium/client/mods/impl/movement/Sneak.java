package dev.elysium.client.mods.impl.movement;

import dev.elysium.base.events.types.EventTarget;
import dev.elysium.base.mods.Category;
import dev.elysium.base.mods.Mod;
import dev.elysium.base.mods.settings.BooleanSetting;
import dev.elysium.base.mods.settings.ModeSetting;
import dev.elysium.client.events.EventMotion;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;

public class Sneak extends Mod {
    public ModeSetting mode = new ModeSetting("Mode",this,"Normal","Spam","NCP");
    public BooleanSetting render = new BooleanSetting("Render",true,this);
    public boolean sneaking = false;
    public Sneak() {
        super("Sneak","Sneaks serverside", Category.MOVEMENT);
    }

    public void onEnable() {
        sneaking = false;
        if(mode.is("Normal"))
            mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
    }

    public void onDisable() {
        mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
    }

    @EventTarget
    public void onEventMotion(EventMotion e) {
        if(render.isEnabled())
            mc.thePlayer.posY -= 0.075;
        switch(mode.getMode()) {
            case "NCP":
                if(e.isPost())
                    mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
                else
                    mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
                break;
            case "Spam":
                if(e.isPre()) {
                    if(mc.thePlayer.ticksExisted % 4 == 0)
                        mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
                    else if(mc.thePlayer.ticksExisted % 4 == 2)
                        mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
                }
                break;
        }
    }
}
