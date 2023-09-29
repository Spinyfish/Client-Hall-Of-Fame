/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  org.apache.commons.lang3.RandomUtils
 */
package me.nekoWare.client.module.movement;

import me.hippo.api.lwjeb.annotation.Handler;
import me.nekoWare.client.event.events.MovementEvent;
import me.nekoWare.client.event.events.PacketInEvent;
import me.nekoWare.client.event.events.UpdateEvent;
import me.nekoWare.client.module.Module;
import me.nekoWare.client.util.ChatUtil;
import me.nekoWare.client.util.blocks.misc.BlockUtil;
import me.nekoWare.client.util.movement.DynamicFriction;
import me.nekoWare.client.util.movement.MovementUtils;
import me.nekoWare.client.util.packet.PacketUtil;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C18PacketSpectate;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import org.apache.commons.lang3.RandomUtils;

import java.util.function.Consumer;

public class Speed extends Module {
    private DynamicFriction dynamicNCPFriction = new DynamicFriction();
    private double speed, currentDistance, lastDistance;
    public boolean prevOnGround;

    public Speed() {
        super("Speed", 33, Module.Category.MOVEMENT);
        addModes("Vanilla", "Verus");
        addBoolean("Flag Check", true);
        addDouble("Speed", 1, 0, 5);
    }

    @Handler
    public Consumer<PacketInEvent> eventConsumer0 = (event) -> {
        if (event.getPacket() instanceof S08PacketPlayerPosLook && getBool("Flag Check")) {
            if (mc.thePlayer != null && mc.theWorld != null) {
                ChatUtil.printChat("Speed disabled because you flagged!");
                toggle();
            }
        }

    };

    @Handler
    public Consumer<MovementEvent> eventConsumer1 = (event) -> {

        if (isMode("Vanilla")) {
            MovementUtils.setMoveSpeed(this.getDouble("Speed"));
            
            if (mc.thePlayer.isMovingOnGround()) {
                MovementUtils.setMoveSpeed(this.getDouble("Speed")*1.25);
                event.setY(mc.thePlayer.motionY = 0.42F);
            }
        }
        
        if (isMode("Verus")) {
            
            if (mc.thePlayer.isMovingOnGround()) {
                MovementUtils.setMoveSpeed(0.42);
                event.setY(mc.thePlayer.motionY = 0.42F);
            }else {
                MovementUtils.setMoveSpeed(0.36);
            }
        }
     
    };

    @Handler
    public Consumer<UpdateEvent> eventConsumer2 = (event) -> {
    	
    };

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {
    	MovementUtils.setMoveSpeed(0);
    }
}