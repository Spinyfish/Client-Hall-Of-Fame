package net.minecraft.client.renderer.tileentity;

import net.minecraft.entity.item.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.*;
import net.minecraft.util.*;
import net.minecraft.entity.*;

public class RenderEnderCrystal extends Render<EntityEnderCrystal>
{
    private static final ResourceLocation enderCrystalTextures;
    private ModelBase modelEnderCrystal;
    
    public RenderEnderCrystal(final RenderManager renderManagerIn) {
        super(renderManagerIn);
        this.modelEnderCrystal = new ModelEnderCrystal(0.0f, true);
        this.shadowSize = 0.5f;
    }
    
    @Override
    public void doRender(final EntityEnderCrystal entity, final double x, final double y, final double z, final float entityYaw, final float partialTicks) {
        final float f = entity.innerRotation + partialTicks;
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)x, (float)y, (float)z);
        this.bindTexture(RenderEnderCrystal.enderCrystalTextures);
        float f2 = MathHelper.sin(f * 0.2f) / 2.0f + 0.5f;
        f2 += f2 * f2;
        this.modelEnderCrystal.render(entity, 0.0f, f * 3.0f, f2 * 0.2f, 0.0f, 0.0f, 0.0625f);
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }
    
    @Override
    protected ResourceLocation getEntityTexture(final EntityEnderCrystal entity) {
        return RenderEnderCrystal.enderCrystalTextures;
    }
    
    static {
        enderCrystalTextures = new ResourceLocation("textures/entity/endercrystal/endercrystal.png");
    }
}
