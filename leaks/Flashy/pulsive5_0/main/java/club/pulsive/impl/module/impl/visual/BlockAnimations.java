package club.pulsive.impl.module.impl.visual;

import club.pulsive.impl.module.Category;
import club.pulsive.impl.module.Module;
import club.pulsive.impl.module.ModuleInfo;
import club.pulsive.impl.property.implementations.DoubleProperty;
import club.pulsive.impl.property.implementations.EnumProperty;
import lombok.AllArgsConstructor;

@ModuleInfo(name = "Block Animations", renderName = "Block Animations", description = "Changes camera related things.", aliases = "Block Animations", category = Category.VISUALS)
//This class is not used
public class BlockAnimations extends Module {
    public static EnumProperty<ELEMENTS> prop_elements = new  EnumProperty<>("Elements", ELEMENTS.MOON);
    public static DoubleProperty animationSpeed = new DoubleProperty("Animation Speed", 8,1,20,1);
    public static DoubleProperty rescaleFactor = new DoubleProperty("Rescale Factor", 0.2,0.2,2,0.01);
    public static DoubleProperty height = new DoubleProperty("Heigth", 0,-200,200,1);

    @AllArgsConstructor
    public enum ELEMENTS {
        MOON("Moon"),
        SWING("Swing"),
        SWANG("Swang"),
        ONE_POINT_EIGHT("1.8"),
        ONE_POINT_SEVEN("1.7"),
        SLIDE("Slide"),
        INVERT("Invert"),
        EXHIBITION("Exhibition"),
        UP_N_DOWN("Up N Down");

        private final String modeName;

        @Override
        public String toString() {return modeName;}
    }
}
