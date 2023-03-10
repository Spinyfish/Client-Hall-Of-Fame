/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.chunks;

import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectOpenHashMap;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.IntTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import java.util.Arrays;
import java.util.List;

public class FakeTileEntity {
    private static final Int2ObjectMap<CompoundTag> tileEntities = new Int2ObjectOpenHashMap<CompoundTag>();

    private static void register(int material, String name) {
        CompoundTag comp = new CompoundTag();
        comp.put(name, new StringTag());
        tileEntities.put(material, comp);
    }

    private static void register(List<Integer> materials, String name) {
        for (int id : materials) {
            FakeTileEntity.register(id, name);
        }
    }

    public static boolean hasBlock(int block) {
        return tileEntities.containsKey(block);
    }

    public static CompoundTag getFromBlock(int x, int y, int z, int block) {
        CompoundTag originalTag = (CompoundTag)tileEntities.get(block);
        if (originalTag != null) {
            CompoundTag tag = originalTag.clone();
            tag.put("x", new IntTag(x));
            tag.put("y", new IntTag(y));
            tag.put("z", new IntTag(z));
            return tag;
        }
        return null;
    }

    static {
        FakeTileEntity.register(Arrays.asList(61, 62), "Furnace");
        FakeTileEntity.register(Arrays.asList(54, 146), "Chest");
        FakeTileEntity.register(130, "EnderChest");
        FakeTileEntity.register(84, "RecordPlayer");
        FakeTileEntity.register(23, "Trap");
        FakeTileEntity.register(158, "Dropper");
        FakeTileEntity.register(Arrays.asList(63, 68), "Sign");
        FakeTileEntity.register(52, "MobSpawner");
        FakeTileEntity.register(25, "Music");
        FakeTileEntity.register(Arrays.asList(33, 34, 29, 36), "Piston");
        FakeTileEntity.register(117, "Cauldron");
        FakeTileEntity.register(116, "EnchantTable");
        FakeTileEntity.register(Arrays.asList(119, 120), "Airportal");
        FakeTileEntity.register(138, "Beacon");
        FakeTileEntity.register(144, "Skull");
        FakeTileEntity.register(Arrays.asList(178, 151), "DLDetector");
        FakeTileEntity.register(154, "Hopper");
        FakeTileEntity.register(Arrays.asList(149, 150), "Comparator");
        FakeTileEntity.register(140, "FlowerPot");
        FakeTileEntity.register(Arrays.asList(176, 177), "Banner");
        FakeTileEntity.register(209, "EndGateway");
        FakeTileEntity.register(137, "Control");
    }
}

