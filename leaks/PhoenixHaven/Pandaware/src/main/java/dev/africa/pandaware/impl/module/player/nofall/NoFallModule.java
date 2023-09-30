package dev.africa.pandaware.impl.module.player.nofall;

import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.interfaces.Category;
import dev.africa.pandaware.api.module.interfaces.ModuleInfo;
import dev.africa.pandaware.impl.module.player.nofall.modes.*;
import dev.africa.pandaware.utils.player.PlayerUtils;

@ModuleInfo(name = "No Fall", category = Category.PLAYER)
public class NoFallModule extends Module {
    public NoFallModule() {
        this.registerModes(
                new GroundNoFall("Ground", this),
                new CollideNoFall("Collide", this),
                new BlinkNoFall("Blink", this),
                new GlitchNoFall("Glitch", this),
                new NoGroundNoFall("No Ground", this),
                new PacketNoFall("Packet", this),
                new PositionRoundNoFall("Position Round", this),
                new ReduceNoFall("Reduce", this),
                new VulcanNoFall("Vulcan", this)
        );
    }

    public boolean canFall() {
        return mc.thePlayer.isEntityAlive() && mc.theWorld != null && !mc.thePlayer.isInWater() && !mc.thePlayer.isInLava()
                && PlayerUtils.isBlockUnderNoCollisions();
    }

    @Override
    public String getSuffix() {
        return this.getCurrentMode().getName();
    }
}
