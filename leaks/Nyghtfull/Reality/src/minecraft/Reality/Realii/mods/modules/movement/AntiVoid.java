package Reality.Realii.mods.modules.movement;

import net.minecraft.block.BlockAir;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;
import Reality.Realii.event.EventHandler;
import Reality.Realii.event.events.world.EventPacketSend;
import Reality.Realii.event.events.world.EventPreUpdate;
import Reality.Realii.event.value.Mode;
import Reality.Realii.event.value.Option;
import Reality.Realii.mods.Module;
import Reality.Realii.mods.ModuleType;
import Reality.Realii.utils.cheats.world.TimerUtil;

import java.util.ArrayList;
public class AntiVoid
        extends Module {
	private Mode mode = new Mode("Mode","Mode",new String[] {"BlocksMc","dosentwork"},"BlocksMc");
	private Option<Boolean> sufix = new Option<Boolean>("ShowSufix", "ShowSufix", false);
    public AntiVoid() {
        super("AntiVoid", ModuleType.Movement);
        this.addValues(this.mode, this.sufix);
    }

    public ArrayList<Packet> packets = new ArrayList();
    public TimerUtil timerUtil = new TimerUtil();
    
    

    @EventHandler
    public void onPacket(EventPacketSend e) {
        if (this.mc.thePlayer.fallDistance < 3.0f) {
            return;
        }
        boolean blockUnderneath = false;
        int i = 0;
        while ((double) i < this.mc.thePlayer.posY + 2.0) {
            BlockPos pos = new BlockPos(this.mc.thePlayer.posX, (double) i, this.mc.thePlayer.posZ);
            if (!(this.mc.theWorld.getBlockState(pos).getBlock() instanceof BlockAir)) {
                blockUnderneath = true;
            }
            ++i;
        }
        if (blockUnderneath) {
            return;
        }
        if(mode.getValue().equals("Hypixel2"))
        	mc.thePlayer.sendQueue.addToSendQueueWithoutEvent(new C03PacketPlayer.C04PacketPlayerPosition(((C03PacketPlayer) e.getPacket()).getPositionX(), ((C03PacketPlayer) e.getPacket()).getPositionY(), ((C03PacketPlayer) e.getPacket()).getPositionZ(), ((C03PacketPlayer) e.getPacket()).isOnGround()));
//        if (!this.mc.thePlayer.onGround && !this.mc.thePlayer.isCollidedVertically) {
//            if (packets.size() < 10 && (e.getPacket() instanceof C03PacketPlayer.C06PacketPlayerPosLook || e.getPacket() instanceof C03PacketPlayer.C04PacketPlayerPosition)) {
//                packets.add(e.getPacket());
//                e.setCancelled(true);
//            } else {
//                for (Packet p : packets) {
//                    mc.getNetHandler().addToSendQueueWithoutEvent(p);
//                }
//                packets.clear();
//            }
//        }
    }

    @EventHandler
    private void onUpdate(EventPreUpdate e) {
    	if (this.sufix.getValue().booleanValue()) {
    		this.setSuffix(mode.getModeAsString());
    		
    	}
    	 
        if(!mode.getValue().equals("BlocksMc"))
        	return;
        boolean blockUnderneath = false;
        int i = 0;
        while ((double) i < this.mc.thePlayer.posY + 2.0) {
            BlockPos pos = new BlockPos(this.mc.thePlayer.posX, (double) i, this.mc.thePlayer.posZ);
            if (!(this.mc.theWorld.getBlockState(pos).getBlock() instanceof BlockAir)) {
                blockUnderneath = true;
            }
            ++i;
        }
        if (blockUnderneath) {
            return;
        }
        if (this.mc.thePlayer.fallDistance < 4.0f) {
            return;
        }

           mc.thePlayer.motionY = 0.0;


//        if (!this.mc.thePlayer.onGround && !this.mc.thePlayer.isCollidedVertically) {
//            this.mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 100, mc.thePlayer.posZ);
//        }
    }
}

