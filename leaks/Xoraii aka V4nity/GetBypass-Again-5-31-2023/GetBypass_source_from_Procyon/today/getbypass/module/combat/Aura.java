// 
// Decompiled by Procyon v0.5.36
// 

package today.getbypass.module.combat;

import java.util.Iterator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import today.getbypass.module.Category;
import today.getbypass.module.Module;

public class Aura extends Module
{
    public Aura() {
        super("Aura", 37, "Automatically attacks entities for you!", Category.COMBAT);
    }
    
    @Override
    public void onUpdate() {
        if (!this.isToggled()) {
            return;
        }
        for (final Object theObject : this.mc.theWorld.loadedEntityList) {
            if (theObject instanceof EntityLivingBase) {
                final EntityLivingBase entity = (EntityLivingBase)theObject;
                if (entity instanceof EntityPlayerSP) {
                    continue;
                }
                if (this.mc.thePlayer.getDistanceToEntity(entity) > 6.2173615f || !entity.isEntityAlive()) {
                    continue;
                }
                this.mc.playerController.attackEntity(this.mc.thePlayer, entity);
                this.mc.thePlayer.swingItem();
                try {
                    Thread.sleep(9L);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        super.onUpdate();
    }
}
