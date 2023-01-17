package com.alan.clients.component.impl.viamcp;

import com.alan.clients.component.Component;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.render.MouseOverEvent;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.viamcp.ViaMCP;

public final class HitboxFixComponent extends Component {

    @EventLink()
    public final Listener<MouseOverEvent> onMouseOver = event -> {

        if (ViaMCP.getInstance().getVersion() > ProtocolVersion.v1_8.getVersion()) {
            event.setExpand(event.getExpand() - 0.1f);
        }
    };
}
