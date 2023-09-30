package dev.africa.pandaware.impl.module.movement.flight;

import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.interfaces.Category;
import dev.africa.pandaware.api.module.interfaces.ModuleInfo;
import dev.africa.pandaware.impl.module.movement.flight.modes.*;
import dev.africa.pandaware.utils.player.MovementUtils;
import lombok.Getter;

@Getter
@ModuleInfo(name = "Flight", category = Category.MOVEMENT)
public class FlightModule extends Module {

    public FlightModule() {
        this.registerModes(
                new MotionFlight("Motion", this),
                new VerusFlight("Verus", this),
                new CollideFlight("Collide", this),
                new FuncraftFlight("Funcraft", this),
                new PacketFlight("Packet", this),
                new VulcanFlight("Vulcan", this),
                new AAC3Flight("AAC3", this),
                new MineboxFlight("Minebox", this),
                new DamageFlight("Damage", this),
                new BlockdropFlight("Blockdrop", this),
                new ZonecraftFlight("Zonecraft", this)
        );
    }

    @Override
    public void onDisable() {
        if (!(this.getCurrentMode() instanceof CollideFlight)) {
            MovementUtils.slowdown();
        }

        mc.timer.timerSpeed = 1f;
    }

    @Override
    public String getSuffix() {
        String add = this.getCurrentMode().getInformationSuffix() != null ? " "
                + this.getCurrentMode().getInformationSuffix() : "";

        return this.getCurrentMode().getName() + add;
    }
}
