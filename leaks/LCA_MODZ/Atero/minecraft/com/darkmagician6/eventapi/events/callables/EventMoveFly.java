package com.darkmagician6.eventapi.events.callables;

import net.minecraft.client.Minecraft;

public class EventMoveFly extends EventCancellable {

    public float yaw;

    public EventMoveFly() {

        yaw = Minecraft.thePlayer.rotationYawHead;
    }
}
