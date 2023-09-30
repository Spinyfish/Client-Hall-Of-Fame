package felix.module.impl.player;

import felix.api.annotations.Handler;
import felix.events.player.EventMotionUpdate;
import felix.module.Module;
import felix.value.impl.BooleanValue;
import felix.value.impl.EnumValue;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;

public class FastUse extends Module {
	
	private EnumValue<Mode> mode = new EnumValue("FastUse Mode", Mode.NCP);
	
	private BooleanValue groundOnly = new BooleanValue("GroundOnly", true);
	
	public FastUse() {
		super("FastUse", 0, ModuleCategory.PLAYER);
		addValues(mode, groundOnly);
	}
	
	@Handler
	public void onMotionUpdate(final EventMotionUpdate event) {
		EntityPlayerSP player = mc.thePlayer;
		GameSettings gameSettings = mc.gameSettings;
		Item item = player.getHeldItem().getItem();
		switch (mode.getValue()) {
		case NCP:
	        if (item != null && (item instanceof ItemFood || item instanceof ItemPotion) && gameSettings.keyBindDrop.isKeyDown() && (!groundOnly.getValue() || player.onGround) && player.ticksExisted % 4 == 0) {
	            for (int i = 0; i < 2; ++i) {
	                player.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(player.posX, player.posY + 1.0E-9, player.posZ, player.rotationYaw, player.rotationPitch, true));
	            }
	        }
			break;
		case Vanilla:
	        if (player.getCurrentEquippedItem() != null && (player.getCurrentEquippedItem().getItem() instanceof ItemFood || player.getCurrentEquippedItem().getItem() instanceof ItemPotion) && gameSettings.keyBindDrop.isKeyDown() && (!this.groundOnly.getValue() || player.onGround)) {
	            for (int i = 0; i < 9; ++i) {
	                player.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch, true));
	            }
	        }
			break;
		}
	}
	
	public void onEnable() {
		super.onEnable();
	}
	
	public void onDisable() {
		super.onDisable();
	}
	
	private enum Mode {
		NCP, Vanilla
	}
}
