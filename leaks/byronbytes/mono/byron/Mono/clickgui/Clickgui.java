package byron.Mono.clickgui;

import byron.Mono.Mono;
import byron.Mono.animation.Animate;
import byron.Mono.animation.Easing;
import byron.Mono.clickgui.comp.CheckBox;
import byron.Mono.clickgui.comp.Combo;
import byron.Mono.clickgui.comp.Comp;
import byron.Mono.clickgui.comp.Slider;
import byron.Mono.clickgui.setting.Setting;
import byron.Mono.font.FontUtil;
import byron.Mono.module.Category;
import byron.Mono.module.Module;
import byron.Mono.module.impl.hud.ClickGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import byron.Mono.clickgui.setting.Setting;
import byron.Mono.event.impl.EventUpdate;
import byron.Mono.interfaces.ModuleInterface;
import byron.Mono.module.Category;
import byron.Mono.module.Module;
import byron.Mono.utils.ColorUtils;
import byron.Mono.utils.MovementUtils;
import byron.Mono.utils.TimeUtil;

import byron.Mono.clickgui.setting.Setting;
import byron.Mono.event.impl.EventUpdate;
import byron.Mono.interfaces.ModuleInterface;
import byron.Mono.module.Category;

import com.mojang.realmsclient.gui.ChatFormatting;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class Clickgui extends GuiScreen {

    public double posX, posY, width, height, dragX, dragY;
    public boolean dragging;
    public Category selectedCategory;

    private Module selectedModule;
    public int modeIndex;

    Animate anim = new Animate();

    public ArrayList<Comp> comps = new ArrayList<>();

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
        return Color.HSBtoRGB(hue, 0.4F, 1.0F);
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
    
    public Clickgui() {
        dragging = false;
        posX = getScaledRes().getScaledWidth() / 2 - 150;
        posY = getScaledRes().getScaledHeight() / 2 - 100;
        width = posX + 150 * 2;
        height = height + 200;
        selectedCategory = Category.Combat;
        anim.setEase(Easing.BOUNCE_IN).setMin(100).setMax(500).setSpeed(10).setReversed(false).update();
        anim.setMax(150);
        
        
    }

    
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    	
  
    
        super.drawScreen(mouseX, mouseY, partialTicks);
        anim.setEase(Easing.BOUNCE_IN).setMin(100).setMax(500).setSpeed(10).setReversed(false).update();
        int count = 0;
        anim.setMax(150);
        anim.getEase();
        anim.update();
        if (dragging) {
            posX = mouseX - dragX;
            posY = mouseY - dragY;
        }
        width = posX + 150 * 2;
        height = posY + 200;
        ScaledResolution sr;
        sr = new ScaledResolution(this.mc);
       
        
       	if(Module.getSetting("Blur").getValBoolean())
       	{
       	    this.drawGradientRect((int) sr.getScaledWidth() - 790 - 500, (int) ((int)sr.getScaledHeight() - 1000 - 1 - 27), (int) sr.getScaledWidth() - -19 - 1, (int) sr.getScaledHeight() - 4 - -1, 0, -15777216);
       	//	drawSmoothRect((float)(sr.getScaledWidth() - 790 - 500), (float)(sr.getScaledHeight() - 1000 - 1 - 27), (float)(sr.getScaledWidth() - -19 - 1), (float)(sr.getScaledHeight() - 4 - -1), -1878061297);

       	}
       	
        drawSmoothRect((float) posX,(float) posY - 10, (float) width, (float) posY, new Color(24,24,24).getRGB());
       drawSmoothRect((float) posX, (float) posY, (float) width, (float) height, new Color(45,45,45, 250).getRGB());
        
     
        FontUtil.normal.drawStringWithShadow("Mono b4", (float) posX, (float) posY - 8, new Color(255, 255, 255).getRGB());
        FontUtil.normal.drawStringWithShadow("M", (float) posX, (float) posY - 8, ColorUtils.astolfoColors(count * 10,1000));
        
  
    	
    	
      this.drawGradientRect((int) sr.getScaledWidth() - 720 - 300, (int) ((int)sr.getScaledHeight() - 0.1 - 1 - 27), (int) sr.getScaledWidth() - -29 - 1, (int) sr.getScaledHeight() - 1 - -10, 0, -15777216);
        drawSmoothRect((float)(sr.getScaledWidth() - 720 - 300), (float)(sr.getScaledHeight() - 0.1 - 25), (float)(sr.getScaledWidth() - -29 - 1), (float)(sr.getScaledHeight() - 1 - -10), -1878061297);
        
        
	   	if(Module.getSetting("HideOnTopMessage").getValBoolean())
    	{
    		FontUtil.normal.drawStringWithShadow("Buy Mono Client Today!", 0.0F, 490.0F, ColorUtils.astolfoColors(count * 10, 1000));  
    	}
    	else
    	{
    		FontUtil.normal.drawStringWithShadow("Developers: Byron, Apple, Hotaru, Sperm, Marsh", 0.0F, 490.0F, ColorUtils.astolfoColors(count * 10, 1000));
    	}
        

        int offset = 0;
        for (Category category : Category.values()) {
            drawSmoothRect((float)posX,(float) posY + 1 + offset,(float) posX + 60,(float) posY + 15 + offset,category.equals(selectedCategory) ? new Color(90,90,90).getRGB() : new Color(28,28,28).getRGB());
            FontUtil.normal.drawString(category.name(),(int)posX + 2, (int)(posY + 5) + offset, new Color(170,170,170).getRGB());
            offset += 15;
        }
        offset = 0;
        for (Module m : Mono.INSTANCE.getModuleManager().getModules(selectedCategory)) {
            drawSmoothRect((float) posX + 65,(float) posY + 1 + offset,(float) posX + 125,(float) posY + 15 + offset,m.isToggled() ? new Color(90,90,90).getRGB() : new Color(28,28,28).getRGB());
            FontUtil.normal.drawString(m.getName(),(int)posX + 67, (int)(posY + 5) + offset, new Color(170,170,170).getRGB());
            offset += 15;
        }

        for (Comp comp : comps) {
            comp.drawScreen(mouseX, mouseY);
        }

    }
    

    

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        for (Comp comp : comps) {
            comp.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (isInside(mouseX, mouseY, posX, posY - 10, width, posY) && mouseButton == 0) {
            dragging = true;
            dragX = mouseX - posX;
            dragY = mouseY - posY;
        }
        int offset = 0;
        for (Category category : Category.values()) {
            if (isInside(mouseX, mouseY,posX,posY + 1 + offset,posX + 60,posY + 15 + offset) && mouseButton == 0) {
                selectedCategory = category;
            }
            offset += 15;
        }
        offset = 0;
        for (Module m : Mono.INSTANCE.getModuleManager().getModules(selectedCategory)) {
            if (isInside(mouseX, mouseY,posX + 65,posY + 1 + offset,posX + 125,posY + 15 + offset)) {
                if (mouseButton == 0) {
                    m.toggle();
                }
                if (mouseButton == 1) {
                    int sOffset = 3;
                    comps.clear();
                    if (Mono.INSTANCE.getSettingsManager().getSettingsByMod(m) != null)
                    for (Setting setting : Mono.INSTANCE.getSettingsManager().getSettingsByMod(m)) {
                        selectedModule = m;
                        if (setting.isCombo()) {
                            comps.add(new Combo(275, sOffset, this, selectedModule, setting));
                            sOffset += 15;
                        }
                        if (setting.isCheck()) {
                            comps.add(new CheckBox(275, sOffset, this, selectedModule, setting));
                            sOffset += 15;
                        }
                        if (setting.isSlider()) {
                            comps.add(new Slider(275, sOffset, this, selectedModule, setting));
                            sOffset += 25;
                        }
                    }
                }
            }
            offset += 15;
        }
        for (Comp comp : comps) {
            comp.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        dragging = false;
        for (Comp comp : comps) {
            comp.mouseReleased(mouseX, mouseY, state);
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        dragging = false;
    }

    public boolean isInside(int mouseX, int mouseY, double x, double y, double x2, double y2) {
        return (mouseX > x && mouseX < x2) && (mouseY > y && mouseY < y2);
    }

    public ScaledResolution getScaledRes() {
        return new ScaledResolution(Minecraft.getMinecraft());
    }




}
