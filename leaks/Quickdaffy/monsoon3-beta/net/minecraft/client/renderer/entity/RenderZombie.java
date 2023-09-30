/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package net.minecraft.client.renderer.entity;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelZombie;
import net.minecraft.client.model.ModelZombieVillager;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.layers.LayerVillagerArmor;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.ResourceLocation;

public class RenderZombie
extends RenderBiped<EntityZombie> {
    private static final ResourceLocation zombieTextures = new ResourceLocation("textures/entity/zombie/zombie.png");
    private static final ResourceLocation zombieVillagerTextures = new ResourceLocation("textures/entity/zombie/zombie_villager.png");
    private final ModelBiped field_82434_o;
    private final ModelZombieVillager zombieVillagerModel;
    private final List<LayerRenderer<EntityZombie>> field_177121_n;
    private final List<LayerRenderer<EntityZombie>> field_177122_o;

    public RenderZombie(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelZombie(), 0.5f, 1.0f);
        LayerRenderer layerrenderer = (LayerRenderer)this.layerRenderers.get(0);
        this.field_82434_o = this.modelBipedMain;
        this.zombieVillagerModel = new ModelZombieVillager();
        this.addLayer(new LayerHeldItem(this));
        LayerBipedArmor layerbipedarmor = new LayerBipedArmor(this){

            @Override
            protected void initArmor() {
                this.field_177189_c = new ModelZombie(0.5f, true);
                this.field_177186_d = new ModelZombie(1.0f, true);
            }
        };
        this.addLayer(layerbipedarmor);
        this.field_177122_o = Lists.newArrayList((Iterable)this.layerRenderers);
        if (layerrenderer instanceof LayerCustomHead) {
            this.removeLayer(layerrenderer);
            this.addLayer(new LayerCustomHead(this.zombieVillagerModel.bipedHead));
        }
        this.removeLayer(layerbipedarmor);
        this.addLayer(new LayerVillagerArmor(this));
        this.field_177121_n = Lists.newArrayList((Iterable)this.layerRenderers);
    }

    @Override
    public void doRender(EntityZombie entity, double x, double y, double z, float entityYaw, float partialTicks) {
        this.func_82427_a(entity);
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityZombie entity) {
        return entity.isVillager() ? zombieVillagerTextures : zombieTextures;
    }

    private void func_82427_a(EntityZombie zombie) {
        if (zombie.isVillager()) {
            this.mainModel = this.zombieVillagerModel;
            this.layerRenderers = this.field_177121_n;
        } else {
            this.mainModel = this.field_82434_o;
            this.layerRenderers = this.field_177122_o;
        }
        this.modelBipedMain = (ModelBiped)this.mainModel;
    }

    @Override
    protected void rotateCorpse(EntityZombie bat, float p_77043_2_, float p_77043_3_, float partialTicks) {
        if (bat.isConverting()) {
            p_77043_3_ += (float)(Math.cos((double)bat.ticksExisted * 3.25) * Math.PI * 0.25);
        }
        super.rotateCorpse(bat, p_77043_2_, p_77043_3_, partialTicks);
    }
}

