package dev.tenacity.ui.sidegui.utils;

import dev.tenacity.ui.Screen;
import dev.tenacity.ui.sidegui.SideGUI;
import dev.tenacity.utils.animations.Animation;
import dev.tenacity.utils.animations.ContinualAnimation;
import dev.tenacity.utils.animations.Direction;
import dev.tenacity.utils.animations.impl.DecelerateAnimation;
import dev.tenacity.utils.font.FontUtil;
import dev.tenacity.utils.misc.HoveringUtil;
import dev.tenacity.utils.render.ColorUtil;
import dev.tenacity.utils.render.RenderUtil;
import dev.tenacity.utils.render.RoundedUtil;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class DropdownObject implements Screen {

    @Getter
    @Setter
    private float x, y, width, height, alpha;
    @Setter
    private Color accentColor;

    private final String name;
    @Getter
    private String selection;
    private List<String> options;
    private boolean opened = false;
    @Setter
    private boolean bypass = false;

    public DropdownObject(String name, String... options) {
        this.name = name;
        selection = options[0];
        this.options = Arrays.asList(options);
    }

    public DropdownObject(String name) {
        this.name = name;
    }

    private final Animation openAnimation = new DecelerateAnimation(250, 1).setDirection(Direction.BACKWARDS);
    private final Animation hoverAnimation = new DecelerateAnimation(250, 1).setDirection(Direction.BACKWARDS);
    private final ContinualAnimation hoverRectAnimation = new ContinualAnimation();
    private final Animation hoverRectFadeAnimation = new DecelerateAnimation(250, 1).setDirection(Direction.BACKWARDS);


    @Override
    public void initGui() {


    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }


    @Override
    public void drawScreen(int mouseX, int mouseY) {
        boolean hoveringMainRect = SideGUI.isHovering(x, y, width, height, mouseX, mouseY);
        if (bypass) {
            hoveringMainRect = HoveringUtil.isHovering(x, y, width, height, mouseX, mouseY);
        }

        hoverAnimation.setDirection(hoveringMainRect ? Direction.FORWARDS : Direction.BACKWARDS);


        Color textColor = ColorUtil.applyOpacity(Color.WHITE, alpha);
        openAnimation.setDirection(opened ? Direction.FORWARDS : Direction.BACKWARDS);
        float openAnim = openAnimation.getOutput().floatValue();
        //Drawn behind the main rect
        if (!openAnimation.isDone() || opened) {
            float dropdownY = getY() + ((getHeight() + 4) * openAnim);
            float dropdownHeight = options.size() * getHeight();
            RoundedUtil.drawRound(getX(), dropdownY, getWidth(), dropdownHeight, 3, ColorUtil.tripleColor(17, getAlpha() * openAnim));


            boolean mouseOutsideRect = (mouseY < dropdownY || mouseY > dropdownY + dropdownHeight) || (mouseX < getX() || mouseX > getX() + getWidth());

            hoverRectFadeAnimation.setDirection(mouseOutsideRect ? Direction.BACKWARDS : Direction.FORWARDS);
            hoverRectFadeAnimation.setDuration(mouseOutsideRect ? 200 : 350);

            RoundedUtil.drawRound(getX(), hoverRectAnimation.getOutput(), getWidth(), getHeight(), 3,
                    ColorUtil.tripleColor(26, getAlpha() * hoverRectFadeAnimation.getOutput().floatValue() * openAnim));

            int seperation = 0;
            for (String option : options) {
                boolean hovering = SideGUI.isHovering(getX(), dropdownY + seperation, getWidth(), getHeight(), mouseX, mouseY);
                if(bypass){
                    hovering = HoveringUtil.isHovering(getX(), dropdownY + seperation, getWidth(), getHeight(), mouseX, mouseY);
                }

                if (hovering) {
                    hoverRectAnimation.animate(dropdownY + seperation, 18);
                }


                Color optionColor = selection.equals(option) ? accentColor : textColor;
                lithiumFont18.drawString(option, getX() + 5, dropdownY + seperation + lithiumFont18.getMiddleOfBox(getHeight()),
                        ColorUtil.applyOpacity(optionColor, openAnim));

                seperation += getHeight();
            }


        }

        //Main rect
        RoundedUtil.drawRound(getX(), getY(), getWidth(), getHeight(), 3,
                ColorUtil.tripleColor(17 + (int) (3 * hoverAnimation.getOutput().floatValue()), getAlpha()));


        lithiumFont18.drawString("§l" + name + ":§r " + selection, getX() + 4, getY() + lithiumFont18.getMiddleOfBox(getHeight()), textColor);


        float iconX = getX() + getWidth() - 10;
        float iconY = getY() + iconFont16.getMiddleOfBox(getHeight());

        RenderUtil.rotateStart(iconX, iconY, iconFont20.getStringWidth(FontUtil.DROPDOWN_ARROW), iconFont20.getHeight(), 180 * openAnimation.getOutput().floatValue());
        iconFont20.drawString(FontUtil.DROPDOWN_ARROW, getX() + getWidth() - 10, getY() + iconFont20.getMiddleOfBox(getHeight()) + 1, textColor);
        RenderUtil.rotateEnd();


    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        boolean hoveringMainRect = SideGUI.isHovering(x, y, width, height, mouseX, mouseY);
        if (bypass) {
            hoveringMainRect = HoveringUtil.isHovering(x, y, width, height, mouseX, mouseY);
        }
        if (hoveringMainRect && button == 1) {
            opened = !opened;
        }


        if (opened) {
            float dropdownY = getY() + ((getHeight() + 4));
            int seperation = 0;
            for (String option : options) {
                boolean hovering = SideGUI.isHovering(getX(), dropdownY + seperation, getWidth(), getHeight(), mouseX, mouseY);
                if (bypass) {
                    hovering = HoveringUtil.isHovering(getX(), dropdownY + seperation, getWidth(), getHeight(), mouseX, mouseY);
                }
                if (hovering && button == 0) {
                    selection = option;
                    opened = false;
                }


                seperation += getHeight();
            }


        }

    }

    public boolean isClosed() {
        return openAnimation.finished(Direction.BACKWARDS);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {

    }


    public void setupOptions(String... options) {
        this.options = Arrays.asList(options);
        selection = options[0];
    }

}
