package club.marsh.bloom.impl.mods.combat;

import org.lwjgl.input.Keyboard;

import com.google.common.eventbus.Subscribe;

import club.marsh.bloom.api.module.Category;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.impl.events.PacketEvent;
import club.marsh.bloom.api.value.ModeValue;
import club.marsh.bloom.api.value.NumberValue;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;

public class Velocity extends Module
{
	public Velocity()
	{
		super("Velocity", Keyboard.KEY_NONE, Category.COMBAT);
	}
	
	ModeValue mode = new ModeValue("Mode", "Packet", new String[] {"Packet", "Cancel", "Watchdog", "Matrix"});
	NumberValue vertical = new NumberValue("Vertical", 100, 0, 500, () -> mode.getMode() == "Packet");
	NumberValue horizontal = new NumberValue("Horizontal", 0, 0, 500, () -> mode.getMode() == "Packet");
	int tookvelo;
	
	@Subscribe
	public void onPacket(PacketEvent e)
	{
		if (mode == null || mc.thePlayer == null || mc.theWorld == null)
		{
			return;
		}
		
		switch (mode.mode)
		{
			case "Cancel":
			{
				if (e.getPacket() instanceof S12PacketEntityVelocity)
				{
					S12PacketEntityVelocity velo = ((S12PacketEntityVelocity) (e.getPacket()));
					
					if (velo.getEntityID() != mc.thePlayer.getEntityId())
					{
						return;
					}
					
					e.setCancelled(true);
				}
				
				else if (e.getPacket() instanceof S27PacketExplosion)
				{
					e.setCancelled(true);
				}
				
				break;
			}
			
			case "Packet":
			{
				if (e.getPacket() instanceof S27PacketExplosion)
				{
					if (horizontal.getValDouble() == 0 && vertical.getValDouble() == 0) {
						e.setCancelled(true); 
						return;
					}
					
					S27PacketExplosion exp = ((S27PacketExplosion) (e.getPacket()));
					exp.setPosX(exp.getX()*(horizontal.getValDouble() / 100));
					exp.setPosZ(exp.getZ()*(horizontal.getValDouble() / 100));
					exp.setPosY(exp.getY()*(vertical.getValDouble() / 100));
				}
				
				else if (e.getPacket() instanceof S12PacketEntityVelocity)
				{
					S12PacketEntityVelocity velo = (S12PacketEntityVelocity) e.getPacket();
					
					if (velo.getEntityID() != mc.thePlayer.getEntityId())
					{
						return;
					}
					
					if (horizontal.getValDouble() == 0 && vertical.getValDouble() == 0)
					{
						e.setCancelled(true); 
						return;
					}
					
					velo.setMotionX((int) (velo.getMotionX() * (horizontal.getValDouble() / 100)));
					velo.setMotionZ((int) (velo.getMotionZ() * (horizontal.getValDouble() / 100)));
					velo.setMotionY((int) (velo.getMotionY() * (vertical.getValDouble() / 100)));
				}
				
				break;
			}
			
			case "Watchdog":
			{
				if (e.getPacket() instanceof S12PacketEntityVelocity)
				{
					S12PacketEntityVelocity velo = ((S12PacketEntityVelocity) (e.getPacket()));
	            	
					if (velo.getEntityID() != mc.thePlayer.getEntityId())
					{
	            		return;
					}
					
	            	if (mc.thePlayer.onGround)
	            	{
	            		velo.setMotionX(0);
	            		velo.setMotionZ(0);
	            	}
	            	
	            	else
	            	{
	            		e.setCancelled(true);
	            	}
				}
				
				else if (e.getPacket() instanceof S27PacketExplosion)
				{
					S27PacketExplosion exp = (S27PacketExplosion) e.getPacket();
					
	            	if (mc.thePlayer.onGround)
	            	{
	            		exp.setPosX(0);
	            		exp.setPosZ(0);
	            	}
	            	
	            	else
	            	{
	            		e.setCancelled(true);
	            	}
				}
				
				break;
			}
			
			case "Matrix":
				if (e.getPacket() instanceof S12PacketEntityVelocity) {
					S12PacketEntityVelocity velo = ((S12PacketEntityVelocity) (e.getPacket()));
	            	if (velo.getEntityID() != mc.thePlayer.getEntityId())
	            		return;
					tookvelo = mc.thePlayer.ticksExisted;
					velo.setMotionX(-(velo.getMotionX()));
					velo.setMotionZ(-(velo.getMotionZ()));
				}
				break;
		}
	}
	
	@Override
	public void addModesToModule() {
		autoSetName(mode);
	}
}
