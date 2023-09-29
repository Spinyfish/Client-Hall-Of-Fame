package wtf.automn.gui.clickgui.phantom;

import net.minecraft.client.gui.GuiScreen;
import wtf.automn.fontrenderer.ClientFont;
import wtf.automn.fontrenderer.GlyphPageFontRenderer;
import wtf.automn.module.Category;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DropdownClickGui extends GuiScreen {
    public static final DropdownClickGui INSTANCE = new DropdownClickGui();

    List<Panel> panels = new ArrayList<>();

    public DropdownClickGui() {
        float xOffset = 20;
        for (Category value : Category.values()) {
            Panel panel = new Panel(xOffset, 20, 100, 20, value);
            panels.add(panel);
            xOffset += panel.width() + 10;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        for (Panel panel : panels) {
            panel.drawPanel(mouseX, mouseY, partialTicks);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for (Panel panel : panels) {
            panel.mouseClicked(mouseX, mouseY, mouseButton);
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        for (Panel panel : panels) {
            panel.mouseReleased(mouseX, mouseY, state);
        }
        super.mouseReleased(mouseX, mouseY, state);
    }
}
