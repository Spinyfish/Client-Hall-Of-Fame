package com.alan.clients.newevent.impl.input;

import com.alan.clients.newevent.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.gui.GuiScreen;

/**
 * @author Patrick
 * @since 10/19/2021
 */
@Getter
@AllArgsConstructor
public final class KeyboardInputEvent implements Event {
    private final int keyCode;
    private final GuiScreen guiScreen;
}
