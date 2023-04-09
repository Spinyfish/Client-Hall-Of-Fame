package tech.dort.dortware.impl.modules.render;

import com.google.common.eventbus.Subscribe;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.api.property.impl.BooleanValue;
import tech.dort.dortware.api.property.impl.NumberValue;
import tech.dort.dortware.impl.events.Render3DEvent;

public class DragonWings extends Module {

    public BooleanValue colored = new BooleanValue("Colored", this, true);
    public BooleanValue chroma = new BooleanValue("Chroma", this, true);

    public NumberValue hue = new NumberValue("Hue", this, 100, 1, 100, true);
    public NumberValue scale = new NumberValue("Scale", this, 100, 1, 100, true);

    public DragonWings(ModuleData moduleData) {
        super(moduleData);
        register(colored, chroma, hue, scale);
    }

    @Subscribe
    public void onRender3D(Render3DEvent event) {
        //TODO:
        /*

         **Fix dragon wings for dortware base**

         */
    }

}
