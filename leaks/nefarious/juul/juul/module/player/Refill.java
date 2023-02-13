package juul.module.player;

import org.lwjgl.input.Keyboard;

import juul.Juul;
import juul.event.Event;
import juul.event.EventUpdate;
import juul.inject.wrappers.ContainerWrapper;
import juul.inject.wrappers.EntityPlayerSPWrapper;
import juul.inject.wrappers.GuiInventoryWrapper;
import juul.inject.wrappers.PlayerControllerMPWrapper;
import juul.module.Module;
import juul.module.settings.BooleanSetting;
import juul.module.settings.RangeSetting;
import juul.util.misc.BypassUtils;
import juul.util.objects.Timer;

public class Refill extends Module {

	public BooleanSetting soups, potions;
	public RangeSetting delay;
	public Timer delayTimer = new Timer();
	public int inventoryTicks;
	
	public Refill() {
		super("Refill", "Automatically populates hotbar with buff.", Keyboard.KEY_G, Category.PLAYER);
		addSetting(soups = new BooleanSetting("Soups", "Fill hotbar with soups", false));
		addSetting(potions = new BooleanSetting("Potions", "Fill hotbar with potions", true));
		addSetting(delay = new RangeSetting("Delay", "Delay in milliseconds between item move", 100, 200, 50, 500, 50));
	}
	
	public void onEvent(Event e) {
		if(e instanceof EventUpdate) {
			GuiInventoryWrapper guiInventoryWrapper = Juul.INSTANCE.getWrapperManager().get(GuiInventoryWrapper.class);
			boolean invOpen = guiInventoryWrapper.isInstance(mc.currentScreen());
			if(invOpen)
				inventoryTicks++;
			else inventoryTicks = 0;
			
			boolean shouldPot = inventoryTicks > 2 && potions.isEnabled() && invOpen && Juul.INSTANCE.invUtil.hotbarHasSpace() && Juul.INSTANCE.invUtil.inventoryHasPotion();
			boolean shouldSoup = inventoryTicks > 2 && soups.isEnabled() && invOpen && Juul.INSTANCE.invUtil.hotbarHasSpace() && Juul.INSTANCE.invUtil.inventoryHasSoup();
			EntityPlayerSPWrapper player = new EntityPlayerSPWrapper(mc.player());
			ContainerWrapper inventoryContainer = new ContainerWrapper(player.inventoryContainer());
			PlayerControllerMPWrapper playerController = new PlayerControllerMPWrapper(mc.playerController());
			
			long delay = (long) BypassUtils.range((int)this.delay.getStart(), (int)this.delay.getEnd());
			
			if (shouldPot && delayTimer.hasTimeElapsed(delay, true)) {
				playerController.windowClick(inventoryContainer.windowId(), Juul.INSTANCE.invUtil.potionInventorySlot(), 0, 1, player.getClassInstance());
	         }
	         if (shouldSoup && delayTimer.hasTimeElapsed(delay, true)) {
	        	 playerController.windowClick(inventoryContainer.windowId(), Juul.INSTANCE.invUtil.soupInventorySlot(), 0, 1, player.getClassInstance());
	         }
		}
	}
	
}
