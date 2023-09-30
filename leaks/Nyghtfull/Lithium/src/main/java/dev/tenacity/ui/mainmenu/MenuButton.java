package dev.tenacity.ui.mainmenu;

import dev.tenacity.ui.Screen;
import dev.tenacity.utils.animations.Animation;
import dev.tenacity.utils.animations.Direction;
import dev.tenacity.utils.animations.impl.DecelerateAnimation;
import dev.tenacity.utils.misc.HoveringUtil;
import dev.tenacity.utils.render.RenderUtil;
import dev.tenacity.utils.render.RoundedUtil;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class MenuButton implements Screen {

    public final String text;
    public float x, y, width, height;
    public Runnable clickAction;

    public MenuButton(String text) {
        this.text = text;
    }


    @Override
    public void initGui() {

    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {

        boolean hovered = HoveringUtil.isHovering(x, y, width, height, mouseX, mouseY);

        RoundedUtil.drawRound(x, y, width, height, 5, hovered ? new Color(255, 255, 255, 35) : new Color(205, 205, 205, 25));

        lithiumFont22.drawCenteredString(text, x + width / 2f, y + lithiumFont22.getMiddleOfBox(height), -1);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        boolean hovered = HoveringUtil.isHovering(x, y, width, height, mouseX, mouseY);

        if (hovered) {
            clickAction.run();
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {

    }
}
