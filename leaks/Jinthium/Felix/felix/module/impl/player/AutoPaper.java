package felix.module.impl.player;

import java.util.concurrent.ThreadLocalRandom;

import felix.api.annotations.Handler;
import felix.events.packet.EventPacketReceive;
import felix.events.player.EventMotionUpdate;
import felix.module.Module;
import felix.util.other.ItemUtils;
import felix.util.other.PlayerUtil;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.server.S02PacketChat;

public class AutoPaper extends Module {

	private int lastSlot;

	private boolean selected;

	private final String item = "Skywars Challenges";
	private final String activated = "You have activated the paper challenge!";
	private final String deactivated = "You have deactivated the paper challenge!";

	public AutoPaper() {
		super("AutoPaper", 0, ModuleCategory.PLAYER);
	}

	public void onEnable() {
		super.onEnable();
		if (mc.thePlayer == null) return;
		lastSlot = mc.thePlayer.inventory.currentItem;
		selected = false;
	}

	@Handler
	public void onMotionUpdate(final EventMotionUpdate event) {
		if (PlayerUtil.isOnServer("hypixel") && !selected) {
			if (mc.currentScreen == null) {
				for (int i = 7; i < 45; ++i) {
					if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
						final ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
						if (stack.getDisplayName().toLowerCase().contains("SkyWars Challenges")) {
							mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem = 7));
							KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
						}
					}
				}
			}
			if (mc.currentScreen instanceof GuiChest) {
	            final GuiChest chest = (GuiChest) mc.currentScreen; 
	            if (chest.getLowerChestInventory().getDisplayName().getUnformattedText().contains("SkyWars Challenges")) {
	            	if (selected) {
	                	mc.thePlayer.closeScreen();
	                	mc.thePlayer.inventory.currentItem = lastSlot;
	                	KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
	                    return;
	                }
	                for (int index = 0; index < chest.getLowerChestInventory().getSizeInventory(); ++index) {
	                    final ItemStack stack = chest.getLowerChestInventory().getStackInSlot(index);
	                    if (stack != null) {
	                    	boolean paperchallenge = stack.getDisplayName().contains("Paper Challenge");
	                    	if (paperchallenge) {
		                        mc.playerController.windowClick(chest.inventorySlots.windowId, index, 0, 1, mc.thePlayer);
		                        break;
	                    	}
	                    }
	                }
	            }
	        }
		}
	}

	@Handler
	public void onReceivePacket(final EventPacketReceive event) {

		if (event.getPacket() instanceof S02PacketChat) {
			final S02PacketChat packet = (S02PacketChat) event.getPacket();
			if (packet.getChatComponent().getFormattedText().equalsIgnoreCase(activated)) {
				selected = true;
			} else if (packet.getChatComponent().getFormattedText().equalsIgnoreCase(deactivated)) {
				selected = false;
			}
		}
	}
}
