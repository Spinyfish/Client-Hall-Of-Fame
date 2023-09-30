
package me.nekoWare.client.module.combat;

import java.util.function.Consumer;

import me.hippo.api.lwjeb.annotation.Handler;
import me.nekoWare.client.event.events.UpdateEvent;
import me.nekoWare.client.module.Module;
import me.nekoWare.client.module.combat.aura.MultiAura;
import me.nekoWare.client.module.combat.aura.SingleAura;
import me.nekoWare.client.module.combat.aura.SwitchAura;
import me.nekoWare.client.util.packet.PacketUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class Aura extends Module {

	public static boolean isBlocking;
	public static EntityLivingBase currentEntity;

	public Aura() {
		super("KillAura", 19, Module.Category.COMBAT);
		addModes("Single", "Switch", "Multi");
		addInt("Click Speed", 12.0, 1.0, 20.0);
		addInt("Block Chance", 100.0, 1.0, 100.0);
		addDouble("Range", 4, 3.0, 6.0);
		addBoolean("Rots", true);
		addBoolean("Auto Block", true);
		addBoolean("Auto Block2", true);
		addBoolean("Keep Sprint", true);
		addBoolean("Players", true);
		addBoolean("Animals", true);
		addBoolean("Monsters", true);
		addBoolean("Invisibles", true);
		addBoolean("Teams", true);
	}
	
	public static boolean Rots;

	@Handler
	public Consumer<UpdateEvent> onUpdate = (event) -> {
		
		if(this.getBool("Rots") == true) {
			Rots = true;
		}else {
			Rots = false;
		}
		
		
		if (isMode("Single")) {
			SingleAura.doUpdate(this, event, mc);
		}
		if (isMode("Multi")) {
			MultiAura.doUpdate(this, event, mc);
		}
		if (isMode("Switch")) {
			SwitchAura.doUpdate(this, event, mc);
		}
	};

	@Override
	public void onDisable() {
		isBlocking = false;
		currentEntity = null;
		if (SingleAura.unblock) {
			PacketUtil.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM,
					new BlockPos(0.221, 0.221, 0.213), EnumFacing.UP));
		}
	}
}
