package crispy.features.event.impl.render;

import crispy.features.event.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.entity.player.EntityPlayer;

@RequiredArgsConstructor
@Getter
public class EventUpdateModel extends Event<EventUpdateModel> {
    private final EntityPlayer player;
    private final ModelPlayer model;
    private final float partialTicks;

}
