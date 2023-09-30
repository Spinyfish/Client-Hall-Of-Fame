/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.ui.primitive;

import java.util.Arrays;

public enum Click {
    LEFT(0),
    RIGHT(1),
    MIDDLE(2),
    SIDE_ONE(3),
    SIDE_TWO(4);

    private final int button;

    private Click(int button) {
        this.button = button;
    }

    public int getButton() {
        return this.button;
    }

    public static Click getClick(int in) {
        return Arrays.stream(Click.values()).filter(c -> c.getButton() == in).findFirst().orElse(null);
    }
}

