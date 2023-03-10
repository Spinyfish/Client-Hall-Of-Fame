/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.protocols.protocol1_13to1_12_2.data;

import com.viaversion.viaversion.libs.fastutil.ints.Int2IntMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2IntOpenHashMap;

public class EntityTypeRewriter {
    private static final Int2IntMap ENTITY_TYPES = new Int2IntOpenHashMap(83, 0.99f);

    private static void registerEntity(int type1_12, int type1_13) {
        ENTITY_TYPES.put(type1_12, type1_13);
    }

    public static int getNewId(int type1_12) {
        return ENTITY_TYPES.getOrDefault(type1_12, type1_12);
    }

    static {
        ENTITY_TYPES.defaultReturnValue(-1);
        EntityTypeRewriter.registerEntity(1, 32);
        EntityTypeRewriter.registerEntity(2, 22);
        EntityTypeRewriter.registerEntity(3, 0);
        EntityTypeRewriter.registerEntity(4, 15);
        EntityTypeRewriter.registerEntity(5, 84);
        EntityTypeRewriter.registerEntity(6, 71);
        EntityTypeRewriter.registerEntity(7, 74);
        EntityTypeRewriter.registerEntity(8, 35);
        EntityTypeRewriter.registerEntity(9, 49);
        EntityTypeRewriter.registerEntity(10, 2);
        EntityTypeRewriter.registerEntity(11, 67);
        EntityTypeRewriter.registerEntity(12, 34);
        EntityTypeRewriter.registerEntity(13, 65);
        EntityTypeRewriter.registerEntity(14, 75);
        EntityTypeRewriter.registerEntity(15, 23);
        EntityTypeRewriter.registerEntity(16, 77);
        EntityTypeRewriter.registerEntity(17, 76);
        EntityTypeRewriter.registerEntity(18, 33);
        EntityTypeRewriter.registerEntity(19, 85);
        EntityTypeRewriter.registerEntity(20, 55);
        EntityTypeRewriter.registerEntity(21, 24);
        EntityTypeRewriter.registerEntity(22, 25);
        EntityTypeRewriter.registerEntity(23, 30);
        EntityTypeRewriter.registerEntity(24, 68);
        EntityTypeRewriter.registerEntity(25, 60);
        EntityTypeRewriter.registerEntity(26, 13);
        EntityTypeRewriter.registerEntity(27, 89);
        EntityTypeRewriter.registerEntity(28, 63);
        EntityTypeRewriter.registerEntity(29, 88);
        EntityTypeRewriter.registerEntity(30, 1);
        EntityTypeRewriter.registerEntity(31, 11);
        EntityTypeRewriter.registerEntity(32, 46);
        EntityTypeRewriter.registerEntity(33, 20);
        EntityTypeRewriter.registerEntity(34, 21);
        EntityTypeRewriter.registerEntity(35, 78);
        EntityTypeRewriter.registerEntity(36, 81);
        EntityTypeRewriter.registerEntity(37, 31);
        EntityTypeRewriter.registerEntity(40, 41);
        EntityTypeRewriter.registerEntity(41, 5);
        EntityTypeRewriter.registerEntity(42, 39);
        EntityTypeRewriter.registerEntity(43, 40);
        EntityTypeRewriter.registerEntity(44, 42);
        EntityTypeRewriter.registerEntity(45, 45);
        EntityTypeRewriter.registerEntity(46, 43);
        EntityTypeRewriter.registerEntity(47, 44);
        EntityTypeRewriter.registerEntity(50, 10);
        EntityTypeRewriter.registerEntity(51, 62);
        EntityTypeRewriter.registerEntity(52, 69);
        EntityTypeRewriter.registerEntity(53, 27);
        EntityTypeRewriter.registerEntity(54, 87);
        EntityTypeRewriter.registerEntity(55, 64);
        EntityTypeRewriter.registerEntity(56, 26);
        EntityTypeRewriter.registerEntity(57, 53);
        EntityTypeRewriter.registerEntity(58, 18);
        EntityTypeRewriter.registerEntity(59, 6);
        EntityTypeRewriter.registerEntity(60, 61);
        EntityTypeRewriter.registerEntity(61, 4);
        EntityTypeRewriter.registerEntity(62, 38);
        EntityTypeRewriter.registerEntity(63, 17);
        EntityTypeRewriter.registerEntity(64, 83);
        EntityTypeRewriter.registerEntity(65, 3);
        EntityTypeRewriter.registerEntity(66, 82);
        EntityTypeRewriter.registerEntity(67, 19);
        EntityTypeRewriter.registerEntity(68, 28);
        EntityTypeRewriter.registerEntity(69, 59);
        EntityTypeRewriter.registerEntity(200, 16);
        EntityTypeRewriter.registerEntity(90, 51);
        EntityTypeRewriter.registerEntity(91, 58);
        EntityTypeRewriter.registerEntity(92, 9);
        EntityTypeRewriter.registerEntity(93, 7);
        EntityTypeRewriter.registerEntity(94, 70);
        EntityTypeRewriter.registerEntity(95, 86);
        EntityTypeRewriter.registerEntity(96, 47);
        EntityTypeRewriter.registerEntity(97, 66);
        EntityTypeRewriter.registerEntity(98, 48);
        EntityTypeRewriter.registerEntity(99, 80);
        EntityTypeRewriter.registerEntity(100, 29);
        EntityTypeRewriter.registerEntity(101, 56);
        EntityTypeRewriter.registerEntity(102, 54);
        EntityTypeRewriter.registerEntity(103, 36);
        EntityTypeRewriter.registerEntity(104, 37);
        EntityTypeRewriter.registerEntity(105, 50);
        EntityTypeRewriter.registerEntity(120, 79);
    }
}

