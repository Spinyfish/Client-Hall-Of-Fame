package dev.echo.ui.sidegui.utils;

import dev.echo.module.impl.render.HUDMod;
import dev.echo.ui.Screen;
import dev.echo.ui.sidegui.SideGUI;
import dev.echo.utils.animations.Animation;
import dev.echo.utils.animations.ContinualAnimation;
import dev.echo.utils.animations.Direction;
import dev.echo.utils.animations.impl.DecelerateAnimation;
import dev.echo.utils.misc.HoveringUtil;
import dev.echo.utils.render.ColorUtil;
import dev.echo.utils.render.RoundedUtil;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CarouselButtons implements Screen {
    @Getter
    @Setter
    private float x, y, rectWidth, rectHeight, alpha;
    @Getter
    private boolean hovering = false;
    @Setter
    private Color backgroundColor = new Color(39,39,39);
    @Getter
    private String currentButton;
    private final Map<String, Animation> options;

    public CarouselButtons(String... options) {
        this.options = Arrays.stream(options).collect(Collectors.toMap(Function.identity(), v -> new DecelerateAnimation(250, 1)));
        currentButton = options[0];
    }

    private final ContinualAnimation rectAnimation = new ContinualAnimation();


    @Override
    public void initGui() {

    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }


    @Override
    public void drawScreen(int mouseX, int mouseY) {
        float buttonWidth = options.size() * rectWidth;
        Color textColor = ColorUtil.applyOpacity(Color.WHITE, alpha);

        hovering = HoveringUtil.isHovering(x,y, buttonWidth, rectHeight, mouseX, mouseY);

        //This is the background of the carousel buttons
        RoundedUtil.drawRound(x, y, buttonWidth, rectHeight, 5, ColorUtil.applyOpacity(backgroundColor, alpha));

        Color accentColor = ColorUtil.applyOpacity(HUDMod.getClientColors().getFirst(), getAlpha());

        float coloredX = rectAnimation.getOutput();

        //This is the colored rectangle that indicate the current button
        RoundedUtil.drawRound(x + coloredX, y, rectWidth, rectHeight, 5, accentColor);

        int seperation = 0;
        for (Map.Entry<String, Animation> entry : options.entrySet()) {
            float buttonX = x + seperation;
            boolean isCurrentButton = entry.getKey().equals(currentButton);
            Animation animation = entry.getValue();

            //If it is the currentButton then set the animateX value to the current button's x position
            if (isCurrentButton) {
                rectAnimation.animate(seperation, 18);
            }

            boolean hovering = SideGUI.isHovering(buttonX, y, buttonWidth, rectHeight, mouseX, mouseY);
            animation.setDirection(hovering ? Direction.FORWARDS : Direction.BACKWARDS);

            float textAlpha = (.5f + (.3f * animation.getOutput().floatValue()));
            echoFont24.drawCenteredString(entry.getKey(), x + rectWidth / 2f + seperation, y + echoFont24.getMiddleOfBox(rectHeight),
                    ColorUtil.applyOpacity(isCurrentButton ? textColor : ColorUtil.applyOpacity(textColor, textAlpha), getAlpha()));


            seperation += rectWidth;
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        int seperation = 0;
        for (Map.Entry<String, Animation> entry : options.entrySet()) {
            float buttonX = x + seperation;

            boolean hovering = SideGUI.isHovering(buttonX, y, rectWidth, rectHeight, mouseX, mouseY);
            if (hovering) {
                currentButton = entry.getKey();
            }
            seperation += rectWidth;
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {

    }

    public float getTotalWidth() {
        return options.size() * rectWidth;
    }

}
