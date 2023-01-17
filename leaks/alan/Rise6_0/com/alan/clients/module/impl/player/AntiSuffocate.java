package com.alan.clients.module.impl.player;

import com.alan.clients.api.Rise;
import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.value.impl.BooleanValue;
import com.alan.clients.util.packet.PacketUtil;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

@Rise
@ModuleInfo(name = "Anti Suffocate", description = "module.player.antifsuffocate.description", category = Category.PLAYER)
public class AntiSuffocate extends Module {

    private final BooleanValue swing = new BooleanValue("Swing", this, true);


    @EventLink()
    public final Listener<PreMotionEvent> onPreMotionEvent = event -> {

        if (mc.thePlayer.isEntityInsideOpaqueBlock()) {
            PacketUtil.send(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, new BlockPos(mc.thePlayer).down(), EnumFacing.UP));

            if (swing.getValue()) {
                mc.thePlayer.swingItem();
            }
        }
    };
}