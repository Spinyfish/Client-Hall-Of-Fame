/*
 * Decompiled with CFR 0.152.
 */
package optifine;

import java.lang.reflect.Field;
import optifine.IFieldLocator;

public class FieldLocatorFixed
implements IFieldLocator {
    private Field field;

    public FieldLocatorFixed(Field p_i37_1_) {
        this.field = p_i37_1_;
    }

    @Override
    public Field getField() {
        return this.field;
    }
}

