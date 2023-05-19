package club.marsh.bloom.impl.mods.combat;

import java.util.ArrayList;


import club.marsh.bloom.impl.events.PacketEvent;
import net.minecraft.network.play.server.S14PacketEntity;
import org.lwjgl.input.Keyboard;

import com.google.common.eventbus.Subscribe;

import club.marsh.bloom.api.module.Category;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.impl.events.UpdateEvent;
import club.marsh.bloom.api.value.BooleanValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class AntiBot extends Module
{
	public AntiBot()
	{
		super("Anti Bot", Keyboard.KEY_NONE, Category.COMBAT);
	}
	
    public static ArrayList<Entity> invalid = new ArrayList<>();
	public BooleanValue soundCheck = new BooleanValue("Sound Check", true, () -> true);
	public BooleanValue invisibleCheck = new BooleanValue("Invisible Check", true, () -> true);
	public BooleanValue tabCheck = new BooleanValue("Tab Check", true, () -> true);
	
	@Override
	public void onDisable() {
		invalid.clear();
	}

	@Subscribe
	public void onUpdate(UpdateEvent e) {
		if (mc.thePlayer.ticksExisted < 1) {
            invalid.clear();
        }
		if (invalid.isEmpty()) return;
		for (Entity entity : invalid) {
			mc.theWorld.removeEntity(entity);
		}
	}

	@Subscribe
	public void onPacket(PacketEvent e) {
		if (e.getPacket() instanceof S14PacketEntity) {
			try {
				S14PacketEntity entityPacket = (S14PacketEntity) (e.getPacket());
				Entity entity = entityPacket.getEntity(mc.theWorld);
				if (entity instanceof EntityPlayer && isBot(entity))
					invalid.add(entity);
			} catch (Exception exception) { }
		}
	}

	public boolean isBot(Entity entity) {
		if (soundCheck.isOn() && entity.doesEntityNotTriggerPressurePlate())
			return true;
		if (invisibleCheck.isOn() && entity.isInvisible())
			return true;
		if (tabCheck.isOn() && alreadyInTab(entity))
			return true;
		return false;
	}

	public boolean alreadyInTab(Entity entity) {
		return mc.getNetHandler().getPlayerInfoMap().stream().filter((player) -> player != null && entity != null && player.getDisplayName() != null && entity.getDisplayName() != null && player.getDisplayName().getUnformattedText().equals(entity.getDisplayName().getUnformattedText())).count() > 0;
	}
}
