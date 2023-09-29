package de.fanta.module.impl.movement;


import java.awt.Color;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;



import de.fanta.Client;
import de.fanta.events.Event;
import de.fanta.events.listeners.EventReceivedPacket;
import de.fanta.events.listeners.EventTick;
import de.fanta.module.Module;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C16PacketClientStatus.EnumState;

public class InvMove extends Module {
    public InvMove() {
        super("InvMove", 0, Type.Player, Color.YELLOW);

        /*
        Always use new Setting() in combination with setting name and the type of setting (CheckBox, Slider or DropdownBox)
        There is no limit for how many settings + you can use the same names in different modules.
        Examples for how to setup settings are shown here
         */

    }

    public void onEvent(Event event) {
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	if (event instanceof EventTick) {
			Packet p = EventReceivedPacket.INSTANCE.getPacket();
			
		//	mc.getNetHandler().addToSendQueue(new C16PacketClientStatus());
			//if(mc.thePlayer.)
			//mc.getNetHandler().addToSendQueue(new C16PacketClientStatus(EnumState.OPEN_INVENTORY_ACHIEVEMENT));
			//mc.getNetHandler().addToSendQueue(new C0DPacketCloseWindow());

			//if (p instanceof C16PacketClientStatus) {

			//	final C16PacketClientStatus clientStatus = (C16PacketClientStatus) p;
			//	if (clientStatus.getStatus() == C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT) {
			//		EventReceivedPacket.INSTANCE.setCancelled(true);
			//	}
				
		//	}
			
    	}
        if (event instanceof EventTick && event.isPre()) {
            /*
            Use the right cast to get the value of every setting
            Can be used in combination with Client.INSTANCE.modulemanager.getModule() to get values from other modules
             */
            //System.out.println(((DropdownBox) this.getSetting("Modes").getSetting()).curOption);
            if (mc.currentScreen instanceof GuiChat) return;
        
//         if(!Client.INSTANCE.moduleManager.getModule("ClickGui").isState()) {
//             	this.setIngameFocus();
//         }
         	
         //   }
            
            mc.gameSettings.keyBindForward.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindForward);
            mc.gameSettings.keyBindBack.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindBack);
            mc.gameSettings.keyBindRight.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindRight);
            mc.gameSettings.keyBindLeft.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindLeft);
            mc.gameSettings.keyBindSprint.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindSprint);
            mc.gameSettings.keyBindJump.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindJump);
        }
    }


public void setIngameFocus() {
	 
	if (Display.isActive()) {
		
		if (!mc.inGameHasFocus) {
			mc.inGameHasFocus = true;
			mc.mouseHelper.grabMouseCursor();
		//	mc.displayGuiScreen(null);
			mc.leftClickCounter = 10000;
		}
	}
}
}

