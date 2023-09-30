package me.nekoWare.client.module.movement;

import me.hippo.api.lwjeb.annotation.Handler;
import me.nekoWare.client.event.events.EventCollide;
import me.nekoWare.client.event.events.MovementEvent;
import me.nekoWare.client.event.events.PacketOutEvent;
import me.nekoWare.client.event.events.UpdateEvent;
import me.nekoWare.client.module.Module;
import me.nekoWare.client.util.blocks.misc.BlockUtil;
import me.nekoWare.client.util.misc.Timer;
import me.nekoWare.client.util.movement.MovementUtils;
import me.nekoWare.client.util.packet.PacketUtil;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C18PacketSpectate;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovementInput;
import org.apache.commons.lang3.RandomUtils;

import java.util.function.Consumer;

public class Fly extends Module {

    private Timer timer = new Timer();

    private double y;

    public Fly() {
        super("Fly", 34, Module.Category.MOVEMENT);
        this.addDouble("Speed", 2.5, 0.1, 5.0);
        this.addModes("Vanilla", "Damage", "Verus");
        this.addBoolean("Anti-Kick", false);
    }

    @Handler
    public Consumer<MovementEvent> eventConsumer0 = event -> {

    };

    @Handler
    public Consumer<UpdateEvent> eventConsumer1 = (event) -> {

        if (isMode("Vanilla")) {
            mc.timer.timerSpeed = 1f;
            if (event.isPre()) {
                float speed = getDouble("Speed").floatValue();
                MovementInput movementInput = mc.thePlayer.movementInput;
                mc.thePlayer.motionY = movementInput.isJumping() ? speed * 0.5F
                        : movementInput.isSneaking() ? -speed * 0.5F
                        : 0.0F;
                MovementUtils.setMoveSpeed(speed);
                if (getBool("Anti-Kick") && timer.delay(700.0f)) {
                    MovementUtils.fallPacket();
                    MovementUtils.ascendPacket();
                    timer.reset();
                }
            }
        }
        
        if (isMode("Damage")) {
        	
        	
            mc.timer.timerSpeed = 1f;
			if (event.isPre()) {
				mc.thePlayer.sendQueue.addToSendQueue(new C0CPacketInput());
				mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
				mc.thePlayer.onGround = false;
				float speed = getDouble("Speed").floatValue();
				MovementInput movementInput = mc.thePlayer.movementInput;
				mc.thePlayer.motionY = movementInput.isJumping() ? speed * 0.5F
						: movementInput.isSneaking() ? -speed * 0.5F : 0.0F;
				MovementUtils.setMoveSpeed(speed);
			}
        }
        
        if (isMode("Verus")) {
        	

        	mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.5, mc.thePlayer.posZ), 1, new ItemStack(Blocks.stone.getItem(mc.theWorld, new BlockPos(-1, -1, -1))), 0, 0.94f, 0));
            MovementUtils.setMoveSpeed(0.36);
            mc.timer.timerSpeed = 1f;



            if (y >= 11) {
                y = 0;
            }

            y++;

    mc.thePlayer.motionY = 0f;
        }
    };

    @Handler
    public Consumer<PacketOutEvent> eventConsumer11 = (event) -> {
        if (event.getPacket() instanceof C0FPacketConfirmTransaction) {
            event.cancel();

            timer.reset();
        }
        
        if (event.getPacket() instanceof C00PacketKeepAlive) {
            event.cancel();

            timer.reset();
        }
    };

    @Override
    public void onEnable() {
if(this.isMode("Damage")) {
    	mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
				mc.thePlayer.posY + 3.05, mc.thePlayer.posZ, false));
		mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
				mc.thePlayer.posY, mc.thePlayer.posZ, false));
		mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
				mc.thePlayer.posY + 0.41999998688697815, mc.thePlayer.posZ, true));
		mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.5, mc.thePlayer.posZ);
		}
    }

    @Override
    public void onDisable() {

    	MovementUtils.setMoveSpeed(0);
    	
    }
}