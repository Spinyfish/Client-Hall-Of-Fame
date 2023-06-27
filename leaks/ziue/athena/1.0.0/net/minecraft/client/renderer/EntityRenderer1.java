package net.minecraft.client.renderer;

import com.google.common.base.*;
import net.minecraft.entity.*;

class EntityRenderer1 implements Predicate
{
    final EntityRenderer field_90032_a;
    
    EntityRenderer1(final EntityRenderer p_i1243_1_) {
        this.field_90032_a = p_i1243_1_;
    }
    
    public boolean apply(final Entity p_apply_1_) {
        return p_apply_1_.canBeCollidedWith();
    }
    
    public boolean apply(final Object p_apply_1_) {
        return this.apply((Entity)p_apply_1_);
    }
}
