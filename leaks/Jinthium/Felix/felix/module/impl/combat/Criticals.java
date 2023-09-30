package felix.module.impl.combat;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import felix.Client;
import felix.api.annotations.Handler;
import felix.events.packet.EventPacketReceive;
import felix.events.packet.EventPacketSend;
import felix.events.player.EventMotionUpdate;
import felix.module.Module;
import felix.module.impl.movement.Flight;
import felix.module.impl.movement.Speed;
import felix.util.other.TimeHelper;
import felix.util.player.MovementUtils;
import felix.value.impl.EnumValue;
import felix.value.impl.NumberValue;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;

public class Criticals extends Module {
	
    public EnumValue<Mode> mode = new EnumValue("Mode", Mode.Packet);
    
    boolean attacking;
	private TimeHelper timer = new TimeHelper();

	int stage, count;
	double y;
	private int groundTicks;
	
    private long ms;
    private int ticks;
    private boolean c;
    private long ms2;
    private long ms3;
	
	public Criticals() {
		super("Criticals", 0, ModuleCategory.COMBAT);
		addValues(mode);
	}

	@Override
    public void onEnable() {
    	attacking = false;
    	groundTicks = 0;
    	timer.reset();
    	super.onEnable();
    }
    
    private enum Mode {
    	Vanilla, Packet, Pos, Watchdog
    }

    @Handler
    public void a(EventMotionUpdate event){
        if (event.isPre())
            this.groundTicks = MovementUtils.isOnGround() ? this.groundTicks + 1 : 0;
    }
    
	@Handler
    public void onMotionUpdate(final EventMotionUpdate event) {
    	setSuffix(mode.getValueAsString());
        final KillAura killAura = (KillAura) Client.INSTANCE.getModuleManager().getModule(KillAura.class);
    	switch (mode.getValue()) {
    	case Packet:
    		final double y = event.getPosY();
            if (System.currentTimeMillis() - ms2 >= 450L) {
                ms2 = System.currentTimeMillis();
                ticks = 0;
                return;
            }
            if (event.isPre() && shouldCritical() && killAura.target != null && killAura.target.hurtTime < 12) {
                event.setOnGround(false);
                if (++ticks == 1) {
                    event.setPosY(event.getPosY() + 1.05E-8);
                }
                switch (ticks) {
                    case 2:
                        event.setPosY(event.getPosY() + 1.05E-9);
                        break;
                    case 3:
                        event.setPosY(event.getPosY() + 1.05E-10);
                        break;
                    case 4:
                        event.setPosY(event.getPosY() + 1.05E-11);
                        ticks = 0;
                        break;
                }
            }
            if (event.isPost() && shouldCritical() && killAura.target != null && System.currentTimeMillis() - ms >= 1050L) {
                ms = System.currentTimeMillis();
                final double[] array = {0.06259999647412343, 1.05E-11, 1.05E-11};
                for (int length = array.length, i = 0; i < length; ++i) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + array[i], mc.thePlayer.posZ, false));
                }
                event.setOnGround(false);
                event.setPosY(y + 3.6799195752495E-10);
            }
            break;
    		case Pos:
    			if (event.isPre() && killAura.target != null) {
	    	        if (mc.thePlayer.hurtResistantTime > 13 && shouldCritical() && timer.reach(500)) {
	    	            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.062575, mc.thePlayer.posZ, false));
	    	            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.015, mc.thePlayer.posZ, false));
	    	            timer.reset();
	    	        }
	    	    }
    			break;
    		case Vanilla:
    			boolean nig = mc.thePlayer.ticksExisted % 6 == 0 ? mc.thePlayer.onGround : false;
    			if (attacking) {
    			event.setOnGround(nig);
    		}
    		break;
    	}
    }

    @Handler
    public void aa(EventPacketSend event){
        if (event.getPacket() instanceof C0APacketAnimation && hasTarget()) {
            if (this.timer.isDelayComplete(500)) {
                if (this.groundTicks > 1) {
                    for (double offset : new double[]{0.056f, 0.016f, 0.003f}) {
                        mc.getNetHandler().addToSendQueueNoEvent(
                                new C03PacketPlayer.C04PacketPlayerPosition(
                                        this.mc.thePlayer.posX,
                                        this.mc.thePlayer.posY + offset + (StrictMath.random() * 0.0003F),
                                        this.mc.thePlayer.posZ,
                                        false));
                    }
                    this.timer.reset();
                }
            }
        }
    }

    private boolean hasTarget() {
        if (Client.INSTANCE.getModuleManager().aura.target != null)
            return true;
        final MovingObjectPosition target = mc.objectMouseOver;
        return target != null && target.entityHit != null;
    }
    
    private boolean shouldCritical() {
		return mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically && !Client.INSTANCE.getModuleManager().getModule(Speed.class).isEnabled() && !Client.INSTANCE.getModuleManager().getModule(Flight.class).isEnabled() && mc.thePlayer.fallDistance == 0;
	}

	@Handler
    public void onSendPacket(final EventPacketSend event) {
    	final C02PacketUseEntity packet = (C02PacketUseEntity) event.getPacket();
    	attacking = event.getPacket() instanceof C02PacketUseEntity && packet.getAction().equals(C02PacketUseEntity.Action.ATTACK);
    }
}
