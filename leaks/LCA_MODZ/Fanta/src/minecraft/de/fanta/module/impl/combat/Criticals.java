package de.fanta.module.impl.combat;

import java.awt.Color;

import de.fanta.Client;
import de.fanta.events.Event;
import de.fanta.events.listeners.EventTick;
import de.fanta.module.Module;
import de.fanta.module.impl.combat.Killaura;
import de.fanta.setting.settings.CheckBox;
import net.minecraft.network.play.client.C03PacketPlayer;

public class Criticals extends Module {
    public Criticals() {
        super("Criticals", 0, Type.Combat, new Color(108,2,139));
    }

    @Override
    public void onEvent(Event event) {
    	if(event  instanceof EventTick) {
    		if(Killaura.hasTarget()&& mc.thePlayer.ticksExisted % 4 == 0 && mc.thePlayer.onGround && Client.INSTANCE.moduleManager.getModule("Killaura").isState() && !Client.INSTANCE.moduleManager.getModule("Flight").isState()) {
    			mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
						mc.thePlayer.posY, mc.thePlayer.posZ, true));
    			mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
						mc.thePlayer.posY + 0.05F, mc.thePlayer.posZ, false));
    			mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
						mc.thePlayer.posY, mc.thePlayer.posZ, true));
    		}
    	}
    }
}
