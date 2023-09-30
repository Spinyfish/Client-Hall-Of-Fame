/*
 * Decompiled with CFR 0_132.
 */
package Reality.Realii.mods.modules.world;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import Reality.Realii.event.EventHandler;
import Reality.Realii.event.events.world.EventPreUpdate;
import Reality.Realii.event.value.Numbers;
import Reality.Realii.event.value.Option;
import Reality.Realii.managers.FriendManager;
import Reality.Realii.mods.Module;
import Reality.Realii.mods.ModuleType;
import Reality.Realii.utils.cheats.world.TimerUtil;

import java.util.ArrayList;

public class Banwave
extends Module {
    private TimerUtil timer = new TimerUtil();
    public ArrayList<Entity> banned;
    private String banMessage = " bannnnnn!";
    private Option<Boolean> tempBan = new Option<Boolean>("Temp Ban", "temp", false);
    private Numbers<Number> banDelay = new Numbers<Number>("Delay", "delay", 10.0, 1.0, 20.0, 1.0);

    public Banwave() {
        super("Ban everyone", ModuleType.Render);
        this.banned = new ArrayList();
        this.addValues(this.tempBan, this.banDelay);
    }

    @Override
    public void onEnable() {
        this.banned.clear();
        super.onEnable();
    }

    @EventHandler
    public void onUpdate(EventPreUpdate event) {
        for (Object o : this.mc.theWorld.getLoadedEntityList()) {
            if (!(o instanceof EntityOtherPlayerMP)) continue;
            EntityOtherPlayerMP e = (EntityOtherPlayerMP)o;
            if (!this.timer.hasReached(this.banDelay.getValue().intValue() * 100.0) || FriendManager.isFriend(e.getName()) || e.getName() == this.mc.thePlayer.getName() || this.banned.contains(e)) continue;
            if (this.tempBan.getValue().booleanValue()) {
                this.mc.thePlayer.sendChatMessage("/tempban " + e.getName() + " 7d" + " " + this.banMessage);
                System.out.println("/tempban " + e.getName() + " 7d" + " " + this.banMessage);
            } else {
                this.mc.thePlayer.sendChatMessage("/ban " + e.getName() + " " + this.banMessage);
                System.out.println("/ban " + e.getName() + " " + this.banMessage);
            }
            this.banned.add(e);
            this.timer.reset();
        }
    }
}

