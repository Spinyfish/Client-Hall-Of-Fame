package club.marsh.bloom.impl.mods.player;

import org.lwjgl.input.Keyboard;

import com.google.common.eventbus.Subscribe;

import club.marsh.bloom.api.module.Category;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.impl.events.PacketEvent;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;


public class GhostBlockCreator extends Module {
	public GhostBlockCreator() {
		super("GhostblockMaker",Keyboard.KEY_NONE,Category.PLAYER);
	}
	@Subscribe
	public void onPacket(PacketEvent e) {
		if (e.getPacket() instanceof C08PacketPlayerBlockPlacement) {
			e.setCancelled(true);
		}
	}
}
