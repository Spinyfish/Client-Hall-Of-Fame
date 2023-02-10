package dev.myth.ui.clickgui.blubgui.module;

import dev.myth.api.utils.font.FontLoaders;
import dev.myth.api.utils.mouse.MouseUtil;
import dev.myth.api.utils.render.RenderUtil;
import dev.myth.main.ClientMain;
import dev.myth.managers.FeatureManager;
import dev.myth.ui.clickgui.Component;
import dev.myth.ui.clickgui.blubgui.BlubUI;
import org.lwjgl.input.Mouse;

import java.awt.*;

public class RectModule extends Component {


    public String name;
    public int x,y;
    public double width, height;

    public RectModule(final String name, int x, int y, double width, double height) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

    }

    @Override
    public void drawComponent(int mouseX, int mouseY) {



        RenderUtil.drawRoundedRect((float) (x), (float) y, width, height, 5, new Color(23, 25,24).getRGB(), new Color(32, 35,34).getRGB(), 1f);
        FontLoaders.BOLD_13.drawString(name, x + 8, y + 10, -1);

        super.drawComponent(mouseX, mouseY);
    }
}
