package net.minecraft.entity.monster;

import net.minecraft.entity.passive.*;
import com.google.common.base.*;
import net.minecraft.entity.*;

public interface IMob extends IAnimals
{
    public static final Predicate<Entity> mobSelector = new Predicate<Entity>() {
        public boolean apply(final Entity p_apply_1_) {
            return p_apply_1_ instanceof IMob;
        }
    };
    public static final Predicate<Entity> VISIBLE_MOB_SELECTOR = new Predicate<Entity>() {
        public boolean apply(final Entity p_apply_1_) {
            return p_apply_1_ instanceof IMob && !p_apply_1_.isInvisible();
        }
    };
}
