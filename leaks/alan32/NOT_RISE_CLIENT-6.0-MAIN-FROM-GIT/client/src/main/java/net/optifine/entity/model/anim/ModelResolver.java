package net.optifine.entity.model.anim;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.src.Config;
import net.minecraft.tileentity.TileEntity;
import net.optifine.entity.model.CustomModelRenderer;
import net.optifine.entity.model.ModelAdapter;
import net.optifine.expr.IExpression;

public class ModelResolver implements IModelResolver {
    private final ModelAdapter modelAdapter;
    private final ModelBase model;
    private final CustomModelRenderer[] customModelRenderers;
    private ModelRenderer thisModelRenderer;
    private ModelRenderer partModelRenderer;
    private final IRenderResolver renderResolver;

    public ModelResolver(final ModelAdapter modelAdapter, final ModelBase model, final CustomModelRenderer[] customModelRenderers) {
        this.modelAdapter = modelAdapter;
        this.model = model;
        this.customModelRenderers = customModelRenderers;
        final Class oclass = modelAdapter.getEntityClass();

        if (TileEntity.class.isAssignableFrom(oclass)) {
            this.renderResolver = new RenderResolverTileEntity();
        } else {
            this.renderResolver = new RenderResolverEntity();
        }
    }

    public IExpression getExpression(final String name) {
        final IExpression iexpression = this.getModelVariable(name);

        if (iexpression != null) {
            return iexpression;
        } else {
            final IExpression iexpression1 = this.renderResolver.getParameter(name);
            return iexpression1;
        }
    }

    public ModelRenderer getModelRenderer(final String name) {
        if (name == null) {
            return null;
        } else if (name.indexOf(":") >= 0) {
            final String[] astring = Config.tokenize(name, ":");
            ModelRenderer modelrenderer3 = this.getModelRenderer(astring[0]);

            for (int j = 1; j < astring.length; ++j) {
                final String s = astring[j];
                final ModelRenderer modelrenderer4 = modelrenderer3.getChildDeep(s);

                if (modelrenderer4 == null) {
                    return null;
                }

                modelrenderer3 = modelrenderer4;
            }

            return modelrenderer3;
        } else if (this.thisModelRenderer != null && name.equals("this")) {
            return this.thisModelRenderer;
        } else if (this.partModelRenderer != null && name.equals("part")) {
            return this.partModelRenderer;
        } else {
            final ModelRenderer modelrenderer = this.modelAdapter.getModelRenderer(this.model, name);

            if (modelrenderer != null) {
                return modelrenderer;
            } else {
                for (int i = 0; i < this.customModelRenderers.length; ++i) {
                    final CustomModelRenderer custommodelrenderer = this.customModelRenderers[i];
                    final ModelRenderer modelrenderer1 = custommodelrenderer.getModelRenderer();

                    if (name.equals(modelrenderer1.getId())) {
                        return modelrenderer1;
                    }

                    final ModelRenderer modelrenderer2 = modelrenderer1.getChildDeep(name);

                    if (modelrenderer2 != null) {
                        return modelrenderer2;
                    }
                }

                return null;
            }
        }
    }

    public ModelVariableFloat getModelVariable(final String name) {
        final String[] astring = Config.tokenize(name, ".");

        if (astring.length != 2) {
            return null;
        } else {
            final String s = astring[0];
            final String s1 = astring[1];
            final ModelRenderer modelrenderer = this.getModelRenderer(s);

            if (modelrenderer == null) {
                return null;
            } else {
                final ModelVariableType modelvariabletype = ModelVariableType.parse(s1);
                return modelvariabletype == null ? null : new ModelVariableFloat(name, modelrenderer, modelvariabletype);
            }
        }
    }

    public void setPartModelRenderer(final ModelRenderer partModelRenderer) {
        this.partModelRenderer = partModelRenderer;
    }

    public void setThisModelRenderer(final ModelRenderer thisModelRenderer) {
        this.thisModelRenderer = thisModelRenderer;
    }
}
