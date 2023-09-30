package de.verschwiegener.atero.ui.multiplayer;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import de.verschwiegener.atero.Management;
import de.verschwiegener.atero.design.font.Font;
import de.verschwiegener.atero.design.font.Fontrenderer;
import de.verschwiegener.atero.util.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.ServerListEntryNormal;
import net.minecraft.client.gui.ServerSelectionList;
import net.minecraft.client.renderer.GlStateManager;

public class CustomGUISlotRenderer {

    private int selectedEntryIndex = 0;
    private final GuiMultiplayer guiMultiplayer;

    int scrollamount;
    
    public CustomGUISlotRenderer(GuiMultiplayer guiMultiplayer) {
	this.guiMultiplayer = guiMultiplayer;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
	GlStateManager.enableTexture2D();
	for (int i = 0; i < guiMultiplayer.getServerListSelector().getServerListNormal().size(); i++) {
	    if (mouseY > (40 * i + 20) - scrollamount && mouseY < (40 * i + 53) - scrollamount) {
		if (mouseX > ((guiMultiplayer.width / 2) - 150) && mouseX < ((guiMultiplayer.width / 2) + 150)) {
		    //System.out.println("Selected");
		    selectedEntryIndex = i;
		    break;
		}
	    }
	}
	
	Management.instance.font.drawString("Multiplayer",(guiMultiplayer.width / 2) - Management.instance.font.getStringWidth2("Multiplayer"), 10,
		Color.white.getRGB());
	
	GL11.glEnable(GL11.GL_SCISSOR_TEST);
	GL11.glScissor((guiMultiplayer.width / 2) - 150, ((guiMultiplayer.height * 2) - 110 - Minecraft.getMinecraft().displayHeight) /-1, 900, (guiMultiplayer.height * 2) - 150);
	scrollamount = guiMultiplayer.getServerListSelector().getAmountScrolled();
	if(scrollamount + guiMultiplayer.height - 60 > 40 * guiMultiplayer.getServerListSelector().getServerListNormal().size() && scrollamount > 0) {
	    scrollamount = -((guiMultiplayer.height - 60) - 40 * guiMultiplayer.getServerListSelector().getServerListNormal().size()) + 5;
	}
	if(scrollamount < 0) {
	    scrollamount = 0;
	}
	guiMultiplayer.getServerListSelector().setAmountScrolled(scrollamount);
	for (int i = 0; i < guiMultiplayer.getServerListSelector().getServerListNormal().size(); i++) {
	    ServerListEntryNormal entry = guiMultiplayer.getServerListSelector().getServerListNormal().get(i);
	    entry.drawEntry(i, (guiMultiplayer.width / 2) - 150, 40 * i + 20 - scrollamount, 300, 20, mouseX, mouseY,
		    (i == selectedEntryIndex && isHovered(mouseX, mouseY) && !guiMultiplayer.isCustomGui()) ? true : false, (!guiMultiplayer.isCustomGui()) ? true: false);
	    if(guiMultiplayer.getServerListSelector().isSelected(i)) {
		RenderUtil.drawRect((guiMultiplayer.width / 2) - 150, 40 * i + 20 - scrollamount, 300, 32, Color.GRAY, 2);
	    }
	}
	GL11.glDisable(GL11.GL_SCISSOR_TEST);
	GlStateManager.disableTexture2D();
    }

    public void handleMouseClicked(int mouseX, int mouseY, int mouseButton) {
	if (isHovered(mouseX, mouseY)) {
	    int l = mouseX - ((guiMultiplayer.width / 2) - 150);
	    int i1 = mouseY - (40 * selectedEntryIndex + 20);
	    guiMultiplayer.getServerListSelector().getServerListNormal().get(selectedEntryIndex)
		    .mousePressed(selectedEntryIndex, mouseX, mouseY, mouseButton, l, i1);
	}
    }
    
    public void selectServer(int slot) {
	this.selectedEntryIndex = slot;
    }
    private boolean isHovered(int mouseX, int mouseY) {
	for (int i = 0; i < guiMultiplayer.getServerListSelector().getServerListNormal().size(); i++) {
	    if (mouseY > (40 * i + 20) - scrollamount && mouseY < (40 * i + 53) - scrollamount) {
		if (mouseX > ((guiMultiplayer.width / 2) - 150) && mouseX < ((guiMultiplayer.width / 2) + 150)) {
		    //System.out.println("Server: " + guiMultiplayer.getServerListSelector().getServerListNormal().get(i).getServerData().getServerName());
		    return true;
		}
	    }
	}
	return false;
    }
}
