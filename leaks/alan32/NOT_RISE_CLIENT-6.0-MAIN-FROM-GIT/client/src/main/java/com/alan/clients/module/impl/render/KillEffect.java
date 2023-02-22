package com.alan.clients.module.impl.render;

import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.newevent.impl.other.AttackEvent;
import com.alan.clients.util.sound.SoundUtil;
import com.alan.clients.value.impl.BooleanValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.util.EnumParticleTypes;

@ModuleInfo(name = "module.render.killeffect.name", description = "module.render.killeffect.description", category = Category.RENDER)
public final class KillEffect extends Module {

    private final BooleanValue lightning = new BooleanValue("Lightning", this, true);
    private final BooleanValue explosion = new BooleanValue("Explosion", this, true);

    private EntityLivingBase target;

    @EventLink()
    public final Listener<PreMotionEvent> onPreMotionEvent = event -> {

        if (this.target != null && !mc.theWorld.loadedEntityList.contains(this.target)) {
            if (this.lightning.getValue()) {
                final EntityLightningBolt entityLightningBolt = new EntityLightningBolt(mc.theWorld, target.posX, target.posY, target.posZ);
                mc.theWorld.addEntityToWorld((int) (-Math.random() * 100000), entityLightningBolt);

                SoundUtil.playSound("ambient.weather.thunder");
            }

            if (this.explosion.getValue()) {
                for (int i = 0; i <= 8; i++) {
                    mc.effectRenderer.emitParticleAtEntity(target, EnumParticleTypes.FLAME);
                }

                SoundUtil.playSound("item.fireCharge.use");
            }

            this.target = null;
        }
    };

    @EventLink()
    public final Listener<AttackEvent> onAttack = event -> {

        final Entity entity = event.getTarget();

        if (entity instanceof EntityLivingBase) {
            target = (EntityLivingBase) entity;
        }
    };
}