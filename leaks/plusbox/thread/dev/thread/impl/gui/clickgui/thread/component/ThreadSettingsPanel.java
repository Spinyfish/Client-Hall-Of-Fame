package dev.thread.impl.gui.clickgui.thread.component;

import dev.thread.api.module.Module;
import dev.thread.api.setting.Setting;
import dev.thread.api.util.math.MathUtil;
import dev.thread.api.util.render.RenderUtil;
import dev.thread.api.util.render.component.Component;
import dev.thread.client.Thread;

import java.awt.*;

public class ThreadSettingsPanel extends Component {
    private float x, y, width, height;
    private final Module module;

    public ThreadSettingsPanel(Object parent, Module module) {
        super(parent);
        this.module = module;
    }

    @Override
    public void render(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        RenderUtil.scissor(x, y, width, height);

        RenderUtil.drawRoundedRect(x + 9, y + 9, 37, 24, 3, new Color(255, 255, 255, 36));
        Thread.INSTANCE.getFontManager().get("poppins").drawString(20, "Back", x + 15.5F, y + 16.5F, new Color(255, 255, 255, 112));

        float index = y + 52;
        for (Setting<?> s : module.getSettings()) {
            RenderUtil.drawRoundedRect(x + 19, index, width - 28, 52, 3, new Color(255, 255, 255, 50));
            Thread.INSTANCE.getFontManager().get("poppins-semibold").drawString(35, s.getName(), x + 26, index + 12, new Color(255, 255, 255, 255));
            Thread.INSTANCE.getFontManager().get("poppins").drawString(25, s.getDescription(), x + 26, index + 30, new Color(255, 255, 255, 255));
            index += 61;
        }

        RenderUtil.endScissor();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (MathUtil.isMouseHovered(mouseX, mouseY, x + 9, y + 9, 37, 24)) {
            ((ThreadCategoryPanel)getParent()).setSettingsPanel(null);
        }
    }

    @Override
    public void keyTyped(int keyCode) {

    }
}
