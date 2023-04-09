package tech.dort.dortware.impl.modules.player;

import com.google.common.eventbus.Subscribe;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.api.property.impl.BooleanValue;
import tech.dort.dortware.api.property.impl.NumberValue;
import tech.dort.dortware.impl.events.UpdateEvent;

public class FastInteract extends Module {

    private final NumberValue hasteLevel = new NumberValue("Haste Level", this, 1, 0, 3, true);
    private final BooleanValue fastPlace = new BooleanValue("Fast Place", this, false);

    public FastInteract(ModuleData moduleData) {
        super(moduleData);
        register(hasteLevel, fastPlace);
    }

    @Subscribe
    public void handlePlayerUpdate(UpdateEvent event) {
        if (event.isPre()) {
            if (fastPlace.getValue()) {
                mc.rightClickDelayTimer = 0;
            }

            if (hasteLevel.getCastedValue().intValue() > 1 && !mc.thePlayer.isPotionActive(Potion.digSpeed)) {
                mc.thePlayer.addPotionEffect(new PotionEffect(Potion.digSpeed.id, 0, hasteLevel.getCastedValue().intValue()));
            }
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (hasteLevel.getValue() != 0) {
            mc.thePlayer.removePotionEffect(Potion.digSpeed.getId());
        }
    }
}