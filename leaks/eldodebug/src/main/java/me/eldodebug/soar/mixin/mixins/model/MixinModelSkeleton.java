package me.eldodebug.soar.mixin.mixins.model;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelSkeleton;

@Mixin(ModelSkeleton.class)
public class MixinModelSkeleton extends ModelBiped{

    @Override
    public void postRenderArm(float scale) {
        this.bipedRightArm.rotationPointX++;
        this.bipedRightArm.postRender(scale);
        this.bipedRightArm.rotationPointX--;
    }
}
