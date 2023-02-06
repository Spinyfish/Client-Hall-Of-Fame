package dev.elysium.client.mods.impl.combat;

import dev.elysium.base.events.types.EventTarget;
import dev.elysium.base.mods.Category;
import dev.elysium.base.mods.Mod;
import dev.elysium.base.mods.settings.ModeSetting;
import dev.elysium.base.mods.settings.NumberSetting;
import dev.elysium.client.Elysium;
import dev.elysium.client.events.EventMotion;
import dev.elysium.client.events.EventVelocity;
import net.minecraft.network.Packet;
import net.minecraft.util.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Velocity extends Mod {
    public HashMap<Vec3, Long> stored_velocity = new HashMap<Vec3, Long>();
    public double motionYexploit;
    public boolean shouldExploit, shouldStop = false;

    public ModeSetting mode = new ModeSetting("Mode",this,"Packet","Reverse","Exploit","Stop","Delay");
    public NumberSetting xzmodifier = new NumberSetting("XZ Modifier",-100,100,0,1,this);
    public NumberSetting ymodifier = new NumberSetting("Y Modifier",-100,100,0,1,this);

    public NumberSetting stopdelay = new NumberSetting("Stop Delay",1,10,1,1,this);

    public NumberSetting delay = new NumberSetting("Delay (MS)",0,5000,500,5,this);
    public Velocity() {
        super("Velocity","Modifies incoming velocity", Category.COMBAT);
    }

    @EventTarget
    public void onEventVelocity(EventVelocity e) {
        switch (mode.getMode()) {
            case "Packet":
                e.x *= xzmodifier.getValue() / 100;
                e.z *= xzmodifier.getValue() / 100;
                e.y *= ymodifier.getValue()  / 100;
                break;
            case "Reverse":
                e.x *= -1;
                e.z *= -1;
                break;
            case "Stop":
                shouldStop = true;
                break;
            case "Delay":
                stored_velocity.put(new Vec3(e.x,e.y,e.z),System.currentTimeMillis());
                e.setCancelled(true);
                break;
            case "Exploit":
                shouldExploit = true;
                motionYexploit = e.y;
                e.setCancelled(true);
                break;
        }
    }

    @EventTarget
    public void onEventMotion(EventMotion e) {
        switch (mode.getMode()) {
            case "Stop":
                if(shouldStop && mc.thePlayer.hurtTime == 10-stopdelay.getValue()) {
                    mc.thePlayer.motionX = mc.thePlayer.motionZ = 0;
                    shouldStop = false;
                }
                break;
            case "Delay":
                List<Object []> removelist = new ArrayList<>();
                for (Vec3 p : stored_velocity.keySet()) {
                    long timestamp = stored_velocity.get(p);
                    if (timestamp < System.currentTimeMillis() - delay.getValue()) {
                        mc.thePlayer.motionX = p.xCoord;
                        mc.thePlayer.motionY = p.yCoord;
                        mc.thePlayer.motionZ = p.zCoord;
                        removelist.add(new Object[]{p, timestamp});
                    }
                }

                for (Object[] o : removelist) {
                    stored_velocity.remove(o[0], o[1]);
                }
                break;
            case "Exploit":
                if(shouldExploit && mc.thePlayer.hurtTime == 9 && e.isPre()) {
                    e.y += motionYexploit;
                    shouldExploit = false;
                }
                break;
        }
    }
}
