package com.alan.clients.script.api.wrapper.impl;

import com.alan.clients.script.api.wrapper.ScriptWrapper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

/**
 * @author Strikeless
 * @since 20.06.2022
 */
public class ScriptWorld extends ScriptWrapper<World> {

    public ScriptWorld(final World wrapped) {
        super(wrapped);
    }

    public String getDimensionName() {
        return this.wrapped.provider.getDimensionName();
    }

    public int getDimensionId() {
        return this.wrapped.provider.getDimensionId();
    }

    public long getTime() {
        return this.wrapped.getWorldTime();
    }

    public ScriptEntityLiving[] getLivingEntities() {
        return this.wrapped.loadedEntityList.stream()
                .filter(entity -> entity instanceof EntityLivingBase)
                .map(entity -> new ScriptEntityLiving((EntityLivingBase) entity))
                .toArray(ScriptEntityLiving[]::new);
    }

    public ScriptEntityLiving[] getPlayers() {
        return this.wrapped.playerEntities.stream()
                .map(ScriptEntityLiving::new)
                .toArray(ScriptEntityLiving[]::new);
    }
}
