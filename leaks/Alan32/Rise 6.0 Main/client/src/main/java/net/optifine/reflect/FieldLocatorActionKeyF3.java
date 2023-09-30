package net.optifine.reflect;

import net.minecraft.client.Minecraft;
import net.minecraft.src.Config;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class FieldLocatorActionKeyF3 implements IFieldLocator {
    public Field getField() {
        final Class oclass = Minecraft.class;
        final Field field = this.getFieldRenderChunksMany();

        if (field == null) {
            Config.log("(Reflector) Field not present: " + oclass.getName() + ".actionKeyF3 (field renderChunksMany not found)");
            return null;
        } else {
            final Field field1 = ReflectorRaw.getFieldAfter(Minecraft.class, field, Boolean.TYPE, 0);

            if (field1 == null) {
                Config.log("(Reflector) Field not present: " + oclass.getName() + ".actionKeyF3");
                return null;
            } else {
                return field1;
            }
        }
    }

    private Field getFieldRenderChunksMany() {
        final Minecraft minecraft = Minecraft.getMinecraft();
        final boolean flag = minecraft.renderChunksMany;
        final Field[] afield = Minecraft.class.getDeclaredFields();
        minecraft.renderChunksMany = true;
        final Field[] afield1 = ReflectorRaw.getFields(minecraft, afield, Boolean.TYPE, Boolean.TRUE);
        minecraft.renderChunksMany = false;
        final Field[] afield2 = ReflectorRaw.getFields(minecraft, afield, Boolean.TYPE, Boolean.FALSE);
        minecraft.renderChunksMany = flag;
        final Set<Field> set = new HashSet(Arrays.asList(afield1));
        final Set<Field> set1 = new HashSet(Arrays.asList(afield2));
        final Set<Field> set2 = new HashSet(set);
        set2.retainAll(set1);
        final Field[] afield3 = set2.toArray(new Field[set2.size()]);
        return afield3.length != 1 ? null : afield3[0];
    }
}
