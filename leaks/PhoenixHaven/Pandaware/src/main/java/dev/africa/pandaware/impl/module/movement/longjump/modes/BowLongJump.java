package dev.africa.pandaware.impl.module.movement.longjump.modes;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.event.Event;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.player.MotionEvent;
import dev.africa.pandaware.impl.event.player.MoveEvent;
import dev.africa.pandaware.impl.module.movement.longjump.LongJumpModule;
import dev.africa.pandaware.impl.setting.BooleanSetting;
import dev.africa.pandaware.impl.setting.NumberSetting;
import dev.africa.pandaware.impl.ui.notification.Notification;
import dev.africa.pandaware.utils.math.TimeHelper;
import dev.africa.pandaware.utils.player.MovementUtils;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class BowLongJump extends ModuleMode<LongJumpModule> { //ty sage for hot module uwu
    public final NumberSetting bowArrowTime = new NumberSetting("Arrow time", 1000, 100, 110, 1);
    public final NumberSetting bowSpeedTicks = new NumberSetting("Speed ticks", 20, 1, 1, 1);
    public final NumberSetting bowDivision = new NumberSetting("Division", 300, 2, 199, 1);
    public final NumberSetting bowGlide = new NumberSetting("Glide", 0.1, 0, 0);
    public final NumberSetting bowSpeedMultiply = new NumberSetting("Speed multiply", 10, 1, 1);
    public final NumberSetting glideTicks = new NumberSetting("Glide ticks", 20, 1, 1, 1);
    public final NumberSetting bowHeight = new NumberSetting("Height", 8, 0.1, 0.42f);

    public final BooleanSetting bowTimer = new BooleanSetting("Timer", false);
    public final BooleanSetting bowFloat = new BooleanSetting("Float", false);

    public final BooleanSetting bowFloatGlide = new BooleanSetting("Float glide",
            false, this.bowFloat::getValue);
    public final NumberSetting bowTimerAmount = new NumberSetting("Timer amount",
            5, 0.1, 1.0, this.bowTimer::getValue);
    public final NumberSetting bowFloatDistance = new NumberSetting("Float distance",
            5, 0, 0, this.bowFloat::getValue);
    public final BooleanSetting bowSpeedOnce = new BooleanSetting("Speed once", false);
    private final BooleanSetting ignoreSpeed = new BooleanSetting("Ignore Speed pots", false);

    public BowLongJump(String name, LongJumpModule parent) {
        super(name, parent);

        this.registerSettings(
                this.bowArrowTime,
                this.bowSpeedTicks,
                this.bowDivision,
                this.bowGlide,
                this.bowSpeedMultiply,
                this.glideTicks,
                this.bowHeight,
                this.bowTimerAmount,
                this.bowFloatDistance,
                this.bowSpeedOnce,
                this.ignoreSpeed,
                this.bowTimer,
                this.bowFloat,
                this.bowFloatGlide
        );
    }

    private final TimeHelper timer = new TimeHelper();
    private double lastDistance, movementSpeed;
    private int beforeSlot;
    private boolean jumped, shouldRun, shouldBow;
    private int stage;
    private boolean shouldSpeed;

    @Override
    public void onEnable() {
        if (this.getBowSlot() == -1) {
            this.toggle(false);

            Client.getInstance().getNotificationManager().addNotification(Notification.Type.NOTIFY,
                    "You do not have a bow in your hotbar", 5);
        } else {
            this.stage = 1;
        }

        this.beforeSlot = mc.thePlayer.inventory.currentItem;

        this.shouldBow = true;
        this.shouldRun = false;
        this.shouldSpeed = true;
        this.jumped = false;
        this.timer.reset();
    }

    @Override
    public void onDisable() {
        if (this.shouldBow && !this.timer.reach(this.bowArrowTime.getValue().longValue())) {
            mc.thePlayer.inventory.currentItem = this.beforeSlot;
        }

        this.shouldBow = true;
        this.shouldRun = false;
        this.shouldSpeed = true;
        this.jumped = false;
        this.timer.reset();
    }

    @EventHandler
    EventCallback<MotionEvent> onMotion = event -> {
        if (event.getEventState() != Event.EventState.PRE) return;

        this.lastDistance = MovementUtils.getLastDistance();

        if (this.stage >= 1 && !this.shouldRun && this.shouldBow) {
            mc.thePlayer.inventory.currentItem = getBowSlot();

            if (++this.stage >= 4) {
                mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C08PacketPlayerBlockPlacement(
                        new BlockPos(-1, -1, -1), 255,
                        mc.thePlayer.inventory.getCurrentItem(), 0.0F, 0.0F, 0.0F
                ));

                this.stage = 0;
            }

            this.timer.reset();
        }

        if (!this.shouldRun) {
            if (this.shouldBow) {
                event.setYaw(event.getYaw() - 180);
                event.setPitch(-90);

                if (this.timer.reach(this.bowArrowTime.getValue().longValue())) {
                    mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C07PacketPlayerDigging(
                            C07PacketPlayerDigging.Action.RELEASE_USE_ITEM,
                            new BlockPos(0, 0, 0), EnumFacing.DOWN
                    ));

                    mc.thePlayer.inventory.currentItem = this.beforeSlot;
                    this.shouldBow = false;
                }
            } else {
                this.shouldRun = mc.thePlayer.hurtTime > 0;
            }
        }
    };

    @EventHandler
    EventCallback<MoveEvent> onMove = event -> {
        if (this.shouldRun) {
            if (this.bowTimer.getValue()) {
                mc.timer.timerSpeed = this.bowTimerAmount.getValue().floatValue();
            }

            if (!this.jumped) {
                event.y = mc.thePlayer.motionY = this.bowHeight.getValue().floatValue();

                this.jumped = true;
            } else {
                if (this.bowFloat.getValue() && mc.thePlayer.fallDistance >=
                        this.bowFloatDistance.getValue().doubleValue()) {
                    event.y = mc.thePlayer.motionY = (this.bowFloatGlide.getValue() ? -0.015 : 0);
                }

                if (mc.thePlayer.ticksExisted % this.glideTicks.getValue().intValue() == 0 &&
                        mc.thePlayer.fallDistance > 0) {
                    event.y = mc.thePlayer.motionY += this.bowGlide.getValue().doubleValue();
                }
            }

            if (mc.thePlayer.ticksExisted % this.bowSpeedTicks.getValue().intValue() == 0) {
                if (this.shouldSpeed) {
                    double speedAmplifier = (mc.thePlayer.isPotionActive(Potion.moveSpeed)
                            ? ((mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1) * 0.15) : 0);
                    switch (this.stage) {
                        case 0:
                            this.movementSpeed = this.getParent().getSpeed().getValue().doubleValue() +
                                    (this.ignoreSpeed.getValue() ? 0 : speedAmplifier);
                            break;

                        case 1:
                            this.movementSpeed *= this.bowSpeedMultiply.getValue().doubleValue();
                            break;

                        case 2:
                            this.movementSpeed = this.lastDistance - 0.2f *
                                    (this.lastDistance - MovementUtils.getBaseMoveSpeed());
                            break;

                        default:
                            this.movementSpeed = this.lastDistance - (this.lastDistance /
                                    this.bowDivision.getValue().doubleValue());
                            break;
                    }

                    this.movementSpeed = Math.max(this.movementSpeed, MovementUtils.getBaseMoveSpeed());

                    MovementUtils.strafe(event, this.movementSpeed);

                    this.shouldSpeed = !this.bowSpeedOnce.getValue();
                }
            }
        } else {
            MovementUtils.strafe(event, 0);
        }
    };

    int getBowSlot() {
        for (int i = 8; i >= 0; i--) {
            ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);

            if (itemStack != null && itemStack.getItem() instanceof ItemBow) {
                return i;
            }
        }

        return -1;
    }
}
