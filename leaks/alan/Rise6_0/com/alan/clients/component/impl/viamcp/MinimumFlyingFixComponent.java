package com.alan.clients.component.impl.viamcp;

import com.alan.clients.component.Component;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.MinimumFlyingEvent;
import com.alan.clients.util.player.MoveUtil;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.viamcp.ViaMCP;

public final class MinimumFlyingFixComponent extends Component {

    @EventLink()
    public final Listener<MinimumFlyingEvent> onMinimumFlying = event -> {

//        /* This isn't a great way of fixing some detection methods but cope */
        if (ViaMCP.getInstance().getVersion() > ProtocolVersion.v1_8.getVersion()) {
            event.setMinimum(MoveUtil.isMoving() || mc.gameSettings.keyBindJump.isKeyDown() ? 0 : 0.03);
        }
    };
}
