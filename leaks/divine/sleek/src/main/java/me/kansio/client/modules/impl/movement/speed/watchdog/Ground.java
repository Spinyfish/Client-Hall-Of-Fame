package me.kansio.client.modules.impl.movement.speed.watchdog;

import com.google.common.eventbus.Subscribe;
import me.kansio.client.event.impl.MoveEvent;
import me.kansio.client.event.impl.UpdateEvent;
import me.kansio.client.modules.impl.Module;
import me.kansio.client.modules.impl.movement.speed.SpeedMode;
import me.kansio.client.utils.player.PlayerUtil;

public class Ground extends SpeedMode {

    private double moveSpeed;
    private double lastDist;
    private int stage;

    public Ground() {
        super("Watchdog (Ground)");
    }

    @Override
    public void onEnable() {
        this.moveSpeed = PlayerUtil.getSpeed() / 1.54;
        this.lastDist = 0.0;
        this.stage = 4;
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onMove(MoveEvent event) {
        if (!mc.thePlayer.onGround) {
            return;
        }

        switch (this.stage) {
            case 1: {
                this.moveSpeed = 0.459;
                break;
            }
            case 2: {
                this.moveSpeed = 0.46581;
                break;
            }
            default: {
                this.moveSpeed = PlayerUtil.getSpeed() / 1.59;
                break;
            }
        }
        PlayerUtil.setMotion(moveSpeed);
    }

    @Override
    public void onUpdate(UpdateEvent event) {
        if (!mc.thePlayer.onGround) {
            return;
        }

        switch (this.stage) {
            case 1: {
                ++this.stage;
                break;
            }
            case 2: {
                ++this.stage;
                break;
            }
            default: {
                this.stage = 1;
                if (mc.thePlayer.isMoving() && !mc.gameSettings.keyBindJump.isPressed()) {
                    this.stage = 1;
                    break;
                }
                this.moveSpeed = PlayerUtil.getSpeed() / 1.54;
                break;
            }
        }
    }
}
