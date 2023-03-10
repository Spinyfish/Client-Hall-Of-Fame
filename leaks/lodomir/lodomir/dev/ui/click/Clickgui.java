/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.ui.click;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import lodomir.dev.November;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import lodomir.dev.settings.Setting;
import lodomir.dev.settings.impl.BooleanSetting;
import lodomir.dev.settings.impl.ModeSetting;
import lodomir.dev.settings.impl.NumberSetting;
import lodomir.dev.ui.click.comp.CheckBox;
import lodomir.dev.ui.click.comp.Combo;
import lodomir.dev.ui.click.comp.Comp;
import lodomir.dev.ui.click.comp.Slider;
import lodomir.dev.ui.font.TTFFontRenderer;
import lodomir.dev.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

public class Clickgui
extends GuiScreen {
    public double posX;
    public double posY;
    public double width;
    public double height;
    public double dragX;
    public double dragY;
    public boolean dragging;
    public Category selectedCategory;
    public TTFFontRenderer fr;
    public int transparent;
    public int color;
    private Module selectedModule;
    public int modeIndex;
    public ArrayList<Comp> comps;

    public Clickgui() {
        this.fr = November.INSTANCE.fm.getFont("PRODUCT SANS 18");
        this.transparent = new Color(32, 166, 255, 170).getRGB();
        this.color = new Color(32, 166, 255).getRGB();
        this.comps = new ArrayList();
        this.dragging = false;
        this.posX = this.getScaledRes().getScaledWidth() / 2 - 400;
        this.posY = this.getScaledRes().getScaledHeight() / 2 - 300;
        this.width = this.posX + 240.0;
        this.height += 200.0;
        this.selectedCategory = Category.COMBAT;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        TTFFontRenderer logo = November.INSTANCE.fm.getFont("SFUI BOLD 26");
        TTFFontRenderer icons = November.INSTANCE.fm.getFont("ICONS 18");
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (this.dragging) {
            this.posX = (double)mouseX - this.dragX;
            this.posY = (double)mouseY - this.dragY;
        }
        this.width = this.posX + 440.0;
        this.height = this.posY + 300.0;
        RenderUtils.drawRect(this.posX, this.posY, this.width, this.height, new Color(9, 8, 13).getRGB());
        RenderUtils.drawRect(this.posX, this.posY - 18.0, this.width, this.posY, new Color(6, 4, 6).getRGB());
        RenderUtils.drawRect(this.posX, this.posY - 18.0, this.posX + (double)logo.getStringWidth(November.INSTANCE.name.toUpperCase()) + 2.0, this.height, new Color(15, 22, 32, 220).getRGB());
        logo.drawStringWithShadow(November.INSTANCE.name.toUpperCase(), this.posX + 2.0, this.posY + 3.0 - 18.0, this.transparent);
        logo.drawStringWithShadow(November.INSTANCE.name.toUpperCase(), this.posX + 3.0, this.posY + 3.0 - 18.0, -1);
        int offset = 0;
        for (Category category : Category.values()) {
            RenderUtils.drawRect(this.posX + 2.0, this.posY + 1.0 + (double)offset, this.posX + 76.0, this.posY + 15.0 + (double)offset, category.equals((Object)this.selectedCategory) ? this.transparent : new Color(0, 0, 0, 0).getRGB());
            switch (category) {
                case COMBAT: {
                    icons.drawStringWithShadow("A", this.posX + 6.0, this.posY + 5.0, this.color);
                    break;
                }
                case MOVEMENT: {
                    icons.drawStringWithShadow("G", this.posX + 6.0, this.posY + 20.0, this.color);
                    break;
                }
                case PLAYER: {
                    icons.drawStringWithShadow("F", this.posX + 6.0, this.posY + 35.0, this.color);
                    break;
                }
                case RENDER: {
                    icons.drawStringWithShadow("C", this.posX + 6.0, this.posY + 50.0, this.color);
                    break;
                }
                case OTHER: {
                    icons.drawStringWithShadow("I", this.posX + 6.0, this.posY + 65.0, this.color);
                }
            }
            this.fr.drawStringWithShadow(category.getName(), (int)this.posX + 19, (int)(this.posY + 5.0) + offset, -1);
            offset += 15;
        }
        offset = 0;
        for (Module m : November.INSTANCE.getModuleManager().getModulesByCategory(this.selectedCategory)) {
            RenderUtils.drawRect(this.posX + 85.0, this.posY + 1.0 + (double)offset, this.posX + 300.0, this.posY + 15.0 + (double)offset, m.isEnabled() ? this.transparent : new Color(28, 28, 28).getRGB());
            this.fr.drawStringWithShadow(m.getName(), (int)this.posX + 87, (int)(this.posY + 5.0) + offset, -1);
            offset += 15;
        }
        for (Comp comp : this.comps) {
            comp.drawScreen(mouseX, mouseY);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        for (Category c : Category.values()) {
            if (keyCode != 1) continue;
            this.selectedCategory = c;
        }
        for (Comp comp : this.comps) {
            comp.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.isInside(mouseX, mouseY, this.posX, this.posY - 18.0, this.width, this.posY) && mouseButton == 0) {
            this.dragging = true;
            this.dragX = (double)mouseX - this.posX;
            this.dragY = (double)mouseY - this.posY;
        }
        int offset = 0;
        for (Category category : Category.values()) {
            if (this.isInside(mouseX, mouseY, this.posX + 2.0, this.posY + 1.0 + (double)offset, this.posX + 76.0, this.posY + 15.0 + (double)offset) && mouseButton == 0) {
                this.selectedCategory = category;
            }
            offset += 15;
        }
        offset = 0;
        for (Module m : November.INSTANCE.getModuleManager().getModulesByCategory(this.selectedCategory)) {
            if (this.isInside(mouseX, mouseY, this.posX + 85.0, this.posY + 1.0 + (double)offset, this.posX + 300.0, this.posY + 15.0 + (double)offset)) {
                if (mouseButton == 0) {
                    m.toggle();
                }
                if (mouseButton == 1) {
                    int sOffset = 3;
                    this.comps.clear();
                    if (m.getSettings() != null) {
                        for (Setting setting : m.getSettings()) {
                            if (setting.isVisible()) {
                                this.selectedModule = m;
                                if (setting instanceof ModeSetting) {
                                    this.comps.add(new Combo(275.0, sOffset, this, this.selectedModule, (ModeSetting)setting));
                                    sOffset += 15;
                                }
                                if (setting instanceof BooleanSetting) {
                                    this.comps.add(new CheckBox(275.0, sOffset, this, this.selectedModule, (BooleanSetting)setting));
                                    sOffset += 15;
                                }
                                if (!(setting instanceof NumberSetting)) continue;
                                this.comps.add(new Slider(275.0, sOffset, this, this.selectedModule, (NumberSetting)setting));
                                sOffset += 25;
                                continue;
                            }
                            if (setting instanceof ModeSetting) {
                                this.comps.remove(new Combo(275.0, sOffset, this, this.selectedModule, (ModeSetting)setting));
                            }
                            if (setting instanceof BooleanSetting) {
                                this.comps.remove(new CheckBox(275.0, sOffset, this, this.selectedModule, (BooleanSetting)setting));
                            }
                            if (!(setting instanceof NumberSetting)) continue;
                            this.comps.remove(new Slider(275.0, sOffset, this, this.selectedModule, (NumberSetting)setting));
                        }
                    }
                }
            }
            offset += 15;
        }
        for (Comp comp : this.comps) {
            comp.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        this.dragging = false;
        for (Comp comp : this.comps) {
            comp.mouseReleased(mouseX, mouseY, state);
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        this.dragging = false;
    }

    public boolean isInside(int mouseX, int mouseY, double x, double y, double x2, double y2) {
        return (double)mouseX > x && (double)mouseX < x2 && (double)mouseY > y && (double)mouseY < y2;
    }

    public ScaledResolution getScaledRes() {
        return new ScaledResolution(Minecraft.getMinecraft());
    }
}

