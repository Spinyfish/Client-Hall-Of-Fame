package store.femboy.spring.impl.module.impl.movement;

import best.azura.eventbus.core.EventPriority;
import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import org.lwjgl.input.Keyboard;
import store.femboy.spring.impl.event.impl.EventMotion;
import store.femboy.spring.impl.module.Category;
import store.femboy.spring.impl.module.Module;
import store.femboy.spring.impl.module.settings.impl.ModeSetting;
import store.femboy.spring.impl.util.MoveUtil;
import store.femboy.spring.impl.util.TimeUtil;

public final class Flight extends Module {
    public Flight() {
        super("Flight", "Makes u fly lol", Keyboard.KEY_F, Category.MOVEMENT);
        addSettings(mode);
    }

    final ModeSetting mode = new ModeSetting("Mode", "Hypixel", "Hypixel", "Vanilla");

    TimeUtil timer = new TimeUtil();

    @Override
    public void onEnable() {
        if(mode.is("Hypixel")){
        }
        super.onEnable();
    }

    @Override
    public void onDisable() {
        mc.thePlayer.capabilities.isFlying = false;
        MoveUtil.setSpeed(1);
        if(mode.is("Hypixel")){
            if(mc.thePlayer.onGround)
                mc.thePlayer.motionY = 0.4249f;
        }
        super.onDisable();
    }
    @EventHandler(EventPriority.DEFAULT)
    public final Listener<EventMotion> onUpdate = e -> {

        switch(mode.getMode()){
            case "Vanilla":
                mc.thePlayer.motionY = 0;
                if(!mc.thePlayer.onGround) {
                    MoveUtil.setSpeed(2);
                }
                if(mc.gameSettings.keyBindJump.isKeyDown()){
                    mc.thePlayer.motionY = 2f;
                }
                if(mc.gameSettings.keyBindSneak.isKeyDown()){
                    mc.thePlayer.motionY = -2f;
                }
                break;
            case "Hypixel":
                mc.thePlayer.motionX=0;
                mc.thePlayer.motionZ=0;
                if(timer.hasTimeElapsed(1000)) {
                    this.toggle();
                    timer.reset();
                }
                break;
        }
    };
}
