package de.fanta.module.impl.player;


import java.awt.Color;

import de.fanta.Client;
import de.fanta.events.Event;
import de.fanta.events.listeners.EventTick;
import de.fanta.module.Module;
import de.fanta.module.impl.combat.Killaura;
import de.fanta.setting.Setting;
import de.fanta.setting.settings.CheckBox;
import de.fanta.setting.settings.DropdownBox;
import net.minecraft.network.play.client.C03PacketPlayer;

public class Regen extends Module {
    public Regen() {
        super("Regen",0, Type.Player, Color.white);
        this.settings.add(new Setting("Modes", new DropdownBox("Intave", new String[] { "Intave" })));
    }

    @Override
    public void onEvent(Event event) {
    	if(event instanceof EventTick) {
    		 if (this.mc.thePlayer.getHealth() < 20.0F && this.mc.thePlayer.getFoodStats().getFoodLevel() > 19) {
    			 for (int i = 0; i < 100; i++) {
                     this.mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
                 }
    		 }
    	}
    }
}
