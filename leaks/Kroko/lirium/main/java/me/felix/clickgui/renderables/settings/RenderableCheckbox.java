package me.felix.clickgui.renderables.settings;

import de.lirium.Client;
import de.lirium.base.setting.impl.CheckBox;
import de.lirium.util.render.FontRenderer;
import de.lirium.util.render.RenderUtil;
import de.lirium.util.render.StencilUtil;
import me.felix.clickgui.abstracts.ClickGUIHandler;
import net.minecraft.client.gui.Gui;

import java.awt.*;

public class RenderableCheckbox extends ClickGUIHandler {

    public CheckBox checkBox;

    private float animation = 0;

    private boolean hovering;

    public RenderableCheckbox(CheckBox checkBox) {
        this.checkBox = checkBox;
    }

    @Override
    public void onMouseReleased(int mouseX, int mouseY) {

    }

    @Override
    public void onMouseClicked(int mouseX, int mouseY, int mouseKey) {

        if (!hovering) return;

        if (mouseKey == 0)
            checkBox.setValue(!checkBox.getValue());

    }

    @Override
    public void drawScreen(int mouseX, int mouseY, int x, int y) {
        final FontRenderer fontRenderer = Client.INSTANCE.getFontLoader().get("arial", 17);

        Gui.drawRect(x, y, x + width, y + height, new Color(30, 30, 30).getRGB());

        final int checkBoxWidth = 8;

        final int checkBoxHeight = 8;

        hovering = this.isHovered(mouseX, mouseY, x - checkBoxWidth + width - checkBoxWidth, y + 2 - checkBoxHeight + height - checkBoxHeight, 10, 2 + height - 5);

        RenderUtil.drawRoundedRect(x + width - checkBoxWidth - 8, y + 2.5f, 10, 10, 2, hovering ? new Color(10, 10, 10) : new Color(20, 20, 20));

      //  Gui.drawRect(x - checkBoxWidth + width - checkBoxWidth, y + 2 - checkBoxHeight + height - checkBoxHeight, x + width - 5, y + 2 + height - 5, hovering ? new Color(10, 10, 10).getRGB() : new Color(20, 20, 20).getRGB());

        animation = (float) RenderUtil.getAnimationState(animation, checkBox.getValue() ? 0 : 30, 70);

        StencilUtil.init();
        Gui.drawRect(x - checkBoxWidth + width - checkBoxWidth, y + 2 - checkBoxHeight + height - checkBoxHeight, (int) (x + width - animation), y + height, new Color(30, 30, 30).getRGB());
        StencilUtil.readBuffer(1);

        RenderUtil.drawCheckMark(x + width - 11f, y, 10, -1);

        StencilUtil.uninit();


        StencilUtil.init();
        Gui.drawRect(x, y, x + width - 20, y + height, new Color(30, 30, 30).getRGB());
        StencilUtil.readBuffer(1);
        fontRenderer.drawString(checkBox.getDisplay(), x + 3, y + 3, -1);
        StencilUtil.uninit();
    }
}
