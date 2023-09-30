package dev.africa.pandaware.impl.module.combat.criticals.modes;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.player.MotionEvent;
import dev.africa.pandaware.impl.module.combat.criticals.CriticalsModule;
import dev.africa.pandaware.impl.module.combat.criticals.ICriticalsMode;
import dev.africa.pandaware.impl.module.movement.flight.FlightModule;
import dev.africa.pandaware.utils.player.PlayerUtils;

public class VerusCriticals extends ModuleMode<CriticalsModule> implements ICriticalsMode {
    public VerusCriticals(String name, CriticalsModule parent) {
        super(name, parent);
    }

    private int stage;

    @Override
    public void onEnable() {
        this.stage = 0;
    }

    @Override
    public void handle(MotionEvent event, int ticksExisted) {
        if (Client.getInstance().getModuleManager().getByClass(FlightModule.class).getData().isEnabled()) return;
        double yGround = mc.thePlayer.posY % 0.015625;
        if (this.isOnGround() && yGround >= 0 && yGround < .1 && !PlayerUtils.isBlockAbove(1)) {

            switch (stage++) {
                case 2:
                case 1: {
                    event.setOnGround(false);
                    event.setY(event.getY() + 0.419999986886978);
                    break;
                }

                case 4:
                case 3: {
                    event.setOnGround(false);
                    event.setY(event.getY() + 0.341599985361099);
                    break;
                }

                case 6:
                case 5: {
                    event.setOnGround(false);
                    event.setY(event.getY() + 0.186367980844497);
                    break;
                }
            }

            this.stage++;

            if (this.stage >= 9) {
                this.stage = 0;
            }
        } else {
            this.stage = 0;
        }
    }

    @Override
    public void entityIsNull() {
        if (stage != 0) {
            this.stage++;
        }
    }

    private boolean isOnGround() {
        return !mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer,
                mc.thePlayer.getEntityBoundingBox().offset(0.0D, -0.1, 0.0D)).isEmpty();
    }
}
