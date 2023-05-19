package marshscript.events;

import club.marsh.bloom.impl.events.UpdateEvent;
import lombok.Getter;
import lombok.Setter;

public class ScriptUpdateEvent {
    UpdateEvent eventUpdate;
    @Getter
    @Setter
    boolean cancelled,pre,post,ground;
    @Getter@Setter
    public double x,y,z;
    public ScriptUpdateEvent(UpdateEvent e) {
        this.eventUpdate = e;
        this.x=e.x;this.y=e.y;this.z=e.z;this.ground=e.isGround();
    }
    public void applyevent() {
        eventUpdate.x=this.x;
        eventUpdate.y=this.y;
        eventUpdate.z=this.z;
        eventUpdate.setGround(this.ground);
    }
}
