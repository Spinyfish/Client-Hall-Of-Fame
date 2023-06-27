package net.minecraft.client.renderer.entity;

import net.minecraft.entity.item.*;
import net.minecraft.util.*;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.client.renderer.*;
import net.minecraft.entity.*;

public class ArmorStandRenderer extends RendererLivingEntity<EntityArmorStand>
{
    public static final ResourceLocation TEXTURE_ARMOR_STAND;
    
    public ArmorStandRenderer(final RenderManager p_i46195_1_) {
        super(p_i46195_1_, new ModelArmorStand(), 0.0f);
        final LayerBipedArmor layerbipedarmor = new LayerBipedArmor(this) {
            @Override
            protected void initArmor() {
                this.field_177189_c = new ModelArmorStandArmor(0.5f);
                this.field_177186_d = new ModelArmorStandArmor(1.0f);
            }
        };
        ((RendererLivingEntity<EntityLivingBase>)this).addLayer(layerbipedarmor);
        ((RendererLivingEntity<EntityLivingBase>)this).addLayer(new LayerHeldItem(this));
        ((RendererLivingEntity<EntityLivingBase>)this).addLayer(new LayerCustomHead(this.getMainModel().bipedHead));
    }
    
    @Override
    protected ResourceLocation getEntityTexture(final EntityArmorStand entity) {
        return ArmorStandRenderer.TEXTURE_ARMOR_STAND;
    }
    
    @Override
    public ModelArmorStand getMainModel() {
        return (ModelArmorStand)super.getMainModel();
    }
    
    @Override
    protected void rotateCorpse(final EntityArmorStand bat, final float p_77043_2_, final float p_77043_3_, final float partialTicks) {
        GlStateManager.rotate(180.0f - p_77043_3_, 0.0f, 1.0f, 0.0f);
    }
    
    @Override
    protected boolean canRenderName(final EntityArmorStand entity) {
        return entity.getAlwaysRenderNameTag();
    }
    
    static {
        TEXTURE_ARMOR_STAND = new ResourceLocation("textures/entity/armorstand/wood.png");
    }
}
