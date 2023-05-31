package de.lirium.impl.events;

import best.azura.eventbus.core.Event;
import lombok.AllArgsConstructor;
import net.minecraft.client.gui.ChatLine;

@AllArgsConstructor
public class ChatRenderEvent implements Event {
    public final ChatLine chatLine;
    public String text;
    public final int left, top, right, bottom;
    public final int mouseX, mouseY;
}