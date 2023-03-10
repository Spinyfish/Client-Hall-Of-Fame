package store.femboy.spring.impl.module.impl.movement;

import best.azura.eventbus.core.EventPriority;
import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import com.google.common.collect.ImmutableMap;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3i;
import org.lwjgl.input.Keyboard;
import store.femboy.spring.Spring;
import store.femboy.spring.impl.event.impl.EventMotion;
import store.femboy.spring.impl.module.Category;
import store.femboy.spring.impl.module.Module;
import store.femboy.spring.impl.module.settings.impl.BooleanSetting;
import store.femboy.spring.impl.module.settings.impl.ModeSetting;
import store.femboy.spring.impl.module.settings.impl.NumberSettings;
import store.femboy.spring.impl.util.corrosionutils.ScaffoldBlockData;
import store.femboy.spring.impl.util.corrosionutils.ScaffoldUtil;
import store.femboy.spring.impl.util.MathUtil;
import store.femboy.spring.impl.util.MoveUtil;
import store.femboy.spring.impl.util.Rotation;
import store.femboy.spring.impl.util.TimeUtil;

import java.util.Map;

public final class Scaffold extends Module {

    private BooleanSetting sprint = new BooleanSetting("Sprint", false);
    private NumberSettings swapDelay = new NumberSettings("Block Swap Delay", 1F, 1F, 2F, 0.01F);
    private NumberSettings timerSpeed = new NumberSettings("Timer", 1F, 0.1F, 20F, 0.1F);
    private NumberSettings placeDelay = new NumberSettings("Place delay (ms)", 0, 0, 2000, 50);

    private NumberSettings walkSpeed = new NumberSettings("Walk Speed", 1, 0.1f, 10f, 0.01f);
    private ModeSetting rotsSetting = new ModeSetting("Mode", "Hypixel", "Hypixel", "Vulcan", "NCP");
    private TimeUtil timer = new TimeUtil();

    private ScaffoldBlockData blockInfo;

    private int startSlot;

    private float newYaw;
    private float newPitch;

    public Scaffold() {
        super("Scaffold", "Places blocks for u", Keyboard.KEY_NONE, Category.MOVEMENT);
        addSettings(sprint, swapDelay, timerSpeed, placeDelay, rotsSetting, walkSpeed);
    }

    private final Map<EnumFacing, Float> directionYaws = ImmutableMap.of(
            EnumFacing.SOUTH, 200F,
            EnumFacing.NORTH, 20F,
            EnumFacing.EAST, 110F,
            EnumFacing.WEST, -110F
    );

    @Override
    public void onEnable() {
        this.startSlot = mc.thePlayer.inventory.currentItem;
        if(Spring.INSTANCE.getManager().getModule("Sprint").isToggled()){
            Spring.INSTANCE.getManager().getModule("Sprint").setToggled(false);
        }
        mc.timer.timerSpeed = timerSpeed.getValue();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1f;
        mc.thePlayer.inventory.currentItem = startSlot;
        if(!Spring.INSTANCE.getManager().getModule("Sprint").isToggled()){
            Spring.INSTANCE.getManager().getModule("Sprint").setToggled(true);
        mc.timer.timerSpeed = 1f;
        }
        super.onDisable();
    }

    @EventHandler(EventPriority.HIGHER)
    public final Listener<EventMotion> onUpdate = e ->{
        if(MoveUtil.isMoving() && rotsSetting.is("Hypixel"))
            MoveUtil.setSpeed(.08);
        else if (MoveUtil.isMoving()){
            MoveUtil.setSpeed(walkSpeed.getValue());
        }

        int oldSlot = mc.thePlayer.inventory.currentItem;
        if(e.isPre()){
            blockInfo = ScaffoldBlockData.getBlockInfo(new BlockPos(mc.thePlayer));

            if(sprint.isEnabled()){
                if (MoveUtil.isMoving() && !mc.thePlayer.isCollidedHorizontally) {
                    mc.thePlayer.setSprinting(true);
                }
            }else if(mc.thePlayer.isSprinting()){
                mc.thePlayer.setSprinting(false);
            }

            if (ScaffoldUtil.invCheck()) {
                for (int i = 9; i < 36; i++) {
                    if (!ScaffoldUtil.isStackValid(mc.thePlayer.inventoryContainer.getSlot(i).getStack())) {
                        continue;
                    }

                    mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, 1, 2, mc.thePlayer);
                    break;
                }
            }
            e.setYaw(newYaw);
            e.setPitch(newPitch);
            if (blockInfo != null) { // ROTATIONS
                switch(rotsSetting.getMode()){
                    case "Hypixel":
                        Rotation rotation = new Rotation(mc.thePlayer.posX, mc.thePlayer.posY-mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ, mc.thePlayer.rotationYaw - 180, 81f);
                        e.setYaw(rotation.getRotationYaw());
                        e.setPitch(rotation.getRotationPitch());
                        mc.thePlayer.rotationYawHead = rotation.getRotationYaw();
                        mc.thePlayer.renderYawOffset = rotation.getRotationYaw();
                        mc.thePlayer.rotationPitchHead = rotation.getRotationPitch();
                        break;
                    case "NCP":
                        Rotation rotation2 = new Rotation(mc.thePlayer.posX, mc.thePlayer.posY-mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ, mc.thePlayer.rotationYaw - MathUtil.getRandFloat(170f, 190f), MathUtil.getRandFloat(70f, 90f));
                        e.setYaw(rotation2.getRotationYaw());
                        e.setPitch(rotation2.getRotationPitch());
                        mc.thePlayer.rotationYawHead = rotation2.getRotationYaw();
                        mc.thePlayer.renderYawOffset = rotation2.getRotationYaw();
                        mc.thePlayer.rotationPitchHead = rotation2.getRotationPitch();
                        break;
                    case "Vulcan":
                        Rotation vulcanrotation = new Rotation(mc.thePlayer.posX, mc.thePlayer.posY-mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ, mc.thePlayer.rotationYaw - 180, 0f);
                        e.setYaw(vulcanrotation.getRotationYaw());
                        e.setPitch(vulcanrotation.getRotationPitch());
                        mc.thePlayer.rotationYawHead = vulcanrotation.getRotationYaw();
                        mc.thePlayer.renderYawOffset = vulcanrotation.getRotationYaw();
                        mc.thePlayer.rotationPitchHead = vulcanrotation.getRotationPitch();
                        break;

                }
            }
        } else if (blockInfo != null && ScaffoldUtil.getSlot() != -1) {
            BlockPos pos = blockInfo.getBlockPos();
            EnumFacing direction = blockInfo.getDirection();

            Vec3i vec3i = direction.getDirectionVec();
            Vec3 vec3 = new Vec3(pos).addVector(vec3i.getX() * 0.5F + 0.5F, vec3i.getY() * 0.5F + 0.5F, vec3i.getZ() + 0.5F);

            mc.thePlayer.inventory.currentItem = ScaffoldUtil.getSlot();

            if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.inventoryContainer.getSlot(36 + ScaffoldUtil.getSlot()).getStack(), blockInfo.getBlockPos(), blockInfo.getDirection(), vec3)) { // PLACING
                mc.thePlayer.swingItem();
            }

            mc.thePlayer.inventory.currentItem = oldSlot;
            blockInfo = null;
        }

    };


}
