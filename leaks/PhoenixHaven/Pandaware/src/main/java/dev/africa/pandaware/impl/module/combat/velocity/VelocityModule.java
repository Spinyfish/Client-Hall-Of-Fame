package dev.africa.pandaware.impl.module.combat.velocity;


import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.interfaces.Category;
import dev.africa.pandaware.api.module.interfaces.ModuleInfo;
import dev.africa.pandaware.impl.module.combat.velocity.modes.*;

@ModuleInfo(name = "Velocity", category = Category.COMBAT)
public class VelocityModule extends Module {
    public VelocityModule() {
        this.registerModes(
                new PacketVelocity("Packet", this),
                new GroundVelocity("Ground", this),
                new TickVelocity("Tick", this),
                new GlitchVelocity("Glitch", this),
                new VulcanVelocity("Vulcan", this),
                new ReverseVelocity("Reverse", this)
        );
    }

    @Override
    public String getSuffix() {
        return this.getCurrentMode().getName();
    }
}
