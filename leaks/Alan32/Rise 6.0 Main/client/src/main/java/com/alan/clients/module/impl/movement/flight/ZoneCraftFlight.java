package com.alan.clients.module.impl.movement.flight;

import com.alan.clients.Client;
import com.alan.clients.component.impl.render.NotificationComponent;
import com.alan.clients.module.impl.movement.Flight;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.newevent.impl.motion.StrafeEvent;
import com.alan.clients.util.player.MoveUtil;
import com.alan.clients.value.Mode;
import net.minecraft.util.Vec3;

public class ZoneCraftFlight extends Mode<Flight> {

    public ZoneCraftFlight(String name, Flight parent) {
        super(name, parent);
    }

    public Vec3 position = new Vec3(0, 0, 0);

    @Override
    public void onEnable() {
        if (!mc.thePlayer.onGround) {
            toggle();
        }

        if (!Client.DEVELOPMENT_SWITCH) {
            NotificationComponent.post("Flight", "This feature is only enabled for developers atm");
        }

        position = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
    }

    @Override
    public void onDisable() {
        MoveUtil.stop();
    }

    @EventLink()
    public final Listener<PreMotionEvent> onPreMotionEvent = event -> {
        event.setPosX(position.xCoord);
        event.setPosY(position.yCoord);
        event.setPosZ(position.zCoord);
        event.setOnGround(true);
    };

    @EventLink()
    public final Listener<StrafeEvent> onStrafe = event -> {
        final float speed = 3;

        event.setSpeed(speed);

        mc.thePlayer.motionY = 0.0D;
    };

}
