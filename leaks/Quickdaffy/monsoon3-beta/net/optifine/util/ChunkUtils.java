/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.optifine.reflect.Reflector;
import net.optifine.reflect.ReflectorClass;
import net.optifine.reflect.ReflectorField;

public class ChunkUtils {
    private static ReflectorClass chunkClass = new ReflectorClass(Chunk.class);
    private static ReflectorField fieldHasEntities = ChunkUtils.findFieldHasEntities();
    private static ReflectorField fieldPrecipitationHeightMap = new ReflectorField(chunkClass, int[].class, 0);

    public static boolean hasEntities(Chunk chunk) {
        return Reflector.getFieldValueBoolean(chunk, fieldHasEntities, true);
    }

    public static int getPrecipitationHeight(Chunk chunk, BlockPos pos) {
        int[] aint = (int[])Reflector.getFieldValue(chunk, fieldPrecipitationHeightMap);
        if (aint != null && aint.length == 256) {
            int j;
            int i = pos.getX() & 0xF;
            int k = i | (j = pos.getZ() & 0xF) << 4;
            int l = aint[k];
            if (l >= 0) {
                return l;
            }
            BlockPos blockpos = chunk.getPrecipitationHeight(pos);
            return blockpos.getY();
        }
        return -1;
    }

    /*
     * WARNING - void declaration
     */
    private static ReflectorField findFieldHasEntities() {
        try {
            void var7_13;
            Chunk chunk = new Chunk(null, 0, 0);
            ArrayList<Object> list = new ArrayList<Object>();
            ArrayList<Object> list1 = new ArrayList<Object>();
            Field[] afield = Chunk.class.getDeclaredFields();
            for (int i = 0; i < afield.length; ++i) {
                Field field = afield[i];
                if (field.getType() != Boolean.TYPE) continue;
                field.setAccessible(true);
                list.add(field);
                list1.add(field.get(chunk));
            }
            chunk.setHasEntities(false);
            ArrayList<Object> list2 = new ArrayList<Object>();
            for (Object e : list) {
                Field field = (Field)e;
                list2.add(field.get(chunk));
            }
            chunk.setHasEntities(true);
            ArrayList<Object> list3 = new ArrayList<Object>();
            for (Object e : list) {
                Field field2 = (Field)e;
                list3.add(field2.get(chunk));
            }
            ArrayList<Field> list4 = new ArrayList<Field>();
            boolean bl = false;
            while (var7_13 < list.size()) {
                Field field3 = (Field)list.get((int)var7_13);
                Boolean obool = (Boolean)list2.get((int)var7_13);
                Boolean obool1 = (Boolean)list3.get((int)var7_13);
                if (!obool.booleanValue() && obool1.booleanValue()) {
                    list4.add(field3);
                    Boolean obool2 = (Boolean)list1.get((int)var7_13);
                    field3.set(chunk, obool2);
                }
                ++var7_13;
            }
            if (list4.size() == 1) {
                Field field = (Field)list4.get(0);
                return new ReflectorField(field);
            }
        }
        catch (Exception exception) {
            Config.warn(exception.getClass().getName() + " " + exception.getMessage());
        }
        Config.warn("Error finding Chunk.hasEntities");
        return new ReflectorField(new ReflectorClass(Chunk.class), "hasEntities");
    }
}

