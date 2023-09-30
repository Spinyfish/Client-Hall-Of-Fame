package Reality.Realii.mods.modules.world;

import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import Reality.Realii.event.EventHandler;
import Reality.Realii.event.events.world.EventMove;
import Reality.Realii.event.events.world.EventPacketSend;
import Reality.Realii.event.events.world.EventPreUpdate;
import Reality.Realii.event.value.Numbers;
import Reality.Realii.event.value.Option;
import Reality.Realii.mods.Module;
import Reality.Realii.mods.ModuleType;
import Reality.Realii.utils.cheats.player.PlayerUtils;
import Reality.Realii.event.EventHandler;
import Reality.Realii.event.events.world.EventMove;
import Reality.Realii.event.events.world.EventPreUpdate;
import Reality.Realii.event.value.Mode;
import Reality.Realii.managers.ModuleManager;
import Reality.Realii.mods.Module;
import Reality.Realii.mods.ModuleType;
import Reality.Realii.utils.cheats.world.TimerUtil;
import Reality.Realii.utils.math.MathUtil;
import libraries.optifine.MathUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import Reality.Realii.event.EventHandler;
import Reality.Realii.event.events.world.EventPacketSend;
import Reality.Realii.mods.Module;
import Reality.Realii.mods.ModuleType;
import Reality.Realii.utils.cheats.world.TimerUtil;

public class Disabler extends Module{
	private Mode mode = new Mode("Mode", "Mode", new String[]{"BlocksMcLessFlags", "VerusCevata","WatchdogSpoof"}, "BlocksMcLessFlags");
	public static Numbers<Number>  Ping = new Numbers<>("WatchdogPingSpoofAmount", 100, 100, 10089, 823);
	  private List<Packet> packetList = new CopyOnWriteArrayList<Packet>();
	    private TimerUtil timer = new TimerUtil();
	    private Option<Boolean> sufix = new Option<Boolean>("ShowSufix", "ShowSufix", false);

	public Disabler(){
		super("Disabler", ModuleType.World);
		  this.addValues(this.mode, sufix, Ping);
		
	}

    @Override
    public void onEnable() {
        super.onEnable();

    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
    
    @EventHandler
    private void onPacketSend(EventPacketSend e) {
    	  if (this.mode.getValue().equals("VerusCevata")) {
    	mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(null));
    	mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(null));
    	this.mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY + 0.42, this.mc.thePlayer.posZ, this.mc.thePlayer.onGround));
		this.mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY + 0.753, this.mc.thePlayer.posZ, this.mc.thePlayer.onGround));
		mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY - 1, this.mc.thePlayer.posZ, this.mc.thePlayer.onGround));
    }
    	  if (this.mode.getValue().equals("BlocksMcLessFlags")) {
    	        if (e.getPacket() instanceof C00PacketKeepAlive && this.mc.thePlayer.isEntityAlive()) {
    	            this.packetList.add(e.getPacket());
    	            e.setCancelled(true);
    	        }
    	        if (this.timer.hasReached(750.0)) {
    	            if (!this.packetList.isEmpty()) {
    	                int i = 0;
    	                double totalPackets = MathUtils.getIncremental(Math.random() * 10.0, 1.0);
    	                for (Packet packet : this.packetList) {
    	                    if ((double)i >= totalPackets) continue;
    	                    ++i;
    	                    this.mc.getNetHandler().getNetworkManager().sendPacket(packet);
    	                    this.packetList.remove(packet);
    	                }
    	            }
    	            this.mc.getNetHandler().getNetworkManager().sendPacket(new C00PacketKeepAlive(10000));
    	            this.timer.reset();
    	        }
    		  
    	  }
    	  
    	  if (this.mode.getValue().equals("WatchdogSpoof")) {
  	        if (e.getPacket() instanceof C00PacketKeepAlive && this.mc.thePlayer.isEntityAlive()) {
  	            this.packetList.add(e.getPacket());
  	            e.setCancelled(true);
  	        }
  	        if (this.timer.hasReached(750.0)) {
  	            if (!this.packetList.isEmpty()) {
  	                int i = 0;
  	                double totalPackets = MathUtils.getIncremental(Math.random() * 10.0, 1.0);
  	                for (Packet packet : this.packetList) {
  	                    if ((double)i >= totalPackets) continue;
  	                    ++i;
  	                    this.mc.getNetHandler().getNetworkManager().sendPacket(packet);
  	                    this.packetList.remove(packet);
  	                }
  	            }
  	            this.mc.getNetHandler().getNetworkManager().sendPacket(new C00PacketKeepAlive(Ping.getValue().intValue()));
  	            this.timer.reset();
  	        }
  		  
  	  }
    	  
    }
    
    @EventHandler
    public void onUpdate(EventPreUpdate e) {
    	if (this.sufix.getValue().booleanValue()) {
            this.setSuffix(mode.getModeAsString());
            
        }
    }
}



   
    
    


