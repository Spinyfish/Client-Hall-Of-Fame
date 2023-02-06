package dev.elysium.client.mods.impl.player;

import dev.elysium.base.events.types.EventTarget;
import dev.elysium.base.mods.Category;
import dev.elysium.base.mods.Mod;
import dev.elysium.base.mods.settings.BooleanSetting;
import dev.elysium.base.mods.settings.NumberSetting;
import dev.elysium.client.events.EventMotion;
import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;

public class Breaker extends Mod {

    public BlockPos respawnPoint;

    public NumberSetting range = new NumberSetting("Range",0,8,3,0.25,this);
    public BooleanSetting swing = new BooleanSetting("Swing",false,this);
    public BooleanSetting rotate = new BooleanSetting("Rotate",false,this);
    public BooleanSetting lockview = (BooleanSetting) new BooleanSetting("Lockview",false,this).setPredicate(mod -> rotate.isEnabled());

    public Breaker() {
        super("Breaker","Breaks blocks for you", Category.PLAYER);
    }

    @EventTarget
    public void onEventMotion(EventMotion e) {
        if(e.isPost())
            return;


    }


    public BlockPos getBlock(final Block b, final int reach) {
        BlockPos out;

        for (int x = -reach; x < reach; ++x) {
            for (int y = reach; y > -reach; --y) {
                for (int z = -reach; z < reach; ++z) {
                    BlockPos theblockinquestion = new BlockPos(mc.thePlayer.posX + x, mc.thePlayer.posY + y, mc.thePlayer.posZ + z);
                    if (theblockinquestion.getBlock() == b) {
                        return theblockinquestion;
                    }
                }
            }
        }
        return null;
    }
}
