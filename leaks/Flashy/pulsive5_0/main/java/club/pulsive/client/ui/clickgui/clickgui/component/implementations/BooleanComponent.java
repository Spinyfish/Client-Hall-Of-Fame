package club.pulsive.client.ui.clickgui.clickgui.component.implementations;


import club.pulsive.client.ui.clickgui.clickgui.component.SettingComponent;
import club.pulsive.impl.property.Property;
import club.pulsive.impl.util.render.RenderUtil;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

@Getter
@Setter
public class BooleanComponent extends SettingComponent<Property<Boolean>> {

    private int opacity = 0;

    public BooleanComponent(Property<Boolean> setting, float x, float y, float width, float height) {
        super(setting, x, y, width, height);
    }

    public BooleanComponent(Property<Boolean> setting, float x, float y, float width, float height, boolean visible) {
        super(setting, x, y, width, height, visible);
    }

    @Override
    public void reset() {
        opacity = 0;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        if (!visible) return;
        if (setting.getValue()) {
            if (opacity < 255)
                opacity += 1;
        } else if (opacity > 0)
            opacity -= 1;
        theme.drawBooleanComponent(this, x, y, width, height, 9, 9, setting.getValue() ? -1 : Color.TRANSLUCENT);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (!visible) return;
        if (RenderUtil.inBounds(x + width - 9 - 1, y, x + width - 1, y + height, mouseX, mouseY)) {
            setting.setValue(!setting.getValue());
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        if (!visible) return;
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (!visible) return;
    }
}
