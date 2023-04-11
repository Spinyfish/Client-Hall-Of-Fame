package byron.Mono.module.impl.hud;

import java.awt.Color;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import org.lwjgl.opengl.GL11;

import com.google.common.eventbus.Subscribe;

import byron.Mono.Mono;
import byron.Mono.clickgui.setting.Setting;
import byron.Mono.event.impl.Event2D;
import byron.Mono.interfaces.ModuleInterface;
import byron.Mono.module.Category;
import byron.Mono.font.FontUtil;
import byron.Mono.module.Module;
import byron.Mono.utils.ColorUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

@ModuleInterface(name = "Arraylist", description = "the best of course.", category = Category.HUD)

public class Arraylist extends Module{
	
	private int posX, posY, width, height;
	
	public void setup()
	{
		super.setup();
		rSetting(new Setting("Custom Font", this, true));
		rSetting(new Setting("Background", this, true));
		rSetting(new Setting("Arraylist Sidebar", this, false));
	}

	
	@Subscribe
	public void renderActiveMods (Event2D eventRender2D)
	{
		posX = eventRender2D.getScaledResolution().getScaledWidth() - 75;
		posY = eventRender2D.getScaledResolution().getScaledHeight() / eventRender2D.getScaledResolution().getScaledHeight() - 1;
		width = posX + 80;
		height = posY + 20;
		int offset = 0;
		int count = 0;
		

		if(Module.getSetting("Custom Font").getValBoolean())
		{
		Mono.INSTANCE.getModuleManager().getModules().sort(Comparator.comparingInt(module -> (int) FontUtil.normal.getStringWidth(((Module)module).getName())).reversed());
		}
		else
		{
			Mono.INSTANCE.getModuleManager().getModules().sort(Comparator.comparingInt(module ->  mc.fontRendererObj.getStringWidth(((Module)module).getName())).reversed());
		}
		
		for (Module module : Mono.INSTANCE.getModuleManager().getModules())
		{
			if (module.isToggled())
			{
				if(Module.getSetting("Custom Font").getValBoolean())
				{
					if(Module.getSetting("Arraylist Sidebar").getValBoolean())
					{
						drawSmoothRect(posX + 105 - 31, posY + offset, width - 4, height + (int) FontUtil.normal.getStringWidth(module.getName()) - 20, ColorUtils.astolfoColors(1000, 1 * offset));
					}
					
					if(Module.getSetting("Background").getValBoolean())
					{
						
					drawSmoothRect(posX + 63 - (int) FontUtil.normal.getStringWidth(module.getName()), posY + offset, width - 1, height + offset - 6.5F, new Color(75, 75, 75, 100).getRGB());
					}
					
					FontUtil.normal.drawStringWithShadow(module.getName(), posX + 70 - FontUtil.normal.getStringWidth(module.getName()), posY + 5 + offset, ColorUtils.astolfoColors(count * 10, 1000));
			
				}
				else
				{
					if(Module.getSetting("Arraylist Sidebar").getValBoolean())
					{
						drawSmoothRect(posX + 105 - 31, posY + offset, width - 1, height + (int) eventRender2D.getFontRenderer().getStringWidth(module.getName()) - 25, ColorUtils.astolfoColors(1000, 1 * offset));
					}
					
					
					if(Module.getSetting("Background").getValBoolean())
					{
						 drawSmoothRect(posX + 63 - (int) eventRender2D.getFontRenderer().getStringWidth(module.getName()), posY + offset, width - 6, height + offset - 6, new Color(75, 75, 75, 100).getRGB());
					}
					
					eventRender2D.getFontRenderer().drawString(module.getName(), posX + 70 - eventRender2D.getFontRenderer().getStringWidth(module.getName()), posY + 5 + offset, ColorUtils.astolfoColors(count * 10, 1000));
				
				}
			
				offset += 14.9;
				 ++count;
				
			}
			
		}

		   
		
	}
	
	  public static int astolfoColors(int yOffset, int yTotal) {
		  float speed = 1900.0F;

	        float hue;
	        for(hue = (float)(System.currentTimeMillis() % (long)((int)speed)) + (float)((yTotal - yOffset) * 9); hue > speed; hue -= speed) {
	        }

	        hue /= speed;
	        if ((double)hue > 1.5D) {
	            hue = 0.7F - (hue - 1.0F);
	        }

	        ++hue;
	        return Color.HSBtoRGB(hue, 0.5F, 1.0F);
	    }
	   
	
	  public static final void drawSmoothRect(float left, float top, float right, float bottom, int color) {
	        GL11.glEnable(3042);
	        GL11.glEnable(2848);
	        Gui.drawRect(left, top, right, bottom, color);
	        GL11.glScalef(0.5f, 0.5f, 0.5f);
	        Gui.drawRect(left * 2.0f - 1.0f, top * 2.0f, left * 2.0f, bottom * 2.0f - 1.0f, color);
	        Gui.drawRect(left * 2.0f, top * 2.0f - 1.0f, right * 2.0f, top * 2.0f, color);
	        Gui.drawRect(right * 2.0f, top * 2.0f, right * 2.0f + 1.0f, bottom * 2.0f - 1.0f, color);
	        Gui.drawRect(left * 2.0f, bottom * 2.0f - 1.0f, right * 2.0f, bottom * 2.0f, color);
	        GL11.glDisable(3042);
	        GL11.glScalef(2.0f, 2.0f, 2.0f);
	    }
	    
	
	public void drawRect(int left, int top, int right, int bottom, int color)
    {
        if (left < right)
        {
            int i = left;
            left = right;
            right = i;
            
        }
        
        if (top < bottom)
        {
            int j = top;
            top = bottom;
            bottom = j;
            
        }

        float f3 = (color >> 24 & 255) / 255.0F;
        float f = (color >> 16 & 255) / 255.0F;
        float f1 = (color >> 8 & 255) / 255.0F;
        float f2 = (color & 255) / 255.0F;
        
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(f, f1, f2, f3);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(left, bottom, 0.0D).endVertex();
        worldrenderer.pos(right, bottom, 0.0D).endVertex();
        worldrenderer.pos(right, top, 0.0D).endVertex();
        worldrenderer.pos(left, top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        
    }
	
}
