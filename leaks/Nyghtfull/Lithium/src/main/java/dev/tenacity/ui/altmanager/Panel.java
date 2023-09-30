package dev.tenacity.ui.altmanager;

import dev.tenacity.ui.Screen;
import dev.tenacity.utils.render.ColorUtil;
import dev.tenacity.utils.render.RoundedUtil;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Panel implements Screen {
    private float x, y, width, height;


    @Override
    public void drawScreen(int mouseX, int mouseY) {
        RoundedUtil.drawRound(x, y, width, height, 5, ColorUtil.tripleColor(27));
    }


}
