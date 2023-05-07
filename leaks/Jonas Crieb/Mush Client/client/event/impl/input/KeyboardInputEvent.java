package client.event.impl.input;

import client.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.gui.GuiScreen;

@Getter
@AllArgsConstructor
public final class KeyboardInputEvent implements Event {
    private final int keyIndex;
    private final GuiScreen screen;
}
