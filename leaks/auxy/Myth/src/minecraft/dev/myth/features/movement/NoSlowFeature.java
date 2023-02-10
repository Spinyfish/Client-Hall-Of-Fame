/**
 * @project Myth
 * @author CodeMan
 * @at 25.08.22, 21:05
 */
package dev.myth.features.movement;

import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import dev.myth.api.event.EventState;
import dev.myth.api.feature.Feature;
import dev.myth.api.utils.MovementUtil;
import dev.myth.api.utils.PacketUtil;
import dev.myth.events.PacketEvent;
import dev.myth.events.SlowDownEvent;
import dev.myth.events.UpdateEvent;
import dev.myth.features.combat.KillAuraFeature;
import dev.myth.main.ClientMain;
import dev.myth.managers.FeatureManager;
import dev.myth.settings.BooleanSetting;
import dev.myth.settings.EnumSetting;
import net.minecraft.init.Items;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.server.*;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.apache.commons.lang3.RandomUtils;

import java.util.concurrent.ThreadLocalRandom;

@Feature.Info(
        name = "NoSlow",
        description = "Prevents you from slowing down when using items",
        category = Feature.Category.MOVEMENT
)
public class NoSlowFeature extends Feature {

    public final EnumSetting<Mode> mode = new EnumSetting<>("Mode", Mode.NCP);
    public final BooleanSetting eating = new BooleanSetting("Eating", true);
    public final BooleanSetting bow = new BooleanSetting("Bow", true);

    private boolean blocked;
    private long lastBlock;

    @Handler
    public final Listener<UpdateEvent> updateEventListener = event -> {
        if (!getPlayer().isUsingItem() || !MovementUtil.isMoving()) {
            if (blocked) {
                blocked = false;
                getPlayer().sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            }
            return;
        }
        if (getPlayer().isUsingItem()) {
            if ((getPlayer().getItemInUse().getItem() == Items.bow && !bow.getValue()) || ((getPlayer().getItemInUse().getItem() instanceof ItemFood || getPlayer().getItemInUse().getItem() instanceof ItemPotion) && !eating.getValue()))
                return;
        }
        switch (mode.getValue()) {
            case HYPIXEL:
                if (getPlayer().isBlocking() && event.getState() == EventState.PRE) {
                    if (getPlayer().getHeldItem().getItem() instanceof ItemSword) {
                        if (getPlayer().ticksExisted % 3 == 0) {
                            sendPacket(new C09PacketHeldItemChange((getPlayer().inventory.currentItem + 1) % 9));
                            sendPacket(new C09PacketHeldItemChange(getPlayer().inventory.currentItem));
                        }
                    }
                }
                break;
            case NCP: {
                if(getPlayer().isBlocking()) {
                    if (event.getState() == EventState.PRE) {
                        if (blocked) {
                            sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                            blocked = false;
                        }
                    } else {
                        if (!blocked) {
                            MC.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(null));
                            blocked = true;
                        }
                    }
                }
                break;
            }
            case AAC4:
                if (System.currentTimeMillis() - lastBlock > 80) {
                    if (event.getState() == EventState.PRE) {
                        if (blocked) {
                            sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                            blocked = false;
                        }
                    } else {
                        if (!blocked) {
                            MC.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(null));
                            blocked = true;
                            lastBlock = System.currentTimeMillis();
                        }
                    }
                }
                break;
            case AAC5:
                if (event.getState() == EventState.PRE) {
                    MC.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(null));
                }
                break;
            case ZONECRAFT:
            case BLOCKSMC:
                if (event.getState() == EventState.PRE) {
                    /*if(getPlayer().isBlocking())*/ sendPacket(new C08PacketPlayerBlockPlacement(null));
//                    blocked = true;
                }
                break;
        }
    };

    @Handler
    public final Listener<PacketEvent> packetEventListener = event -> {
        if(getPlayer() == null) return;

        if(mode.is(Mode.BLOCKSMC) || mode.is(Mode.ZONECRAFT)) {
            if(!getPlayer().isUsingItem())
                return;
            if ((getPlayer().getItemInUse().getItem() == Items.bow && !bow.getValue()) || ((getPlayer().getItemInUse().getItem() instanceof ItemFood || getPlayer().getItemInUse().getItem() instanceof ItemPotion) && !eating.getValue()))
                return;

            PacketUtil.sillyS30(event);

            if(mode.is(Mode.ZONECRAFT))
                PacketUtil.sillyS2F(event);

        }
    };

    @Handler
    public final Listener<SlowDownEvent> slowDownEventListener = event -> {
        if (!MovementUtil.isMoving()) return;
        if ((getPlayer().getItemInUse().getItem() == Items.bow && !bow.getValue()) || ((getPlayer().getItemInUse().getItem() instanceof ItemFood || getPlayer().getItemInUse().getItem() instanceof ItemPotion) && !eating.getValue()))
            return;

        event.setCancelled(true);
        event.setStrafeMultiplier(1);
        event.setForwardMultiplier(1);
    };

    @Override
    public void onEnable() {
        super.onEnable();
        blocked = false;
        lastBlock = 0;
    }

    @Override
    public void onDisable() {
        super.onDisable();

        if (blocked) {
            sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            blocked = false;
        }
    }

    @Override
    public String getSuffix() {
        return mode.getValue().toString();
    }

    public enum Mode {
        VANILLA("Vanilla"),
        NCP("NCP"),
        AAC4("AAC4"),
        AAC5("AAC5"),
        BLOCKSMC("BlocksMC"),
        ZONECRAFT("Zonecraft"),
        HYPIXEL("Hypixel");

        private final String name;

        Mode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
