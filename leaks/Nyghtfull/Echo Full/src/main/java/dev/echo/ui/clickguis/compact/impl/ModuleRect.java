package dev.echo.ui.clickguis.compact.impl;

import dev.echo.utils.tuples.Pair;
import dev.echo.module.Module;
import dev.echo.module.impl.render.HUDMod;
import dev.echo.module.settings.impl.KeybindSetting;
import dev.echo.ui.Screen;
import dev.echo.ui.sidegui.utils.TooltipObject;
import dev.echo.utils.animations.Animation;
import dev.echo.utils.animations.Direction;
import dev.echo.utils.animations.impl.DecelerateAnimation;
import dev.echo.utils.font.FontUtil;
import dev.echo.utils.misc.HoveringUtil;
import dev.echo.utils.render.ColorUtil;
import dev.echo.utils.render.RenderUtil;
import dev.echo.utils.time.TimerUtil;
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
        Gui.drawRect2(x, y, rectWidth, 20, new Color(0, 15, 45, 60).getRGB());

        Pair<Color, Color> colors = HUDMod.getClientColors();
        Color actualColor = colors.getFirst();

        if (binding != null && !typing) {
            typing = true;
        }

        echoBoldFont20.drawString(module.getName(), x + 5, y + echoBoldFont20.getMiddleOfBox(20), -1);

        KeybindSetting keybindSetting = module.getKeybind();

        float bindWidth = (float) (echoFont14.getStringWidth(Keyboard.getKeyName(keybindSetting.getCode())) + 4);

        boolean hovered = HoveringUtil.isHovering((float) (x + echoFont20.getStringWidth(module.getName()) + 13), y + 6, bindWidth, 8, mouseX, mouseY);
        boolean hoveringModule = HoveringUtil.isHovering(x, y, width, 20, mouseX, mouseY);

        if (!hoveringModule) {
            timerUtil.reset();
        }

        tooltipObject.setTip(module.getDescription());
        tooltipObject.setRound(false);
        tooltipObject.setHovering(timerUtil.hasTimeElapsed(900));


        Color bindRect = new Color(255, 255, 255,  50);
        Gui.drawRect2(x + echoFont20.getStringWidth(module.getName()) + 13, y + 6,
                echoFont14.getStringWidth(Keyboard.getKeyName(keybindSetting.getCode())) + 4, 8,
                hovered ? bindRect.brighter().getRGB() : bindRect.getRGB());

        echoFont14.drawCenteredString(Keyboard.getKeyName(keybindSetting.getCode()), (float) (x + echoFont20.getStringWidth(module.getName()) + 13 + bindWidth / 2f),
                y + 8, -1);

        Gui.drawRect2(x, y + 20, rectWidth, rectHeight, new Color(0, 15, 45, 60).getRGB());


        enableAnimation.setDirection(module.isEnabled() ? Direction.FORWARDS : Direction.BACKWARDS);


        float o = (float) enableAnimation.getOutput().floatValue();

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
        float bindWidth = echoFont14.getStringWidth(Keyboard.getKeyName(module.getKeybind().getCode())) + 4;
        boolean hovered = HoveringUtil.isHovering(x + echoFont20.getStringWidth(module.getName()) + 13, y + 6, bindWidth, 8, mouseX, mouseY);
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
