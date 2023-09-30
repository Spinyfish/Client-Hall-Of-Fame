package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelSpider;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSpider;
import net.minecraft.entity.monster.EntitySpider;

public class ModelAdapterSpider extends ModelAdapter {
    public ModelAdapterSpider() {
        super(EntitySpider.class, "spider", 1.0F);
    }

    protected ModelAdapterSpider(final Class entityClass, final String name, final float shadowSize) {
        super(entityClass, name, shadowSize);
    }

    public ModelBase makeModel() {
        return new ModelSpider();
    }

    public ModelRenderer getModelRenderer(final ModelBase model, final String modelPart) {
        if (!(model instanceof ModelSpider)) {
            return null;
        } else {
            final ModelSpider modelspider = (ModelSpider) model;
            return modelPart.equals("head") ? modelspider.spiderHead : (modelPart.equals("neck") ? modelspider.spiderNeck : (modelPart.equals("body") ? modelspider.spiderBody : (modelPart.equals("leg1") ? modelspider.spiderLeg1 : (modelPart.equals("leg2") ? modelspider.spiderLeg2 : (modelPart.equals("leg3") ? modelspider.spiderLeg3 : (modelPart.equals("leg4") ? modelspider.spiderLeg4 : (modelPart.equals("leg5") ? modelspider.spiderLeg5 : (modelPart.equals("leg6") ? modelspider.spiderLeg6 : (modelPart.equals("leg7") ? modelspider.spiderLeg7 : (modelPart.equals("leg8") ? modelspider.spiderLeg8 : null))))))))));
        }
    }

    public String[] getModelRendererNames() {
        return new String[]{"head", "neck", "body", "leg1", "leg2", "leg3", "leg4", "leg5", "leg6", "leg7", "leg8"};
    }

    public IEntityRenderer makeEntityRender(final ModelBase modelBase, final float shadowSize) {
        final RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        final RenderSpider renderspider = new RenderSpider(rendermanager);
        renderspider.mainModel = modelBase;
        renderspider.shadowSize = shadowSize;
        return renderspider;
    }
}
