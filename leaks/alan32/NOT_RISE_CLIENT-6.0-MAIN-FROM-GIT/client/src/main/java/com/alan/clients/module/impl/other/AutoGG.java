package com.alan.clients.module.impl.other;

import com.alan.clients.api.Rise;
import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.newevent.impl.other.WorldChangeEvent;
import com.alan.clients.value.impl.StringValue;

@Rise
@ModuleInfo(name = "module.other.autogg.name", description = "module.other.autogg.description", category = Category.OTHER)
public final class AutoGG extends Module {
    private StringValue message = new StringValue("Message", this, "Why waste another game without Rise?");
    private boolean active;

    @EventLink()
    public final Listener<PreMotionEvent> onPreMotionEvent = event -> {
        if (mc.thePlayer.ticksExisted % 18 != 0 || mc.thePlayer.ticksExisted < 20 * 20 || !active || !mc.thePlayer.sendQueue.doneLoadingTerrain) return;

        if (mc.theWorld.playerEntities.stream().filter(entityPlayer -> !entityPlayer.isInvisible()).count() <= 1) {
            active = false;

            mc.thePlayer.sendChatMessage(message.getValue());
        }
    };

    @EventLink()
    public final Listener<WorldChangeEvent> onWorldChange = event -> {
        active = true;
    };
}
