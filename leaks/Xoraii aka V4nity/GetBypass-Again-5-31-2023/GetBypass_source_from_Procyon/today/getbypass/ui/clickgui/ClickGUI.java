// 
// Decompiled by Procyon v0.5.36
// 

package today.getbypass.ui.clickgui;

import java.io.IOException;
import java.util.Iterator;
import today.getbypass.utils.fontrenderer.FontRenderer;
import today.getbypass.GetBypass;
import today.getbypass.module.ModuleManager;
import today.getbypass.utils.RoundedUtils;
import java.awt.Color;
import today.getbypass.module.Module;
import today.getbypass.module.Category;
import today.getbypass.ui.GuiCustomButton;
import today.getbypass.ui.clickgui.mb.CategoryButton;
import today.getbypass.ui.clickgui.mb.ModButton;
import java.util.ArrayList;
import net.minecraft.client.gui.GuiScreen;

public class ClickGUI extends GuiScreen
{
    public ArrayList<ModButton> guiModButtons;
    public ArrayList<CategoryButton> guiCategoryButtons;
    public ArrayList<GuiCustomButton> guiCustomButtons;
    public Category currentCategory;
    public Module currentModule;
    public CurrentScreen SCREEN;
    
    public ClickGUI() {
        this.guiModButtons = new ArrayList<ModButton>();
        this.guiCategoryButtons = new ArrayList<CategoryButton>();
        this.guiCustomButtons = new ArrayList<GuiCustomButton>();
        this.currentCategory = Category.MOVEMENT;
        this.currentModule = null;
        this.SCREEN = CurrentScreen.BASIC_MODULE_SCREEN;
    }
    
    @Override
    public void initGui() {
        super.initGui();
        this.guiCustomButtons.add(new GuiCustomButton(1, this.width / 2, this.height / 2 + 40, "Back"));
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        RoundedUtils.drawRoundedRect((float)(this.width / 2 - 400), (float)(this.height / 2 - 250), (float)(this.width / 2 + 400), (float)(this.height / 2 - 100 + 350), 20.0f, Color.black.getRGB());
        if (this.SCREEN != CurrentScreen.BASIC_SETTINGS_SCREEN) {
            final int drawX = 460;
            final int drawY = 200;
            int offsetX = 1;
            final int offsetY = 1;
            final int categoryDrawX = 300;
            final int categoryDrawY = 200;
            int categoryOffsetX = 0;
            int categoryOffsetY = 0;
            this.guiModButtons.clear();
            this.guiCategoryButtons.clear();
            Category[] values;
            for (int length = (values = Category.values()).length, j = 0; j < length; ++j) {
                final Category c = values[j];
                this.guiCategoryButtons.add(new CategoryButton(categoryOffsetX, categoryOffsetY, categoryDrawX, categoryDrawY, this, c));
                categoryOffsetY += 60;
                categoryOffsetX += 100;
            }
            for (final Module m : ModuleManager.getModules()) {
                if (m.category == this.currentCategory) {
                    this.guiModButtons.add(new ModButton(offsetX, drawX, drawY, 530, this.mc.fontRendererObj.FONT_HEIGHT + 30, m, this));
                    offsetX += 58;
                }
            }
            GetBypass.title.drawString(GetBypass.name, 565.0f, 120.0f, Color.WHITE);
            for (final ModButton i : this.guiModButtons) {
                i.drawButton(mouseX, mouseY);
            }
            for (final CategoryButton c2 : this.guiCategoryButtons) {
                c2.drawButton(mouseX, mouseY);
            }
        }
        else {
            final FontRenderer descRen = new FontRenderer("assets/minecraft/GetBypass/font/Inter-Regular.ttf", 35.0f);
            GetBypass.title.drawCenteredString(this.currentModule.getName(), (float)(this.width / 2), (float)(this.height / 2 - 200), Color.WHITE);
            descRen.drawCenteredString("Oops! You discovered an unreleased feature! We could not get this to work", (float)(this.width / 2), (float)(this.height / 2 - 150), Color.WHITE);
        }
    }
    
    @Override
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        for (final ModButton m : this.guiModButtons) {
            m.mouseClick(mouseX, mouseY, mouseButton);
        }
        for (final CategoryButton i : this.guiCategoryButtons) {
            i.mouseClick(mouseX, mouseY);
        }
    }
}
