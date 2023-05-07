package client.gayclicks;



import client.Client;
import client.gayclicks.comp.CheckBox;
import client.gayclicks.comp.Combo;
import client.gayclicks.comp.Comp;
import client.gayclicks.comp.Slider;
import client.gayclicks.setting.Setting;
import client.module.Category;
import client.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class Clickgui extends GuiScreen {

    public double posX, posY, width, height, dragX, dragY;
    public boolean dragging;
    public Category selectedCategory;

    private Module selectedModule;
    public int modeIndex;

    public ArrayList<Comp> comps = new ArrayList<>();

    public Clickgui() {
        dragging = false;
        posX = getScaledRes().getScaledWidth() / 2 - 150;
        posY = getScaledRes().getScaledHeight() / 2 - 100;
        width = posX + 150 * 2;
        height = height + 200;
        selectedCategory = Category.COMBAT;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (dragging) {
            posX = mouseX - dragX;
            posY = mouseY - dragY;
        }
        width = posX + 250 * 2;
        height = posY + 350;
        Gui.drawRect(posX, posY - 10, width, posY, new Color(100,10,100).getRGB());
        Gui.drawRect(posX, posY, width, height, new Color(45,45,45).getRGB());

        int offset = 0;
        for (Category category : Category.values()) {
            Gui.drawRect(posX,posY + 1 + offset,posX + 60,posY + 15 + offset,category.equals(selectedCategory) ? new Color(230,10,230).getRGB() : new Color(28,28,28).getRGB());
            fontRendererObj.drawString(category.name(),(int)posX + 2, (int)(posY + 5) + offset, new Color(170,170,170).getRGB());
            offset += 15;
        }
        offset = 0;
        for (Module m : Client.INSTANCE.getModuleManager()) {
            Gui.drawRect(posX + 65,posY + 1 + offset,posX + 125,posY + 15 + offset,m.isEnabled() ? new Color(230,10,230).getRGB() : new Color(28,28,28).getRGB());
            fontRendererObj.drawString(m.getDisplayName(),(int)posX + 67, (int)(posY + 5) + offset, new Color(170,170,170).getRGB());
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
        for (Module m : Client.INSTANCE.getModuleManager().getAll()) {
            if (isInside(mouseX, mouseY,posX + 65,posY + 1 + offset,posX + 125,posY + 15 + offset)) {
                if (mouseButton == 0) {
                    m.toggle();
                }
                if (mouseButton == 1) {
                    int sOffset = 3;
                    comps.clear();
                    /*
                    if (Client.INSTANCE.getSettingsManager().getSettingsByMod(m) != null)
                    for (Setting setting : Client.INSTANCE.getSettingsManager().getSettingsByMod(m)) {
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

                     */
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
