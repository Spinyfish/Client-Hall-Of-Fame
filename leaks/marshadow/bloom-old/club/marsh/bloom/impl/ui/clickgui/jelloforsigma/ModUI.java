package club.marsh.bloom.impl.ui.clickgui.jelloforsigma;

import java.awt.Color;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import club.marsh.bloom.Bloom;
import club.marsh.bloom.api.module.Category;
import club.marsh.bloom.api.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

public class ModUI
{
	private JelloForSigmaUI parent;
	private int marginX, marginY;
	private Category category;
	private ModsList modsList;
	private Module module;
	private int x, y;
	
	public ModUI(ModsList modsList, JelloForSigmaUI parent, Module module, Category category, int y)
	{
		this.modsList = modsList;
		this.parent = parent;
		this.module = module;
		this.category = category;
		this.y = y;
		int x = 0;
		
		for (int i = 0; i < Category.values().length; i++)
		{
			if (i == 3)
			{
				x = 0;
			}
			
			if (category == Category.values()[i])
			{
				break;
			}
			
			x += 110;
		}
	}
	
	/**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
    	float fixedIndex = this.parent.index > 1 ? 1 : this.parent.index;
    	int x = modsList.marginX + modsList.x;
    	int y = modsList.marginY + modsList.y;
    	Gui.drawRect(this.marginX + this.x, (this.marginY + this.y) > (y + 160) ? (y + 160) : (this.marginY + this.y) < (y + 30) ? (y + 30) : (this.marginY + this.y), this.marginX + this.x + 100, (this.marginY + this.y + 15) > (y + 160) ? (y + 160) : (this.marginY + this.y + 15) < (y + 30) ? (y + 30) : (this.marginY + this.y + 15), this.module.isToggled() ? (this.isInside(mouseX, mouseY, this.marginX + this.x, this.marginY + this.y, this.marginX + this.x + 100, this.marginY + this.y + 15) ? new Color(40, 180, 250, (int) (255 * fixedIndex)).getRGB() : new Color(40, 165, 255, (int) (255 * fixedIndex)).getRGB()) : (this.isInside(mouseX, mouseY, this.marginX + this.x, this.marginY + this.y, this.marginX + this.x + 100, this.marginY + this.y + 15) ? this.parent.whiteHovered.getRGB() : 0));
    	
    	if ((this.y + this.marginY + 2) < (y + 160) && (this.y + this.marginY + 2) > (y + 30))
    	{
    		Color white = new Color(255, 255, 255, (int) (255 * fixedIndex) < 5 ? 5 : (int) (255 * fixedIndex));
    		Color black = new Color(0, 0, 0, (int) (255 * fixedIndex) < 5 ? 5 : (int) (255 * fixedIndex));
    		this.parent.getFontRendererNormal().drawString(this.module.getName(), this.x + this.marginX + (this.module.isToggled() ? 15 : 10), this.y + this.marginY + 2, this.module.isToggled() ? white.getRGB() : black.getRGB());
    	}
    }
    
    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
    	if (this.parent.selectedModule == null)
    	{
    		if (this.isInside(mouseX, mouseY, this.marginX + this.x, this.marginY + this.y, this.marginX + this.x + 100, this.marginY + this.y + 15))
        	{
        		if (mouseButton == 0)
        		{
        			this.module.setToggled(!this.module.isToggled());
        			
        			if (this.module.isToggled())
        			{
        				this.playEnableSound();
        			}
        			
        			else
        			{
        				this.playDisableSound();
        			}
        		}
        		
        		else
        		{
        			if (!Bloom.INSTANCE.valueManager.getAllValuesFrom(this.module.getName()).isEmpty())
        			{
        				Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1));
            			this.parent.selectedModule = this.module;
        			}
        		}
        	}
    	}
    }
    
    public void setX(int x)
    {
    	this.marginX = x;
    }
    
    public void setY(int y)
    {
    	this.marginY = y;
    }
    
    public boolean isInside(int mouseX, int mouseY, int x, int y, int width, int height)
    {
    	return this.parent.selectedModule == null && mouseX > x && mouseX < width && mouseY > y && mouseY < height;
    }
    
    public synchronized void playEnableSound()
	{
		new Thread(() ->
		{
			try
	    	{
	    		Clip clip = AudioSystem.getClip();
		        AudioInputStream inputStream = AudioSystem.getAudioInputStream(Minecraft.getMinecraft().mcDefaultResourcePack.getInputStream(new ResourceLocation("Bloom/enable.wav")));
		        clip.open(inputStream);
		        clip.start();
		    }
	    	
	    	catch (Exception e)
	    	{
	    		;
	    	}
		}).start();
	}
    
    public synchronized void playDisableSound()
	{
		new Thread(() ->
		{
			try
	    	{
	    		Clip clip = AudioSystem.getClip();
		        AudioInputStream inputStream = AudioSystem.getAudioInputStream(Minecraft.getMinecraft().mcDefaultResourcePack.getInputStream(new ResourceLocation("Bloom/disable.wav")));
		        clip.open(inputStream);
		        clip.start();
		    }
	    	
	    	catch (Exception e)
	    	{
	    		;
	    	}
		}).start();
	}
}
