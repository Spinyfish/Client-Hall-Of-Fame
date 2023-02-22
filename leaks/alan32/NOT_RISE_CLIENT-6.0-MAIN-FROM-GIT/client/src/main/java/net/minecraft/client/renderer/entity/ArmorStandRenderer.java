package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelArmorStand;
import net.minecraft.client.model.ModelArmorStandArmor;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.util.ResourceLocation;

public class ArmorStandRenderer extends RendererLivingEntity<EntityArmorStand> {
    /**
     * A constant instance of the armor stand texture, wrapped inside a ResourceLocation wrapper.
     */
    public static final ResourceLocation TEXTURE_ARMOR_STAND = new ResourceLocation("textures/entity/armorstand/wood.png");

    public ArmorStandRenderer(final RenderManager p_i46195_1_) {
        super(p_i46195_1_, new ModelArmorStand(), 0.0F);
        final LayerBipedArmor layerbipedarmor = new LayerBipedArmor(this) {
            protected void initArmor() {
                this.field_177189_c = new ModelArmorStandArmor(0.5F);
                this.field_177186_d = new ModelArmorStandArmor(1.0F);
            }
        };
        this.addLayer(layerbipedarmor);
        this.addLayer(new LayerHeldItem(this));
        this.addLayer(new LayerCustomHead(this.getMainModel().bipedHead));
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(final EntityArmorStand entity) {
        return TEXTURE_ARMOR_STAND;
    }

    public ModelArmorStand getMainModel() {
        return (ModelArmorStand) super.getMainModel();
    }

    protected void rotateCorpse(final EntityArmorStand bat, final float p_77043_2_, final float p_77043_3_, final float partialTicks) {
        GlStateManager.rotate(180.0F - p_77043_3_, 0.0F, 1.0F, 0.0F);
    }

    protected boolean canRenderName(final EntityArmorStand entity) {
        return entity.getAlwaysRenderNameTag();
    }
}
