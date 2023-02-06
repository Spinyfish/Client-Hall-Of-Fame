package dev.elysium.client.mods.impl.player;

import dev.elysium.base.events.types.EventTarget;
import dev.elysium.base.mods.Category;
import dev.elysium.base.mods.Mod;
import dev.elysium.client.events.EventUpdate;
import net.minecraft.block.BlockAir;
import net.minecraft.util.BlockPos;

public class SumoWalls extends Mod {
    public SumoWalls() {
        super("SumoWalls","Prevents you from falling by creating walls", Category.PLAYER);
    }

    @EventTarget
    public void onEventUpdate(EventUpdate e) {
        if(mc.thePlayer.ticksExisted < 10 || !mc.thePlayer.isMoving() && mc.thePlayer.onGround) {
            mc.theWorld.fences.clear();
        }

        for(int x = -2; x <= 2; x++) {
            for(int z = -2; z <= 2; z++) {
                boolean setIt = true;
                for(double y = 0; y >= -3; y--) {
                    if(!(new BlockPos(mc.thePlayer.posX + x, mc.thePlayer.posY + y, mc.thePlayer.posZ + z).getBlock() instanceof BlockAir)) {
                        setIt = false;
                    }
                }
                if(setIt) {
                    mc.theWorld.fences.add(new BlockPos(mc.thePlayer.posX + x, mc.thePlayer.posY + 1, mc.thePlayer.posZ + z));
                }
            }
        }
    }
}
