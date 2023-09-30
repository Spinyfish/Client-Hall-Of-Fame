package dev.africa.pandaware.api.screen;

import dev.africa.pandaware.utils.math.vector.Vec2i;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;

public abstract class ScreenGUI extends GuiScreen implements GUIRenderer {
    @Override
    public final void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.handleRender(new Vec2i(mouseX, mouseY), partialTicks);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected final void keyTyped(char typedChar, int keyCode) throws IOException {
        this.handleKeyboard(typedChar, keyCode);

        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected final void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        this.handleClick(new Vec2i(mouseX, mouseY), mouseButton);

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected final void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        this.handleClickMove(new Vec2i(mouseX, mouseY), clickedMouseButton, timeSinceLastClick);

        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }

    @Override
    protected final void mouseReleased(int mouseX, int mouseY, int state) {
        this.handleRelease(new Vec2i(mouseX, mouseY), state);

        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected final void actionPerformed(GuiButton button) throws IOException {
        this.handleActionPerformed(button);

        super.actionPerformed(button);
    }

    @Override
    public final void updateScreen() {
        this.handleScreenUpdate();

        super.updateScreen();
    }

    @Override
    public final void onGuiClosed() {
        this.handleGuiClose();

        super.onGuiClosed();
    }

    @Override
    public final void initGui() {
        this.handleGuiInit();

        super.initGui();
    }
}
