package alos.stella.event.events;

import alos.stella.event.Event;
import net.minecraft.client.multiplayer.WorldClient;
import org.jetbrains.annotations.Nullable;

public final class WorldEvent extends Event {
    @Nullable
    private final WorldClient worldClient;

    @Nullable
    public final WorldClient getWorldClient() {
        return this.worldClient;
    }

    public WorldEvent(@Nullable WorldClient worldClient) {
        this.worldClient = worldClient;
    }
}
