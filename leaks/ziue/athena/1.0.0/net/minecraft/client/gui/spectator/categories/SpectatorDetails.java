package net.minecraft.client.gui.spectator.categories;

import java.util.*;
import net.minecraft.client.gui.spectator.*;
import com.google.common.base.*;

public class SpectatorDetails
{
    private final ISpectatorMenuView field_178684_a;
    private final List<ISpectatorMenuObject> field_178682_b;
    private final int field_178683_c;
    
    public SpectatorDetails(final ISpectatorMenuView p_i45494_1_, final List<ISpectatorMenuObject> p_i45494_2_, final int p_i45494_3_) {
        this.field_178684_a = p_i45494_1_;
        this.field_178682_b = p_i45494_2_;
        this.field_178683_c = p_i45494_3_;
    }
    
    public ISpectatorMenuObject func_178680_a(final int p_178680_1_) {
        return (ISpectatorMenuObject)((p_178680_1_ >= 0 && p_178680_1_ < this.field_178682_b.size()) ? Objects.firstNonNull((Object)this.field_178682_b.get(p_178680_1_), (Object)SpectatorMenu.field_178657_a) : SpectatorMenu.field_178657_a);
    }
    
    public int func_178681_b() {
        return this.field_178683_c;
    }
}
