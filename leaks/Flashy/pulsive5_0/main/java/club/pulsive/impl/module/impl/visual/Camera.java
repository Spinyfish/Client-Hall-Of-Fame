package club.pulsive.impl.module.impl.visual;

import club.pulsive.impl.module.Category;
import club.pulsive.impl.module.Module;
import club.pulsive.impl.module.ModuleInfo;
import club.pulsive.impl.property.implementations.MultiSelectEnumProperty;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;

@ModuleInfo(name = "Camera", renderName = "Camera", description = "Changes camera related things.", aliases = "Camera", category = Category.VISUALS)
//This class is not used
public class Camera extends Module {
    public MultiSelectEnumProperty<ELEMENTS> prop_elements = new  MultiSelectEnumProperty<>("Elements", Lists.newArrayList(ELEMENTS.NO_HURT_CAM), ELEMENTS.values());

    @AllArgsConstructor
    public enum ELEMENTS {
        NO_HURT_CAM("No Hurt Cam"),
        NO_GAME_ID("No GameID"),
        MORE_BOB("More Bobbing"),
        MORE_PARTICLES("More Particles"),
        NO_ARMOR("No Amor"),
        STREAMING("Streaming");

        private final String addonName;

        @Override
        public String toString() {return addonName;}
    }
}
