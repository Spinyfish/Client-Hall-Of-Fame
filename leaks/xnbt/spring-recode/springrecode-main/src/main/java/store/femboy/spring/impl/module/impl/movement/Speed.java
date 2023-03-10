package store.femboy.spring.impl.module.impl.movement;

import best.azura.eventbus.core.EventPriority;
import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import org.lwjgl.input.Keyboard;
import store.femboy.spring.Spring;
import store.femboy.spring.impl.event.impl.EventMotion;
import store.femboy.spring.impl.module.Category;
import store.femboy.spring.impl.module.Module;
import store.femboy.spring.impl.module.settings.impl.BooleanSetting;
import store.femboy.spring.impl.module.settings.impl.ModeSetting;
import store.femboy.spring.impl.module.settings.impl.NumberSettings;
import store.femboy.spring.impl.util.MoveUtil;

public final class Speed extends Module {
    public Speed() {
        super("Speed", "Go fast", Keyboard.KEY_R, Category.MOVEMENT);
        addSettings(fuckniggers, timerSettings, assNigger);
    }

    final BooleanSetting fuckniggers = new BooleanSetting("Strafe", false);
    final NumberSettings timerSettings = new NumberSettings("Timer", 1f, 0.1f, 2f, 0.1f );

    final ModeSetting assNigger = new ModeSetting("Mode", "Hypixel", "Hypixel", "AAC");

    private int ticksInAir;

    @Override
    public void onEnable() {
        System.out.println("Speed enabled");
        if(Spring.INSTANCE.getManager().getModule("Sprint").isToggled()){
            Spring.INSTANCE.getManager().getModule("Sprint").setToggled(false);
        }
        mc.timer.timerSpeed = timerSettings.getValue();

        super.onEnable();
    }

    @Override
    public void onDisable() {
        System.out.println("Speed disabled");
        if(!Spring.INSTANCE.getManager().getModule("Sprint").isToggled()){
            Spring.INSTANCE.getManager().getModule("Sprint").setToggled(true);
        }
        mc.timer.timerSpeed = 1f;
        super.onDisable();
    }

    @EventHandler(EventPriority.LOWEST)
    public final Listener<EventMotion> onUpdate = e -> {

        switch(assNigger.getMode()){
            case "Hypixel":
                if (mc.thePlayer.moveForward != 0) {
                    if (mc.thePlayer.onGround) {
                        ticksInAir = 0;
                        if (mc.thePlayer.posY != Math.floor(mc.thePlayer.posY) + 0.42) {
                            mc.thePlayer.jump();
                            MoveUtil.setSpeed(MoveUtil.getSpeed() * .82f);
                        }
                    }
                    else {
                        mc.thePlayer.setSprinting(true);
                        ticksInAir++;
                        if (mc.thePlayer.isInLava() || mc.thePlayer.isInWater())
                            return;
                        float spread = 0.37f;
                        MoveUtil.strafe();
                    }
                }
                break;
            case "AAC":
                mc.thePlayer.jumpMovementFactor = 0.055f;
                MoveUtil.strafe();
                if(mc.thePlayer.onGround && MoveUtil.isMoving()){
                    mc.thePlayer.jump();
                }
        }
    };
}
