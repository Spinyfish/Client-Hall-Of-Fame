package dev.africa.pandaware.impl.module.movement.flight.modes;

import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.player.MoveEvent;
import dev.africa.pandaware.impl.module.movement.flight.FlightModule;
import dev.africa.pandaware.impl.setting.BooleanSetting;
import dev.africa.pandaware.impl.setting.NumberSetting;
import dev.africa.pandaware.utils.math.TimeHelper;
import dev.africa.pandaware.utils.player.MovementUtils;
import dev.africa.pandaware.utils.player.PlayerUtils;
import net.minecraft.network.play.client.C03PacketPlayer;

public class MotionFlight extends ModuleMode<FlightModule> {
    private final NumberSetting speed = new NumberSetting("Speed", 10, 0.1, 1, 0.1);
    private final BooleanSetting kickBypass = new BooleanSetting("Kick Bypass", false);

    private final TimeHelper timer = new TimeHelper();

    public MotionFlight(String name, FlightModule parent) {
        super(name, parent);

        this.registerSettings(
                this.speed,
                this.kickBypass
        );
    }

    @EventHandler
    EventCallback<MoveEvent> onMove = event -> {
        event.y = mc.thePlayer.motionY = mc.gameSettings.keyBindJump.isKeyDown() ? this.speed.getValue().floatValue()
                : mc.gameSettings.keyBindSneak.isKeyDown() ? -this.speed.getValue().floatValue() : 0.0F;
        MovementUtils.strafe(event, this.speed.getValue().doubleValue());
        if (this.kickBypass.getValue() && timer.reach(1069)) {
            handleVanillaKickBypass();
        }
    };

    private void handleVanillaKickBypass() {
        double posY;
        double x = mc.thePlayer.posX;
        double y = mc.thePlayer.posY;
        double z = mc.thePlayer.posZ;
        double ground = PlayerUtils.findGround();
        for (posY = y; posY > ground; posY -= 8.0) {
            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, posY, z, true));
            if (posY - 8.0 < ground) break;
        }
        mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, ground, z, true));
        for (posY = ground; posY < y; posY += 8.0) {
            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, posY, z, true));
            if (posY + 8.0 > y) break;
        }
        mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, true));
        this.timer.reset();
    }
}
