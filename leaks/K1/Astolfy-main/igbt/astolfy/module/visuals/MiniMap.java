package igbt.astolfy.module.visuals;

import static org.lwjgl.opengl.GL11.*;

import igbt.astolfy.events.Event;
import igbt.astolfy.events.listeners.EventRender2D;
import igbt.astolfy.module.ModuleBase;
import igbt.astolfy.ui.inGame.GuiElement.GuiElement;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public class MiniMap extends ModuleBase {

	public GuiElement guiElement = new GuiElement("MiniMap",this,5,15,70, 70);
	public MiniMap() {
		super("MiniMap", 0, Category.VISUALS);
	}
	public void onEvent(Event e) {
		if(e instanceof EventRender2D) {
			guiElement.renderStart();
			int x = 0;
			int y = 0;
			int size = 70;
			Gui.drawRect(x, y, x + size, y + size, 0xFF121212);
			for(Entity ent : mc.theWorld.loadedEntityList) {
				if(ent instanceof EntityLivingBase) {
					double posX = mc.thePlayer.posX - ent.posX;
					double posZ = mc.thePlayer.posZ - ent.posZ;
					if(posX <= size /2 && posX >= -size /2 && posZ <= size /2 && posZ >= -size /2)
						if(ent == mc.thePlayer) {
							GlStateManager.pushMatrix();
							Gui.drawRect(posX - 1 + x + (size / 2), posZ - 1 + y + (size / 2), posX + 1 + x + (size / 2), posZ + 1 + y + (size / 2), 0xFF00FF00);
							//Gui.drawRect(posX - 1 + x + (size / 2), posZ - 0.5 + y + (size / 2), posX + 2 + x + (size / 2), posZ + 0.5 + y + (size / 2), 0xFF00FF00);
							GlStateManager.popMatrix();
						}
						else
							if(!ent.isInvisible())
								Gui.drawRect(posX - 0.5 + x + (size / 2), posZ - 0.5 + y + (size / 2), posX + 0.5 + x + (size / 2), posZ + 0.5 + y + (size / 2), 0xFFFF0000);
				}
			}
			guiElement.renderEnd();
		}
	}

}
