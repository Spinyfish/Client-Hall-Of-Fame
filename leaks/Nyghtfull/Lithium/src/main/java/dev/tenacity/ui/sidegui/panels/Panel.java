package dev.tenacity.ui.sidegui.panels;

import dev.tenacity.module.impl.render.HUDMod;
import dev.tenacity.ui.Screen;
import dev.tenacity.utils.render.ColorUtil;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

@Getter
@Setter
public abstract class Panel implements Screen {
    private float x, y, width, height, alpha;

    public Color getTextColor() {
        return ColorUtil.applyOpacity(Color.WHITE, alpha);
    }

    public Color getAccentColor() {
        return ColorUtil.applyOpacity(HUDMod.getClientColors().getFirst(), alpha);
    }

}
