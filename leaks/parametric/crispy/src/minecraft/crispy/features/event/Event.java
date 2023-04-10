package crispy.features.event;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Event<T> {
    @Setter
    private boolean cancelled;
    @Setter
    private EventType type;


    public boolean isPre() {
        if (type == null)
            return false;
        return type == EventType.PRE;
    }

    public boolean isPost() {
        if (type == null)
            return false;
        return type == EventType.POST;
    }
}
