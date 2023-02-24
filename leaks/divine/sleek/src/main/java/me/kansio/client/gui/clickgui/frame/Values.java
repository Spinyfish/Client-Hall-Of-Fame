package me.kansio.client.gui.clickgui.frame;

import me.kansio.client.utils.render.ColorPalette;
import me.kansio.client.utils.render.ColorUtils;

import java.awt.*;

public interface Values {
    int stringColor = -1;

    int defaultWidth = 125;
    int defaultHeight = 300;

    int enabledColor = ColorPalette.LIGHT_BLUE.getColor().getRGB();

    int mainColor = new Color(0,0,0, 130).getRGB();
    int darkerMainColor = new Color(32, 32, 32).getRGB();

    int headerColor = ColorPalette.LIGHT_BLUE.getColor().getRGB();

    int outlineWidth = 0;
    int categoryNameHeight = 20;

    int moduleHeight = 15;

    boolean hoveredColor = false;
}
