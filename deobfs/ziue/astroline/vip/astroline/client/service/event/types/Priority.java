/*
 * Decompiled with CFR 0.152.
 */
package vip.astroline.client.service.event.types;

public class Priority {
    public static final byte FIRST = 0;
    public static final byte SECOND;
    public static final byte THIRD;
    public static final byte FOURTH;
    public static final byte FIFTH;
    public static final byte[] VALUE_ARRAY;

    static {
        FIFTH = (byte)4;
        FOURTH = (byte)3;
        SECOND = 1;
        THIRD = (byte)2;
        VALUE_ARRAY = new byte[]{0, 1, 2, 3, 4};
    }
}
