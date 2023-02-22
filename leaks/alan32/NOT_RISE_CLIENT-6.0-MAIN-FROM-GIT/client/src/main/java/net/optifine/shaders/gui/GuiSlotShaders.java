package net.optifine.shaders.gui;

import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.resources.I18n;
import net.minecraft.src.Config;
import net.optifine.Lang;
import net.optifine.shaders.IShaderPack;
import net.optifine.shaders.Shaders;
import net.optifine.util.ResUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

class GuiSlotShaders extends GuiSlot {
    private ArrayList shaderslist;
    private int selectedIndex;
    private long lastClickedCached = 0L;
    final GuiShaders shadersGui;

    public GuiSlotShaders(final GuiShaders par1GuiShaders, final int width, final int height, final int top, final int bottom, final int slotHeight) {
        super(par1GuiShaders.getMc(), width, height, top, bottom, slotHeight);
        this.shadersGui = par1GuiShaders;
        this.updateList();
        this.amountScrolled = 0.0F;
        final int i = this.selectedIndex * slotHeight;
        final int j = (bottom - top) / 2;

        if (i > j) {
            this.scrollBy(i - j);
        }
    }

    /**
     * Gets the width of the list
     */
    public int getListWidth() {
        return this.width - 20;
    }

    public void updateList() {
        this.shaderslist = Shaders.listOfShaders();
        this.selectedIndex = 0;
        int i = 0;

        for (final int j = this.shaderslist.size(); i < j; ++i) {
            if (this.shaderslist.get(i).equals(Shaders.currentShaderName)) {
                this.selectedIndex = i;
                break;
            }
        }
    }

    protected int getSize() {
        return this.shaderslist.size();
    }

    /**
     * The element in the slot that was clicked, boolean for whether it was double clicked or not
     */
    protected void elementClicked(final int index, final boolean doubleClicked, final int mouseX, final int mouseY) {
        if (index != this.selectedIndex || this.lastClicked != this.lastClickedCached) {
            final String s = (String) this.shaderslist.get(index);
            final IShaderPack ishaderpack = Shaders.getShaderPack(s);

            if (this.checkCompatible(ishaderpack, index)) {
                this.selectIndex(index);
            }
        }
    }

    private void selectIndex(final int index) {
        this.selectedIndex = index;
        this.lastClickedCached = this.lastClicked;
        Shaders.setShaderPack((String) this.shaderslist.get(index));
        Shaders.uninit();
        this.shadersGui.updateButtons();
    }

    private boolean checkCompatible(final IShaderPack sp, final int index) {
        if (sp == null) {
            return true;
        } else {
            final InputStream inputstream = sp.getResourceAsStream("/shaders/shaders.properties");
            final Properties properties = ResUtils.readProperties(inputstream, "Shaders");

            if (properties == null) {
                return true;
            } else {
                final String s = "version.1.8.9";
                String s1 = properties.getProperty(s);

                if (s1 == null) {
                    return true;
                } else {
                    s1 = s1.trim();
                    final String s2 = "L5";
                    final int i = Config.compareRelease(s2, s1);

                    if (i >= 0) {
                        return true;
                    } else {
                        final String s3 = ("HD_U_" + s1).replace('_', ' ');
                        final String s4 = I18n.format("of.message.shaders.nv1", s3);
                        final String s5 = I18n.format("of.message.shaders.nv2");
                        final GuiYesNoCallback guiyesnocallback = new GuiYesNoCallback() {
                            public void confirmClicked(final boolean result, final int id) {
                                if (result) {
                                    GuiSlotShaders.this.selectIndex(index);
                                }

                                GuiSlotShaders.this.mc.displayGuiScreen(GuiSlotShaders.this.shadersGui);
                            }
                        };
                        final GuiYesNo guiyesno = new GuiYesNo(guiyesnocallback, s4, s5, 0);
                        this.mc.displayGuiScreen(guiyesno);
                        return false;
                    }
                }
            }
        }
    }

    /**
     * Returns true if the element passed in is currently selected
     */
    protected boolean isSelected(final int index) {
        return index == this.selectedIndex;
    }

    protected int getScrollBarX() {
        return this.width - 6;
    }

    /**
     * Return the height of the content being scrolled
     */
    protected int getContentHeight() {
        return this.getSize() * 18;
    }

    protected void drawBackground() {
    }

    protected void drawSlot(final int index, final int posX, final int posY, final int contentY, final int mouseX, final int mouseY) {
        String s = (String) this.shaderslist.get(index);

        if (s.equals("OFF")) {
            s = Lang.get("of.options.shaders.packNone");
        } else if (s.equals("(internal)")) {
            s = Lang.get("of.options.shaders.packDefault");
        }

        this.shadersGui.drawCenteredString(s, this.width / 2, posY + 1, 14737632);
    }

    public int getSelectedIndex() {
        return this.selectedIndex;
    }
}
