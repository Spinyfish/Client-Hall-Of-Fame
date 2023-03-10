package store.femboy.spring.impl.module.impl.visual.clickgui.impl;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import ru.hogoshi.Animation;
import ru.hogoshi.util.Easings;
import store.femboy.spring.Spring;
import store.femboy.spring.impl.module.Category;
import store.femboy.spring.impl.module.Module;
import store.femboy.spring.impl.module.impl.visual.clickgui.ClickGui;
import store.femboy.spring.impl.module.settings.Settings;
import store.femboy.spring.impl.module.settings.impl.BooleanSetting;
import store.femboy.spring.impl.module.settings.impl.ModeSetting;
import store.femboy.spring.impl.module.settings.impl.NumberSettings;
import store.femboy.spring.impl.util.FontUtil;
import store.femboy.spring.impl.util.RenderUtil;
import store.femboy.spring.impl.util.RoundedUtil;

import java.awt.*;
import java.io.IOException;

public final class ClickGuiScreen extends GuiScreen {

    private final Animation animation = new Animation();

    Module selectedModule = null;
    boolean keySettingClicked = false;
    Category selectedCat = Category.COMBAT;
    boolean dragging = false;
    int w = 400;
    int h = 300;
    public double ok = 600;

    boolean numSettingFast;
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        float x = (float) (this.width - w) / 2;
        float y = (float) (this.height -h) / 2;

        double oke = animation.getValue() - ok;
        switch(ClickGui.clickGuiAnimationDirection.getMode()){
            case "Up":
                GlStateManager.translate(0, -oke, 0);
                break;
            case "Down":
                GlStateManager.translate(0, oke, 0);
                break;
            case "Left":
                GlStateManager.translate(-oke, 0, 0);
                break;
            case "Right":
                GlStateManager.translate(oke, 0, 0);
                break;
            case "Left + Down":
                GlStateManager.translate(-oke, oke, 0);
                break;
            case "Left + Up":
                GlStateManager.translate(-oke, -oke, 0);
                break;
            case "Right + Down":
                GlStateManager.translate(oke, oke, 0);
                break;
            case "Right + Up":
                GlStateManager.translate(oke, -oke, 0);
        }
        animation.update();

        animation.animate(ok, 1, Easings.EXPO_OUT,true);

        RoundedUtil.drawRoundedRect(x, y, x + w, y + h, 10,new Color(25,24,23).getRGB());
        RoundedUtil.drawRoundedRect(x, y, x + 120, y + h, 10,new Color(45,44,43).getRGB());
        RoundedUtil.drawRoundedRect(x, y, x + w, y + 20, 10,new Color(35,34,33).getRGB());
        RoundedUtil.drawRoundedOutline(x, y, x + w, y + h, 10,50,new Color(0,0,0).getRGB());
        int catCount = 0;
        int count = 0;

        for(Category cat : Category.values()) {
            catCount++;
            FontUtil.icons.drawString(cat.name, x - 50 + (catCount * 68), y + 6, new Color(200,200,200).getRGB());
        }
        for(Module m : Spring.INSTANCE.getManager().getModulesByCategory(selectedCat)) {
            count++;
            FontUtil.normal.drawString(m.name, x + 5, y + 8 + (count * 16), m.isToggled() ? -1 : new Color(200,200,200).getRGB());
            int index = 0;
            if (!m.settings.isEmpty()) {
                if(selectedModule != null) {
                    index = 0;
                    for (Settings setting : selectedModule.settings) {
                        if (setting instanceof BooleanSetting) {
                            BooleanSetting bool = (BooleanSetting) setting;
                            if(bool.parent != null) {
                                if(bool.mode != null) {
                                    if(bool.parent.getMode() == bool.mode) {
                                        FontUtil.normal.drawString(setting.name,  x + 125, y + 26 + index * 16, -1);
                                        RenderUtil.drawRoundedRect(x + 383, y + 26 + index * 16, 10, 10, 5, !bool.enabled ? new Color(250,0,0).getRGB() : new Color(0,250,0).getRGB());
                                    } else {
                                        index--;
                                    }
                                }
                            } else {
                                FontUtil.normal.drawString(setting.name,  x + 125, y + 26 + index * 16, -1);
                                RenderUtil.drawRoundedRect(x + 383, y + 26 + index * 16, 10, 10, 5, !bool.enabled ? new Color(250,0,0).getRGB() : new Color(0,250,0).getRGB());
                            }
                        }
                        if (setting instanceof NumberSettings) {
                            NumberSettings number = (NumberSettings) setting;
                            if(number.parent != null) {
                                if(number.mode != null) {
                                    if(number.parent.getMode() == number.mode) {

                                        FontUtil.normal.drawString(setting.name + ": " + number.getValue(), x + 125, y + 26 + index * 16, -1);
                                        FontUtil.normal.drawString("+", (int) (x + 363), (int) (y + 26 + index * 16), -1);
                                        FontUtil.normal.drawString("/", (int) (x + 373), (int) (y + 26 + index * 16), -1);
                                        FontUtil.normal.drawString("-", (int) (x + 383), (int) (y + 26 + index * 16), -1);
                                    } else {
                                        index--;
                                    }
                                }
                            } else {
                                FontUtil.normal.drawString(setting.name + ": " + number.getValue(), x + 125, y + 26 + index * 16, -1);
                                FontUtil.normal.drawString("+", (int) (x + 363), (int) (y + 26 + index * 16), -1);
                                FontUtil.normal.drawString("/", (int) (x + 373), (int) (y + 26 + index * 16), -1);
                                FontUtil.normal.drawString("-", (int) (x + 383), (int) (y + 26 + index * 16), -1);
                            }
                        }
                        if (setting instanceof ModeSetting) {
                            ModeSetting mode = (ModeSetting) setting;
                            FontUtil.normal.drawString(setting.name + ": " + mode.getMode(), x + 125, y + 27 + index * 16, -1);
                        }
                        index++;
                    }
                }
            }
        }
    }



    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {

        float x = (float) (this.width - w) / 2;
        float y = (float) (this.height -h) / 2;
        int count = 0;
        int catCount = 0;
        for(Category cat : Category.values()) {
            catCount++;
            if(isHoveringCoords(x - 50 + (catCount * 68), y + 5,50, 10, mouseX, mouseY)) {
                selectedCat = cat;
            }

        }
        for(Module m : Spring.INSTANCE.getManager().getModulesByCategory(selectedCat)) {
            count++;
            int index = 0;
            if(isHoveringCoords(x + 5, y + 6 + (count * 16), (float) FontUtil.normal.getStringWidth(m.name), FontUtil.normal.getHeight(), mouseX, mouseY)) {
                if(mouseButton == 0) {
                    m.toggle();
                } if(mouseButton == 1) {
                    selectedModule = m;

                }
            }
        }
        int index = 0;
        if(selectedModule != null) {
            index = 0;
            for (Settings setting : selectedModule.settings) {
                if (setting instanceof BooleanSetting) {
                    BooleanSetting bool = (BooleanSetting) setting;
                    if(bool.parent != null) {
                        if(bool.mode != null) {
                            if(bool.parent.getMode() == bool.mode) {
                                if(isHoveringCoords(x + 385, y + 26 + index * 16, 10, 10, mouseX, mouseY)) {
                                    ((BooleanSetting) setting).toggle();
                                }
                            } else {
                                index--;
                            }
                        }
                    } else {
                        if(isHoveringCoords(x + 385, y + 26 + index * 16, 10, 10, mouseX, mouseY)) {
                            ((BooleanSetting) setting).toggle();
                        }
                    }
                }

                if (setting instanceof NumberSettings) {
                    NumberSettings number = (NumberSettings) setting;
                    if(number.parent != null) {
                        if(number.mode != null) {
                            if(number.parent.getMode() == number.mode) {

                                if(isHoveringCoords(x + 363, y + 26 + index * 16, 10, FontUtil.normal.getHeight(), mouseX, mouseY)) {
                                    ((NumberSettings)setting).step(true);
                                } else if(isHoveringCoords(x + 383, y + 28 + index * 16, 10, 10, mouseX, mouseY)) {
                                    ((NumberSettings)setting).step(false);
                                }
                            }
                            else {
                                index--;
                            }
                        }
                    } else {
                        if(isHoveringCoords(x + 363, y + 26 + index * 16, 10, FontUtil.normal.getHeight(), mouseX, mouseY)) {
                            ((NumberSettings)setting).step(true);
                        } else if(isHoveringCoords(x + 383, y + 28 + index * 16, 10, 10, mouseX, mouseY)) {
                            ((NumberSettings)setting).step(false);
                        }
                    }
                }
                if (setting instanceof ModeSetting) {
                    ModeSetting mode = (ModeSetting) setting;
                    if(isHoveringCoords(x + 125, y + 27 + index * 16, (float) (10 + FontUtil.normal.getStringWidth(setting.name + ": " + mode.getMode())), 10, mouseX, mouseY)) {
                        ((ModeSetting)setting).cycle();
                    }
                }
                index++;
            }
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public boolean isHoveringCoords(float x, float y, float width, float height, int mouseX, int mouseY){
        return mouseX >= x && mouseY >= y && mouseX <= x + width && mouseY <= y + height-0.5f;
    }

    @Override
    public void onGuiClosed()
    {
        ok = 900;
        Spring.INSTANCE.getManager().getModule("ClickGUI").toggled = false;
    }
}
