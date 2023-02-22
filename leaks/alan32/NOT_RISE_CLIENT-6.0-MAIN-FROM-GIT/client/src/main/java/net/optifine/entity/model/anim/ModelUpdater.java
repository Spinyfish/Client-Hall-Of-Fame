package net.optifine.entity.model.anim;

public class ModelUpdater {
    private final ModelVariableUpdater[] modelVariableUpdaters;

    public ModelUpdater(final ModelVariableUpdater[] modelVariableUpdaters) {
        this.modelVariableUpdaters = modelVariableUpdaters;
    }

    public void update() {
        for (int i = 0; i < this.modelVariableUpdaters.length; ++i) {
            final ModelVariableUpdater modelvariableupdater = this.modelVariableUpdaters[i];
            modelvariableupdater.update();
        }
    }

    public boolean initialize(final IModelResolver mr) {
        for (int i = 0; i < this.modelVariableUpdaters.length; ++i) {
            final ModelVariableUpdater modelvariableupdater = this.modelVariableUpdaters[i];

            if (!modelvariableupdater.initialize(mr)) {
                return false;
            }
        }

        return true;
    }
}
