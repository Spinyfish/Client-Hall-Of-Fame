package crispy.gui.clickgui;

import crispy.gui.clickgui.component.Component;
import crispy.gui.clickgui.component.Frame;
import crispy.features.hacks.Category;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;
import java.util.ArrayList;

//Your Imports

public class ClickGui extends GuiScreen {
    public static ArrayList<Frame> frames;
    public static int color = -1;

    public ClickGui() {
        frames = new ArrayList<Frame>();
        int frameX = 5;
        for (Category category : Category.values()) {

            Frame frame = new Frame(category);
            frame.setX(frameX);
            frames.add(frame);
            frameX += frame.getWidth() + 1;
        }
    }


    @Override
    public void initGui() {

    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (crispy.features.hacks.impl.render.ClickGui.modeValue.getObject() == 0) {
            this.drawDefaultBackground();
        }
        for (Frame frame : frames) {
            frame.renderFrame(Minecraft.fontRendererObj);
            frame.updatePosition(mouseX, mouseY);
            for (Component comp : frame.getComponents()) {
                comp.updateComponent(mouseX, mouseY);
            }
        }
    }

    @Override
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
        for (Frame frame : frames) {
            if (frame.isWithinHeader(mouseX, mouseY) && mouseButton == 0) {
                frame.setDrag(true);
                frame.dragX = mouseX - frame.getX();
                frame.dragY = mouseY - frame.getY();
            }
            if (frame.isWithinHeader(mouseX, mouseY) && mouseButton == 1) {
                frame.setOpen(!frame.isOpen());
            }
            if (frame.isOpen()) {
                if (!frame.getComponents().isEmpty()) {
                    for (Component component : frame.getComponents()) {
                        component.mouseClicked(mouseX, mouseY, mouseButton);
                    }
                }
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        for (Frame frame : frames) {
            if (frame.isOpen() && keyCode != 1) {
                if (!frame.getComponents().isEmpty()) {
                    for (Component component : frame.getComponents()) {
                        component.keyTyped(typedChar, keyCode);
                    }
                }
            }
        }
        if (keyCode == 1) {
            this.mc.displayGuiScreen(null);
        }
    }


    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        for (Frame frame : frames) {
            frame.setDrag(false);
        }
        for (Frame frame : frames) {
            if (frame.isOpen()) {
                if (!frame.getComponents().isEmpty()) {
                    for (Component component : frame.getComponents()) {
                        component.mouseReleased(mouseX, mouseY, state);
                    }
                }
            }
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }
}
