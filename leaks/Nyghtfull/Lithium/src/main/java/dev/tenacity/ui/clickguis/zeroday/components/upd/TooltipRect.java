package dev.tenacity.ui.clickguis.zeroday.components.upd;

import dev.tenacity.ui.Screen;
import dev.tenacity.utils.animations.Animation;
import dev.tenacity.utils.animations.Direction;
import dev.tenacity.utils.animations.impl.DecelerateAnimation;
import dev.tenacity.utils.render.ColorUtil;
import dev.tenacity.utils.render.GradientUtil;
import dev.tenacity.utils.render.RenderUtil;
import dev.tenacity.utils.render.RoundedUtil;
import dev.tenacity.utils.tuples.mutable.MutablePair;
import lombok.Getter;
import lombok.Setter;

public class TooltipRect implements Screen {

    @Setter
    @Getter
    private boolean hovering = false;
    @Setter
    private boolean round = true;

    @Getter
    private final Animation fadeInAnimation = new DecelerateAnimation(250, 1).setDirection(Direction.BACKWARDS);

    private String tooltip;
    //This is so stupid but it works
    private String additionalInformation;

    public TooltipRect(String tooltip) {
        this.tooltip = tooltip;
    }

    public TooltipRect() { /* */ }

    @Override
    public void initGui() {

    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }

    private float width = 150;
    private float height = 40;

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        fadeInAnimation.setDirection(hovering ? Direction.FORWARDS : Direction.BACKWARDS);
        float x = mouseX - 2, y = mouseY + 13;
        float fadeAnim = fadeInAnimation.getOutput().floatValue();
        if (tooltip == null || fadeInAnimation.finished(Direction.BACKWARDS)) return;

        if (tooltip.contains("\n")) {
            RenderUtil.scissorStart(x - 1.5f, y - 1.5f, (width + 4) * fadeAnim, height + 4);
            RoundedUtil.drawRound(x - .75f, y - .75f, width + 1.5f, height + 1.5f, 3, ColorUtil.tripleColor(45, fadeAnim));
            RoundedUtil.drawRound(x, y, width, height, 2.5f, ColorUtil.applyOpacity(ColorUtil.tripleColor(15), fadeAnim));

            MutablePair<Float, Float> whPair = lithiumFont20.drawNewLineText(tooltip, x + 2, y + 2, ColorUtil.applyOpacity(-1, fadeAnim), 3);

            float additionalHeight = 0;
            if (additionalInformation != null) {
                additionalHeight = lithiumFont20.drawWrappedText(additionalInformation, x + 2,
                        y + 1.5f + whPair.getSecond(), ColorUtil.applyOpacity(-1, fadeAnim), width, 3);
            }


            RenderUtil.scissorEnd();


            if (additionalInformation != null) {
                width = Math.max(150, whPair.getFirst() + 4);
            } else {
                width = whPair.getFirst() + 4;
            }
            height = whPair.getSecond() + additionalHeight;

        } else {

            width = lithiumFont20.getStringWidth(tooltip) + 4;
            height = lithiumFont20.getHeight() + 2;

            RenderUtil.scissorStart(x - 1.5f, y - 1.5f, (width + 4) * fadeAnim, height + 4);

            GradientUtil.drawGradientLR(
                    x, y,
                    width, height,
                    0.8F,
                    ColorUtil.rainbow(10, (int) (x) / 10, 0.45F, 0.65F, 1.0F),
                    ColorUtil.rainbow(10, (int) (x + width) / 10, 0.45F, 0.65F, 1.0F)
            );

            lithiumFont20.drawCenteredString(tooltip, x + width / 2f, y + lithiumFont20.getMiddleOfBox(height), ColorUtil.applyOpacity(-1, 0.8F));

            RenderUtil.scissorEnd();
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {

    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {

    }

    public void setTip(String tooltip) {
        this.tooltip = tooltip;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }
}
