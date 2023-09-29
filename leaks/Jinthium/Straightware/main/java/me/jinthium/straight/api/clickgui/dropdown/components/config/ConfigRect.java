package me.jinthium.straight.api.clickgui.dropdown.components.config;

import me.jinthium.straight.api.clickgui.dropdown.Screen;
import me.jinthium.straight.api.config.LocalConfig;
import me.jinthium.straight.impl.utils.animation.Animation;
import me.jinthium.straight.impl.utils.animation.Direction;
import me.jinthium.straight.impl.utils.animation.impl.EaseInOutQuad;
import me.jinthium.straight.impl.utils.misc.HoveringUtil;
import me.jinthium.straight.impl.utils.render.ColorUtil;
import me.jinthium.straight.impl.utils.render.RenderUtil;
import me.jinthium.straight.impl.utils.render.RoundedUtil;
import net.minecraft.client.gui.Gui;

import java.awt.*;

public class ConfigRect implements Screen {

    private float x, y;
    private final LocalConfig config;
    private Animation animation = new EaseInOutQuad(250, 1);
    public static LocalConfig selectedConfig;

    public ConfigRect(LocalConfig config){
        this.config = config;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    @Override
    public void initGui() {

    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        boolean hovered = HoveringUtil.isHovering(x, y, 300, 30, mouseX, mouseY);
        animation.setDirection(hovered ? Direction.FORWARDS : Direction.BACKWARDS);
        RoundedUtil.drawRoundOutline(x, y, 300, 30, 2f,
                2f, ConfigRect.selectedConfig == config ? new Color(30, 30, 30).darker()
                        : ColorUtil.applyOpacity(new Color(30, 30, 30), animation.getOutput().floatValue()), new Color(20, 20, 20));
        normalFont16.drawStringWithShadow(this.config.getName(), x + 2, y + normalFont16.getMiddleOfBox(30) + 2, -1);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
//        ConfigRect.selectedConfig = null;
        if(HoveringUtil.isHovering(x, y, 300, 30, mouseX, mouseY)){
            ConfigRect.selectedConfig = config;
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {

    }
}
