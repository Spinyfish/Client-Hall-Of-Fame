package club.marsh.bloom.api.value;



import lombok.Getter;

import java.util.function.Supplier;

public class ModeValue extends Value<ModeValue> {

    public String[] modes;
    @Getter
    public String mode;
    public int modeIndex = 0;
    public ModeValue(String name, String mode, String[] modes, Supplier<Boolean> visible) {
        super(false);
        this.visible = visible;
        this.mode = mode;
        this.name = name;
        this.modes = modes;
        this.hitboxname = name + ":" + mode;
    }

    public ModeValue(String name, String mode, String[] modes) {
        super(false);
        this.visible = () -> true;
        this.mode = mode;
        this.name = name;
        this.modes = modes;
        this.hitboxname = name + ":" + mode;
    }


    public void cycle() {
        int max = modes.length;
        if (modeIndex + 1 >= max) {
            modeIndex = 0;
        } else {
            modeIndex++;
        }
        mode = modes[modeIndex];
        this.hitboxname = name + ":" + mode;
    }
    public void cycleReverse() {
        int max = modes.length;
        if (modeIndex - 1 < 0) {
            modeIndex = max-1;
        } else {
            modeIndex--;
        }
        mode = modes[modeIndex];
        this.hitboxname = name + ":" + mode;
    }
}
