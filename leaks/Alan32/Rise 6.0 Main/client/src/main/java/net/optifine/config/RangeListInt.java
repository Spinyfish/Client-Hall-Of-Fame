package net.optifine.config;

import net.minecraft.src.Config;

public class RangeListInt {
    private RangeInt[] ranges = new RangeInt[0];

    public RangeListInt() {
    }

    public RangeListInt(final RangeInt ri) {
        this.addRange(ri);
    }

    public void addRange(final RangeInt ri) {
        this.ranges = (RangeInt[]) Config.addObjectToArray(this.ranges, ri);
    }

    public boolean isInRange(final int val) {
        for (int i = 0; i < this.ranges.length; ++i) {
            final RangeInt rangeint = this.ranges[i];

            if (rangeint.isInRange(val)) {
                return true;
            }
        }

        return false;
    }

    public int getCountRanges() {
        return this.ranges.length;
    }

    public RangeInt getRange(final int i) {
        return this.ranges[i];
    }

    public String toString() {
        final StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("[");

        for (int i = 0; i < this.ranges.length; ++i) {
            final RangeInt rangeint = this.ranges[i];

            if (i > 0) {
                stringbuffer.append(", ");
            }

            stringbuffer.append(rangeint.toString());
        }

        stringbuffer.append("]");
        return stringbuffer.toString();
    }
}
