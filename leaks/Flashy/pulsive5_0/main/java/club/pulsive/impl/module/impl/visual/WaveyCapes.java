package club.pulsive.impl.module.impl.visual;

import club.pulsive.api.main.Pulsive;
import club.pulsive.impl.module.Category;
import club.pulsive.impl.module.Module;
import club.pulsive.impl.module.ModuleInfo;
import club.pulsive.impl.property.Property;
import club.pulsive.impl.property.implementations.DoubleProperty;
import club.pulsive.impl.property.implementations.EnumProperty;

@ModuleInfo(name = "WaveyCapes", renderName = "Wavey Capes", category = Category.VISUALS)
public class WaveyCapes extends Module {

    public EnumProperty<WindMode> windModeEnumProperty = new EnumProperty<>("Wind Mode", WindMode.None);
    public EnumProperty<CapeStyle> capeStyleEnumProperty = new EnumProperty<>("Cape Style", CapeStyle.Smooth);
    public EnumProperty<CapeMovement> capeMovementEnumProperty = new EnumProperty<>("Cape Movement", CapeMovement.Basic_Simulation);
    public DoubleProperty gravity = new DoubleProperty("Gravity", 5, 0, 32, 1);
    public DoubleProperty heightMultiplier = new DoubleProperty("Height Multiplier", 4, 0, 16, 1);

    public Property<Boolean> movementRotation = new Property<>("Movement Rotation", true);



    public static WaveyCapes getInstance(){
        return Pulsive.INSTANCE.getModuleManager().getModule(WaveyCapes.class);
    }


    public enum WindMode{
        None, Waves
    }

    public enum CapeStyle {
        Blocky, Smooth;
    }

    public enum CapeMovement {
        Vanilla, Basic_Simulation
    }
}
