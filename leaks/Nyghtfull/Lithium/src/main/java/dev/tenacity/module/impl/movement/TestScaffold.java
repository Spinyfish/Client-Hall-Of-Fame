package dev.tenacity.module.impl.movement;

import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.settings.impl.BooleanSetting;
import dev.tenacity.module.settings.impl.ModeSetting;
import dev.tenacity.module.settings.impl.NumberSetting;
import dev.tenacity.utils.player.scaffold.EnumFacingOffset;
import net.minecraft.util.BlockPosition;
import net.minecraft.util.Vec3;

public class TestScaffold extends Module {
    public static ModeSetting mode = new ModeSetting("Scaffold Mode", "Normal", "Normal","Snap", "Telly","UPDATED-NCP");
    public static ModeSetting raycast = new ModeSetting("Raycast Mode", "Off", "Normal","Strict");

    public static ModeSetting sprint = new ModeSetting("Sprint Mode", "Normal", "Disabled","Legit","Watchdog");

    public static ModeSetting tower = new ModeSetting("Tower Mode", "Disabled", "Disabled","Vulcan", "Watchdog","NCP");
    public static ModeSetting sameY = new ModeSetting("SameY Mode", "Off", "Off","On", "Auto Jump");

    public static NumberSetting rotationSpeed = new NumberSetting("Rotation Speed",5,10,0,1);

    public static NumberSetting placeDelay = new NumberSetting("Place Delay",0,5,0,1);

    public static NumberSetting timer = new NumberSetting("Timer",1,10,1,0.1);

    public static BooleanSetting movementCorrection = new BooleanSetting("Movement Correction",false);
    public static BooleanSetting safeWalk = new BooleanSetting("Safe Walk",false);

    public static ModeSetting yawOffset = new ModeSetting("Yaw Offset", "0", "0","45", "-45");


    public TestScaffold() {
        super("TestScaffold", Category.MOVEMENT, "a new scaffold for testing most likely will be the new scaffold");
        addSettings(mode,raycast,sprint,tower,sameY,rotationSpeed,placeDelay,timer,movementCorrection,safeWalk,yawOffset);
    }
    private Vec3 targetBlock;
    private EnumFacingOffset enumFacing;
    private BlockPosition blockFace;
    private float targetYaw, targetPitch;
    private int ticksOnAir, sneakingTicks;
    private double startY;
    private float forward, strafe;
    private int placements;
    private boolean incrementedPlacements;

    @Override
    public void onEnable() {
        targetYaw = mc.thePlayer.rotationYaw - 180;
        targetPitch = 90;

        startY = Math.floor(mc.thePlayer.posY);
        targetBlock = null;

        this.sneakingTicks = -1;
        super.onEnable();
    }
    @Override
    public void onDisable() {
       // mc.gameSettings.keyBindSneak.setPressed(Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()));
       // mc.gameSettings.keyBindJump.setPressed(Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode()));



        // This is a temporary patch
      //  SlotComponent.setSlot(mc.thePlayer.inventory.currentItem);
        super.onDisable();
    }






}
