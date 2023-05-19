package club.marsh.bloom.api.value;


import lombok.Getter;
import lombok.Setter;

import java.util.function.Supplier;

public class BooleanValue extends Value<BooleanValue> {
    @Getter
    @Setter
    private boolean on;


    public boolean getObject() {
        return on;
    }

    public BooleanValue(String name, boolean on, Supplier<Boolean> visible) {
        super(false);
        this.visible = visible;
        this.on = on;
        this.name = name;
        this.hitboxname = name + ":" + on;
    }

    public BooleanValue(String name, boolean on) {
        super(false);
        this.visible = () -> true;
        this.on = on;
        this.name = name;
        this.hitboxname = name + ":" + on;
    }

    public void flip() {
        on = !on;
        this.hitboxname = name + ":" + on;
    }


}
