package frapppyz.cutefurry.pics.clickgui;

import frapppyz.cutefurry.pics.Wrapper;
import frapppyz.cutefurry.pics.modules.Category;
import frapppyz.cutefurry.pics.modules.Mod;
import frapppyz.cutefurry.pics.modules.settings.Setting;
import frapppyz.cutefurry.pics.modules.settings.impl.Boolean;
import frapppyz.cutefurry.pics.modules.settings.impl.Mode;
import frapppyz.cutefurry.pics.modules.settings.impl.Number;
import frapppyz.cutefurry.pics.util.MathUtil;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;

public class ClickGUIMain extends GuiScreen {

    public final int categoryOffset = 20;
    public final int moduleOffset = 18;
    public final int settingOffset = 14;
    public final int settingShadowOffset = 0;

    private boolean holdingMouseButton;
    private Category isDraggingCategory;

    private Mod keybindFocusedModule;

    public ClickGUIMain() {
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution sr = new ScaledResolution(mc);
        FontRenderer fr = mc.fontRendererObj;
        for(Category c : Category.values()) {

            int startX = c.getX();
            int startY = c.getY();

            int endX = startX + 100;
            int endY = startY + categoryOffset;

            for(int i = startX; i < endX; i++) {
                Gui.drawRect(i, startY, i + 1, endY, 0x84000000);
            }

            fr.drawStringWithShadow(c.name(), startX + 4, startY + 6, new Color(240, 240, 240).getRGB());

            int currentOffsetY = startY + categoryOffset;

            boolean firstModule = true;
            if(c.equals(isDraggingCategory)){
                c.setX(mouseX-40);
                c.setY(mouseY-10);
            }
            if(c.isOpen()) {
                for(Mod m : Wrapper.getModManager().mods) {
                    if(m.getCategory() == c) {

                        int moduleStartX = startX;
                        int moduleEndX = endX;

                        int moduleStartY = currentOffsetY;
                        int moduleEndY = currentOffsetY + moduleOffset;

                        drawModule(m, moduleStartX, moduleStartY, moduleEndX, moduleEndY);

                        currentOffsetY += moduleOffset;

                        if(m.getSettings() != null && m.isSettingsShowed()) {
                            int settingIndex = 0;

                            for(Setting s : m.getSettings()) {
                                if(!holdingMouseButton) {
                                    s.setHoldingMouseButton(false);
                                }

                                if(s.isShowed()) {
                                    int settingStartX = startX + 1;
                                    int settingEndX = endX - 1;

                                    if(settingIndex == 0) {
                                        Gui.drawRect(settingStartX, currentOffsetY, settingEndX, currentOffsetY + settingShadowOffset, new Color(50, 50, 50).getRGB());
                                        currentOffsetY += settingShadowOffset;

                                    }

                                    if(s instanceof Mode) {
                                        Mode mode = (Mode) s;

                                        Gui.drawRect(startX, currentOffsetY, endX, currentOffsetY + 14, 0x84000000);

                                        fr.drawStringWithShadow(s.getName() + " (" + mode.getMode() + ")", startX + 5, currentOffsetY + 3, new Color(240, 240, 240).getRGB());
                                    }else if(s instanceof Number) {
                                        Number n = (Number) s;

                                        Gui.drawRect(startX, currentOffsetY, endX, currentOffsetY + 14, 0x84000000);

                                        fr.drawStringWithShadow(s.getName() + " (" + MathUtil.round(n.getVal(), 0.1) + ")", startX + 5, currentOffsetY + 3, new Color(240, 240, 240).getRGB());

                                    }else if(s instanceof Boolean) {

                                        Boolean b = (Boolean) s;

                                        Gui.drawRect(startX, currentOffsetY, endX, currentOffsetY + 14, 0x84000000);

                                        fr.drawStringWithShadow(s.getName() + " (" + b.getValueName() + ")", startX + 5, currentOffsetY + 3, new Color(240, 240, 240).getRGB());

                                    }

                                    currentOffsetY += settingOffset;

                                    settingIndex++;
                                } else {
                                    s.setHoldingMouseButton(false);
                                }
                            }
                        }
                        firstModule = false;
                    }
                }
            }
        }

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        holdingMouseButton = true;

        for(Category c : Category.values()) {
            int startX = c.getX();
            int startY = c.getY();

            int endX = startX + 100;
            int endY = startY + categoryOffset;

            if(mouseX >= startX && mouseX <= endX && mouseY >= startY && mouseY <= endY) {
                onCategoryClicked(c, mouseX, mouseY, button);
            }

            int currentOffsetY = startY + categoryOffset;

            if(c.isOpen()) {
                for(Mod m : Wrapper.getModManager().mods) {

                    if(m.getCategory() == c) {
                        int moduleStartX = startX;
                        int moduleEndX = startX + 100;

                        int moduleStartY = currentOffsetY;
                        int moduleEndY = currentOffsetY + moduleOffset;

                        if(mouseX >= moduleStartX && mouseX <= moduleEndX && mouseY >= moduleStartY && mouseY <= moduleEndY) {
                            if (button == 0) {
                                if (mc.thePlayer != null) {
                                    m.toggle();
                                }
                            } else if (button == 1) {
                                m.setSettingsShowed(!m.isSettingsShowed());
                            }
                        }

                        currentOffsetY += moduleOffset;

                        if(m.getSettings() != null && m.isSettingsShowed()) {
                            int settingIndex = 0;

                            for(Setting s : m.getSettings()) {
                                if(s.isShowed()) {
                                    int settingStartX = startX + 1;
                                    int settingEndX = endX - 1;

                                    int settingStartY = currentOffsetY;
                                    int settingEndY = currentOffsetY + settingOffset;

                                    if(settingIndex == 0) {
                                        currentOffsetY += settingShadowOffset;

                                        settingStartY = currentOffsetY;
                                        settingEndY = currentOffsetY + settingOffset;
                                    }

                                    if(mouseX >= settingStartX && mouseX <= settingEndX && mouseY >= settingStartY && mouseY <= settingEndY) {
                                        if(s instanceof Mode) {
                                            Mode mode = (Mode) s;

                                            if(button == 0) {
                                                mode.increment();
                                            } else if(button == 1) {
                                                mode.decrement();
                                            }
                                        }else if(s instanceof Number) {
                                            Number n = (Number) s;
                                            if(button == 0) {
                                                n.increment();
                                            } else if(button == 1) {
                                                n.decrement();
                                            }
                                        }else if(s instanceof Boolean) {
                                            Boolean b = (Boolean) s;
                                            b.cycle();
                                        }
                                    }

                                    currentOffsetY += settingOffset;

                                    settingIndex++;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    private void onCategoryClicked(Category c, int mouseX, int mouseY, int button) {
        if(button == 1) {
            c.setOpen(!c.isOpen());
        }else{
            isDraggingCategory = c;
            c.setX(mouseX-40);
            c.setY(mouseY-10);
        }
    }

    private void drawModule(Mod m, int startX, int startY, int endX, int endY) {
        FontRenderer fr = mc.fontRendererObj;

        if(m.isToggled()) {
            for(int i = startX; i < endX; i++) {
                Gui.drawRect(i, startY, i + 1, endY, 0x99000000);
            }
        } else {
            Gui.drawRect(startX, startY, endX, endY, 0x84000000);
        }

        if(m.getSettings().size() > 0) {

            Gui.drawRect(endX - 11, startY + 7, endX - 5, startY + 7.5, new Color(225, 225, 225).getRGB());
        }
        if(m.isToggled()) {
            fr.drawStringWithShadow(m.getName(), startX + 4, startY + 5, new Color(174, 12, 134).getRGB());
        }else{
            fr.drawStringWithShadow(m.getName(), startX + 4, startY + 5, new Color(47, 47, 47).getRGB());
        }

    }

    @Override
    public void onGuiClosed() {
        onClickGuiDisabled();
    }

    private void onClickGuiDisabled() {
        Wrapper.getModManager().getModByName("ClickGUI").setToggledSilently(false);
        keybindFocusedModule = null;

        holdingMouseButton = false;
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        holdingMouseButton = false;
        isDraggingCategory=null;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

}