package com.alan.clients.module.impl.other;

import com.alan.clients.api.Rise;
import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.newevent.impl.other.WorldChangeEvent;
import com.alan.clients.util.chat.ChatUtil;
import com.alan.clients.util.player.PlayerUtil;
import net.minecraft.entity.player.EntityPlayer;

@Rise
@ModuleInfo(name = "Murder Mystery", description = "module.other.murdermystery.description", category = Category.OTHER)
public final class MurderMystery extends Module {

    private EntityPlayer murderer;

    @EventLink()
    public final Listener<PreMotionEvent> onPreMotionEvent = event -> {
        // no need to waste performance so every second tick is enough
        if (mc.thePlayer.ticksExisted % 2 == 0 || this.murderer != null) {
            return;
        }

        for (EntityPlayer player : mc.theWorld.playerEntities) {
            if (player.getHeldItem() != null) {
                if (player.getHeldItem().getDisplayName().contains("Knife")) { // TODO: add other languages
                    ChatUtil.display(PlayerUtil.name(player) + " is The Murderer.");
                    this.murderer = player;
                }
            }
        }
    };

    @EventLink()
    public final Listener<WorldChangeEvent> onWorldChange = event -> this.murderer = null;
}
