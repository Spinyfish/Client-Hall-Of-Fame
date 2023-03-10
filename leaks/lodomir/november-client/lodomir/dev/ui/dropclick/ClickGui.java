/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.ui.dropclick;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lodomir.dev.modules.Category;
import lodomir.dev.ui.dropclick.Frame;
import net.minecraft.client.gui.GuiScreen;

public class ClickGui
extends GuiScreen {
    public static ClickGui INSTANCE = new ClickGui();
    private List<Frame> frames = new ArrayList<Frame>();

    public ClickGui() {
        int offset = 5;
        for (Category category : Category.values()) {
            this.frames.add(new Frame(category, offset, 5, 105, 15));
            offset += 110;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        for (Frame frame : this.frames) {
            frame.render(mouseX, mouseY, partialTicks);
            frame.updatePos(mouseX, mouseY);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for (Frame frame : this.frames) {
            frame.mouseClicked(mouseX, mouseY, mouseButton);
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        for (Frame frame : this.frames) {
            frame.mouseReleased(mouseX, mouseY, state);
        }
        super.mouseReleased(mouseX, mouseY, state);
    }
}

