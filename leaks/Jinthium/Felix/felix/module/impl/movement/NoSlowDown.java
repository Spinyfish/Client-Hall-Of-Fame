package felix.module.impl.movement;

import org.lwjgl.input.Keyboard;

import felix.Client;
import felix.api.annotations.Handler;
import felix.events.player.EventMotionUpdate;
import felix.module.Module;
import felix.module.impl.combat.KillAura;
import felix.value.impl.EnumValue;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class NoSlowDown extends Module {
	
	private final EnumValue<Mode> mode = new EnumValue("NoSlow Mode", Mode.Vanilla);
	
	public NoSlowDown() {
		super("NoSlowDown", 0, ModuleCategory.MOVEMENT);
		addValues(mode);
	}
	
	public void onEnable() {
		super.onEnable();
	}
	
	public void onDisable() {
		super.onDisable();
	}
	
	@Handler
	public void onMotionUpdate(final EventMotionUpdate event) {
		setSuffix(mode.getValueAsString());
		switch (mode.getValue()) {
		case NCP:
			final KillAura aura = (KillAura) Client.INSTANCE.getModuleManager().getModule(KillAura.class);
			if (mc.thePlayer.getHeldItem().getItem() instanceof ItemSword && mc.thePlayer.isUsingItem() && aura.target == null) {
				switch (event.getType()) {
				case PRE:
					mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
					break;
				case POST:
					mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.getHeldItem(), 0, 0, 0));
					break;
				}
			}
			break;
		case Vanilla:
			break;
		}
	}
	
	private enum Mode {
		Vanilla, NCP
	}
}
