package club.marsh.bloom.impl.ui.clickgui.dropdown;

import club.marsh.bloom.Bloom;
import club.marsh.bloom.api.font.FontManager;
import club.marsh.bloom.api.module.Category;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.impl.utils.render.FontRenderer;
import club.marsh.bloom.impl.utils.render.RenderUtil;
import club.marsh.bloom.api.value.BooleanValue;
import club.marsh.bloom.api.value.ModeValue;
import club.marsh.bloom.api.value.NumberValue;
import club.marsh.bloom.api.value.Value;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.lwjgl.opengl.Display;

public class DropdownUI extends GuiScreen {
    FontRenderer fr = Bloom.INSTANCE.fontManager.defaultFont;
    float index = 2;
    boolean closingGUI = false;
    boolean holding = false;
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    	if (closingGUI)
        {
        	if (index < 2)
        	{
        		index += 0.1F;
        		
        		if (index > 2)
        		{
        			index = 2;
        		}
        		
        		if (index == 2)
        		{
                    mc.displayGuiScreen((GuiScreen)null);

                    if (mc.currentScreen == null)
                    {
                        mc.setIngameFocus();
                    }
                    
        			closingGUI = false;
        		}
        	}
        }
        
        else
        {
        	if (index > 1)
        	{
        		index -= 0.1F;
        		
        		if (index < 1)
        		{
        			index = 1;
        		}
        	}
        }
        GlStateManager.pushMatrix();
        GlStateManager.scale(this.index, this.index, this.index);
    	FontRenderer fr2 = Bloom.INSTANCE.fontManager.quickSandBig;
    	//Gui.drawRect(0, 0, this.width,this.height, new Color(0, 0, 0, 75).getRGB());
        // TODO Auto-generated method stub
        int index = 2;
        float indexx = (2 / this.index) - 1;
        //BlurUtil.blur(0,0,0,0,1);
        for (Category c : Category.values()) {

            int modulecount = 0;
            for (Module m : Bloom.INSTANCE.moduleManager.getModules()) {
                if (m.category == c && c.isExpanded()) {
                    modulecount += 1;
                }
            }
            if (c.holding) {
                c.x = mouseX-45;
                c.y = mouseY-10;
            }
            //c.y = 10;
            //if (c.isExpanded()) {
                //Gui.drawRect(c.x, c.y, c.x+(110), (int) (c.y+30+(modulecount*20)),new Color(0,0,0,150).getRGB());
                Bloom.INSTANCE.bloomHandler.bloom(c.x,c.y,110,30+(modulecount*20), 10, new Color(25,25,25,(int) (125 * indexx)));
            //}
            //Gui.drawRect(c.x, c.y, c.x+(110), (int) (c.y+20), Hud.rgb(index).getRGB());
            //RenderUtils.drawRoundedRect(c.x, 10, c.x+(110), (int) (30+(modulecount*30)), 20D, new Color(255,255,255,50));
            fr2.drawStringWithShadow(c.getName(), (float) ((c.x + 55) - (fr2.getWidth(c.getName()) / 2)), (float) (c.y+8), new Color(255,255,255,(int) (255 * indexx) < 4 ? 4 : (int) (255 * indexx)).getRGB());
            modulecount = 0;
            if (c.isExpanded()) {
                for (Module m : Bloom.INSTANCE.moduleManager.getModules()) {
                    if (m.category == c) {

                        //Gui.drawRect(c.x, 35+(modulec.x), c.x+(110), 50+(modulecount*20), new Color(50,50,50).getRGB());
                        if (m.toggled) {
                            fr.drawStringWithShadow(m.getName(), c.x+5, (c.y+30) + modulecount*20, new Color(42,175,86,(int) (255 * indexx) < 4 ? 4 : (int) (255 * indexx)).getRGB());
                        } else {
                            fr.drawStringWithShadow(m.getName(), c.x+5, (c.y+30) + modulecount*20, new Color(255,255,255,(int) (255 * indexx) < 4 ? 4 : (int) (255 * indexx)).getRGB());
                        }
                        if (m.expanded && indexx == 1) {
                            float valuecount = 0;
                            int expandwidth = 0;
                            int AIOAVJRO = 0;
                            for (Value val : Bloom.INSTANCE.valueManager.getAllValuesFrom(m.getName())) {
                                if (!val.isVisible())
                                    continue;
                                AIOAVJRO++;
                                if (fr.getWidth(val.hitboxname) > expandwidth) {
                                    expandwidth = (int) fr.getWidth(val.hitboxname);
                                }
                            };
                            if (AIOAVJRO > 0) {
                                Bloom.INSTANCE.bloomHandler.bloom(c.x+110, (c.y+30 + modulecount*20), expandwidth, AIOAVJRO*10, 10, new Color(0,0,0,125));
                            }
                            //if (AIOAVJRO > 0) {
                            //BlurUtil.blur(c.x+80, (c.y+30 + modulecount*20), c.x+110+expandwidth, (c.y+30 + modulecount*20)+AIOAVJRO*10, 5);
                            //Gui.drawRect(c.x+110, (c.y+30 + modulecount*20), c.x+110+expandwidth, (c.y+30 + modulecount*20)+AIOAVJRO*10,new Color(0,0,0,150).getRGB());
                            //}
                            for (Value val : Bloom.INSTANCE.valueManager.getAllValuesFrom(m.getName())) {
                                if (!val.isVisible())
                                    continue;
                                //if (val instanceof NumberValue) {
                                //Gui.drawRect(c.x+110, (40 + modulecount*20)+valuecount*20, c.x+(110+fr.getStringWidth(val.hitboxname)), (50 + modulecount*20)+valuecount*20, new Color(50,50,50).getRGB());
                                //BlurUtil.blur(c.x+110, (int) ((39 + modulecount*20)+valuecount*20), c.x+(95+fr.getStringWidth(val.hitboxname)), (int) ((50 + modulecount*20)+valuecount*20), 10);
                                //Gui.drawRect(c.x+110, (int) ((c.y+30 + modulecount*20)+valuecount*20), c.x+(95+fr.getStringWidth(val.hitboxname)), (int) ((c.y+30 + modulecount*20)+valuecount*20), new Color(255,255,255,50).getRGB());
                                //} else if (val instanceof BooleanValue) {
                                //	Gui.drawRect(c.x+110, (40 + modulecount*20)+valuecount*20, c.x+(110+fr.getStringWidth(val.hitboxname)), (50 + modulecount*20)+valuecount*20, new Color(50,50,50).getRGB());
                                //}
                                if (val instanceof NumberValue) {
                                    
                                    Gui.drawRect(
                                            (int) (c.x + (110 + ((((NumberValue) val).getObject().doubleValue()/((NumberValue) val).getMax().doubleValue())*fr.getWidth(val.hitboxname)))),
                                            (int) ((c.y + 30 + modulecount * 20) + valuecount * 20),
                                            c.x + 110,
                                            (int) ((c.y + 40 + modulecount * 20) + valuecount * 20),
                                            Color.getHSBColor(((System.currentTimeMillis() / 10) % 255) / 255F, 0.5F, 0.5F).getRGB()
                                    );
                                    if (holding && inrange((int) (c.x + (110 + fr.getWidth(val.hitboxname))), c.x + 110, (c.y + 30 + modulecount * 20) + valuecount * 20, (c.y + 40 + modulecount * 20) + valuecount * 20, mouseX, mouseY)) {
                                        double diff = Math.abs(((c.x + 110) - mouseX) - 1) / ((double) (fr.getWidth(val.hitboxname)));
                                        //System.out.println(diff);
                                        if (diff <= 0.04 || diff * ((NumberValue) val).getMax().doubleValue() < ((NumberValue) val).getMin().doubleValue()) {
                                            ((NumberValue) val).setObject(((NumberValue) val).getMin());
                                        } else {
                                            double calculatedValue = roundToPlace(diff * ((NumberValue) val).getMax().doubleValue(), 2);
                                            Number number = ((NumberValue) val).getObject();
                                            if (number instanceof Double) {
                                                ((NumberValue) val).setObject(calculatedValue);
                                            } else if (number instanceof Integer) {
                                                ((NumberValue) val).setObject((int) (calculatedValue));
                                            } else if (number instanceof Long) {
                                                ((NumberValue) val).setObject((long) (calculatedValue));
                                            } else if (number instanceof Float) {
                                                ((NumberValue) val).setObject((float) (calculatedValue));
                                            }
                                        }
                                    }
                                }
                                //if (val instanceof BooleanValue && ((BooleanValue) val).getObject()) {
                                //	fr.drawString(val.hitboxname, c.x+110, (c.y+30 + modulecount*20)+valuecount*20, new Color(42,175,86).getRGB());
                                //} else {
                                if (val instanceof BooleanValue) {
                                    fr.drawStringWithShadow(val.name, c.x + 110, (c.y + 30 + modulecount * 20) + valuecount * 20, new Color(255, 255, 255).getRGB());
                                    RenderUtil.drawRoundedRect(
                                            (int) (c.x + 111 + fr.getWidth(val.name)),
                                            (int) ((c.y + 31 + modulecount * 20) + valuecount * 20),
                                            (int) (c.x + 118 + fr.getWidth(val.name)),
                                            (int) ((c.y + 38 + modulecount * 20) + valuecount * 20),
                                            4, ((BooleanValue) val).getObject() ? Color.getHSBColor(((System.currentTimeMillis() / 10) % 255) / 255F, 0.5F, 1) : Color.darkGray);
                                } else {
                                    fr.drawStringWithShadow(val.hitboxname, c.x + 110, (c.y + 30 + modulecount * 20) + valuecount * 20, new Color(255, 255, 255).getRGB());
                                }
                                //}
                                valuecount += 0.5;
                            }
                        }
                        modulecount += 1;
                    }
                }
            }
            index += 5;
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
        GlStateManager.popMatrix();
    }

    public boolean inrange(int x, int minx, int miny, int y, int mousex, int mousey) {
        return mousex > minx && mousex < x && mousey > miny && mousey < y;
    }
    public boolean inrange(int x, int minx, float miny, float y, int mousex, int mousey) {
        return mousex > minx && mousex < x && mousey > miny && mousey < y;
    }


    public static double roundToPlace(double value, int places) {//:O FLASHY GONNA SIKD THIS!??!??
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0) {
            holding = true;
        }
        // TODO Auto-generated method stub
        int index = 2;
        for (Category c : Category.values()) {
            int modulecount = 0;
            //Gui.drawRect(c.x, 10, c.x+(110), 30+(modulecount*30), new Color(40,40,40).getRGB());
            if (inrange(c.x+110,c.x,c.y,c.y+20,mouseX,mouseY)) {
                if (mouseButton == 0) {
                    c.holding = true;
                } else {
                    c.setExpanded(!c.isExpanded());
                }
            }
            if (c.isExpanded()) {
                for (Module m : Bloom.INSTANCE.moduleManager.getModules()) {
                    if (m.category == c) {
                        if (m.expanded) {
                            float valuecount = 0;

                            for (Value val : Bloom.INSTANCE.valueManager.getAllValuesFrom(m.getName())) {
                                if (!val.isVisible())
                                    continue;
                                //System.out.println(inrange(c.x+110, c.x+(110+fr.getStringWidth(val.hitboxname)), (40 + modulecount*20)+valuecount*20, (50 + modulecount*20)+valuecount*20,mouseX,mouseY) + " " + c.x+110 + " " + mouseX + " " + mouseY);
                                //Gui.drawRect
                                if (inrange((int) (c.x+(110+fr.getWidth(val.hitboxname))), c.x+110, (c.y+30 + modulecount*20)+valuecount*20, ((c.y+40) + modulecount*20)+valuecount*20,mouseX,mouseY)) {
                                    if (val instanceof BooleanValue) {
                                        ((BooleanValue) val).flip();
                                    } else if (val instanceof ModeValue) {
                                        if (mouseButton != 1) {
                                            ((ModeValue) val).cycle();
                                        } else {
                                            ((ModeValue) val).cycleReverse();
                                        }
                                    }
                                }
                                valuecount += 0.5;
                            }
                        }
                        if (inrange(c.x+(110),c.x, c.y+20+(modulecount*20),  c.y+40+(modulecount*20),mouseX,mouseY)) {
                            if (mouseButton == 1) {
                                if (!m.expanded)
                                    for (Module n : Bloom.INSTANCE.moduleManager.getModules())
                                        n.expanded = false;
                                m.expanded = !m.expanded;
                            } else
                                m.setToggled(!m.isToggled());
                        }
                        modulecount += 1;
                    }
                }
            }
            index += 5;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (state == 0) {
            holding = false;
            for (Category c : Category.values()) {
                c.holding = false;
            }
        }
        // TODO Auto-generated method stub
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        // TODO Auto-generated method stub
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }

    public void keyTyped(char typedChar, int keyCode)
    {
    	if (keyCode == 1)
    	{
    		closingGUI = true;
    	}
    }
    
    @Override
    public void initGui() {
        // TODO Auto-generated method stub
    	
    	if (mc.gameSettings.ofFastRender)
    	{
    		mc.gameSettings.setOptionValueOF(GameSettings.Options.FAST_RENDER, 0);
    	}
    	
        Display.setVSyncEnabled(true);
        super.initGui();
    }

    @Override
    public void onGuiClosed() {
        // TODO Auto-generated method stub
        Display.setVSyncEnabled(mc.gameSettings.enableVsync);
        super.onGuiClosed();
    }

}
