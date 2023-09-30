package Reality.Realii.guis.material;

import Reality.Realii.mods.ModuleType;
import Reality.Realii.utils.math.AnimationUtils;

public class Category {
    public ModuleType moduleType;
    public boolean needRemove;
    public AnimationUtils rollAnim2 = new AnimationUtils();
    float anim;
    public boolean show;
    public float modsY2 = 0;
    public float modsY3 = 0;

    public Category(ModuleType mt, int i, boolean b) {
        this.moduleType = mt;
        this.anim = i;
        this.show = b;
    }
}
