package club.marsh.bloom.impl.ui.clickgui.jelloforsigma;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import club.marsh.bloom.Bloom;
import club.marsh.bloom.api.font.FontManager;
import club.marsh.bloom.api.module.Category;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.api.value.BooleanValue;
import club.marsh.bloom.api.value.ModeValue;
import club.marsh.bloom.api.value.NumberValue;
import club.marsh.bloom.api.value.Value;
import club.marsh.bloom.impl.utils.render.BlurUtil;
import club.marsh.bloom.impl.utils.render.FontRenderer;
import club.marsh.bloom.impl.utils.render.RenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.GameSettings.Options;
import net.minecraft.util.ResourceLocation;

public class JelloForSigmaUI extends GuiScreen
{
	private FontRenderer fontRendererBold = new FontRenderer(FontManager.getFontFromTTF(new ResourceLocation("Bloom/jelloforsigmabold.ttf"), 36, Font.PLAIN));
	private FontRenderer fontRendererNormal = new FontRenderer(FontManager.getFontFromTTF(new ResourceLocation("Bloom/jelloforsigma.ttf"), 20, Font.PLAIN));
	private FontRenderer fontRendererBig = new FontRenderer(FontManager.getFontFromTTF(new ResourceLocation("Bloom/jelloforsigma.ttf"), 24, Font.PLAIN));
	private ArrayList<ModsList> modsLists = new ArrayList<>();
	public Color categoryFontColor = new Color(120, 120, 120);
	public Color whiteHovered = new Color(220, 220, 220);
	private boolean guiClosing = false, bounce = true;
	public Color darkWhite = new Color(240, 240, 240);
	public Color white = new Color(250, 250, 250);
	public Module selectedModule;
	public float index = 0;
	
	public JelloForSigmaUI()
	{
		int x = 0, y = 0;
		
		for (int i = 0; i < Category.values().length; i++)
		{
			if (i == 3)
			{
				x = 0;
				y += 170;
			}
			
			this.modsLists.add(new ModsList(this, Category.values()[i], x, y));
			x += 110;
		}
	}
	
	/**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
	@Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
    	super.drawScreen(mouseX, mouseY, partialTicks);
    	BlurUtil.blur((30 * this.index) < 1 ? 1 : (30 * this.index));
    	float fixedIndex = (this.index > 1 ? 1 : this.index);
    	this.white = new Color(250, 250, 250, (int) (255 * fixedIndex));
    	this.darkWhite = new Color(240, 240, 240, (int) (255 * fixedIndex));
    	this.whiteHovered = new Color(220, 220, 220, (int) (255 * fixedIndex));
    	this.categoryFontColor = new Color(120, 120, 120, (int) (255 * fixedIndex));
    	
    	if (this.guiClosing)
    	{
    		if (this.index > 0)
    		{
    			this.index -= 0.1F;
    			
    			if (this.index < 0)
    			{
    				this.index = 0;
    			}
    			
    			if (this.index == 0)
    			{
    				mc.displayGuiScreen(null);

    	            if (mc.currentScreen == null)
    	            {
    	                mc.setIngameFocus();
    	            }
    			}
    		}
    	}
    	
    	else
    	{
    		if (this.bounce)
    		{
    			if (this.index < 1.1F)
        		{
        			this.index += 0.1F;
        			
        			if (this.index > 1.1F)
        			{
        				this.index = 1.1F;
        			}
        			
        			if (this.index == 1.1F)
        			{
        				this.bounce = false;
        			}
        		}
    		}
    		
    		else
    		{
    			if (this.index > 1)
        		{
        			this.index -= 0.01F;
        			
        			if (this.index < 1)
        			{
        				this.index = 1;
        			}
        		}
    		}
    	}
    	
    	ScaledResolution scaledResolution = new ScaledResolution(mc);
    	float scale = (3 - scaledResolution.getScaleFactor()) < 1 ? 1 : (3 - scaledResolution.getScaleFactor());
    	GlStateManager.pushMatrix();
    	GlStateManager.scale(scale, scale, scale);
    	mouseX /= scale;
    	mouseY /= scale;
    	this.width /= scale;
    	this.height /= scale;
    	
    	if (this.selectedModule != null)
    	{
        	for (ModsList modsList : this.modsLists)
        	{
        		modsList.drawScreen(mouseX, mouseY, partialTicks);
        	}
        	
    		this.drawRect(0, 0, width, this.height, new Color(0, 0, 0, (int) (128 * fixedIndex)).getRGB());
    		RenderUtil.drawRoundedRect((this.width / 2) - 125, (this.height / 2) - 150, (this.width / 2) + 125, (this.height / 2) + 150, 10, this.white);
    		this.fontRendererBold.drawString(this.selectedModule.getName(), (this.width / 2) - 125, (this.height / 2) - 175, new Color(250, 250, 250, (int) (255 * fixedIndex) < 5 ? 5 : (int) (255 * fixedIndex)).getRGB());
    		float offset = 0;
    		
    		for (Value value : Bloom.INSTANCE.valueManager.getAllValuesFrom(this.selectedModule.getName()))
    		{
    			if (!value.isVisible())
    			{
    				continue;
    			}
    			
    			if (value.isCheck())
    			{
    				this.fontRendererBig.drawString(value.getName(), (this.width / 2) - 110, (this.height / 2) - 135 + offset, new Color(0, 0, 0, (int) (255 * fixedIndex) < 5 ? 5 : (int) (255 * fixedIndex)).getRGB());
    				RenderUtil.drawRoundedRect((this.width / 2) + 96, (this.height / 2) - 135 + offset, (this.width / 2) + 109, (this.height / 2) - 122 + offset, 13, ((BooleanValue) value).isOn() ? new Color(40, 165, 255, (int) (255 * fixedIndex) < 5 ? 5 : (int) (255 * fixedIndex)) : this.whiteHovered);
    				
    				if (((BooleanValue) value).isOn())
    				{
    					mc.getTextureManager().bindTexture(new ResourceLocation("Bloom/gui/check.png"));
        				GlStateManager.color(1, 1, 1, fixedIndex);
        				GlStateManager.enableBlend();
        				GlStateManager.enableAlpha();
        				this.drawScaledCustomSizeModalRect((this.width / 2) + 98.5F, (this.height / 2) - 131 + offset, 0, 0, 8, 6, 8, 6, 8, 6);
    				}
    				
    				offset += 17.5F;
    			}
    			
    			if (value.isCombo())
    			{
    				this.fontRendererBig.drawString(value.getName(), (this.width / 2) - 110, (this.height / 2) - 135 + offset, new Color(0, 0, 0, (int) (255 * fixedIndex) < 5 ? 5 : (int) (255 * fixedIndex)).getRGB());
    				this.fontRendererNormal.drawString(((ModeValue) value).mode, ((this.width / 2) + 110) - this.fontRendererNormal.getWidth(((ModeValue) value).mode), (this.height / 2) - 133 + offset, new Color(100, 100, 100, (int) (255 * fixedIndex) < 5 ? 5 : (int) (255 * fixedIndex)).getRGB());
    				offset += 17.5F;
    			}
    			
    			if (value.isSlider())
    			{
    				if (((NumberValue) value).isDragging())
    				{
    					float temp = mouseX - ((this.width / 2) + 55);
    					float temp2 = temp < 0 ? 0 : temp > 55 ? 55 : temp;
    					float temp3 = temp2 / 55;
    					float finalResult = this.getSliderValueFromPercentage(((NumberValue) value).min, temp3, ((NumberValue) value).max);
    					((NumberValue) value).setObject(finalResult);
    				}
    				
    				String roundedValue = String.valueOf(new BigDecimal(((NumberValue) value).value.floatValue()).setScale(1, BigDecimal.ROUND_HALF_UP).floatValue());
    				this.fontRendererBig.drawString(value.getName() + ":", (this.width / 2) - 110, (this.height / 2) - 135 + offset, new Color(0, 0, 0, (int) (255 * fixedIndex) < 5 ? 5 : (int) (255 * fixedIndex)).getRGB());
    				this.fontRendererNormal.drawString(roundedValue, ((this.width / 2) - 105) + this.fontRendererBig.getWidth(value.getName()), (this.height / 2) - 133 + offset, new Color(100, 100, 100, (int) (255 * fixedIndex) < 5 ? 5 : (int) (255 * fixedIndex)).getRGB());
    				RenderUtil.drawRoundedRect((this.width / 2) + 55, (this.height / 2) - 130 + offset, (this.width / 2) + 110, (this.height / 2) - 126 + offset, 4, this.whiteHovered);
    				
    				if (((NumberValue) value).value.floatValue() != ((NumberValue) value).min.floatValue())
    				{
    					GL11.glEnable(GL11.GL_SCISSOR_TEST);
        				this.scissor((this.width / 2) + 55, (this.height / 2) - 130 + (int) offset, (this.width / 2) + 110, (this.height / 2) - 126 + (int) offset, scale);
    					RenderUtil.drawRoundedRect((this.width / 2) + 55, (this.height / 2) - 130 + offset, (this.width / 2) + 55 + (55 * this.getSliderValue(((NumberValue) value).min, ((NumberValue) value).value, ((NumberValue) value).max)), (this.height / 2) - 126 + offset, 4, new Color(40, 165, 255, (int) (255 * fixedIndex)));
        				GL11.glDisable(GL11.GL_SCISSOR_TEST);
        			}
    				
    				Bloom.INSTANCE.bloomHandler.bloom((this.width / 2) + 49 + (int) (55 * this.getSliderValue(((NumberValue) value).min, ((NumberValue) value).value, ((NumberValue) value).max)), (this.height / 2) - 132 + (int) offset, 10, 9, 10, new Color(0, 0, 0, (int) (75 * fixedIndex)));
    				RenderUtil.drawRoundedRect((this.width / 2) + 49 + (55 * this.getSliderValue(((NumberValue) value).min, ((NumberValue) value).value, ((NumberValue) value).max)), (this.height / 2) - 132 + offset, (this.width / 2) + 57 + (55 * this.getSliderValue(((NumberValue) value).min, ((NumberValue) value).value, ((NumberValue) value).max)), (this.height / 2) - 124 + offset, 8.5F, this.white);
    				offset += 17.5F;
    			}
    		}
    	}
    	
    	else
    	{
        	for (ModsList modsList : this.modsLists)
        	{
        		modsList.drawScreen(mouseX, mouseY, partialTicks);
        	}
    	}
    	
    	this.width *= scale;
    	this.height *= scale;
    	GlStateManager.popMatrix();
    }
	
    /**
     * Delegates mouse and keyboard input.
     */
	@Override
    public void handleInput() throws IOException
    {
    	super.handleInput();
		int i = Integer.compare(0, Mouse.getDWheel());
		
		for (ModsList modsList : this.modsLists)
		{
			modsList.handleInput(i);
		}
    }
	
	/**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
	@Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
    	super.mouseClicked(mouseX, mouseY, mouseButton);
    	ScaledResolution scaledResolution = new ScaledResolution(mc);
    	float scale = (3 - scaledResolution.getScaleFactor()) < 1 ? 1 : (3 - scaledResolution.getScaleFactor());
    	mouseX /= scale;
    	mouseY /= scale;
    	this.width /= scale;
    	this.height /= scale;
    	
    	if (this.selectedModule != null)
    	{
        	for (ModsList modsList : this.modsLists)
        	{
        		modsList.mouseClicked(mouseX, mouseY, mouseButton);
        	}
        	
    		float offset = 0;
    		
    		for (Value value : Bloom.INSTANCE.valueManager.getAllValuesFrom(this.selectedModule.getName()))
    		{
    			if (!value.isVisible())
    			{
    				continue;
    			}
    			
    			if (value.isCheck())
    			{
    				if (this.isInside(mouseX, mouseY, (this.width / 2) + 96, (this.height / 2) - 135 + offset, (this.width / 2) + 109, (this.height / 2) - 122 + offset) && mouseButton == 0)
    				{
    					mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1));
    					((BooleanValue) value).flip();
    				}
    				
    				offset += 17.5F;
    			}
    			
    			if (value.isCombo())
    			{
    				if (this.isInside(mouseX, mouseY, (this.width / 2) + 105 - this.fontRendererNormal.getWidth(((ModeValue) value).mode), (this.height / 2) - 135 + offset, (this.width / 2) + 115, (this.height / 2) - 121 + offset))
    				{
    					mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1));
    					
    					if (mouseButton == 0)
    					{
    						((ModeValue) value).cycle();
    					}
    					
    					else
    					{
    						((ModeValue) value).cycleReverse();
    					}
    				}
    				
    				offset += 17.5F;
    			}
    			
    			if (value.isSlider())
    			{
    				if (this.isInside(mouseX, mouseY, (this.width / 2) + 50, (this.height / 2) - 135 + offset, (this.width / 2) + 110, (this.height / 2) - 121 + offset) && mouseButton == 0)
    				{
    					mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1));
    					((NumberValue) value).setDragging(true);
    				}
    				
    				offset += 17.5F;
    			}
    		}
    	}
    	
    	else
    	{
        	for (ModsList modsList : this.modsLists)
        	{
        		modsList.mouseClicked(mouseX, mouseY, mouseButton);
        	}
    	}
    	
    	this.width *= scale;
    	this.height *= scale;
    }
	
	/**
     * Called when a mouse button is released.  Args : mouseX, mouseY, releaseButton
     */
	@Override
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
    	super.mouseReleased(mouseX, mouseY, state);
    	
    	if (this.selectedModule != null)
    	{
    		for (Value value : Bloom.INSTANCE.valueManager.getAllValuesFrom(this.selectedModule.getName()))
    		{
    			if (!value.isVisible())
    			{
    				continue;
    			}
    			
    			if (value.isSlider())
    			{
    				((NumberValue) value).setDragging(false);
    			}
    		}
    	}
    	
    	for (ModsList modsList : this.modsLists)
    	{
    		modsList.mouseReleased(mouseX, mouseY, state);
    	}
    }
	
	/**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
	@Override
    public void initGui()
    {
    	super.initGui();
    	this.index = 0;
    	this.bounce = true;
    	this.guiClosing = false;
    	
    	if (mc.gameSettings.ofFastRender)
    	{
    		mc.gameSettings.setOptionValueOF(GameSettings.Options.FAST_RENDER, 0);
    	}
    	
    	try
    	{
            Display.setVSyncEnabled(true);
    	}
    	
    	catch (Exception e)
    	{
    		;
    	}
    	
    	for (ModsList modsList : this.modsLists)
    	{
    		modsList.initGui();
    	}
    }
	
	/**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
	@Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
    	if (this.selectedModule != null)
    	{
    		if (keyCode == 1)
    		{
    			this.selectedModule = null;
    		}
    	}
    	
    	else
    	{
    		if (keyCode == 1)
    		{
    			this.guiClosing = true;
    		}
    	}
    }
	
	/**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
	@Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }
	
	public FontRenderer getFontRendererNormal()
	{
		return this.fontRendererNormal;
	}
	
	public FontRenderer getFontRendererBig()
	{
		return this.fontRendererBig;
	}
	
	public float getSliderValue(Number min, Number value, Number max)
	{
		float minimum = min.floatValue();
		float valueFloat = value.floatValue();
		float maximum = max.floatValue();
		float range = maximum - minimum;
        float correctedStartValue = valueFloat - minimum;
		float finalNumber = correctedStartValue / range;
		return finalNumber;
	}
	
	public float getSliderValueFromPercentage(Number min, float value, Number max)
	{
		float minimum = min.floatValue();
		float maximum = max.floatValue();
		float range = maximum - minimum;
        float correctedStartValue = value * range;
		float finalNumber = correctedStartValue + minimum;
		return finalNumber;
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
	
	/**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
	@Override
    public void onGuiClosed()
    {
		try
		{
	        Display.setVSyncEnabled(mc.gameSettings.enableVsync);
		}
		
		catch (Exception e)
		{
			;
		}
		
    	super.onGuiClosed();
    }
	
    public boolean isInside(int mouseX, int mouseY, float x, float y, float width, float height)
    {
    	return mouseX > x && mouseX < width && mouseY > y && mouseY < height;
    }
}
