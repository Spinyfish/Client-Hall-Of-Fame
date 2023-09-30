package dev.tenacity.utils.objects;

import dev.tenacity.ui.Screen;
import dev.tenacity.utils.animations.Animation;
import dev.tenacity.utils.animations.Direction;
import dev.tenacity.utils.animations.impl.DecelerateAnimation;
import dev.tenacity.utils.font.CustomFont;
import dev.tenacity.utils.misc.HoveringUtil;
import dev.tenacity.utils.render.ColorUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.awt.*;

@Getter
@Setter
@RequiredArgsConstructor
public class DoubleIconButton implements Screen {
    private float x, y, alpha;
    private Color accentColor;
    private CustomFont iconFont = iconFont20;
    private boolean enabled = false, hovering;

    private final Animation hoverAnimation = new DecelerateAnimation(200, 1);
    private final String disabledIcon, enabledIcon;


    @Override
    public void initGui() {

    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        accentColor = ColorUtil.applyOpacity(accentColor, alpha);
        hovering = HoveringUtil.isHovering(x - 2, y - 2, getWidth() + 4, getHeight() + 4, mouseX, mouseY);
        hoverAnimation.setDirection(hovering ? Direction.FORWARDS : Direction.BACKWARDS);
        if (enabled) {
            if (!hoverAnimation.isDone() || hovering) {
                iconFont.drawString(disabledIcon, x, y, ColorUtil.applyOpacity(accentColor, alpha));
            }
            iconFont.drawString(enabledIcon, x, y, ColorUtil.applyOpacity(accentColor, (1 - (.45f * hoverAnimation.getOutput().floatValue())) * alpha));
        } else {
            iconFont.drawString(disabledIcon, x, y, ColorUtil.applyOpacity(ColorUtil.interpolateColorC(Color.WHITE, accentColor, hoverAnimation.getOutput().floatValue()), alpha));
        }

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (hovering) {
            enabled = !enabled;
        }

    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {

    }


    public float getWidth() {
        return iconFont.getStringWidth(enabled ? enabledIcon : disabledIcon);
    }

    public float getHeight() {
        return iconFont.getHeight();
    }

}
