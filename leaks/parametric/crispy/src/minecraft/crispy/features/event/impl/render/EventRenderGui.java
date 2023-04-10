package crispy.features.event.impl.render;

import crispy.features.event.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.gui.ScaledResolution;

@RequiredArgsConstructor
@Getter
public class EventRenderGui extends Event<EventRenderGui> {


    private final ScaledResolution scaledResolution;

}
