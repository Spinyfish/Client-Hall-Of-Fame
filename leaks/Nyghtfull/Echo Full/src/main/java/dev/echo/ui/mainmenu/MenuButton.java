package dev.echo.ui.mainmenu;

import dev.echo.ui.Screen;
import dev.echo.utils.animations.Animation;
import dev.echo.utils.animations.Direction;
import dev.echo.utils.animations.impl.DecelerateAnimation;
import dev.echo.utils.misc.HoveringUtil;
import dev.echo.utils.render.RenderUtil;
import net.minecraft.util.ResourceLocation;

public class MenuButton implements Screen {

    public final String text;
    private Animation hoverAnimation;
    public float x, y, width, height;
    public Runnable clickAction;

    public MenuButton(String text) {
        this.text = text;
    }


    @Override
    public void initGui() {
        hoverAnimation = new DecelerateAnimation(200, 1);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }

    private static final ResourceLocation rs = new ResourceLocation("Echo/MainMenu/menu-rect.png");

    @Override
    public void drawScreen(int mouseX, int mouseY) {

        boolean hovered = HoveringUtil.isHovering(x, y, width, height, mouseX, mouseY);
        hoverAnimation.setDirection(hovered ? Direction.FORWARDS : Direction.BACKWARDS);


        RenderUtil.color(-1);
        RenderUtil.drawImage(rs, x,y,width,height);

        echoFont22.drawCenteredString(text, x + width / 2f, y + echoFont22.getMiddleOfBox(height), -1);
    }

    public void drawOutline() {
        RenderUtil.drawImage(rs, x,y,width,height);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        boolean hovered = HoveringUtil.isHovering(x, y, width, height, mouseX, mouseY);
        if(hovered) {
            clickAction.run();
        }

    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {

    }
}
