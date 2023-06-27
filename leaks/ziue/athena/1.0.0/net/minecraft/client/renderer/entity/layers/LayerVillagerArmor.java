package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.model.*;

public class LayerVillagerArmor extends LayerBipedArmor
{
    public LayerVillagerArmor(final RendererLivingEntity<?> rendererIn) {
        super(rendererIn);
    }
    
    @Override
    protected void initArmor() {
        this.field_177189_c = new ModelZombieVillager(0.5f, 0.0f, true);
        this.field_177186_d = new ModelZombieVillager(1.0f, 0.0f, true);
    }
}
