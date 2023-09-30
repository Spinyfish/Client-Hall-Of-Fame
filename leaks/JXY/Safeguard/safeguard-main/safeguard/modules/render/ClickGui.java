package intentions.modules.render;

import intentions.events.Event;
import intentions.events.listeners.EventRenderGUI;
import intentions.events.listeners.EventUpdate;
import intentions.modules.Module;
import intentions.ui.MouseScreen;
import intentions.util.CategoryObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;

public class ClickGui extends Module {

	public static CategoryObject chat, combat, movement, player, render, world;
	
	public static boolean enable = false;
	
	public ClickGui() {
		super("ClickGui", 0, Category.RENDER, "ClickGUI", false);
		this.toggled = true;
	}

	public static Minecraft mc = Minecraft.getMinecraft();
	
	public static void onEventStatic(Event e) {
		
		if(e instanceof EventUpdate) {
			if(enable && mc.currentScreen == null) {
				mc.displayGuiScreen(new MouseScreen());
			}
		}
		
		if(!(e instanceof EventRenderGUI) || !enable)return;
		
		ClickGui.chat = new CategoryObject(Category.CHAT, 10, 10);
		ClickGui.combat = new CategoryObject(Category.COMBAT, 50, 10);
		ClickGui.movement = new CategoryObject(Category.MOVEMENT, 100, 10);
		ClickGui.player = new CategoryObject(Category.PLAYER, 150, 10);
		ClickGui.render = new CategoryObject(Category.RENDER, 200, 10);
		ClickGui.world = new CategoryObject(Category.WORLD, 250, 10);
		
		draw(chat, 1);
		draw(combat, 2);
		draw(movement, 3);
		draw(player, 4);
		draw(render, 5);
		draw(world, 6);
		
	}
	
	private static void draw(CategoryObject co, int count) {
		int width = MouseScreen.getWidth(), height = MouseScreen.getHeight();
		FontRenderer fr = mc.fontRendererObj;
		GlStateManager.pushMatrix();
		GlStateManager.translate(4, 4, 0);
		GlStateManager.scale(2f, 2f, 1);
		GlStateManager.translate(-4, -4, 0);
		Gui.drawRect(co.getX(), co.getY(), co.getX() + (fr.getStringWidth(co.getCategoryName())) + 1, co.getY() + 10, 0xffff1100);
		int x = (int)Math.floor((width / 6 * count) + (width / 6) / 2.0F - fr.getStringWidth(co.getCategoryName()) / 2.0F);
		int y = (int)Math.floor(height - 20);
	    fr.drawString(co.getCategoryName(), x, y, 0xFF8B0000);
		GlStateManager.popMatrix();
	}
	
}
