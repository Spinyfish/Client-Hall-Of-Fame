package me.jinthium.straight.api.clickgui.dropdown.components;


import me.jinthium.straight.api.clickgui.dropdown.DropdownClickGUI;
import me.jinthium.straight.api.clickgui.dropdown.Screen;
import me.jinthium.straight.api.clickgui.dropdown.components.settings.*;
import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.api.setting.Setting;
import me.jinthium.straight.impl.Client;
import me.jinthium.straight.impl.modules.visual.Hud;
import me.jinthium.straight.impl.settings.*;
import me.jinthium.straight.impl.settings.mode.ModuleMode;
import me.jinthium.straight.impl.utils.animation.Animation;
import me.jinthium.straight.impl.utils.animation.Direction;
import me.jinthium.straight.impl.utils.animation.impl.DecelerateAnimation;
import me.jinthium.straight.impl.utils.animation.impl.EaseInOutQuad;
import me.jinthium.straight.impl.utils.animation.impl.EaseOutSine;
import me.jinthium.straight.impl.utils.font.FontUtil;
import me.jinthium.straight.impl.utils.misc.HoveringUtil;
import me.jinthium.straight.impl.utils.misc.TimerUtil;
import me.jinthium.straight.impl.utils.render.ColorUtil;
import me.jinthium.straight.impl.utils.render.RenderUtil;
import me.jinthium.straight.impl.utils.tuples.Pair;
import net.minecraft.client.gui.Gui;
import org.lwjgl.opengl.GL11;
import org.lwjglx.Sys;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ModuleRect implements Screen {

    public final Module module;

    private int searchScore = 1000;
    private final Animation toggleAnimation = new EaseInOutQuad(300, 1);
    private final Animation hoverAnimation = new EaseOutSine(400, 1, Direction.BACKWARDS);
    private final Animation hoverKeybindAnimation = new DecelerateAnimation(200, 1, Direction.BACKWARDS);
    private final Animation settingAnimation = new DecelerateAnimation(250, 1).setDirection(Direction.BACKWARDS);
    private final TimerUtil timerUtil = new TimerUtil();

    private boolean typing;
    public float x, y, width, height, panelLimitY, alpha;

    private double settingSize = 1;
    private final List<SettingComponent> settingComponents;

    public ModuleRect(Module module) {
        this.module = module;
        settingComponents = new ArrayList<>();

        for (Setting setting : module.getSettingsList()) {
            if(setting instanceof NewModeSetting now){
                for (ModuleMode<?> mode : now.getValues()) {
                    for (Setting setting1 : mode.getSettings().keySet()) {
                        if(!module.getSettingsList().contains(setting1)) {
                            setting1.addParent(now, r -> (mode == now.getCurrentMode()));
                            module.addSettings(setting1);
                        }
                    }
                }
            }
        }

        for (Setting setting : module.getSettingsList()) {
            if (setting instanceof KeybindSetting) {
                settingComponents.add(new KeybindComponent((KeybindSetting) setting));
            }
            if(setting instanceof NewModeSetting nms){
                settingComponents.add(new NewModeComponent(nms));
            }
            if (setting instanceof BooleanSetting) {
                settingComponents.add(new BooleanComponent((BooleanSetting) setting));
            }
            if (setting instanceof ModeSetting) {
                settingComponents.add(new ModeComponent((ModeSetting) setting));
            }
            if (setting instanceof NumberSetting) {
                settingComponents.add(new NumberComponent((NumberSetting) setting));
            }
            if (setting instanceof MultiBoolSetting) {
                settingComponents.add(new MultipleBoolComponent((MultiBoolSetting) setting));
            }
            if (setting instanceof StringSetting) {
                settingComponents.add(new StringComponent((StringSetting) setting));
            }
            if (setting instanceof ColorSetting) {
                settingComponents.add(new ColorComponent((ColorSetting) setting));
            }
        }
    }

    @Override
    public void initGui() {
        settingAnimation.setDirection(Direction.BACKWARDS);
        toggleAnimation.setDirection(Direction.BACKWARDS);

        if (settingComponents != null) {
            settingComponents.forEach(SettingComponent::initGui);
        }

    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (module.isExpanded()) {
            for (SettingComponent settingComponent : settingComponents) {
                if (settingComponent.getSetting().cannotBeShown()) continue;
                settingComponent.keyTyped(typedChar, keyCode);
            }
        }
    }

    public double getSettingSize() {
        return settingSize;
    }

    public int getSearchScore() {
        return searchScore;
    }

    public boolean isTyping() {
        return typing;
    }

    private double actualSettingCount;

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        toggleAnimation.setDirection(module.isEnabled() ? Direction.FORWARDS : Direction.BACKWARDS);
        settingAnimation.setDirection(module.isExpanded() ? Direction.FORWARDS : Direction.BACKWARDS);


        boolean hoveringModule = HoveringUtil.isHovering(x, y, width, height, mouseX, mouseY);



        hoverAnimation.setDirection(hoveringModule ? Direction.FORWARDS : Direction.BACKWARDS);
        hoverAnimation.setDuration(hoveringModule ? 250 : 400);


        Hud hud = Client.INSTANCE.getModuleManager().getModule(Hud.class);
        Pair<Color, Color> colors = Pair.of(hud.getHudColor((float) System.currentTimeMillis() / 600), hud.getHudColor((float) System.currentTimeMillis() / 600).darker()).apply((color1, color2) -> {
            return Pair.of(ColorUtil.applyOpacity(color1, alpha), ColorUtil.applyOpacity(color2, alpha));
        });


        Color rectColor = new Color(35, 35, 35, (int) (255 * alpha));
        Color textColor = ColorUtil.applyOpacity(Color.WHITE, alpha);

        float textAlpha = .5f;
        Color moduleTextColor = ColorUtil.applyOpacity(textColor, textAlpha + (.4f * toggleAnimation.getOutput().floatValue()));


        if (module.isEnabled() || !toggleAnimation.isDone()) {
            Color toggleColor = colors.getSecond();

            if (DropdownClickGUI.gradient) {
                toggleColor = ColorUtil.interpolateColorC(ColorUtil.applyOpacity(Color.BLACK, .15f), ColorUtil.applyOpacity(Color.WHITE, .12f), hoverAnimation.getOutput().floatValue());
            }


            rectColor = ColorUtil.interpolateColorC(rectColor, toggleColor, toggleAnimation.getOutput().floatValue());
        }


        RenderUtil.resetColor();
        Gui.drawRect2(x, y, width, height, ColorUtil.interpolateColor(rectColor, ColorUtil.brighter(rectColor, .8f), hoverAnimation.getOutput().floatValue()));

        RenderUtil.resetColor();

        normalFont18.drawString((module.isEnabled() ? "§l" : "") + module.getName(), x + 5, y + normalFont18.getMiddleOfBox(height) + 2, moduleTextColor);


        Color settingRectColor = ColorUtil.tripleColor(30, alpha);

        float arrowX = x + width - 12;
        if (settingComponents.size() > 0) {
            float arrowY = y + iconFont20.getMiddleOfBox(height) + 1;
            RenderUtil.rotateStart(arrowX, arrowY, iconFont20.getStringWidth(FontUtil.DROPDOWN_ARROW), iconFont20.getHeight(), 180 * settingAnimation.getOutput().floatValue());
            iconFont20.drawString(FontUtil.DROPDOWN_ARROW, arrowX, arrowY, ColorUtil.applyOpacity(textColor, .5f));
            RenderUtil.rotateEnd();
        }


        double settingHeight = (actualSettingCount) * settingAnimation.getOutput();
        actualSettingCount = 0;
        if (module.isExpanded() || !settingAnimation.isDone()) {
            float settingRectHeight = 16;
            Gui.drawRect2(x, y + height, width, (float) (settingHeight * settingRectHeight), settingRectColor.getRGB());

            if (!settingAnimation.isDone()) {
                RenderUtil.scissorStart(x, y + height, width, settingHeight * settingRectHeight);
            }


            typing = false;
            for (SettingComponent settingComponent : settingComponents) {
                if (settingComponent.getSetting().cannotBeShown()) continue;

                settingComponent.panelLimitY = panelLimitY;
                settingComponent.settingRectColor = settingRectColor;
                settingComponent.textColor = textColor;
                settingComponent.clientColors = colors;
                settingComponent.alpha = alpha;
                settingComponent.x = x;
                settingComponent.y = (float) (y + height + ((actualSettingCount * settingRectHeight)));
                settingComponent.width = width;
                settingComponent.typing = typing;

                if (settingComponent instanceof ModeComponent) {
                    ModeComponent modeComponent = (ModeComponent) settingComponent;
                    modeComponent.realHeight = settingRectHeight * modeComponent.normalCount;
                }
                if (settingComponent instanceof NewModeComponent) {
                    NewModeComponent modeComponent = (NewModeComponent) settingComponent;
                    modeComponent.realHeight = settingRectHeight * modeComponent.normalCount;
                }
                if (settingComponent instanceof MultipleBoolComponent) {
                    MultipleBoolComponent multipleBoolComponent = (MultipleBoolComponent) settingComponent;
                    multipleBoolComponent.realHeight = settingRectHeight * multipleBoolComponent.normalCount;
                }

                if (settingComponent instanceof ColorComponent) {
                    ColorComponent colorComponent = (ColorComponent) settingComponent;
                    colorComponent.realHeight = settingRectHeight;
                }

                settingComponent.height = settingRectHeight * settingComponent.countSize;

                settingComponent.drawScreen(mouseX, mouseY);

                if (settingComponent.typing) typing = true;

                actualSettingCount += settingComponent.countSize;
            }

            if (!settingAnimation.isDone() || GL11.glIsEnabled(GL11.GL_SCISSOR_TEST)) {
                RenderUtil.scissorEnd();
            }

        }
        settingSize = settingHeight;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        boolean hoveringModule = isClickable(y, panelLimitY) && HoveringUtil.isHovering(x, y, width, height, mouseX, mouseY);
        if (module.isExpanded() && settingAnimation.finished(Direction.FORWARDS)) {
            for (SettingComponent settingComponent : settingComponents) {
                if (settingComponent.getSetting().cannotBeShown()) continue;
                settingComponent.mouseClicked(mouseX, mouseY, button);
            }
        }

        if (hoveringModule) {
            switch (button) {
                case 0 -> {
                    toggleAnimation.setDirection(!module.isEnabled() ? Direction.FORWARDS : Direction.BACKWARDS);
                    module.toggle();
                }
                case 1 -> module.setExpanded(!module.isExpanded());
            }
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (module.isExpanded()) {
            for (SettingComponent settingComponent : settingComponents) {
                if (settingComponent.getSetting().cannotBeShown()) continue;
                settingComponent.mouseReleased(mouseX, mouseY, state);
            }
        }
    }

    public boolean isClickable(float y, float panelLimitY) {
        return y > panelLimitY && y < panelLimitY + Module.allowedClickGuiHeight;
    }


}
