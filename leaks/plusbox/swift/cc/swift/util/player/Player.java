package cc.swift.util.player;

import cc.swift.events.EventState;
import cc.swift.events.UpdateWalkingPlayerEvent;
import cc.swift.util.ChatUtil;
import cc.swift.util.IMinecraft;
import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import lombok.Getter;
import lombok.Setter;

public class Player implements IMinecraft {

    @Getter
    @Setter
    private float airFriction = 0.91f, groundFriction = Float.NaN, fallSpeed = 0.08f, gravity = 0.9800000190734863f;


    // Reset all values
    @Handler
    public final Listener<UpdateWalkingPlayerEvent> updateWalkingPlayerEventListener = event -> {
        ChatUtil.printChatMessage(event);
        if (event.getState() != EventState.PRE) return;
        ChatUtil.printChatMessage("AAAA");
        airFriction = 0.91f;
        fallSpeed = 0.08f;
        gravity = 0.9800000190734863f;
        groundFriction = Float.NaN;
    };
}
