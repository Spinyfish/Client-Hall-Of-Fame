/*
 * Decompiled with CFR 0.152.
 */
package optifine;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiVideoSettings;
import net.minecraft.client.settings.GameSettings;
import optifine.IOptionControl;
import optifine.Lang;

public class TooltipManager {
    private GuiScreen guiScreen = null;
    private int lastMouseX = 0;
    private int lastMouseY = 0;
    private long mouseStillTime = 0L;

    public TooltipManager(GuiScreen p_i97_1_) {
        this.guiScreen = p_i97_1_;
    }

    public void drawTooltips(int p_drawTooltips_1_, int p_drawTooltips_2_, List p_drawTooltips_3_) {
        if (Math.abs(p_drawTooltips_1_ - this.lastMouseX) <= 5 && Math.abs(p_drawTooltips_2_ - this.lastMouseY) <= 5) {
            int i = 700;
            if (System.currentTimeMillis() >= this.mouseStillTime + (long)i) {
                int j = GuiScreen.width / 2 - 150;
                int k = GuiScreen.height / 6 - 7;
                if (p_drawTooltips_2_ <= k + 98) {
                    k += 105;
                }
                int l = j + 150 + 150;
                int i1 = k + 84 + 10;
                GuiButton guibutton = this.getSelectedButton(p_drawTooltips_1_, p_drawTooltips_2_, p_drawTooltips_3_);
                if (guibutton instanceof IOptionControl) {
                    IOptionControl ioptioncontrol = (IOptionControl)((Object)guibutton);
                    GameSettings.Options gamesettings$options = ioptioncontrol.getOption();
                    String[] astring = TooltipManager.getTooltipLines(gamesettings$options);
                    if (astring == null) {
                        return;
                    }
                    GuiVideoSettings.drawGradientRect(this.guiScreen, j, k, l, i1, -536870912, -536870912);
                    for (int j1 = 0; j1 < astring.length; ++j1) {
                        String s = astring[j1];
                        int k1 = 0xDDDDDD;
                        if (s.endsWith("!")) {
                            k1 = 0xFF2020;
                        }
                        FontRenderer fontrenderer = Minecraft.getMinecraft().fontRendererObj;
                        fontrenderer.drawStringWithShadow(s, j + 5, k + 5 + j1 * 11, k1);
                    }
                }
            }
        } else {
            this.lastMouseX = p_drawTooltips_1_;
            this.lastMouseY = p_drawTooltips_2_;
            this.mouseStillTime = System.currentTimeMillis();
        }
    }

    private GuiButton getSelectedButton(int p_getSelectedButton_1_, int p_getSelectedButton_2_, List p_getSelectedButton_3_) {
        for (int i = 0; i < p_getSelectedButton_3_.size(); ++i) {
            boolean flag;
            GuiButton guibutton = (GuiButton)p_getSelectedButton_3_.get(i);
            int j = GuiVideoSettings.getButtonWidth(guibutton);
            int k = GuiVideoSettings.getButtonHeight(guibutton);
            boolean bl = flag = p_getSelectedButton_1_ >= guibutton.xPosition && p_getSelectedButton_2_ >= guibutton.yPosition && p_getSelectedButton_1_ < guibutton.xPosition + j && p_getSelectedButton_2_ < guibutton.yPosition + k;
            if (!flag) continue;
            return guibutton;
        }
        return null;
    }

    private static String[] getTooltipLines(GameSettings.Options p_getTooltipLines_0_) {
        return TooltipManager.getTooltipLines(p_getTooltipLines_0_.getEnumString());
    }

    private static String[] getTooltipLines(String p_getTooltipLines_0_) {
        String s;
        String s1;
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < 10 && (s1 = Lang.get(s = p_getTooltipLines_0_ + ".tooltip." + (i + 1), null)) != null; ++i) {
            list.add(s1);
        }
        if (list.size() <= 0) {
            return null;
        }
        String[] astring = list.toArray(new String[list.size()]);
        return astring;
    }
}

