package dev.africa.pandaware.api.screen;

import dev.africa.pandaware.utils.math.vector.Vec2i;
import net.minecraft.client.gui.GuiButton;

public interface GUIRenderer {
    void handleRender(Vec2i mousePosition, float pTicks);

    default void handlePreRender(Vec2i mousePosition, float pTicks) {

    }

    default void handleKeyboard(char typedChar, int keyCode) {

    }

    default void handleClick(Vec2i mousePosition, int button) {

    }

    default void handleClickMove(Vec2i mousePosition, int button, long lastClick) {

    }

    default void handleRelease(Vec2i mousePosition, int state) {

    }

    default void handleActionPerformed(GuiButton button) {

    }

    default void handleScreenUpdate() {

    }

    default void handleGuiClose() {

    }

    default void handleGuiInit() {

    }
}
