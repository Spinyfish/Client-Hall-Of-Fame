package alos.stella.module.modules.visual;

import alos.stella.module.Module;
import alos.stella.module.ModuleCategory;
import alos.stella.module.ModuleInfo;
import alos.stella.value.BoolValue;
import alos.stella.value.FloatValue;

@ModuleInfo(name = "PlayerEdit",spacedName = "Player Edit", description = "Player Editing", category = ModuleCategory.VISUAL)
public class PlayerEdit extends Module {

    //CHANGE BIPED
    public final BoolValue changeBiped = new BoolValue("ChangeBiped", true);
    //CHANGE BIPED

    //ONLY ME
    public final BoolValue onlyMe = new BoolValue("OnlyMe", true, () -> changeBiped.get());
    //ONLY ME
    //ONLY OTHER
    public static BoolValue onlyOther = new BoolValue("OnlyOther", true);
    //ONLY OTHER

    //FOR FRIEND
    public final BoolValue forFriend = new BoolValue("ForFriend", true, () -> changeBiped.get());
    //FOR FRIEND

    //HEAD
    public final BoolValue head = new BoolValue("Head", true, () -> changeBiped.get());
    public final FloatValue headY = new FloatValue("HeadY", 255f, 0f, 355f, () -> head.get() && changeBiped.get());
    public final FloatValue headX = new FloatValue("HeadX", 255f, 0f, 355f, () -> head.get() && changeBiped.get());
    //HEAD

    //RIGHT-ARM
    public final BoolValue rightArm = new BoolValue("Right-Arm", true, () -> changeBiped.get());
    public final FloatValue rightArmX = new FloatValue("Right-ArmX", 0f, 0f, 10f, () -> rightArm.get() && changeBiped.get());
    public final FloatValue rightArmY = new FloatValue("Right-ArmY", 0f, 0f, 10f, () -> rightArm.get() && changeBiped.get());
    public final FloatValue rightArmZ = new FloatValue("Right-ArmZ", 0f, 0f, 10f, () -> rightArm.get() && changeBiped.get());
    //RIGHT-ARM

    //LEFT-ARM
    public final BoolValue leftArm = new BoolValue("Left-Arm", true, () -> changeBiped.get());
    public final FloatValue leftArmX = new FloatValue("Left-ArmX", 0f, 0f, 10f, () -> leftArm.get() && changeBiped.get());
    public final FloatValue leftArmY = new FloatValue("Left-ArmY", 0f, 0f, 10f, () -> leftArm.get() && changeBiped.get());
    public final FloatValue leftArmZ = new FloatValue("Left-ArmZ", 0f, 0f, 10f, () -> leftArm.get() && changeBiped.get());
    //LEFT-ARM

    //RIGHT LEG
    public final BoolValue rightLeg = new BoolValue("Right-Leg", true, () -> changeBiped.get());
    public final FloatValue rightLegX = new FloatValue("Right-LegX", 0f, 0f, 10f, () -> rightLeg.get() && changeBiped.get());
    public final FloatValue rightLegY = new FloatValue("Right-LegY", 0f, 0f, 10f, () -> rightLeg.get() && changeBiped.get());
    //RIGHT LEG

    //LEFT LEG
    public final BoolValue leftLeg = new BoolValue("Left-Leg", true, () -> changeBiped.get());
    public final FloatValue leftLegX = new FloatValue("Left-LegX", 0f, 0f, 10f, () -> leftLeg.get() && changeBiped.get());
    public final FloatValue leftLegY = new FloatValue("Left-LegY", 0f, 0f, 10f, () -> leftLeg.get() && changeBiped.get());
    //LEFT LEG

    //BABY
    public static BoolValue baby = new BoolValue("Baby", true);
    //BABY

}
