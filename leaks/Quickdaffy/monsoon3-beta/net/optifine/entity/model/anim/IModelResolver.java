/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.entity.model.anim;

import net.minecraft.client.model.ModelRenderer;
import net.optifine.entity.model.anim.ModelVariableFloat;
import net.optifine.expr.IExpressionResolver;

public interface IModelResolver
extends IExpressionResolver {
    public ModelRenderer getModelRenderer(String var1);

    public ModelVariableFloat getModelVariable(String var1);
}

