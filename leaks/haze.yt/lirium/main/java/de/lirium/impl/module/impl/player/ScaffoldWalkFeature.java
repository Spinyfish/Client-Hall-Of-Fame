package de.lirium.impl.module.impl.player;

import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import de.lirium.base.helper.TimeHelper;
import de.lirium.base.setting.Dependency;
import de.lirium.base.setting.Value;
import de.lirium.base.setting.impl.CheckBox;
import de.lirium.base.setting.impl.ComboBox;
import de.lirium.base.setting.impl.SliderSetting;
import de.lirium.impl.events.AttackEvent;
import de.lirium.impl.events.RotationEvent;
import de.lirium.impl.events.UpdateEvent;
import de.lirium.impl.module.ModuleFeature;
import de.lirium.util.random.RandomUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@ModuleFeature.Info(name = "Scaffold Walk", description = "Bridging automatically", category = ModuleFeature.Category.PLAYER)
public class ScaffoldWalkFeature extends ModuleFeature {

    @Value(name = "Swinging")
    private final CheckBox swinging = new CheckBox(true);

    @Value(name = "Two Hand Support")
    private final CheckBox twoHandSupport = new CheckBox(true);

    @Value(name = "Sprint")
    private final CheckBox sprint = new CheckBox(false);

    @Value(name = "Reverse Movement")
    private final CheckBox reverseMovement = new CheckBox(false);

    @Value(name = "Tower")
    private final ComboBox<String> tower = new ComboBox<>("None", new String[]{"Gomme", "Motion", "Pika-Network"});

    @Value(name = "Tower - While moving")
    private final CheckBox towerWhileMoving = new CheckBox(false, new Dependency<>(tower, "Gomme", "Motion", "Pika-Network"));

    @Value(name = "Tower - Motion")
    private final SliderSetting<Double> towerMotion = new SliderSetting<>(0.42, 0.0, 1.0, new Dependency<>(tower, "Motion"));

    @Value(name = "Bridge Type")
    private final ComboBox<String> bridgeType = new ComboBox<>("Fast Bridge", new String[]{});

    @Value(name = "Randomize Pitch")
    private final CheckBox randomizePitch = new CheckBox(false, new Dependency<>(bridgeType, "Fast Bridge"));

    @Value(name = "Delay")
    private final SliderSetting<Long> delay = new SliderSetting<>(0L, 0L, 1000L);

    @Value(name = "unsneak delay")
    private final SliderSetting<Long> unSneakDelay = new SliderSetting<>(0L, 0L, 1000L, new Dependency<>(bridgeType, "Fast Bridge"));

    private final TimeHelper timeHelper = new TimeHelper(), unsneakTimeHelper = new TimeHelper();

    @EventHandler
    public final Listener<RotationEvent> rotationEventListener = e -> {
        switch (bridgeType.getValue()) {
            case "Fast Bridge":
                e.pitch = randomizePitch.getValue() && isMoving() ? (float) ThreadLocalRandom.current().nextDouble(83, 83.3) : 83;
                e.yaw = getYaw() + 180;
                break;
        }
    };

    @EventHandler
    public final Listener<UpdateEvent> updateEventListener = e -> {
        setSuffix(String.valueOf(delay.getValue()), bridgeType.getValue(), tower.getValue());
        getPlayer().setSprinting(sprint.getValue());
        getGameSettings().keyBindSprint.pressed = sprint.getValue();

        if (reverseMovement.getValue()) {
            getGameSettings().keyBindBack.pressed = isKeyDown(getGameSettings().keyBindForward.getKeyCode());
            getGameSettings().keyBindForward.pressed = false;
        }

        switch (bridgeType.getValue()) {
            case "Fast Bridge":
                if (unSneakDelay.getValue() == 0 || unsneakTimeHelper.hasReached((long) (unSneakDelay.getValue() + RandomUtil.getGaussian(20)))) {
                    getGameSettings().keyBindSneak.pressed = false;
                }
                for (int y = -1; y < 0; y++) {
                    final Vec3d pos = getPlayer().getPositionVector().addVector(0, y, 0);
                    final BlockPos blockPos = new BlockPos(pos);
                    if (getWorld().isAirBlock(blockPos)) {
                        getGameSettings().keyBindSneak.pressed = true;
                        unsneakTimeHelper.reset();
                    }
                }
                break;

        }
    };

    @EventHandler
    public final Listener<AttackEvent> attackEvent = e -> {
        if (!mc.playerController.getIsHittingBlock()) {
            if (!getPlayer().isRowingBoat()) {
                if (!getPlayer().isHandActive()) {
                    if (delay.getValue() == 0 || timeHelper.hasReached((long) (delay.getValue() + RandomUtil.getGaussian(20)))) {

                        if (canTower())
                            switch (tower.getValue()) {
                                case "Pika-Network":
                                    if (getPlayer().onGround) {
                                        setPosition(getX(), getY() + 1, getZ());
                                    } else {
                                        if (getPlayer().fallDistance >= 1) {
                                            getPlayer().motionY -= 15;
                                        }
                                    }
                                    break;
                                case "Gomme":
                                    if (getPlayer().onGround) {
                                        getPlayer().motionY = 0.4F;
                                    } else if (getPlayer().motionY <= 0.2)
                                        getPlayer().motionY -= 0.004;
                                    break;
                            }

                        for (EnumHand enumhand : EnumHand.values()) {
                            final ItemStack itemstack = getPlayer().getHeldItem(enumhand);
                            if (itemstack.getItem() instanceof ItemBlock) {
                                if (enumhand.equals(EnumHand.MAIN_HAND) || twoHandSupport.getValue()) {
                                    if (mc.objectMouseOver != null) {
                                        switch (mc.objectMouseOver.typeOfHit) {
                                            case ENTITY:
                                                if (mc.playerController.interactWithEntity(getPlayer(), mc.objectMouseOver.entityHit, mc.objectMouseOver, enumhand) == EnumActionResult.SUCCESS) {
                                                    return;
                                                }

                                                if (mc.playerController.interactWithEntity(getPlayer(), mc.objectMouseOver.entityHit, enumhand) == EnumActionResult.SUCCESS) {
                                                    return;
                                                }

                                                break;

                                            case BLOCK:
                                                final BlockPos blockpos = mc.objectMouseOver.getBlockPos();

                                                if (mc.objectMouseOver.sideHit == EnumFacing.UP && !getGameSettings().keyBindJump.pressed)
                                                    break;
                                                if (getWorld().getBlockState(blockpos).getMaterial() != Material.AIR) {
                                                    int i = itemstack.func_190916_E();
                                                    EnumActionResult enumactionresult = mc.playerController.processRightClickBlock(getPlayer(), getWorld(), blockpos, mc.objectMouseOver.sideHit, mc.objectMouseOver.hitVec, enumhand);

                                                    if (enumactionresult == EnumActionResult.SUCCESS) {
                                                        if (swinging.getValue())
                                                            getPlayer().swingArm(enumhand);

                                                        if (!itemstack.func_190926_b() && (itemstack.func_190916_E() != i || mc.playerController.isInCreativeMode())) {
                                                            mc.entityRenderer.itemRenderer.resetEquippedProgress(enumhand);
                                                        }

                                                        if (canTower())
                                                            switch (tower.getValue()) {
                                                                case "Motion":
                                                                    getPlayer().motionY = towerMotion.getValue();
                                                                    break;
                                                            }
                                                        return;
                                                    } else {
                                                        timeHelper.reset();
                                                    }
                                                }
                                        }
                                    }
                                }
                                if (ThreadLocalRandom.current().nextInt(100) <= 40) {
                                    if (!itemstack.func_190926_b() && mc.playerController.processRightClick(getPlayer(), getWorld(), enumhand) == EnumActionResult.SUCCESS) {
                                        mc.entityRenderer.itemRenderer.resetEquippedProgress(enumhand);
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    };

    private boolean canTower() {
        return (towerWhileMoving.getValue() || !isMoving()) && getGameSettings().keyBindJump.pressed;
    }

    @Override
    public void onEnable() {
        timeHelper.reset();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        getGameSettings().keyBindSneak.pressed = isKeyDown(getGameSettings().keyBindSneak.getKeyCode());
        getGameSettings().keyBindBack.pressed = isKeyDown(getGameSettings().keyBindBack.getKeyCode());
        getGameSettings().keyBindForward.pressed = isKeyDown(getGameSettings().keyBindForward.getKeyCode());

        super.onDisable();
    }
}
