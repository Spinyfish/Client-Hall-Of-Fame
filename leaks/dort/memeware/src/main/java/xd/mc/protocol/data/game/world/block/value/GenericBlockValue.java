package niggerlib.mc.protocol.data.game.world.block.value;

import niggerlib.mc.protocol.util.ObjectUtil;

public class GenericBlockValue implements BlockValue {
    private int value;

    public GenericBlockValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GenericBlockValue)) return false;

        GenericBlockValue that = (GenericBlockValue) o;
        return this.value == that.value;
    }

    @Override
    public int hashCode() {
        return ObjectUtil.hashCode(this.value);
    }

    @Override
    public String toString() {
        return ObjectUtil.toString(this);
    }
}
