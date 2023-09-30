package dev.tenacity.ui.sidegui.utils;

import dev.tenacity.utils.tuples.mutable.MutablePair;
import dev.tenacity.ui.Screen;
import dev.tenacity.utils.animations.Animation;
import dev.tenacity.utils.animations.Direction;
import dev.tenacity.utils.animations.impl.DecelerateAnimation;
import dev.tenacity.utils.render.ColorUtil;
import dev.tenacity.utils.render.RenderUtil;
import dev.tenacity.utils.render.RoundedUtil;
import lombok.Getter;
import lombok.Setter;

public class TooltipObject implements Screen {

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

    public TooltipObject(String tooltip) {
        this.tooltip = tooltip;
    }

    public TooltipObject() {
    }


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

            MutablePair<Float, Float> whPair = lithiumFont14.drawNewLineText(tooltip, x + 2, y + 2, ColorUtil.applyOpacity(-1, fadeAnim), 3);

            float additionalHeight = 0;
            if (additionalInformation != null) {
                additionalHeight = lithiumFont14.drawWrappedText(additionalInformation, x + 2,
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

            width = lithiumFont14.getStringWidth(tooltip) + 4;
            height = lithiumFont14.getHeight() + 2;

            RenderUtil.scissorStart(x - 1.5f, y - 1.5f, (width + 4) * fadeAnim, height + 4);

            if(round){
                RoundedUtil.drawRound(x - .75f, y - .75f, width + 1.5f, height + 1.5f, 3, ColorUtil.tripleColor(45, fadeAnim));
                RoundedUtil.drawRound(x, y, width, height, 2.5f, ColorUtil.applyOpacity(ColorUtil.tripleColor(15), fadeAnim));
            }else {
                RenderUtil.drawBorderedRect(x, y, width, height, 1, ColorUtil.tripleColor(15, fadeAnim).getRGB(),
                        ColorUtil.tripleColor(45, fadeAnim).getRGB());
            }

            lithiumFont14.drawCenteredString(tooltip, x + width / 2f, y + lithiumFont14.getMiddleOfBox(height), ColorUtil.applyOpacity(-1, fadeAnim));

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
