/**
 * @project LiriumV4
 * @author Skush/Duzey
 * @at 06.07.2022
 */
package de.lirium.impl.module.impl.movement;

import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import de.lirium.base.helper.TimeHelper;
import de.lirium.base.setting.Dependency;
import de.lirium.base.setting.Value;
import de.lirium.base.setting.impl.CheckBox;
import de.lirium.base.setting.impl.ComboBox;
import de.lirium.base.setting.impl.SliderSetting;
import de.lirium.impl.events.OrientCameraEvent;
import de.lirium.impl.events.PacketEvent;
import de.lirium.impl.events.PlayerMovePostEvent;
import de.lirium.impl.events.UpdateEvent;
import de.lirium.impl.module.ModuleFeature;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.MobEffects;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.BlockPos;

@ModuleFeature.Info(name = "Speed", description = "Speeds up the player movement", category = ModuleFeature.Category.MOVEMENT)
public class SpeedFeature extends ModuleFeature {

    @Value(name = "Mode")
    final ComboBox<String> mode = new ComboBox<>("Strafe", new String[]{"Legit", "Watchdog", "Vanilla", "NCP"});

    @Value(name = "NCP Type")
    final ComboBox<String> ncpType = new ComboBox<>("Flag Port", new String[]{"Y-Port"}, new Dependency<>(mode, "NCP"));

    @Value(name = "Custom Timer Speed")
    final CheckBox customTimerSpeed = new CheckBox(false);

    @Value(name = "Timer Speed")
    final SliderSetting<Float> timerSpeed = new SliderSetting<>(1F, 0.1F, 3F, new Dependency(customTimerSpeed, true));

    @Value(name = "Stop when flag")
    final CheckBox stopWhenFlag = new CheckBox(true);

    @Value(name = "Hide Jumps", visual = true)
    final CheckBox hideJumps = new CheckBox(true);

    private final TimeHelper timeHelper = new TimeHelper();
    private boolean flagged;
    private double hideY;

    @EventHandler
    public final Listener<OrientCameraEvent> orientCamera = e -> {
        if (hideJumps.getValue() && getGameSettings().thirdPersonView != 0) {
            final double y = getPlayer().prevPosY + (getY() - getPlayer().prevPosY) * (double) getTimer().partialTicks;
            final double difference = y - hideY;
            GlStateManager.translate(0, difference, 0);
        }
    };

    @EventHandler
    public final Listener<PlayerMovePostEvent> postEventListener = e -> {
        boolean hide = false;
        switch (mode.getValue()) {
            case "NCP":
                switch (ncpType.getValue()) {
                    case "Y-Port":
                    case "Flag Port":
                        hide = true;
                        break;
                }
                break;
        }
        if (hide && (!hideJumps.getValue() || getGameSettings().thirdPersonView == 0)) {
            if (!getPlayer().onGround && !getPlayer().isCollidedVertically)
                getPlayer().posY = (int) getPlayer().posY;
            getPlayer().cameraPitch = 0;
        }
    };

    @EventHandler
    public final Listener<UpdateEvent> updateEvent = e -> {
        setSuffix(mode.getValue());

        if (getPlayer().onGround && getPlayer().isCollidedVertically) {
            hideY = (int) getY();
        }

        if (flagged && !timeHelper.hasReached(2000) && stopWhenFlag.getValue()) {
            getTimer().timerSpeed = 1F;
            return;
        } else if (flagged)
            flagged = false;

        if (customTimerSpeed.getValue())
            setTimer(timerSpeed.getValue());
        //setTimer(Client.INSTANCE.getModuleManager().get(AuraFeature.class).isEnabled() && Client.INSTANCE.getModuleManager().get(AuraFeature.class).targets.size() > 0 ? 1.0F : timerSpeed.getValue());
        switch (mode.getValue()) {
            case "NCP":
                switch (ncpType.getValue()) {
                    case "Flag Port":
                        getPlayer().setSprinting(true);
                        if (isMoving()) {
                            if (getPlayer().onGround) {
                                getPlayer().jump();
                                getTimer().timerSpeed = 0.8F;
                                setSpeed(Math.max(getSpeed(), 0.45));
                            } else {
                                if (getPlayer().motionY < 0.4) {
                                    getTimer().timerSpeed = 1.8F;
                                    getPlayer().motionY = -1337.0;
                                    setSpeed(0.26);
                                }
                            }
                        }
                        break;
                    case "Y-Port":
                        getPlayer().setSprinting(true);
                        if (isMoving())
                            if (getPlayer().onGround) {
                                getPlayer().jump();
                                getPlayer().motionX *= 0.75;
                                getPlayer().motionZ *= 0.75;
                                getTimer().timerSpeed = 0.8F;
                            } else {
                                if (getPlayer().motionY < 0.4) {
                                    getTimer().timerSpeed = 1.6F;
                                    getPlayer().motionY = -1337.0;
                                    setSpeed(0.26);
                                }
                            }
                        break;
                }
                break;
            case "Strafe":
                if (isMoving()) {
                    setSpeed(getSpeed());

                    if (isOnGround())
                        getPlayer().jump();
                } else {
                    setTimer(1f);
                }
                break;
            case "Legit":
                if (isMoving()) {
                    if (isOnGround())
                        getPlayer().jump();
                }
                break;
            case "Watchdog":
                if (isMoving()) {
                    if (isOnGround()) {
                        getPlayer().jump();
                        setSpeed(getBaseSpeed() + 0.45F - 0.2873F);
                        if (getPlayer().isPotionActive(MobEffects.SPEED))
                            setSpeed(getSpeed() * 1.2F);
                    }
                    /*final double yaw = getDirection();
                    final double x = -Math.sin(yaw) * getSpeed(), z = Math.cos(yaw) * getSpeed();
                    mc.player.motionX -= (mc.player.motionX - x) * 0.5;
                    mc.player.motionZ -= (mc.player.motionZ - z) * 0.5;*/
                } else setTimer(1f);
                break;
            case "Vanilla":
                if (isMoving()) {
                    if (isOnGround())
                        getPlayer().jump();
                    else if (getWorld().getCollisionBoxes(getPlayer(), getPlayer().getEntityBoundingBox().offset(-Math.sin(getDirection()) * getSpeed(), 0,
                            Math.cos(getDirection()) * getSpeed())).isEmpty())
                        getPlayer().motionY = Math.min(0.1, getPlayer().motionY);
                    setSpeed(0.5);
                } else {
                    setSpeed(0);
                }
                break;
        }
    };

    @EventHandler
    public final Listener<PacketEvent> packetListener = e -> {
        final Packet<?> packet = e.packet;
        if (packet instanceof SPacketPlayerPosLook) {
            final SPacketPlayerPosLook packetPlayerPosLook = (SPacketPlayerPosLook) packet;
            final BlockPos pos = new BlockPos(packetPlayerPosLook.getX(), packetPlayerPosLook.getY(), packetPlayerPosLook.getZ());
            if (pos.getDistance(getPlayer()) < 3) {
                flagged = true;
                timeHelper.reset();
            }
        }
    };

    @Override
    public void onEnable() {
        flagged = false;
        timeHelper.reset();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        setTimer(1.0F);
    }
}
