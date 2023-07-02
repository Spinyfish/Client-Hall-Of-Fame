package me.eldodebug.soar.mixin.mixins.util;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.util.LazyLoadBase;

@Mixin(LazyLoadBase.class)
public abstract class MixinLazyLoadBase<T> {

    @Shadow 
    private boolean isLoaded;

    @Shadow 
    private T value;

    @Shadow 
    protected abstract T load();
    
    @Overwrite
    public T getValue() {
        if (!this.isLoaded) {
            synchronized (this) {
                if (!this.isLoaded) {
                    this.value = this.load();
                    this.isLoaded = true;
                }
            }
        }
        return this.value;
    }
}
