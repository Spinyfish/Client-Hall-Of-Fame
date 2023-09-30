package dev.tenacity.ui.clickguis.compact.impl;

import dev.tenacity.utils.tuples.Pair;
import dev.tenacity.Tenacity;
import dev.tenacity.module.Module;
import dev.tenacity.module.impl.render.HUDMod;
import dev.tenacity.module.settings.impl.KeybindSetting;
import dev.tenacity.ui.Screen;
import dev.tenacity.ui.sidegui.utils.TooltipObject;
import dev.tenacity.utils.animations.Animation;
import dev.tenacity.utils.animations.Direction;
import dev.tenacity.utils.animations.impl.DecelerateAnimation;
import dev.tenacity.utils.font.FontUtil;
import dev.tenacity.utils.misc.HoveringUtil;
import dev.tenacity.utils.render.ColorUtil;
import dev.tenacity.utils.render.RenderUtil;
import dev.tenacity.utils.time.TimerUtil;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class ModuleRect implements Screen {
    @Getter
    @Setter
    private int searchScore;
    public final Module module;
    private final Animation enableAnimation = new DecelerateAnimation(150, 1);
    private final SettingComponents settingComponents;
    public float x, y, width, height;
    public float rectHeight;
    public float rectWidth;
    public Module binding;
    public boolean typing;
    public final TooltipObject tooltipObject = new TooltipObject();
    private final TimerUtil timerUtil = new TimerUtil();

    public ModuleRect(Module module) {
        this.module = module;
        settingComponents = new SettingComponents(module);
    }

    @Override
    public void initGui() {
        settingComponents.initGui();
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (binding != null) {
            if (keyCode == Keyboard.KEY_SPACE || keyCode == Keyboard.KEY_ESCAPE || keyCode == Keyboard.KEY_DELETE)
                binding.getKeybind().setCode(Keyboard.KEY_NONE);
            else
                binding.getKeybind().setCode(keyCode);
            binding = null;
        } else {
            settingComponents.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        typing = false;
        Gui.drawRect2(x, y, rectWidth, 20, new Color(39, 39, 39).getRGB());

        HUDMod hudMod = Tenacity.INSTANCE.getModuleCollection().getModule(HUDMod.class);
        Pair<Color, Color> colors = HUDMod.getClientColors();
        Color actualColor = colors.getFirst();

        if (binding != null && !typing) {
            typing = true;
        }


        lithiumBoldFont20.drawString(module.getName(), x + 5, y + lithiumBoldFont20.getMiddleOfBox(20), -1);

        KeybindSetting keybindSetting = module.getKeybind();

        float bindWidth = (float) (lithiumFont14.getStringWidth(Keyboard.getKeyName(keybindSetting.getCode())) + 4);

        boolean hovered = HoveringUtil.isHovering((float) (x + lithiumFont20.getStringWidth(module.getName()) + 13), y + 6, bindWidth, 8, mouseX, mouseY);
        boolean hoveringModule = HoveringUtil.isHovering(x, y, width, 20, mouseX, mouseY);

        if (!hoveringModule) {
            timerUtil.reset();
        }

        tooltipObject.setTip(module.getDescription());
        tooltipObject.setRound(false);
        tooltipObject.setHovering(timerUtil.hasTimeElapsed(900));


        Color bindRect = new Color(64, 68, 75);
        Gui.drawRect2(x + lithiumFont20.getStringWidth(module.getName()) + 13, y + 6,
                lithiumFont14.getStringWidth(Keyboard.getKeyName(keybindSetting.getCode())) + 4, 8,
                hovered ? bindRect.brighter().getRGB() : bindRect.getRGB());

        lithiumFont14.drawCenteredString(Keyboard.getKeyName(keybindSetting.getCode()), (float) (x + lithiumFont20.getStringWidth(module.getName()) + 13 + bindWidth / 2f),
                y + 8, -1);

        Gui.drawRect2(x, y + 20, rectWidth, rectHeight, new Color(35, 35, 35).getRGB());


        enableAnimation.setDirection(module.isEnabled() ? Direction.FORWARDS : Direction.BACKWARDS);


        float o = (float) enableAnimation.getOutput().floatValue();

//        RenderUtil.fakeCircleGlow(x + rectWidth - 10, y + 10, (float) (2 + (5 * o)), accentColor, 115);
        RenderUtil.drawGoodCircle(x + rectWidth - 10, y + 10, 4, ColorUtil.interpolateColor(new Color(64, 68, 75), actualColor, (float) o));

        GlStateManager.pushMatrix();
        GlStateManager.translate(3.5F, 2, 0);
        GlStateManager.scale(o, o, o);
        GlStateManager.translate(-3.5F, -2, 0);
        FontUtil.iconFont16.drawString(FontUtil.CHECKMARK, (x + rectWidth - 13.5f) / o, (y + 8) / o, ColorUtil.interpolateColor(new Color(45, 45, 45), Color.WHITE, o));
        GlStateManager.scale(1, 1, 1);
        GlStateManager.popMatrix();

        settingComponents.x = x;
        settingComponents.y = y + 20;
        settingComponents.actualColor = actualColor;
        settingComponents.rectWidth = rectWidth;
        settingComponents.drawScreen(mouseX, mouseY);

        if (!typing) {
            typing = settingComponents.typing;
        }

        rectHeight = settingComponents.size > 0 ? settingComponents.size : 0;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        float bindWidth = lithiumFont14.getStringWidth(Keyboard.getKeyName(module.getKeybind().getCode())) + 4;
        boolean hovered = HoveringUtil.isHovering(x + lithiumFont20.getStringWidth(module.getName()) + 13, y + 6, bindWidth, 8, mouseX, mouseY);
        if (!hovered && HoveringUtil.isHovering(x, y, rectWidth, 20, mouseX, mouseY)) {
            if (button == 0) {
                module.toggleSilent();
            }
        } else if (hovered) {
            binding = module;
            return;
        }
        settingComponents.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        settingComponents.mouseReleased(mouseX, mouseY, state);
    }

}
