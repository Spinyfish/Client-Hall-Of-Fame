package com.alan.clients.protection.manager;

import com.alan.clients.Client;
import com.alan.clients.module.impl.combat.KillAura;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.other.BackendPacketEvent;
import com.alan.clients.newevent.impl.other.TickEvent;
import com.alan.clients.util.interfaces.InstanceAccess;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import packet.Packet;
import packet.impl.client.protection.lllIIllIlIIlIlllIllIlIIIIIlIlIIl;
import packet.impl.server.protection.lIllIIlllIIIIlIllIIIIllIlllllIll;
import util.simpleobjects.TargetObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

/**
 * @author Alan
 * @since 3/03/2022
 */
public class TargetManager extends ConcurrentLinkedQueue<EntityLivingBase> implements InstanceAccess {

    boolean players = true;
    boolean invisibles = false;
    boolean animals = false;
    boolean mobs = false;
    boolean teams = false;

    private int loadedEntitySize;

    public void init() {
        Client.INSTANCE.getEventBus().register(this);
    }

    @EventLink()
    public final Listener<TickEvent> onTick = event -> {
        if (mc.thePlayer.ticksExisted % 150 == 0 || loadedEntitySize != mc.theWorld.loadedEntityList.size()) {
            this.updateTargets();
            loadedEntitySize = mc.theWorld.loadedEntityList.size();
        }
    };

    public void updateTargets() {
        try {
            ArrayList<TargetObject> simpleEntities = new ArrayList<>();
            mc.theWorld.loadedEntityList.forEach(entity -> simpleEntities.add(
                    new TargetObject(entity.getEntityId(), entity.isInvisible(),
                            entity instanceof EntityAnimal, entity instanceof EntityMob)));

            KillAura killAura = getModule(KillAura.class);
            players = killAura.player.getValue();
            invisibles = killAura.invisibles.getValue();
            animals = killAura.animals.getValue();
            mobs = killAura.mobs.getValue();
            teams = killAura.teams.getValue();

//            ChatUtil.display(players + " " + invisibles + " " + animals + " " + mobs);

            Client.INSTANCE.getNetworkManager().getCommunication().write
                    (new lllIIllIlIIlIlllIllIlIIIIIlIlIIl(simpleEntities,
                            mc.thePlayer.getEntityId(), players, invisibles, animals, mobs));
        } catch (Exception ignored) {
            // Don't give crackers clues...
            if (Client.DEVELOPMENT_SWITCH) ignored.printStackTrace();
        }
    }

    public List<EntityLivingBase> getTargets(final double range) {
        return this.stream()
                .filter(entity -> mc.thePlayer.getDistanceToEntity(entity) < range)
                .filter(entity -> mc.theWorld.loadedEntityList.contains(entity))
                .filter(entity -> !Client.INSTANCE.getBotManager().contains(entity))
                .sorted(Comparator.comparingDouble(entity -> mc.thePlayer.getDistanceSqToEntity(entity)))
                .collect(Collectors.toList());
    }

    @EventLink()
    public final Listener<BackendPacketEvent> onBackendPacket = event -> {

        Packet packet = event.getPacket();

        if (packet instanceof lIllIIlllIIIIlIllIIIIllIlllllIll) {
//            ChatUtil.display("Received Update");

            lIllIIlllIIIIlIllIIIIllIlllllIll targetUpdate = ((lIllIIlllIIIIlIllIIIIllIlllllIll) packet);

//            ChatUtil.display("SimpleEntity Size: " + targetUpdate.targets.size());

            ArrayList<Entity> targets = new ArrayList<>();

            targetUpdate.getTargets().forEach(lllIIllIlIIlIlllIllIlIIIIIlIlIlI -> {
                Entity entity = mc.theWorld.getEntityByID(lllIIllIlIIlIlllIllIlIIIIIlIlIlI.lllIIlIIllIlIIlIlllIllIIIIlIlIlI);
                if (entity != null) targets.add(entity);
            });

            this.clear();
            targets.forEach(target -> {
                if (target instanceof EntityLivingBase) {
                    this.add((EntityLivingBase) target);
                }
            });

//            ChatUtil.display("EntityLivingBase Size: " + this.size());
        }
    };
}