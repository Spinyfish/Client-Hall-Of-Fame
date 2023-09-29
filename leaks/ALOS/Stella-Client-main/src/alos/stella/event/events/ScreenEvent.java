package alos.stella.event.events;

import alos.stella.event.Event;
import net.minecraft.client.gui.GuiScreen;
import org.jetbrains.annotations.Nullable;

public final class ScreenEvent extends Event {
    @Nullable
    private final GuiScreen guiScreen;

    @Nullable
    public final GuiScreen getGuiScreen() {
        return this.guiScreen;
    }

    public ScreenEvent(@Nullable GuiScreen guiScreen) {
        this.guiScreen = guiScreen;
    }
}