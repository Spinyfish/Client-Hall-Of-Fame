package felix.command.impl;

import java.util.List;

import org.lwjgl.input.Keyboard;

import felix.Client;
import felix.api.annotations.Handler;
import felix.command.Command;
import felix.events.packet.EventPacketReceive;
import felix.events.packet.EventPacketSend;
import felix.events.player.EventMove;
import felix.gui.notification.Notifications;
import felix.util.other.Logger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.world.World;

public class Teleport extends Command {
	
	EntityLivingBase ent;
	
	private Minecraft mc = Minecraft.getMinecraft();
	
	double x, y, z;
	
	public boolean tp;

	public Teleport() {
		super("tp");
	}

	@Override
	public String usage() {
		return "tp <x> <z> or (player name)";
	}

	@Override
	public void executeCommand(String[] commandArguments) {
		Client.INSTANCE.getEventManager().register(this);
		tp = false;
        mc.getNetHandler().addToSendQueueNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
        mc.getNetHandler().addToSendQueueNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.18D, mc.thePlayer.posZ, true));
        mc.getNetHandler().addToSendQueueNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.08D, mc.thePlayer.posZ, true));
        Logger.print("Teleporting, please wait.");
		final EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		final World world = Minecraft.getMinecraft().theWorld;
        if(commandArguments.length > 2 || commandArguments.length < 1) {
            printUsage();
        }
        if (commandArguments.length == 2) {
            String arg1 = commandArguments[0];
            String arg2 = commandArguments[1];
            x = Double.parseDouble(arg1);
            y = player.posY;
            z = Double.parseDouble(arg2);
            //Minecraft.getMinecraft().thePlayer.connection.getNetworkManager().sendPacket(new CPacketPlayer.Position(x, y, z, Minecraft.getMinecraft().thePlayer.onGround));         
            
            if (tp) {
            	player.setPosition(x, y + 50, z);
            }
             
            //Client.INSTANCE.getEventManager().register(this);
            Logger.print("Teleported you to " + arg1 + " " + arg2);
        }
        if (commandArguments.length == 1) {
        	for (final Entity ent : world.getLoadedEntityList()) {
        		if (ent instanceof EntityPlayer) {
	        		final EntityPlayer entPlayer = (EntityPlayer) ent;
	        		if (commandArguments[0].equalsIgnoreCase(entPlayer.getGameProfile().getName())) {
	        			if (tp) {
	        				player.setLocationAndAngles(entPlayer.posX, entPlayer.posY, entPlayer.posZ, player.rotationYaw, player.rotationPitch);
	        				Client.INSTANCE.getEventManager().unregister(this);
	        			}
	        		}
        		}
        	}
        }
	}
	
	@Handler
	public void onSendPacket(final EventPacketSend event) {
		if (event.getPacket() instanceof C03PacketPlayer && !tp) {
			event.setCancelled(true);
		}
	}
	
	@Handler
	public void onReceivePacket(final EventPacketReceive event) {
		if (event.getPacket() instanceof S08PacketPlayerPosLook) {
			if (!tp) {
				tp = true;
			}
		}
	}
	
	@Handler
	public void onMove(final EventMove event) {
		if (!tp) {
			event.setCancelled(true);
		}
	}
}
