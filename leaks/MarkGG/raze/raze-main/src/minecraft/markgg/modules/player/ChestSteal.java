package markgg.modules.player;

import org.lwjgl.input.Keyboard;

import markgg.events.Event;
import markgg.events.listeners.EventUpdate;
import markgg.modules.Module;
import markgg.settings.BooleanSetting;
import markgg.settings.ModeSetting;
import markgg.settings.NumberSetting;
import markgg.util.Timer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemStack;

public class ChestSteal extends Module{	

	public Timer timer = new Timer();

	public BooleanSetting autoClose = new BooleanSetting("Auto Close", this, true);
	public NumberSetting delay = new NumberSetting("Delay", this, 1, 1, 10, 0.1);
	private int ticksInChest;

	public ChestSteal() {
		super("ChestSteal", "Steals from chests", 0, Category.PLAYER);
		addSettings(delay,autoClose);
	}

	public void onEvent(Event e) {
		if(e instanceof EventUpdate) {
			if (mc.thePlayer.openContainer != null && mc.thePlayer.openContainer instanceof ContainerChest) {
				ContainerChest container = (ContainerChest) mc.thePlayer.openContainer;

				for (int i = 0; i < container.getLowerChestInventory().getSizeInventory(); i++) {
					if (container.getLowerChestInventory().getStackInSlot(i) != null) {
						if (timer.hasTimeElapsed((long) (delay.getValue() * 10), true)) {
							mc.playerController.windowClick(container.windowId, i, 0, 1, mc.thePlayer);
							timer.reset();
						}
					}
				}
			}
			if(autoClose.isEnabled() && ticksInChest > 10) {
				if (mc.thePlayer.openContainer != null && mc.thePlayer.openContainer instanceof ContainerChest) {
					ContainerChest container = (ContainerChest) mc.thePlayer.openContainer;
					for (int i = 0; i < container.getLowerChestInventory().getSizeInventory(); i++) {
						if (container.getLowerChestInventory().getStackInSlot(i) == null) {
							if (timer.hasTimeElapsed((long) (delay.getValue() * 10), true)) {
								mc.thePlayer.closeScreen();
							}
						}
					}
				}
			}
		}
	}
}
