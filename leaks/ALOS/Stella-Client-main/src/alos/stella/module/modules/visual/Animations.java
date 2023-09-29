package alos.stella.module.modules.visual;

import alos.stella.module.Module;
import alos.stella.module.ModuleCategory;
import alos.stella.module.ModuleInfo;
import alos.stella.value.FloatValue;
import alos.stella.value.IntegerValue;
import alos.stella.value.ListValue;

@ModuleInfo(name = "Animations", description = "Render items Animations", category = ModuleCategory.VISUAL)
public class Animations extends Module {

    public static final ListValue Sword = new ListValue("Style", new String[]{
            "SlideDown", "SlideDown2", "Slide", "Slide2", "Slide3", "Exhibition", "Exhibition2",
            "Avatar", "Swang", "Swank", "1.7", "Flux", "Stella", "Stella2", "Tifality", "OldExhibition",
    }, "Swang");

    public static final FloatValue Scale = new FloatValue("Scale", 0.4f, 0f, 4f);
    public static final FloatValue itemPosX = new FloatValue("ItemX", 0f, -1f, 1f);
    public static final FloatValue itemPosY = new FloatValue("ItemY", 0f, -1f, 1f);
    public static final FloatValue itemPosZ = new FloatValue("ItemZ", 0f, -1f, 1f);
    public static final IntegerValue SpeedSwing = new IntegerValue("Swing-Speed", 4, 0, 20);
    public static final FloatValue itemDistance = new FloatValue("ItemDistance", 1, 1, 5f);
    public static final FloatValue blockPosX = new FloatValue("BlockingX", 0f, -1f, 1f);
    public static final FloatValue blockPosY = new FloatValue("BlockingY", 0f, -1f, 1f);
    public static final FloatValue blockPosZ = new FloatValue("BlockingZ", 0f, -1f, 1f);

}
