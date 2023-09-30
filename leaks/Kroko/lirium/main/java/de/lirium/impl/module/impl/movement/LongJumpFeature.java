/**
 * @project LiriumV4
 * @author Skush/Duzey
 * @at 06.07.2022
 */
package de.lirium.impl.module.impl.movement;

import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import de.lirium.base.setting.Dependency;
import de.lirium.base.setting.Value;
import de.lirium.base.setting.impl.ComboBox;
import de.lirium.base.setting.impl.SliderSetting;
import de.lirium.impl.events.PacketEvent;
import de.lirium.impl.events.UpdateEvent;
import de.lirium.impl.module.ModuleFeature;
import de.lirium.util.misc.ServerUtil;
import god.buddy.aot.BCompiler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketSpectate;
import net.minecraft.network.play.server.SPacketPlayerPosLook;

import java.util.UUID;

@ModuleFeature.Info(name = "Long Jump", description = "Jump wider", category = ModuleFeature.Category.MOVEMENT)
public class LongJumpFeature extends ModuleFeature {

    @Value(name = "Mode")
    private final ComboBox<String> mode = new ComboBox<>("Sentinel", new String[]{"Vanilla"});

    @Value(name = "Vanilla - Jump Motion", displayName = "Jump Motion")
    private final SliderSetting<Float> vanillaJumpMotion = new SliderSetting<>(0.42F, 0.0F, 1.0F, new Dependency<>(mode, "Vanilla"));

    @Value(name = "Vanilla - Motion Multiplier", displayName = "Motion Multiplier")
    private final SliderSetting<Double> vanillaMotionMultiplier = new SliderSetting<>(1.0, 0.0, 1.0, new Dependency<>(mode, "Vanilla"));

    @Value(name = "Vanilla - Speed Motion", displayName = "Speed Motion")
    private final SliderSetting<Double> vanillaSpeedMotion = new SliderSetting<>(0.4, 0.0, 1.0, new Dependency<>(mode, "Vanilla"));

    private boolean disable;

    @Override
    public void onEnable() {
        super.onEnable();
        disable = false;
    }

    @EventHandler
    public final Listener<PacketEvent> packetEvent = e -> {
        switch (mode.getValue()) {
            case "Sentinel":
                break;
        }
    };

    @EventHandler
    public final Listener<UpdateEvent> updateEvent = e -> {
        switch (mode.getValue()) {
            case "Sentinel":
                doSentinel();
                break;
            case "Vanilla":
                if (getPlayer().onGround) {
                    if(disable) {
                        setEnabled(false);
                        return;
                    }
                    getPlayer().motionY = vanillaJumpMotion.getValue();
                }
                else {
                    disable = true;
                    if (isMoving())
                        setSpeed(vanillaSpeedMotion.getValue());
                    if (getPlayer().motionY < 0)
                        getPlayer().motionY *= vanillaMotionMultiplier.getValue();
                }
                break;
        }
    };

    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    private void doSentinel() {
        if (!ServerUtil.isCubeCraft()) {
            if (mc.player.ticksExisted % 4 == 0)
                mc.player.jump();
            sendPacket(new CPacketSpectate(UUID.randomUUID()));
            sendPacket(new CPacketSpectate(UUID.randomUUID()));
            sendPacket(new CPacketSpectate(UUID.randomUUID()));
            sendPacket(new CPacketSpectate(UUID.randomUUID()));
            return;
        }
        if (disable && mc.player.onGround) {
            setEnabled(false);
            return;
        }
        setSpeed(getSpeed());
        if (mc.player.onGround) {
            mc.player.jump();
            setPosition(getX(), getY() + 2, getZ());
        } else disable = true;
        if (isMoving()) {
            for (int i = 0; i < 4; i++) {
                final double x = getX() - Math.sin(getDirection()) * getSpeed(), y = (int) getY(), z = getZ() + Math.cos(getDirection()) * getSpeed();
                if (!mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(-getX(), -getY(), -getZ()).offset(x, y, z)).isEmpty())
                    break;
                sendPacket(new CPacketPlayer.Position(x, y, z, false));
                setPosition(x, y, z);
            }
        }
        if (getPlayer().fallDistance > 0)
            getPlayer().motionY *= 0.25;
        setPosition(getX(), (int) getY(), getZ());
    }
}
