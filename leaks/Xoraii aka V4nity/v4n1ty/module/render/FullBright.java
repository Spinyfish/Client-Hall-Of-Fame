package v4n1ty.module.render;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import v4n1ty.module.Category;
import v4n1ty.module.Module;

public class FullBright extends Module {
    public FullBright() {
        super("Fullbright", 0, Category.RENDER);
    }

    public void onUpdate(){
        if(this.isToggled()) {
            mc.thePlayer.addPotionEffect(new PotionEffect(Potion.nightVision.getId(), 5201, 1));
        }
    }

    @Override
    public void onDisable() {
        mc.thePlayer.removePotionEffect(Potion.nightVision.getId());
    }
}
