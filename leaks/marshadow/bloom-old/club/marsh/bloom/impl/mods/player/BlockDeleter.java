package club.marsh.bloom.impl.mods.player;

import org.lwjgl.input.Keyboard;

import com.google.common.eventbus.Subscribe;

import club.marsh.bloom.api.module.Category;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.impl.events.PacketEvent;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;


public class BlockDeleter extends Module
{
	public BlockDeleter()
	{
		super("BlockDeleter", Keyboard.KEY_NONE, Category.PLAYER);
	}
	
	@Subscribe
	public void onPacket(PacketEvent e)
	{
		if (e.getPacket() instanceof C08PacketPlayerBlockPlacement)
		{
			mc.theWorld.setBlockToAir(((C08PacketPlayerBlockPlacement) e.getPacket()).getPosition());
			e.setCancelled(true);
		}
	}
}
