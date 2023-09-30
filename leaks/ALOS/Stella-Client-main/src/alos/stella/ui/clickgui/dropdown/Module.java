package alos.stella.ui.clickgui.dropdown;

import alos.stella.Stella;
import alos.stella.module.modules.visual.ClickGUI;
import alos.stella.module.modules.visual.HUD;
import alos.stella.ui.client.font.Fonts;
import alos.stella.utils.ClientUtils;
import alos.stella.utils.render.DrawUtils;
import alos.stella.utils.render.GLUtils;
import alos.stella.utils.timer.MSTimer;
import alos.stella.value.TextValue;
import alos.stella.value.Value;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
public class Module {
    private final alos.stella.module.Module module;
    public int yPerModule, y;
    public Tab tab;
    public boolean opened;
    private boolean listening;
    public List<Setting> settings = new CopyOnWriteArrayList<>();
    public MSTimer hoverTimer = new MSTimer();
    public Module(alos.stella.module.Module module, Tab tab) {
        this.listening = false;
        this.module = module;
        this.tab = tab;
        for (Value setting :module.getValues() ) {
            if (!((boolean)setting.getCanDisplay().invoke()))
                continue;
            settings.add(new Setting(setting, this));
        }
    }
    private double length = 3, anim = 5;
    private int alph = 0;
    float fraction = 0;
    float fractionBackground = 0;
    public void drawScreen(int mouseX, int mouseY) {
        int key = module.getKeyBind();

        Minecraft instance = Minecraft.getMinecraft();
        int debugFPS = instance.getDebugFPS();
        if (module.getState() && fraction < 1) {
            fraction += 0.0025 * (2000 / debugFPS);
        }
        if (!module.getState() && fraction > 0) {
            fraction -= 0.0025 * (2000 / debugFPS);
        }

        if (!module.getState()) {
            if (isHovered(mouseX, mouseY) && fractionBackground < 1) {
                fractionBackground += 0.0025 * (2000 / debugFPS);
            }
            if (!isHovered(mouseX, mouseY) && fractionBackground > 0) {
                fractionBackground -= 0.0025 * (2000 / debugFPS);
            }

        }

        fraction = MathHelper.clamp_float(fraction, 0.0F, 1.0F);
        fractionBackground = MathHelper.clamp_float(fractionBackground, 0.0F, 1F);

        if (yPerModule < getY()) {
            yPerModule = (int) (yPerModule + (getY() + 9 - yPerModule) * 0.1);
        } else if (yPerModule > getY()) {
            yPerModule = (int) (yPerModule + (getY() - yPerModule) * 0.1);
        }

        y = (int) (tab.getPosY() + 15);
        for (Module tabModule : tab.getModules()) {
            if (tabModule == this) {
                break;
            } else {
                y += tabModule.yPerModule;
            }
        }

        HUD hud = (HUD) Stella.moduleManager.getModule(HUD.class);
        Color colorHUD = ClickGUI.generateColor();
        Color white = new Color(0xFFFFFF);

        if (colorHUD.getRed() > 220 && colorHUD.getBlue() > 220 && colorHUD.getGreen() > 220) {
            white = colorHUD.darker().darker();
        }

        DrawUtils.drawRect(tab.getPosX(), y, tab.getPosX() + 100, y + yPerModule, new Color(40, 40, 40, 255).getRGB());
        if (!module.getState() && fraction == 0) {
            DrawUtils.drawRect(tab.getPosX(), y, tab.getPosX() + 100, y + 14, interpolateColor(new Color(40, 40, 40, 255), new Color(29, 29, 29, 255), MathHelper.clamp_float(fractionBackground, 0, 1)));
        } else {
            DrawUtils.drawRect(tab.getPosX(), y, tab.getPosX() + 100, y + 14, interpolateColor(new Color(40, 40, 40, 255), colorHUD, MathHelper.clamp_float(fraction, 0, 1)));
        }
        Fonts.fontSFUI35.drawString(module.getName(), tab.getPosX() + 2, (float) (y + 4), 0xffffffff, true);

        if (listening){
            Fonts.fontSFUI40.drawString("Press New Bind For:" + module.getName(), 10,30 * 10,-1, true);
        }

        if (!settings.isEmpty()) {
            double val = debugFPS / 8.3;
            if (opened && length > -3) {
                length -= 3 / val;
            } else if (!opened && length < 3) {
                length += 3 / val;
            }
            if (opened && anim < 8) {
                anim += 3 / val;
            } else if (!opened && anim > 5) {
                anim -= 3 / val;
            }
            GLUtils.glArrow(tab.getPosX() + 92, y + anim, 2, 0xffffffff, length);
        }
        if (opened || yPerModule != 14) {
            ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
            if (yPerModule != getY() && scaledResolution.getScaleFactor() != 1) {
                GL11.glScissor(0,
                        scaledResolution.getScaledHeight() * 2 - y * 2 - yPerModule * 2,scaledResolution.getScaledWidth() * 2,
                        yPerModule * 2);
                GL11.glEnable(GL11.GL_SCISSOR_TEST);
                settings.stream().filter(s -> s.setting.getDisplayable()).forEach(setting -> setting.drawScreen(mouseX, mouseY));
                GL11.glDisable(GL11.GL_SCISSOR_TEST);
                settings.stream().filter(s ->!s.setting.getDisplayable()).forEach(setting -> setting.setPercent(0));
            } else {
                settings.stream().filter(s -> s.setting.getDisplayable() ).forEach(setting -> setting.drawScreen(mouseX, mouseY));
            }
        } else {
            settings.forEach(setting -> setting.setPercent(0));
        }
    }

    private int interpolateColor(Color color1, Color color2, float fraction) {
        int red = (int) (color1.getRed() + (color2.getRed() - color1.getRed()) * fraction);
        int green = (int) (color1.getGreen() + (color2.getGreen() - color1.getGreen()) * fraction);
        int blue = (int) (color1.getBlue() + (color2.getBlue() - color1.getBlue()) * fraction);
        int alpha = (int) (color1.getAlpha() + (color2.getAlpha() - color1.getAlpha()) * fraction);
        try {
            return new Color(red, green, blue, alpha).getRGB();
        } catch (Exception ex) {
            return 0xffffffff;
        }
    }

    public void keyTyped(char typedChar, int keyCode) {
        if (opened) {
            settings.stream().filter(s -> s.setting.getDisplayable()).forEach(setting -> setting.keyTyped(typedChar, keyCode));
        }
        if (listening) {
            if (keyCode == Keyboard.KEY_ESCAPE) {
                module.setKeyBind(0);

            } else {
                module.setKeyBind(keyCode);
                ClientUtils.displayChatMessage(EnumChatFormatting.DARK_GRAY + "[" + EnumChatFormatting.DARK_PURPLE + EnumChatFormatting.BOLD + "ClickGUI" + EnumChatFormatting.DARK_GRAY + "] " + EnumChatFormatting.DARK_AQUA + "Bound module " + EnumChatFormatting.GREEN + EnumChatFormatting.BOLD + module.getName() + EnumChatFormatting.DARK_AQUA + " to key " + EnumChatFormatting.GREEN + EnumChatFormatting.BOLD + Keyboard.getKeyName(keyCode));
                listening = false;
            }
        }
    }

    public int getY() {
        if (opened) {
            int gay = 17;
            for (Setting setting : settings.stream().filter(s -> s.setting.getDisplayable() ).collect(Collectors.toList())) {
                gay += 15;
            }
            return tab.modules.indexOf(this) == tab.modules.size() - 1 ? gay : gay;
        } else {
            return 14;
        }
    }

    private float alpha = 0;

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (isHovered(mouseX, mouseY)) {
            switch (mouseButton) {
                case 0:
                    module.toggle();
                    break;
                case 1:
                    if (!module.getValues().isEmpty()) {
                        final ClickGUI clickGUI = (ClickGUI) Stella.moduleManager.getModule(ClickGUI.class);
                        if (!opened && clickGUI.getClosePrevious.get())
                            tab.modules.forEach(module -> {
                                if (module.opened)
                                    module.opened = false;
                            });
                        opened = !opened;
                        for (Value setting : module.getValues()) {
                            if (setting instanceof TextValue) {
                                setting.setTextHovered(false);
                            }

                        }
                    }
                    break;
                case 2:
                    listening = true;
                    break;
            }

        }
        if (opened) {
            settings.stream().filter(s -> s.setting.getDisplayable()).forEach(setting -> {
                try {
                    setting.mouseClicked(mouseX, mouseY, mouseButton);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (opened) {
            settings.stream().filter(s -> s.setting.getDisplayable()).forEach(setting -> setting.mouseReleased(mouseX, mouseY, state));
        }
    }
    public boolean isHovered(int mouseX, int mouseY) {
        y = (int) (tab.getPosY() + 15);
        for (Module tabModule : tab.getModules()) {
            if (tabModule == this) {
                break;
            } else {
                y += tabModule.yPerModule;
            }
        }
        if (opened)
            return mouseX >= tab.getPosX() && mouseY >= y && mouseX <= tab.getPosX() + 101 && mouseY <= y + 14;
        return mouseX >= tab.getPosX() && mouseY >= y && mouseX <= tab.getPosX() + 101 && mouseY <= y + yPerModule;
    }
    private void update() {
    }
    public alos.stella.module.Module getModule() {
        return module;
    }
}
