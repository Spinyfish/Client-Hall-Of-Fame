/*
 * Decompiled with CFR 0.152.
 */
package com.alan.clients.protection.manager;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import packet.Packet;
import packet.impl.client.protection.lllIIllIlIIlIlllIllIlIIIIIlIlIIl;
import packet.impl.server.protection.lIllIIlllIIIIlIllIIIIllIlllllIll;
import util.simpleobjects.lllIIllIlIIlIlllIllIlIIIIIlIlIlI;
import wtf.monsoon.Wrapper;
import wtf.monsoon.impl.event.EventBackendPacket;
import wtf.monsoon.impl.event.EventUpdate;

public class TargetManager
extends ConcurrentLinkedQueue<EntityLivingBase> {
    private int loadedEntitySize;
    @EventLink
    public final Listener<EventUpdate> eventUpdateListener = event -> {
        if (Minecraft.getMinecraft().thePlayer.ticksExisted % 150 == 0 || this.loadedEntitySize != Minecraft.getMinecraft().theWorld.loadedEntityList.size()) {
            // empty if block
        }
    };
    @EventLink
    public final Listener<EventBackendPacket> onBackendPacket = event -> {
        Packet packet = event.getPacket();
        if (packet instanceof lIllIIlllIIIIlIllIIIIllIlllllIll) {
            lIllIIlllIIIIlIllIIIIllIlllllIll targetUpdate = (lIllIIlllIIIIlIllIIIIllIlllllIll)packet;
            ArrayList<Entity> targets = new ArrayList<Entity>();
            targetUpdate.getTargets().forEach((? super T lllIIllIlIIlIlllIllIlIIIIIlIlIlI2) -> targets.add(Minecraft.getMinecraft().theWorld.getEntityByID(lllIIllIlIIlIlllIllIlIIIIIlIlIlI2.lllIIlIIllIlIIlIlllIllIIIIlIlIlI)));
            this.clear();
            targets.forEach((? super E target) -> {
                if (target instanceof EntityLivingBase) {
                    this.add((EntityLivingBase)target);
                }
            });
        }
    };

    public void init() {
        Wrapper.getEventBus().subscribe(this);
    }

    public void updateTargets(boolean players, boolean animals, boolean mobs, boolean invisibles) {
        try {
            ArrayList<lllIIllIlIIlIlllIllIlIIIIIlIlIlI> simpleEntities = new ArrayList<lllIIllIlIIlIlllIllIlIIIIIlIlIlI>();
            Minecraft.getMinecraft().theWorld.loadedEntityList.forEach((? super T entity) -> simpleEntities.add(new lllIIllIlIIlIlllIllIlIIIIIlIlIlI(entity.getEntityId(), entity.isInvisible(), entity instanceof EntityAnimal, entity instanceof EntityMob)));
            Wrapper.getMonsoon().getNetworkManager().getCommunication().write(new lllIIllIlIIlIlllIllIlIIIIIlIlIIl(simpleEntities, Minecraft.getMinecraft().thePlayer.getEntityId(), players, invisibles, animals, mobs));
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public List<EntityLivingBase> getTargets(double range, boolean players, boolean animals, boolean mobs, boolean invisibles, boolean walls) {
        return this.stream().filter(entity -> (double)Minecraft.getMinecraft().thePlayer.getDistanceToEntity((Entity)entity) < range).filter(entity -> Minecraft.getMinecraft().theWorld.loadedEntityList.contains(entity)).filter(entity -> entity instanceof EntityPlayer || !players).filter(entity -> entity instanceof EntityAnimal || !animals).filter(entity -> entity instanceof EntityMob || !mobs).filter(entity -> !entity.isInvisible() || invisibles).filter(entity -> entity.canEntityBeSeen(Minecraft.getMinecraft().thePlayer) || walls).filter(entity -> entity.ticksExisted > 15).filter(entity -> entity != Minecraft.getMinecraft().thePlayer).sorted(Comparator.comparingDouble(entity -> Minecraft.getMinecraft().thePlayer.getDistanceSqToEntity((Entity)entity))).collect(Collectors.toList());
    }

    public int getLoadedEntitySize() {
        return this.loadedEntitySize;
    }

    public void setLoadedEntitySize(int loadedEntitySize) {
        this.loadedEntitySize = loadedEntitySize;
    }
}

