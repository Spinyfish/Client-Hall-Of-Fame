package club.marsh.bloom.api.value;

import lombok.Getter;
import lombok.Setter;

import java.util.function.Supplier;

public class Value<T> {
    public Value(boolean iscoolkid) {

    }
    @Getter@Setter
    public String name;
    public String hitboxname = name;
    public Supplier<Boolean> visible;


    public boolean isVisible() {
        return visible.get();
    }


    //apple needed me to make these for settings
    public boolean isCombo() {
        return this instanceof ModeValue;
    }
    public boolean isSlider() {
        return this instanceof NumberValue;
    }
    public boolean isCheck() {
        return this instanceof BooleanValue;
    }

    public String getValString() {

        if (isCombo()) {
            ModeValue val = (ModeValue) (this);
            return val.mode;
        }
        return null;
    }
    public double getValDouble() {
        NumberValue val = (NumberValue) (this);
        return val.value.doubleValue();
    }
}
