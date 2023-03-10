/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viabackwards.protocol.protocol1_11_1to1_12.data;

import com.viaversion.viaversion.libs.fastutil.ints.Int2IntMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2IntOpenHashMap;

public class MapColorMapping {
    private static final Int2IntMap MAPPING = new Int2IntOpenHashMap(64, 0.99f);

    public static int getNearestOldColor(int color) {
        return MAPPING.getOrDefault(color, color);
    }

    static {
        MAPPING.defaultReturnValue(-1);
        MAPPING.put(144, 59);
        MAPPING.put(145, 56);
        MAPPING.put(146, 56);
        MAPPING.put(147, 45);
        MAPPING.put(148, 63);
        MAPPING.put(149, 60);
        MAPPING.put(150, 60);
        MAPPING.put(151, 136);
        MAPPING.put(152, 83);
        MAPPING.put(153, 83);
        MAPPING.put(154, 80);
        MAPPING.put(155, 115);
        MAPPING.put(156, 39);
        MAPPING.put(157, 39);
        MAPPING.put(158, 36);
        MAPPING.put(159, 47);
        MAPPING.put(160, 60);
        MAPPING.put(161, 61);
        MAPPING.put(162, 62);
        MAPPING.put(163, 137);
        MAPPING.put(164, 108);
        MAPPING.put(165, 108);
        MAPPING.put(166, 109);
        MAPPING.put(167, 111);
        MAPPING.put(168, 112);
        MAPPING.put(169, 113);
        MAPPING.put(170, 114);
        MAPPING.put(171, 115);
        MAPPING.put(172, 118);
        MAPPING.put(173, 107);
        MAPPING.put(174, 107);
        MAPPING.put(175, 118);
        MAPPING.put(176, 91);
        MAPPING.put(177, 45);
        MAPPING.put(178, 46);
        MAPPING.put(179, 47);
        MAPPING.put(180, 85);
        MAPPING.put(181, 44);
        MAPPING.put(182, 27);
        MAPPING.put(183, 84);
        MAPPING.put(184, 83);
        MAPPING.put(185, 83);
        MAPPING.put(186, 83);
        MAPPING.put(187, 84);
        MAPPING.put(188, 84);
        MAPPING.put(189, 71);
        MAPPING.put(190, 71);
        MAPPING.put(191, 87);
        MAPPING.put(192, 107);
        MAPPING.put(193, 139);
        MAPPING.put(194, 43);
        MAPPING.put(195, 107);
        MAPPING.put(196, 111);
        MAPPING.put(197, 111);
        MAPPING.put(198, 111);
        MAPPING.put(199, 107);
        MAPPING.put(200, 112);
        MAPPING.put(201, 113);
        MAPPING.put(202, 113);
        MAPPING.put(203, 115);
        MAPPING.put(204, 116);
        MAPPING.put(205, 117);
        MAPPING.put(206, 107);
        MAPPING.put(207, 119);
    }
}

