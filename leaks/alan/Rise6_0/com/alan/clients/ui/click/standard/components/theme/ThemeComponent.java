package com.alan.clients.ui.click.standard.components.theme;

import com.alan.clients.ui.theme.Themes;
import com.alan.clients.util.animation.Animation;
import com.alan.clients.util.animation.Easing;
import com.alan.clients.util.font.FontManager;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.render.ColorUtil;
import com.alan.clients.util.render.RenderUtil;
import com.alan.clients.util.vector.Vector3d;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.awt.*;

/**
 * @author Hazsi
 * @since 10/15/2022
 */
@Getter
@RequiredArgsConstructor
public class ThemeComponent implements InstanceAccess {
    private final Themes activeTheme;
    private Vector3d lastDraw = new Vector3d(0, 0, 0);

    private final Animation xAnimation = new Animation(Easing.EASE_OUT_QUINT, 500);
    private final Animation yAnimation = new Animation(Easing.EASE_OUT_QUINT, 500);
    private final Animation opacityAnimation = new Animation(Easing.EASE_OUT_QUINT, 500);
    private final Animation selectorAnimation = new Animation(Easing.EASE_OUT_QUINT, 500);

    public void draw(double yOffset, double width) {
        final int alpha = (int) opacityAnimation.getValue();

        final boolean active = this.activeTheme.equals(this.getTheme());
        final Color color = active ? new Color(15, 19, 26, (int) opacityAnimation.getValue()) :
                new Color(18, 21, 30, alpha);

        final double x = this.xAnimation.getValue();
        final double y = this.yAnimation.getValue() + yOffset;

        // This needs to be done in a runnable so that's its run AFTER the NORMAL_POST_BLOOM_RUNNABLES runnable
        NORMAL_ABOVE_BLOOM_RUNNABLES.add(() -> {

            // Draw background
            RenderUtil.roundedRectangle(x, y, width, 50, 10, color);

            if (this.activeTheme.isTriColor()) {
                RenderUtil.rectangle(x + (width / 2D) - 10, y + 0.5, 20, 39.5,
                        ColorUtil.withAlpha(activeTheme.getSecondColor(), alpha));
                RenderUtil.rectangle(x + (width / 2D) - 8, y, 16, 0.5,
                        ColorUtil.withAlpha(activeTheme.getSecondColor(), (int) (alpha * 0.8)));
                RenderUtil.drawRoundedGradientRect(x, y, width / 2D, 40, 9,
                        ColorUtil.withAlpha(activeTheme.getFirstColor(), alpha),
                        ColorUtil.withAlpha(activeTheme.getSecondColor(), alpha), false);
                RenderUtil.drawRoundedGradientRect(x + (width / 2D) - 1, y, width / 2D + 1, 40, 9,
                        ColorUtil.withAlpha(activeTheme.getSecondColor(), alpha),
                        ColorUtil.withAlpha(activeTheme.getThirdColor(), alpha), false);
            } else {
                RenderUtil.drawRoundedGradientRect(x, y, width, 40, 9,
                        ColorUtil.withAlpha(activeTheme.getFirstColor(), alpha),
                        ColorUtil.withAlpha(activeTheme.getSecondColor(), alpha), false);
            }

            RenderUtil.rectangle(x, y + 30, width, 10, color);

            FontManager.getProductSansRegular(16).drawCenteredString(activeTheme.getThemeName(),
                    x + width / 2D, y + 37, active ? ColorUtil.withAlpha(this.getTheme().getFirstColor(), alpha).getRGB() :
                            new Color(255, 255, 255, alpha).getRGB());
        });

        // Render selector
        selectorAnimation.run(this.activeTheme.equals(getTheme()) ? 255 : 0);
        int selectorAlpha = (int) Math.min(selectorAnimation.getValue(), alpha);

        if (selectorAlpha > 0) {
            NORMAL_POST_BLOOM_RUNNABLES.add(() -> {
                if (this.activeTheme.isTriColor()) {
                    RenderUtil.rectangle(x + (width / 2D) - 10, y, 20, 50,
                            ColorUtil.withAlpha(activeTheme.getSecondColor(), selectorAlpha));
                    RenderUtil.drawRoundedGradientRect(x, y, width / 2D, 50, 12,
                            ColorUtil.withAlpha(activeTheme.getFirstColor(), selectorAlpha),
                            ColorUtil.withAlpha(activeTheme.getSecondColor(), selectorAlpha), false);
                    RenderUtil.drawRoundedGradientRect(x + (width / 2D) - 1, y, width / 2D, 50, 12,
                            ColorUtil.withAlpha(activeTheme.getSecondColor(), selectorAlpha),
                            ColorUtil.withAlpha(activeTheme.getThirdColor(), selectorAlpha), false);
                } else {
                    RenderUtil.drawRoundedGradientRect(x, y, width, 50, 12,
                            ColorUtil.withAlpha(activeTheme.getFirstColor(), selectorAlpha),
                            ColorUtil.withAlpha(activeTheme.getSecondColor(), selectorAlpha), false);
                }

                FontManager.getProductSansRegular(16).drawCenteredString(activeTheme.getThemeName(),
                        x + width / 2D, y + 37, ColorUtil.withAlpha(activeTheme.getFirstColor(), selectorAlpha).getRGB());
            });
        }

        this.lastDraw = new Vector3d(x, y, width);
    }
}
