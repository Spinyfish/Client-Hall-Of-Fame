/**
 * @project mythrecode
 * @prod HardcodeProductions
 * @author Auxy
 * @at 06.09.22, 01:50
 */
package dev.myth.features.combat;

import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import dev.myth.api.event.EventState;
import dev.myth.api.feature.Feature;
import dev.myth.api.utils.MovementUtil;
import dev.myth.api.utils.math.MathUtil;
import dev.myth.events.MoveEvent;
import dev.myth.events.PacketEvent;
import dev.myth.events.UpdateEvent;
import dev.myth.features.movement.SpeedFeature;
import dev.myth.main.ClientMain;
import dev.myth.managers.FeatureManager;
import dev.myth.settings.EnumSetting;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S06PacketUpdateHealth;
import net.minecraft.util.MovingObjectPosition;

@Feature.Info(name = "Criticals", category = Feature.Category.COMBAT)
public class CriticalsFeature extends Feature {

    public final EnumSetting<Mode> mode = new EnumSetting<>("Mode", Mode.WATCHDOG);

    private KillAuraFeature killAuraFeature;
    private SpeedFeature speedFeature;

    public int level;

    @Handler
    public final Listener<UpdateEvent> updateEventListener = event -> {
        if (killAuraFeature == null || speedFeature == null) {
            FeatureManager featureManager = ClientMain.INSTANCE.manager.getManager(FeatureManager.class);
            killAuraFeature = featureManager.getFeature(KillAuraFeature.class);
            speedFeature = featureManager.getFeature(SpeedFeature.class);
        }

        if(event.getState() != EventState.PRE) return;

        if (getPlayer().ticksExisted < 10
                || getPlayer().isInWeb || getPlayer().isInLava()
                || getPlayer().isInWater() || getPlayer().isOnLadder()
                || speedFeature.isEnabled() || getPlayer().posY != getPlayer().lastTickPosY) {
            level = 0;
            return;
        }

        Block block = getPlayer().worldObj.getBlockState(getPlayer().getPosition().add(0, -0.5, 0)).getBlock();
        if (block instanceof BlockStairs || block instanceof BlockSlab) {
            level = 0;
            return;
        }

        if (killAuraFeature.target != null) {
            switch (mode.getValue()) {
                case WATCHDOG:
                    if (MovementUtil.isOnGround() && getPlayer().posY == Math.round(getPlayer().posY)) {
                        switch (level % 3) {
                            case 1:
                                event.setPosY(event.getPosY() + 0.0005 + Math.random() * 0.00005);
                                event.setOnGround(false);
                                break;
                            case 2:
                                event.setPosY(event.getPosY() + 0.0003 + Math.random() * 0.00005);
                                event.setOnGround(false);
                                break;
                        }
                        level++;
                    } else {
                        level = 0;
                    }
                    break;
            }
        }

    };

    @Handler
    public final Listener<PacketEvent> packetEventListener = event -> {
        if (getPlayer() == null || killAuraFeature == null || killAuraFeature.target == null)
            return;

        if (getPlayer().ticksExisted < 10 || getPlayer().isInWeb || getPlayer().isInLava() || getPlayer().isInWater() || getPlayer().isOnLadder())
            return;

        switch (mode.getValue()) {
            case PACKET:
                sendPacketUnlogged(new C03PacketPlayer.C04PacketPlayerPosition(getX(), getY() + 0.11F, getZ(), false));
                sendPacketUnlogged(new C03PacketPlayer.C04PacketPlayerPosition(getX(), getY(), getZ(), false));
                break;
            case LOW:
                if(getPlayer().onGround) getPlayer().motionY = 0.06D;
                break;
        }
    };


    @Override
    public void onEnable() {
        level = 0;
        super.onEnable();
    }

    @Override
    public String getSuffix() {
        return mode.getValue().toString();
    }

    public enum Mode {
        WATCHDOG("Watchdog"),
        PACKET("Packet"),
        LOW("Low");

        private final String name;

        Mode(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

}
