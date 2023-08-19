package dev.thread.impl.gui.clickgui.thread;

import dev.thread.api.module.ModuleCategory;
import dev.thread.api.util.math.MathUtil;
import dev.thread.api.util.math.animation.Animation;
import dev.thread.api.util.math.animation.Easing;
import dev.thread.api.util.render.RenderUtil;
import dev.thread.api.util.render.StencilUtil;
import dev.thread.client.Thread;
import dev.thread.impl.gui.clickgui.thread.component.ThreadCategoryPanel;
import lombok.Getter;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class ThreadClickGUI extends GuiScreen {
    private ModuleCategory selected = ModuleCategory.COMBAT;
    @Getter
    private final Animation selectionAnimation = new Animation(Easing.CUBIC_IN_OUT, 300, true);
    private final ArrayList<ThreadCategoryPanel> panels = new ArrayList<>();

    public ThreadClickGUI() {
        for (ModuleCategory c : ModuleCategory.values()) {
            panels.add(new ThreadCategoryPanel(this, c));
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        float width = 564;
        float height = 355;
        float x = (this.width - width) / 2;
        float y = (this.height - height) / 2;

        RenderUtil.drawRoundedBlur(x, y, width, height, 23, 30);
        RenderUtil.drawRoundedGradient(x, y, width, height, 23, new Color(26, 16, 52, 99).getRGB(), new Color(14, 12, 35, 105).getRGB(), new Color(26, 16, 52, 99).getRGB(), new Color(14, 12, 35, 105).getRGB());
        StencilUtil.renderStencil(() -> RenderUtil.drawRoundedRect(x, y, width, height, 23, Color.BLACK), () -> RenderUtil.drawRect(x, y, 149, height, new Color(26, 16, 52, 173)));

        RenderUtil.drawRoundedRect(x + (149 - 58) / 2, (y + (149 - Thread.INSTANCE.getFontManager().get("poppins").getStringWidth(30, "thread")) / 2) + 23 + 11, 58, 12, 6, new Color(217, 217, 217, 48));

        for (ThreadCategoryPanel panel : panels) {
            if (selected == panel.getCategory()) {
                panel.render(x + 149, y, width - 149, height);
            }
        }

        //ModuleCategories
        //More autistic math
        float cX = x + 9;
        float cY = y + 92;
        float cWidth = 131;
        float cHeight = 19;
        for (ModuleCategory c : ModuleCategory.values()) {
            RenderUtil.drawRoundedRect(cX, cY, cWidth, cHeight, 3, new Color(217, 217, 217, 28));
            if (selected == c) {
                RenderUtil.drawRoundedRect(cX, cY, cWidth, cHeight, 3, new Color(59, 93, 212, 35 + (int) (135 * selectionAnimation.getFactor())));
            }
            Thread.INSTANCE.getFontManager().get("icons").drawString(20, c.getIcon(), cX + 5, cY + 3, new Color(255, 255, 255, 112));
            Thread.INSTANCE.getFontManager().get("poppins").drawString(16, c.name().toLowerCase(), cX + 16, cY + 5.5F, new Color(255, 255, 255, 112));
            cY += cHeight + 6;
        }

        Thread.INSTANCE.getFontManager().get("poppins").drawString(60, "thread", x + (149 - Thread.INSTANCE.getFontManager().get("poppins").getStringWidth(30, "thread")) / 2, y + (149 - Thread.INSTANCE.getFontManager().get("poppins").getStringWidth(30, "thread")) / 2, Color.WHITE);

        //autistic ass math bc font width isn't properly working and idk opengl
        Thread.INSTANCE.getFontManager().get("poppins").drawString(14, "beta 0.1", (x + ((149 - 58) / 2)) + 16.5F, (y + (149 - Thread.INSTANCE.getFontManager().get("poppins").getStringWidth(30, "thread")) / 2) + 23 + 11 + 3, new Color(255, 255, 255, 112));
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        float width = 564;
        float height = 355;
        float x = (this.width - width) / 2;
        float y = (this.height - height) / 2;
        float cX = x + 9;
        float cY = y + 92;
        float cWidth = 131;
        float cHeight = 19;
        for (ModuleCategory c : ModuleCategory.values()) {
            RenderUtil.drawRoundedRect(cX, cY, cWidth, cHeight, 3, new Color(217, 217, 217, 28));
            if (MathUtil.isMouseHovered(mouseX, mouseY, cX, cY, cWidth, cHeight) && selected != c) {
                selected = c;
                selectionAnimation.setState(true);
            }
            cY += cHeight + 6;
        }

        for (ThreadCategoryPanel panel : panels) {
            if (selected == panel.getCategory()) {
                panel.mouseClicked(mouseX, mouseY, mouseButton);
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        for (ThreadCategoryPanel panel : panels) {
            if (selected == panel.getCategory()) {
                panel.keyTyped(keyCode);
            }
        }
    }
}
