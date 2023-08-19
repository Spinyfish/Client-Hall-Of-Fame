/**
 * @project hakarware
 * @author CodeMan
 * @at 25.07.23, 17:20
 */

package cc.swift.gui.clickgui.components;

import cc.swift.gui.clickgui.GuiComponent;
import cc.swift.util.ColorUtil;
import cc.swift.util.MouseUtil;
import cc.swift.util.render.font.CFontRenderer;
import cc.swift.util.render.font.FontRenderer;
import cc.swift.value.impl.ModeValue;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.StringUtils;

public class ModeValueComponent extends GuiComponent {

    private final ModeValue<?> modeValue;

    public ModeValueComponent(ModeValue<?> modeValue, double width, double height) {
        super(0, 0, width, height);
        this.modeValue = modeValue;
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        Gui.drawRect(x, y, x + width, y + height, ColorUtil.LIGHT_GRAY.getRGB());

        CFontRenderer vietnamFont = FontRenderer.getFont("vietnam.ttf", 15);

        int y = (int) Math.ceil(this.y + (height - vietnamFont.getHeight()) / 2);

        vietnamFont.drawStringWithShadow(modeValue.getName(), x + 6, y, -1);
        vietnamFont.drawStringWithShadow(StringUtils.enumToString((Enum<?>) modeValue.getValue()), x + width - 6 - vietnamFont.getStringWidth(StringUtils.enumToString((Enum<?>) modeValue.getValue())), y, -1);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (MouseUtil.isHovered(x, y, width, height, mouseX, mouseY)) {
            if (mouseButton == 0)
                modeValue.next();
            else if (mouseButton == 1)
                modeValue.previous();
        }
    }

    @Override
    public boolean isVisible() {
        return modeValue.isVisible();
    }
}
