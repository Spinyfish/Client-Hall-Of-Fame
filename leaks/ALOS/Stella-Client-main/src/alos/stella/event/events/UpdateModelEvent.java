package alos.stella.event.events;

import kotlin.jvm.internal.Intrinsics;
import alos.stella.event.Event;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.entity.player.EntityPlayer;
import org.jetbrains.annotations.NotNull;

public final class UpdateModelEvent extends Event {
    @NotNull
    private final EntityPlayer player;
    @NotNull
    private final ModelPlayer model;

    @NotNull
    public final EntityPlayer getPlayer() {
        return this.player;
    }

    @NotNull
    public final ModelPlayer getModel() {
        return this.model;
    }

    public UpdateModelEvent(@NotNull EntityPlayer player, @NotNull ModelPlayer model) {
        super();
        Intrinsics.checkNotNullParameter(player, "player");
        Intrinsics.checkNotNullParameter(model, "model");
        this.player = player;
        this.model = model;
    }
}
