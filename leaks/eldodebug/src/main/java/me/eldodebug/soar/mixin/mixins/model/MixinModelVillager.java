package me.eldodebug.soar.mixin.mixins.model;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.client.model.ModelVillager;

@Mixin(ModelVillager.class)
public class MixinModelVillager {

    @ModifyConstant(method = "<init>(FFII)V", constant = @Constant(intValue = 18))
    private int changeTextureHeight(int original) {
        return 20;
    }
}
