package dev.elysium.client.ui.gui.clickgui.old;

import java.util.Comparator;
import java.util.List;

import dev.elysium.base.mods.Category;
import dev.elysium.base.mods.Mod;
import dev.elysium.base.mods.settings.*;
import dev.elysium.client.Elysium;
import dev.elysium.client.utils.render.RenderUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

public class COMPACT_CG extends GuiScreen {

    public boolean initialize = true;;
    public float initialx, initialy = 0;
    public float releasex, releasey = 0;
    public float mouseywheel = 0;
    public float mouseywheel2 = 0;

    public Minecraft mc = Minecraft.getMinecraft();
    public int delay = 500;
    public int starttime = 1;
    public Category lastcategorymove;
    public Category mouseovercategory;
    public Mod mouseovermodule;
    public Setting mouseoversetting;
    public NumberSetting mouseoversettingstate;
    public int mousesettingnumberstate = 0;
    public String mouseovermode;
    public GuiScreen parentScreen;
    public Elysium Client = Elysium.getInstance();

    public void keyTyped(char typedChar, int code) {
        if(code == Keyboard.KEY_ESCAPE) {
            mc.thePlayer.closeScreen();
        }
        for(Mod m : Client.getModManager().getMods()) {
            if(m.binding) {
                if(code != Keyboard.KEY_UP && code != Keyboard.KEY_DOWN && code != Keyboard.KEY_LEFT && code != Keyboard.KEY_RIGHT && code !=  Keyboard.KEY_RSHIFT && code != Keyboard.KEY_ESCAPE) {
                    if(code == Keyboard.KEY_RETURN){
                        m.binding = false;
                        return;
                    } else if(code == Keyboard.KEY_BACK) {
                        m.keyCode.setKeyCode(Keyboard.CHAR_NONE);
                        m.binding = false;
                    }else {
                        m.keyCode.setKeyCode(code);
                        m.binding = false;
                        return;
                    }
                }


            }
        }
    }

    public void onGuiClosed() {
        Elysium.getInstance().getModManager().getModByName("ClickGui").toggled = false;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        FontRenderer fr = mc.fontRendererObj;
        RenderUtils.drawRoundedRect(200, 100, width-200, height-100, 120, 0xFF040012);
        double cg_width = width-400;
        double cg_height = height-200;
        int cindex = 0;
        boolean categoried = false;
        for(Category c : Category.values()) {
            float x1 = (float) (208+((cg_width/Category.values().length))+((cg_width/Category.values().length))*cindex - 10);
            float x2 = (float) (212+(cg_width/Category.values().length)*cindex - 10);
            if(mouseX > x2 && mouseX < x1) {
                if(mouseY > 102 && mouseY < 115) {
                    mouseovercategory = c;
                    categoried = true;
                }
            }
            RenderUtils.drawRoundedRect(x2, 102, x1, 115, 10, mouseovercategory == c ? 0xFF5a4780 : 0xFF2c194f);
            fr.drawCenteredString(c.name().toLowerCase(), (float) (x1 - (x1-x2)/2), 108.5F-fr.FONT_HEIGHT/2, -1);
            cindex++;
        }

        if(!categoried) mouseovercategory = null;
        int mindex = 0;
        if(lastcategorymove != null) {
            List<Mod> mods = Client.getModManager().getModsByCategory(lastcategorymove);

            mods.sort(Comparator.comparingInt(m ->
                    fr.getStringWidth(((Mod)m).name)).reversed());
            RenderUtils.drawRoundedRect(201.5f, 116.5f, 310.5f, height-102F, 110, 0xFF000000);
            RenderUtils.drawRoundedRect(202, 117, 310, height-102.5F, 110, 0xFF17112b);
            for(Mod m : mods) {
                fr.drawCenteredString(m.name.toLowerCase(), 257, 124.5F-fr.FONT_HEIGHT/2 + (fr.FONT_HEIGHT+5)*mindex, m.toggled ? -1 : 0xFF888888);
                boolean numbered = false;
                if(mouseovermodule == m) {
                    RenderUtils.drawRoundedRect(314.5f, 117.5f, width - 201.5f, height-102.5f, 120, 0xFF000000);
                    RenderUtils.drawRoundedRect(315, 118, width - 202, height-103, 120, 0xFF13112b);
                    fr.drawString(EnumChatFormatting.BOLD + m.name + EnumChatFormatting.GRAY + " (" + (m.toggled ? "enabled" : "disabled") + ")", 319, 122, -1);
                    int sindex = 1;
                    for(Setting s : m.settings) {
                        if(s instanceof NumberSetting) {
                            NumberSetting ns = (NumberSetting)s;
                            fr.drawString(s.name.toLowerCase()+": " + EnumChatFormatting.GRAY + ns.getValue(), 319, 135 + (fr.FONT_HEIGHT+2)*sindex, -1);

                            Gui.drawRect(425, 139 + (fr.FONT_HEIGHT+2)*sindex, width-208, 138 + (fr.FONT_HEIGHT+2)*sindex, 0xFF6027c2);
                            float sliderwidth = width-(632);
                            double xpos = 425+((ns.getValue()-ns.getMin())*(sliderwidth/(ns.getMax()-ns.getMin())));
                            double ypos = 138.5 + (fr.FONT_HEIGHT+2)*sindex;

                            int colour = 0xFF6027c2;

                            if(mouseX > xpos - 4 && xpos + 4 > mouseX && mouseY > ypos - 4 && ypos + 4 > mouseY) {
                                mouseoversettingstate = ns;
                                colour = 0xFF40158a;
                            }

                            RenderUtils.drawCircle(xpos, ypos, 4, 0xFF13112b);
                            RenderUtils.drawCircle(xpos, ypos, 3, colour);

                            if(mousesettingnumberstate == 1 && mouseoversettingstate == ns) {
                                float slider = width-(632);
                                double range = (ns.getMax() - ns.getMin());
                                double value = (mouseX - 425) / slider;
                                if(ns.getMin() < 0) value -= ns.getMax()/range;
                                if(ns.getMin() > 0) value += ns.getMin()/range;
                                ns.setValue((float) (value * range));
                            }

                        }

                        if(s instanceof BooleanSetting) {
                            BooleanSetting bs = (BooleanSetting)s;
                            fr.drawString(s.name.toLowerCase() + " - " + (bs.isEnabled() ? "on" : "off"), 319, 135 + (fr.FONT_HEIGHT+2)*sindex, bs.isEnabled() ? 0xFF888888 : -1);
                        }

                        if(s instanceof ModeSetting) {
                            ModeSetting ms = (ModeSetting)s;
                            fr.drawString(s.name.toLowerCase()+": " + EnumChatFormatting.GRAY + ms.getMode(), 319, 135 + (fr.FONT_HEIGHT+2)*sindex, -1);
                        }

                        if(s instanceof KeybindSetting) {
                            KeybindSetting ks = (KeybindSetting)s;
                            fr.drawString("Bind: " + Keyboard.getKeyName(ks.getKeyCode()), 319, height - 105 - (fr.FONT_HEIGHT), -1);
                        }
                        sindex++;
                    }
                } else if(mouseovermodule == null) {
                    fr.drawCenteredString("Select a module (RMB)", 1.15F*width/2, height/2-fr.FONT_HEIGHT, -1);
                    fr.drawCenteredString("Toggle a module (LMB)", 1.15F*width/2, height/2+fr.FONT_HEIGHT, -1);
                }
                if(!numbered && !Mouse.isButtonDown(0)) {
                    mouseoversettingstate = null;
                }
                mindex++;
            }
        } else {
            fr.drawCenteredString("Select a category (LMB)", width/2, height/2, -1);
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
        mousesettingnumberstate = 0;
        mouseoversettingstate = null;
    }
    public void mouseClickMove(int mouseX, int mouseY, int button, long timeSinceLastClick) {

    }

    public void mouseClicked(int mouseX, int mouseY, int button){
        if(mouseovercategory != null && button == 0) {
            lastcategorymove = mouseovercategory;
        }


        int mindex = 0;
        if(lastcategorymove != null) {
            FontRenderer fr = mc.fontRendererObj;
            List<Mod> modules = Elysium.getInstance().getModManager().getModsByCategory(lastcategorymove);

            modules.sort(Comparator.comparingInt(m ->
                    fr.getStringWidth(((Mod)m).name)).reversed());

            for(Mod m : modules) {
                float stringwidth = fr.getStringWidth(m.name.toLowerCase());
                if(mouseX > 257-stringwidth/2 && 257+stringwidth/2 > mouseX) {

                    if(mouseY > 124.5F-fr.FONT_HEIGHT + 2 + (fr.FONT_HEIGHT+5)*mindex && 124.5F+fr.FONT_HEIGHT - 2 + (fr.FONT_HEIGHT+5)*mindex > mouseY) {
                        switch (button) {
                            case 0:
                                m.toggle();
                                break;

                            case 1:
                                mouseovermodule = m;
                                break;

                        }
                    }

                }
                if(m == mouseovermodule) {
                    int sindex = 1;
                    for(Setting s : m.settings) {
                        if(s instanceof NumberSetting) {
                            NumberSetting ns = (NumberSetting)s;
                            fr.drawString(s.name.toLowerCase()+": " + EnumChatFormatting.GRAY + ns.getValue(), 319, 135 + (fr.FONT_HEIGHT+2)*sindex, -1);

                            Gui.drawRect(425, 139 + (fr.FONT_HEIGHT+2)*sindex, width-208, 138 + (fr.FONT_HEIGHT+2)*sindex, 0xFF6027c2);
                            float sliderwidth = width-(632);
                            double xpos = 425+((ns.getValue()-ns.getMin())*(sliderwidth/(ns.getMax()-ns.getMin())));
                            double ypos = 138.5 + (fr.FONT_HEIGHT+2)*sindex;

                            if(mouseX > xpos - 4 && xpos + 4 > mouseX && mouseY > ypos - 4 && ypos + 4 > mouseY) {
                                mousesettingnumberstate = 1;
                            }

                        }

                        if(s instanceof BooleanSetting) {
                            BooleanSetting bs = (BooleanSetting)s;
                            fr.drawString(s.name.toLowerCase(), 319, 135 + (fr.FONT_HEIGHT+2)*sindex, -1);
                            if(mouseX > 319 && 319 + fr.getStringWidth(s.name.toLowerCase() + " - " + (bs.isEnabled() ? "on" : "off")) > mouseX && mouseY > 135 + (fr.FONT_HEIGHT+2)*sindex && 135 + (fr.FONT_HEIGHT+2)*sindex+fr.FONT_HEIGHT > mouseY) {
                                bs.toggle();
                            }
                        }

                        if(s instanceof ModeSetting) {
                            ModeSetting ms = (ModeSetting)s;
                            fr.drawString(s.name.toLowerCase()+": " + EnumChatFormatting.GRAY + ms.getMode(), 319, 135 + (fr.FONT_HEIGHT+2)*sindex, -1);
                            if(mouseX > 319 && 319 + fr.getStringWidth(ms.name + ": " + ms.getMode()) > mouseX && mouseY > 135 + (fr.FONT_HEIGHT+2)*sindex && 135 + (fr.FONT_HEIGHT+2)*sindex+fr.FONT_HEIGHT > mouseY) {
                                ms.cycle();
                            }
                        }

                        if(s instanceof KeybindSetting) {
                            KeybindSetting ks = (KeybindSetting)s;
                            fr.drawString("Bind: " + Keyboard.getKeyName(ks.parent.getKey()), 319, height - 105 - (fr.FONT_HEIGHT), -1);
                        }
                        sindex++;
                    }
                }
                mindex++;
            }
        }
    }

    public void playPressSound(SoundHandler soundHandlerIn)
    {
        soundHandlerIn.playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
    }
}
