package club.marsh.bloom.api.components;

import club.marsh.bloom.impl.events.PacketEvent;
import com.google.common.eventbus.Subscribe;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

public enum BlinkingComponent
{
    INSTANCE(false);
	
    @Getter @Setter
    public boolean serverside;
    
    @Getter
    public boolean on = false;
    private Minecraft mc = Minecraft.getMinecraft();
    
    @Getter
    private Deque<Packet<?>> packetDeque = new ConcurrentLinkedDeque<>();
    
    BlinkingComponent(boolean serverside)
    {
        this.serverside = serverside;
    }
    
    public void releasePackets()
    {
        while (!packetDeque.isEmpty())
        {
            Packet packet = packetDeque.poll();
            
            if (packet.getClass().getName().contains("client"))
            {
            	mc.getNetHandler().addToSendQueueSilent(packet);
            }
            
            else if (serverside)
            {
            	packet.processPacket(mc.getNetHandler());
            }
        }
        
        packetDeque.clear();
    }
    
    public void setOn(boolean on)
    {
        this.on = on;
        
        if (on == false) {
            releasePackets();
        }
    }
    
    @Subscribe
    public void onPacket(PacketEvent e)
    {
        if (!on)
        {
            return;
        }
        
        if (mc.thePlayer == null || mc.thePlayer.ticksExisted <= 1 || mc.thePlayer.isDead)
        {
            this.setOn(false);
            packetDeque.clear();
        }
        
        if (e.getPacket().getClass().getName().contains("client") || serverside)
        {
            if (e.isCancelled())
            {
                return;
            }
            
            e.setCancelled(true);
            packetDeque.add(e.getPacket());
        }
    }
}
