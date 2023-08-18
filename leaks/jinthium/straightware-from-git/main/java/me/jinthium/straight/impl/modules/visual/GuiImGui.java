package me.jinthium.straight.impl.modules.visual;

import me.jinthium.straight.impl.Client;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;

public class GuiImGui extends GuiScreen {

    private GuiScreen parent;

    public GuiScreen getParent() {
        return parent;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (parent != null) parent.drawScreen(mouseX, mouseY, partialTicks);

        Client.INSTANCE.getCInterface().render();
    }

    @Override
    public void keyTyped(final char typedChar, final int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        if (parent != null) parent.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
//        Client.INSTANCE.getCInterface().updateExtended();
    }

    @Override
    public void mouseClickMove(final int mouseX, final int mouseY, final int clickedMouseButton, final long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        if (parent != null) parent.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }

    @Override
    public void actionPerformed(final GuiButton button) throws IOException {
        super.actionPerformed(button);
        if (parent != null) parent.actionPerformed(button);
        System.out.println("action performed");
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        if (parent != null) {
            parent.onGuiClosed();
        }
    }
}