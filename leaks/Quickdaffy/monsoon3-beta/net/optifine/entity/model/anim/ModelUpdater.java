/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.entity.model.anim;

import net.optifine.entity.model.anim.IModelResolver;
import net.optifine.entity.model.anim.ModelVariableUpdater;

public class ModelUpdater {
    private ModelVariableUpdater[] modelVariableUpdaters;

    public ModelUpdater(ModelVariableUpdater[] modelVariableUpdaters) {
        this.modelVariableUpdaters = modelVariableUpdaters;
    }

    public void update() {
        for (int i = 0; i < this.modelVariableUpdaters.length; ++i) {
            ModelVariableUpdater modelvariableupdater = this.modelVariableUpdaters[i];
            modelvariableupdater.update();
        }
    }

    public boolean initialize(IModelResolver mr) {
        for (int i = 0; i < this.modelVariableUpdaters.length; ++i) {
            ModelVariableUpdater modelvariableupdater = this.modelVariableUpdaters[i];
            if (modelvariableupdater.initialize(mr)) continue;
            return false;
        }
        return true;
    }
}

