package rip.athena.client.gui.clickgui.pages.fps;

import net.minecraft.util.*;
import rip.athena.client.gui.clickgui.pages.*;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.tileentity.*;

public class BlacklistModule
{
    private String name;
    private BlacklistType type;
    private Class<?> clazz;
    private EnumParticleTypes particle;
    private boolean enabled;
    
    public BlacklistModule(final String name, final BlacklistType type, final Class<?> clazz) {
        this.name = name;
        this.type = type;
        this.clazz = clazz;
        switch (type) {
            case BLOCK: {
                this.enabled = SettingsPage.BLOCKS.contains(clazz);
                break;
            }
            case ENTITY: {
                this.enabled = SettingsPage.ENTITIES.contains(clazz);
                break;
            }
            case TILE_ENTITY: {
                this.enabled = SettingsPage.TILE_ENTITIES.contains(clazz);
                break;
            }
        }
    }
    
    public BlacklistModule(final String name, final EnumParticleTypes particle) {
        this.name = name;
        this.type = BlacklistType.PARTICLE;
        this.particle = particle;
        this.enabled = SettingsPage.PARTICLES.contains(particle);
    }
    
    public String getName() {
        return this.name;
    }
    
    public BlacklistType getType() {
        return this.type;
    }
    
    public Class<?> getClazz() {
        return this.clazz;
    }
    
    public EnumParticleTypes getParticle() {
        return this.particle;
    }
    
    public boolean isEnabled() {
        return this.enabled;
    }
    
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
        switch (this.type) {
            case BLOCK: {
                if (this.clazz.getClass().isInstance(Block.class)) {
                    final Class<Block> block = (Class<Block>)this.clazz;
                    if (enabled) {
                        if (!SettingsPage.BLOCKS.contains(block)) {
                            SettingsPage.BLOCKS.add(block);
                        }
                    }
                    else if (SettingsPage.BLOCKS.contains(block)) {
                        SettingsPage.BLOCKS.remove(block);
                    }
                    break;
                }
                break;
            }
            case ENTITY: {
                if (this.clazz.getClass().isInstance(Entity.class)) {
                    final Class<Entity> entity = (Class<Entity>)this.clazz;
                    if (enabled) {
                        if (!SettingsPage.ENTITIES.contains(entity)) {
                            SettingsPage.ENTITIES.add(entity);
                        }
                    }
                    else if (SettingsPage.ENTITIES.contains(entity)) {
                        SettingsPage.ENTITIES.remove(entity);
                    }
                    break;
                }
                break;
            }
            case TILE_ENTITY: {
                if (this.clazz.getClass().isInstance(TileEntity.class)) {
                    final Class<TileEntity> tileEntity = (Class<TileEntity>)this.clazz;
                    if (enabled) {
                        if (!SettingsPage.TILE_ENTITIES.contains(tileEntity)) {
                            SettingsPage.TILE_ENTITIES.add(tileEntity);
                        }
                    }
                    else if (SettingsPage.TILE_ENTITIES.contains(tileEntity)) {
                        SettingsPage.TILE_ENTITIES.remove(tileEntity);
                    }
                    break;
                }
                break;
            }
            case PARTICLE: {
                if (this.particle == null) {
                    break;
                }
                if (enabled) {
                    if (!SettingsPage.PARTICLES.contains(this.particle)) {
                        SettingsPage.PARTICLES.add(this.particle);
                        break;
                    }
                    break;
                }
                else {
                    if (SettingsPage.PARTICLES.contains(this.particle)) {
                        SettingsPage.PARTICLES.remove(this.particle);
                        break;
                    }
                    break;
                }
                break;
            }
        }
    }
}
