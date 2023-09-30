/*
 * Decompiled with CFR 0.152.
 */
package net.optifine;

import java.util.Comparator;
import net.minecraft.src.Config;
import net.optifine.CustomItemProperties;

public class CustomItemsComparator
implements Comparator {
    public int compare(Object o1, Object o2) {
        CustomItemProperties customitemproperties = (CustomItemProperties)o1;
        CustomItemProperties customitemproperties1 = (CustomItemProperties)o2;
        return customitemproperties.weight != customitemproperties1.weight ? customitemproperties1.weight - customitemproperties.weight : (!Config.equals(customitemproperties.basePath, customitemproperties1.basePath) ? customitemproperties.basePath.compareTo(customitemproperties1.basePath) : customitemproperties.name.compareTo(customitemproperties1.name));
    }
}

