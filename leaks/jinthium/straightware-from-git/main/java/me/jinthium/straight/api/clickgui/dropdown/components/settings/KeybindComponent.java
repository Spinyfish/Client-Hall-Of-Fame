package me.jinthium.straight.api.clickgui.dropdown.components.settings;

import me.jinthium.straight.api.clickgui.dropdown.components.SettingComponent;
import me.jinthium.straight.impl.settings.KeybindSetting;
import me.jinthium.straight.impl.utils.animation.Animation;
import me.jinthium.straight.impl.utils.animation.Direction;
import me.jinthium.straight.impl.utils.animation.impl.DecelerateAnimation;
import me.jinthium.straight.impl.utils.misc.HoveringUtil;
import me.jinthium.straight.impl.utils.render.ColorUtil;
import me.jinthium.straight.impl.utils.render.RoundedUtil;
import org.lwjglx.input.Keyboard;

import java.awt.*;

public class KeybindComponent extends SettingComponent<KeybindSetting> {


    private boolean binding;

    private final Animation clickAnimation = new DecelerateAnimation(250, 1, Direction.BACKWARDS);
    private final Animation hoverAnimation = new DecelerateAnimation(250, 1, Direction.BACKWARDS);

    public KeybindComponent(KeybindSetting keybindSetting) {
        super(keybindSetting);
    }


    @Override
    public void initGui() {

    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (binding) {
            if (keyCode == Keyboard.KEY_SPACE || keyCode == Keyboard.KEY_ESCAPE || keyCode == Keyboard.KEY_DELETE) {
                getSetting().setCode(Keyboard.KEY_NONE);
            } else {
                getSetting().setCode(keyCode);
            }

            typing = false;
            binding = false;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {

        clickAnimation.setDirection(binding ? Direction.FORWARDS : Direction.BACKWARDS);

        String bind = Keyboard.getKeyName(getSetting().getCode());

        float fullTextWidth = normalFont16.getStringWidth("Bind: §l" + bind);

        float startX = x + width / 2f - fullTextWidth / 2f;
        float startY = y + normalFont16.getMiddleOfBox(height);

        boolean hovering = HoveringUtil.isHovering(startX - 3, startY - 2, fullTextWidth + 6, normalFont16.getHeight() + 4, mouseX, mouseY);
        hoverAnimation.setDirection(hovering ? Direction.FORWARDS : Direction.BACKWARDS);

        Color rectColor = ColorUtil.brighter(settingRectColor, .7f - (.25f * hoverAnimation.getOutput().floatValue()));
        RoundedUtil.drawRound(startX - 3, startY - 2, fullTextWidth + 6, normalFont16.getHeight() + 4, 4, rectColor);


        normalFont16.drawCenteredString("Bind: §l" + bind, x + width /2f, y + normalFont16.getMiddleOfBox(height) + 2, textColor);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        String bind = Keyboard.getKeyName(getSetting().getCode());
        String text = "§fBind: §r" + bind;
        float textWidth = normalFont18.getStringWidth(text);
        float startX = x + width / 2f - textWidth / 2f;
        float startY = y + normalFont18.getMiddleOfBox(height);
        float rectHeight = normalFont18.getHeight() + 4;

        boolean hovering = HoveringUtil.isHovering(startX - 3, startY - 2, textWidth + 6, normalFont18.getHeight() + 4, mouseX, mouseY);

        if (isClickable(startY + rectHeight) && hovering && button == 0) {
            binding = true;
            typing = true;
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {

    }
}
