package client.module.impl.movement;

import client.event.Listener;
import client.event.annotations.EventLink;
import client.event.impl.motion.MotionEvent;
import client.module.Category;
import client.module.Module;
import client.module.ModuleInfo;
import client.util.chat.ChatUtil;
import net.minecraft.client.entity.EntityPlayerSP;

@ModuleInfo(name = "FastLadder", description = "", category = Category.MOVEMENT, autoEnabled = false)
public class FastLadder extends Module {
    @EventLink
    public final Listener<MotionEvent> onMotion = event -> {
        final EntityPlayerSP player = mc.thePlayer;
        player.jump();

    };
    }

