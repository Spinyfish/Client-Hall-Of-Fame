package wtf.automn.events.impl.visual;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import wtf.automn.events.events.Event;

@Data
public class EventRender3D implements Event {
    private float partialTicks;

    public EventRender3D(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return this.partialTicks;
    }


}
