package felix.module.impl.movement;

import felix.Client;
import felix.api.annotations.Handler;
import felix.events.player.EventMotionUpdate;
import felix.events.player.EventMove;
import felix.gui.notification.Notifications;
import felix.module.Module;
import felix.module.impl.combat.KillAura;
import felix.util.other.TimeHelper;
import felix.util.player.MovementUtils;
import felix.value.impl.EnumValue;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;

public class PaperFlight extends Module {

	double startX;
	double startY;
	double startZ;
	boolean shouldBow;
	boolean shouldLongjump;
	private double lastDistance;
	private double movementSpeed;
	private int stage = 0;
	private TimeHelper timer = new TimeHelper();
	
	private int lastItem = 0;

	private final EnumValue<Mode> mode = new EnumValue<>("PaperLongjump Mode", Mode.Long);

	public PaperFlight() {
		super("PaperLongjump", 0, ModuleCategory.MOVEMENT);
		addValues(mode);
	}
	
	public void onEnable() {
		super.onEnable();
		lastItem = mc.thePlayer.inventory.currentItem;
		timer.reset();
		startX = mc.thePlayer.posX;
		startY = mc.thePlayer.posY;
		startZ = mc.thePlayer.posZ;
		shouldBow = true;
		shouldLongjump = false;
		stage = 0;
		if (getBowSlot() == -1) {
			this.toggle();
			Notifications.getManager().post("Retard found", "you dont have a fucking bow you fucking dumbass",
					Notifications.Type.SUCCESS);
		} else {
			
			mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem = getBowSlot()));
			
			if (mc.thePlayer.inventory.currentItem == getBowSlot() && mc.thePlayer.getHeldItem().getItem() instanceof ItemBow) {
				KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
			}
		}
	}
	
	public void onDisable() {
		mc.timer.timerSpeed = 1.0f;
		super.onDisable();
		mc.getNetHandler().getNetworkManager().sendPacket(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem = lastItem));
		if (mc.gameSettings.keyBindUseItem.isKeyDown()) {
			KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
		}
	}

	@Handler
	public void onMove(final EventMove event) {
		setSuffix(mode.getValueAsString());
		float z = (float) (startX - mc.thePlayer.posX);
		float y = (float) (startY - mc.thePlayer.posY);
		float x = (float) (startZ - mc.thePlayer.posZ);
		double distance = MathHelper.sqrt_float(z * z + y * y + x * x);
		switch (mode.getValue()) {
			case Long:

				if (distance > 20) {
					this.toggle();
				}
				if (shouldLongjump) {
					if (mc.thePlayer.onGround) {
						event.setY(mc.thePlayer.motionY = .42F);
					}
					if (stage == 0) {
						mc.timer.timerSpeed = 0.75f;
						movementSpeed = MovementUtils.getSpeed();
					} else if (stage == 1) {
						movementSpeed *= 2;
					} else if (stage == 2) {
						movementSpeed = lastDistance - (.05 * (lastDistance - 0.2873));
					} else {
						movementSpeed = lastDistance - lastDistance / 139;
					}
					//event.strafe(movementSpeed = Math.max(MovementUtils.getSpeed(), movementSpeed));
					MovementUtils.setMotion(MovementUtils.getSpeed() * movementSpeed * 26);
					// i will fix this later dw
				} else {
					event.setX(0);
					event.setY(0);
					event.setZ(0);
				}
				break;
			case High:
				if (distance > 20) {
					this.toggle();
				}
				if (shouldLongjump) {
					if (mc.thePlayer.onGround) {
						event.setY(mc.thePlayer.motionY = .8F);
					}
					if (stage == 0) {
						mc.timer.timerSpeed = 0.75f;
						movementSpeed = MovementUtils.getSpeed();
					} else if (stage == 1) {
						movementSpeed *= 2;
					} else if (stage == 2) {
						movementSpeed = lastDistance - (.05 * (lastDistance - 0.2873));
					} else {
						movementSpeed = lastDistance - lastDistance / 139;
					}
					//event.strafe(movementSpeed = Math.max(MovementUtils.getSpeed(), movementSpeed));
					MovementUtils.setMotion(MovementUtils.getSpeed() * movementSpeed * 26);
					// i will fix this later dw
				} else {
					event.setX(0);
					event.setY(0);
					event.setZ(0);
				}
				break;
		}
	}
	
	@Handler
	public void onMotionUpdate(final EventMotionUpdate event) {
		event.setPitch(-90);
		if (event.getPitch() == -90) {
			//mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(-1, -1, -1), EnumFacing.DOWN));
			//mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
			if (timer.reach(80)) {
				KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
				timer.reset();
			}
		}
		if (mc.thePlayer.hurtTime > 0) {
			this.shouldLongjump = true;
			event.setPitch(mc.thePlayer.rotationPitch);
		}
	}

	public int getBowSlot() {
		for (int i = 36; i < 45; ++i) {
			ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
			if (stack == null || !(stack.getItem() instanceof ItemBow)) {
				continue;
			}
			return i - 36;
		}
		return -1;
	}
	
	private enum Mode {
		Long, High
	}
}
