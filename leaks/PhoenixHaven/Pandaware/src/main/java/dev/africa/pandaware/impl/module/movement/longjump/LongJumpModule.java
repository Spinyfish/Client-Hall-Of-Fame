package dev.africa.pandaware.impl.module.movement.longjump;

import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.interfaces.Category;
import dev.africa.pandaware.api.module.interfaces.ModuleInfo;
import dev.africa.pandaware.impl.event.player.MotionEvent;
import dev.africa.pandaware.impl.module.movement.longjump.modes.*;
import dev.africa.pandaware.impl.setting.BooleanSetting;
import dev.africa.pandaware.impl.setting.NumberSetting;
import dev.africa.pandaware.utils.player.MovementUtils;
import lombok.Getter;

@Getter
@ModuleInfo(name = "Long Jump", category = Category.MOVEMENT)
public class LongJumpModule extends Module {
    private final NumberSetting speed = new NumberSetting("Speed", 2.5, 0, 1, 0.05);
    private final BooleanSetting autoDisable = new BooleanSetting("Auto Disable", false);

    private boolean wasOnGround;
    private double airTicks;

    public LongJumpModule() {
        this.registerModes(
                new VanillaLongjump("Vanilla", this),
                new HypixelLongJump("Hypixel", this),
                new NCPLongJump("NCP", this),
                new FuncraftLongJump("Funcraft", this),
                new BowLongJump("Bow", this)
        );

        this.registerSettings(this.speed, this.autoDisable);
    }

    @EventHandler
    EventCallback<MotionEvent> onMotion = event -> {
        if (this.autoDisable.getValue()) {
            if (mc.thePlayer.getAirTicks() > 0) {
                airTicks++;
            }
            if (mc.thePlayer.onGround && wasOnGround && airTicks > 0) {
                this.toggle(false);
            } else if (mc.thePlayer.onGround) {
                wasOnGround = true;
            }
        }
    };

    @Override
    public void onDisable() {
        if (!(this.getCurrentMode() instanceof HypixelLongJump)) {
            MovementUtils.slowdown();
        }
        this.wasOnGround = false;
        airTicks = 0;
    }

    @Override
    public String getSuffix() {
        return this.getCurrentMode().getName();
    }
}
