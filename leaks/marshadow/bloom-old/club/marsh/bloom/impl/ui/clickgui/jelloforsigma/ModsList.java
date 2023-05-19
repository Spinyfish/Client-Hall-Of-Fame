package club.marsh.bloom.impl.ui.clickgui.jelloforsigma;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import club.marsh.bloom.Bloom;
import club.marsh.bloom.api.module.Category;
import club.marsh.bloom.api.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

public class ModsList
{
	public int x, y, marginX = 55, marginY = 55, dragX, dragY, modScroll;
	private ArrayList<ModUI> modUIs = new ArrayList<>();
	private Minecraft mc = Minecraft.getMinecraft();
	private boolean dragging = false;
	private JelloForSigmaUI parent;
	private Category category;
	
	public ModsList(JelloForSigmaUI parent, Category category, int x, int y)
	{
		this.parent = parent;
		this.category = category;
		this.x = x;
		this.y = y;
		int i = 0;
		
		for (Module module : Bloom.INSTANCE.moduleManager.getModulesByCategory(category))
		{
			this.modUIs.add(new ModUI(this, parent, module, category, i));
			i += 15;
		}
	}
	
	/**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
    	if (this.dragging)
    	{
    		this.x = mouseX - this.dragX;
    		this.y = mouseY - this.dragY;
    	}
    	
    	float reversedIndex = 1 - this.parent.index;
    	float fixedIndex = (this.parent.index > 1 ? 1 : this.parent.index);
    	int x = this.marginX + this.x;
    	int y = this.marginY + this.y;
    	GlStateManager.pushMatrix();
    	GlStateManager.translate(reversedIndex * -300, reversedIndex * -400, 1);
    	GlStateManager.scale(reversedIndex + 1, reversedIndex + 1, reversedIndex + 1);
    	Bloom.INSTANCE.bloomHandler.bloom(x - 5, y - 5, 110, 170, 10, new Color(0, 0, 0, (int) (75 * fixedIndex)));
    	Gui.drawRect(x, y + 30, x + 100, y + 160, this.parent.white.getRGB());
    	Gui.drawRect(x, y, x + 100, y + 30, this.parent.darkWhite.getRGB());
    	this.parent.getFontRendererBig().drawString(this.category.getName(), x + 10, y + 9, this.parent.categoryFontColor.getAlpha() < 5 ? new Color(this.parent.categoryFontColor.getRed(), this.parent.categoryFontColor.getGreen(), this.parent.categoryFontColor.getBlue(), 5) : this.parent.categoryFontColor);
    	
    	if ((reversedIndex + 1) == 1)
    	{
    		ScaledResolution scaledResolution = new ScaledResolution(mc);
    		float scale = (3 - scaledResolution.getScaleFactor()) < 1 ? 1 : (3 - scaledResolution.getScaleFactor());
        	GL11.glEnable(GL11.GL_SCISSOR_TEST);
        	this.scissor(x, y + 30, x + 100, y + 160, scale);
    	}
    	
    	for (ModUI modUI : this.modUIs)
    	{
    		modUI.setX(x);
    		modUI.setY(y + 30 - this.modScroll);
    		modUI.drawScreen(mouseX, mouseY, partialTicks);
    	}
    	
    	if ((reversedIndex + 1) == 1)
    	{
        	GL11.glDisable(GL11.GL_SCISSOR_TEST);
    	}
    	
    	GlStateManager.popMatrix();
    }
    
    /**
     * Delegates mouse and keyboard input.
     */
    public void handleInput(int i) throws IOException
    {
    	ScaledResolution scaledResolution = new ScaledResolution(mc);
    	float scale = (3 - scaledResolution.getScaleFactor()) < 1 ? 1 : (3 - scaledResolution.getScaleFactor());
    	int mouseX = Mouse.getEventX() * scaledResolution.getScaledWidth() / mc.displayWidth;
        int mouseY = scaledResolution.getScaledHeight() - Mouse.getEventY() * scaledResolution.getScaledHeight() / mc.displayHeight - 1;
    	mouseX /= scale;
    	mouseY /= scale;
    	
    	if (this.isInside(mouseX, mouseY, this.marginX + this.x, this.marginY + this.y, this.marginX + this.x + 100, this.marginY + this.y + 160))
    	{
    		if (i != 0)
    		{
    			if (i < 0)
    			{
    				if (this.modScroll > 0)
    				{
        				this.modScroll -= 15;
    				}
    			}
    			
    			else
    			{
    				this.modScroll += 15;
    			}
    		}
    	}
    }
	
	/**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
    	if (this.parent.selectedModule == null)
    	{	
    		if (this.isInside(mouseX, mouseY, this.marginX + this.x, this.marginY + this.y, this.marginX + this.x + 100, this.marginY + this.y + 30) && mouseButton == 0)
        	{
        		this.dragging = true;
        		this.dragX = mouseX - this.x;
        		this.dragY = mouseY - this.y;
        	}
    		
    		if (this.isInside(mouseX, mouseY, this.marginX + this.x, this.marginY + this.y + 30, this.marginX + this.x + 100, this.marginY + this.y + 160))
    		{
    			for (ModUI modUI : this.modUIs)
            	{
            		modUI.mouseClicked(mouseX, mouseY, mouseButton);
            	}
    		}
    	}
    }
    
    /**
     * Called when a mouse button is released.  Args : mouseX, mouseY, releaseButton
     */
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
    	this.dragging = false;
    }
    
    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
    	this.dragging = false;
        Display.setVSyncEnabled(true);
    }
    
    public boolean isInside(int mouseX, int mouseY, int x, int y, int width, int height)
    {
    	return this.parent.selectedModule == null && mouseX > x && mouseX < width && mouseY > y && mouseY < height;
    }
    
	private void scissor(int x, int y, int width, int height, float size)
	{
		ScaledResolution scaledResolution = new ScaledResolution(mc);
		x *= scaledResolution.getScaleFactor();
		y *= scaledResolution.getScaleFactor();
		width *= scaledResolution.getScaleFactor();
		height *= scaledResolution.getScaleFactor();
		x *= size;
		y *= size;
		width *= size;
		height *= size;
		GL11.glScissor(x, mc.displayHeight - height, width - x, height - y);
	}
}
