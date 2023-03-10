/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.protocols.protocol1_13to1_12_2.providers.blockentities;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.providers.BlockEntityProvider;
import com.viaversion.viaversion.util.Pair;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FlowerPotHandler
implements BlockEntityProvider.BlockEntityHandler {
    private static final Map<Pair<?, Byte>, Integer> flowers = new ConcurrentHashMap();

    public static void register(String identifier, byte numbericBlockId, byte blockData, int newId) {
        flowers.put(new Pair<String, Byte>(identifier, blockData), newId);
        flowers.put(new Pair<Byte, Byte>(numbericBlockId, blockData), newId);
    }

    @Override
    public int transform(UserConnection user, CompoundTag tag) {
        Object data;
        Object item = tag.contains("Item") ? ((Tag)tag.get("Item")).getValue() : null;
        Object object = data = tag.contains("Data") ? ((Tag)tag.get("Data")).getValue() : null;
        Integer flower = flowers.get(new Pair<Object, Byte>(item = item instanceof String ? ((String)item).replace("minecraft:", "") : (item instanceof Number ? Byte.valueOf(((Number)item).byteValue()) : Byte.valueOf((byte)0)), (Byte)(data = data instanceof Number ? Byte.valueOf(((Number)data).byteValue()) : Byte.valueOf((byte)0))));
        if (flower != null) {
            return flower;
        }
        flower = flowers.get(new Pair<Object, Byte>(item, (byte)0));
        if (flower != null) {
            return flower;
        }
        return 5265;
    }

    static {
        FlowerPotHandler.register("air", (byte)0, (byte)0, 5265);
        FlowerPotHandler.register("sapling", (byte)6, (byte)0, 5266);
        FlowerPotHandler.register("sapling", (byte)6, (byte)1, 5267);
        FlowerPotHandler.register("sapling", (byte)6, (byte)2, 5268);
        FlowerPotHandler.register("sapling", (byte)6, (byte)3, 5269);
        FlowerPotHandler.register("sapling", (byte)6, (byte)4, 5270);
        FlowerPotHandler.register("sapling", (byte)6, (byte)5, 5271);
        FlowerPotHandler.register("tallgrass", (byte)31, (byte)2, 5272);
        FlowerPotHandler.register("yellow_flower", (byte)37, (byte)0, 5273);
        FlowerPotHandler.register("red_flower", (byte)38, (byte)0, 5274);
        FlowerPotHandler.register("red_flower", (byte)38, (byte)1, 5275);
        FlowerPotHandler.register("red_flower", (byte)38, (byte)2, 5276);
        FlowerPotHandler.register("red_flower", (byte)38, (byte)3, 5277);
        FlowerPotHandler.register("red_flower", (byte)38, (byte)4, 5278);
        FlowerPotHandler.register("red_flower", (byte)38, (byte)5, 5279);
        FlowerPotHandler.register("red_flower", (byte)38, (byte)6, 5280);
        FlowerPotHandler.register("red_flower", (byte)38, (byte)7, 5281);
        FlowerPotHandler.register("red_flower", (byte)38, (byte)8, 5282);
        FlowerPotHandler.register("red_mushroom", (byte)40, (byte)0, 5283);
        FlowerPotHandler.register("brown_mushroom", (byte)39, (byte)0, 5284);
        FlowerPotHandler.register("deadbush", (byte)32, (byte)0, 5285);
        FlowerPotHandler.register("cactus", (byte)81, (byte)0, 5286);
    }
}

