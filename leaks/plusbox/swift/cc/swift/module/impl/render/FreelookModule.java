package cc.swift.module.impl.render;

import cc.swift.events.StrafeEvent;
import cc.swift.events.UpdateWalkingPlayerEvent;
import cc.swift.module.Module;
import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class FreelookModule extends Module {

    public FreelookModule() {
        super("Freelook", Category.RENDER);
    }

    private float lastPitch, lastYaw;

    private boolean wasHolding;
    private float lastForward;
    @Override
    public void onEnable(){
        wasHolding = false;
    }
    public void onDisable(){
        wasHolding = false;
    }
    @Handler
    public final Listener<UpdateWalkingPlayerEvent> updateWalkingPlayerEventListener = event -> {
        // Check if the middle mouse button is held
        if (Mouse.isButtonDown(2)) { // spectre try not to have bad keybinds challenge
            if(!wasHolding){
                mc.gameSettings.thirdPersonView = 1;
                lastPitch = mc.thePlayer.rotationPitch;
                lastYaw = mc.thePlayer.rotationYaw;
            }
            event.setPitch(lastPitch);
            event.setYaw(lastYaw);
        } else if (wasHolding) {
            mc.gameSettings.thirdPersonView = 0;
            mc.thePlayer.rotationYaw = lastYaw;
            mc.thePlayer.rotationPitch = lastPitch;
        }
        wasHolding = Mouse.isButtonDown(2);
    };

    @Handler
    public final Listener<StrafeEvent> strafeEventListener = event -> {
        // Check if the middle mouse button is held
        if (Mouse.isButtonDown(2)) {
            if(!wasHolding){
               lastForward = event.getForward();
            }
            event.setForward(lastForward);
            event.setYaw(lastYaw);
        }
    };
}
