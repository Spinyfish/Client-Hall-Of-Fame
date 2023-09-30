package de.fanta.module.impl.player;




import java.awt.Color;

import de.fanta.Client;
import de.fanta.events.Event;
import de.fanta.events.listeners.EventNoClip;
import de.fanta.events.listeners.EventTick;
import de.fanta.module.Module;
import de.fanta.module.impl.combat.Killaura;
import de.fanta.setting.Setting;
import de.fanta.setting.settings.CheckBox;
import de.fanta.setting.settings.Slider;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFood;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class FastUse extends Module {
    public FastUse() {
        super("FastUse",0, Type.Player, Color.YELLOW);
    	this.settings.add(new Setting("Packets", new Slider(1, 30, 0.1, 4)));
    }
    public static double Packets;
    @Override
    public void onEvent(Event event) {
    	
    	if (event instanceof EventTick) {
    	//	mc.thePlayer.onGround = true;
    	if ( mc.thePlayer.getCurrentEquippedItem() != null && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemFood) {
			if (mc.thePlayer.isUsingItem()) {
				Packets = ((Slider) this.getSetting("Packets").getSetting()).curValue;
				for (int i = 0; i < Packets; i++) {
					mc.getNetHandler().addToSendQueue(new C03PacketPlayer());
				}
			}
			}
    	}
    }
}
