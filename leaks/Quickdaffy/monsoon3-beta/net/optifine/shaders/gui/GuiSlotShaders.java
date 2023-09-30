/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.shaders.gui;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.resources.I18n;
import net.minecraft.src.Config;
import net.optifine.Lang;
import net.optifine.shaders.IShaderPack;
import net.optifine.shaders.Shaders;
import net.optifine.shaders.gui.GuiShaders;
import net.optifine.util.ResUtils;

class GuiSlotShaders
extends GuiSlot {
    private ArrayList shaderslist;
    private int selectedIndex;
    private long lastClickedCached = 0L;
    final GuiShaders shadersGui;

    public GuiSlotShaders(GuiShaders par1GuiShaders, int width, int height, int top, int bottom, int slotHeight) {
        super(par1GuiShaders.getMc(), width, height, top, bottom, slotHeight);
        this.shadersGui = par1GuiShaders;
        this.updateList();
        this.amountScrolled = 0.0f;
        int i = this.selectedIndex * slotHeight;
        int j = (bottom - top) / 2;
        if (i > j) {
            this.scrollBy(i - j);
        }
    }

    @Override
    public int getListWidth() {
        return this.width - 20;
    }

    public void updateList() {
        this.shaderslist = Shaders.listOfShaders();
        this.selectedIndex = 0;
        int j = this.shaderslist.size();
        for (int i = 0; i < j; ++i) {
            if (!((String)this.shaderslist.get(i)).equals(Shaders.currentShaderName)) continue;
            this.selectedIndex = i;
            break;
        }
    }

    @Override
    protected int getSize() {
        return this.shaderslist.size();
    }

    @Override
    protected void elementClicked(int index, boolean doubleClicked, int mouseX, int mouseY) {
        String s;
        IShaderPack ishaderpack;
        if ((index != this.selectedIndex || this.lastClicked != this.lastClickedCached) && this.checkCompatible(ishaderpack = Shaders.getShaderPack(s = (String)this.shaderslist.get(index)), index)) {
            this.selectIndex(index);
        }
    }

    private void selectIndex(int index) {
        this.selectedIndex = index;
        this.lastClickedCached = this.lastClicked;
        Shaders.setShaderPack((String)this.shaderslist.get(index));
        Shaders.uninit();
        this.shadersGui.updateButtons();
    }

    private boolean checkCompatible(IShaderPack sp, final int index) {
        if (sp == null) {
            return true;
        }
        InputStream inputstream = sp.getResourceAsStream("/shaders/shaders.properties");
        Properties properties = ResUtils.readProperties(inputstream, "Shaders");
        if (properties == null) {
            return true;
        }
        String s = "version.1.8.9";
        String s1 = properties.getProperty(s);
        if (s1 == null) {
            return true;
        }
        String s2 = "L5";
        int i = Config.compareRelease(s2, s1 = s1.trim());
        if (i >= 0) {
            return true;
        }
        String s3 = ("HD_U_" + s1).replace('_', ' ');
        String s4 = I18n.format("of.message.shaders.nv1", s3);
        String s5 = I18n.format("of.message.shaders.nv2", new Object[0]);
        GuiYesNoCallback guiyesnocallback = new GuiYesNoCallback(){

            @Override
            public void confirmClicked(boolean result, int id) {
                if (result) {
                    GuiSlotShaders.this.selectIndex(index);
                }
                GuiSlotShaders.this.mc.displayGuiScreen(GuiSlotShaders.this.shadersGui);
            }
        };
        GuiYesNo guiyesno = new GuiYesNo(guiyesnocallback, s4, s5, 0);
        this.mc.displayGuiScreen(guiyesno);
        return false;
    }

    @Override
    protected boolean isSelected(int index) {
        return index == this.selectedIndex;
    }

    @Override
    protected int getScrollBarX() {
        return this.width - 6;
    }

    @Override
    protected int getContentHeight() {
        return this.getSize() * 18;
    }

    @Override
    protected void drawBackground() {
    }

    @Override
    protected void drawSlot(int index, int posX, int posY, int contentY, int mouseX, int mouseY) {
        String s = (String)this.shaderslist.get(index);
        if (s.equals("OFF")) {
            s = Lang.get("of.options.shaders.packNone");
        } else if (s.equals("(internal)")) {
            s = Lang.get("of.options.shaders.packDefault");
        }
        this.shadersGui.drawCenteredString(s, this.width / 2, posY + 1, 0xE0E0E0);
    }

    public int getSelectedIndex() {
        return this.selectedIndex;
    }
}

