package club.marsh.bloom.impl.events;

import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class ClickMouseEvent {
    public int clickType;
    public ClickMouseEvent(int clickType) {
        this.clickType = clickType;
    }
}
