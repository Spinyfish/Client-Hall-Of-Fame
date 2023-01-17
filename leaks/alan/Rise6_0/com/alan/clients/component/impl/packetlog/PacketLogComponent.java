package com.alan.clients.component.impl.packetlog;

import com.alan.clients.api.Rise;
import com.alan.clients.component.Component;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.other.ServerJoinEvent;
import com.alan.clients.newevent.impl.other.WorldChangeEvent;

@Rise
public class PacketLogComponent extends Component  {

    private int worldChanges;

    @EventLink()
    public final Listener<WorldChangeEvent> onWorldChange = event -> {
        worldChanges++;
    };

    @EventLink()
    public final Listener<ServerJoinEvent> onServerJoin = event -> {
        worldChanges = 0;
    };

    public boolean hasChangedWorlds() {
        return worldChanges > 0;
    }
}