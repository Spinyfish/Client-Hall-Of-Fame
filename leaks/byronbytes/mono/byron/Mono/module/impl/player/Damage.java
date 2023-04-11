package byron.Mono.module.impl.player;

import byron.Mono.interfaces.ModuleInterface;
import byron.Mono.module.Category;
import byron.Mono.module.Module;
import byron.Mono.utils.MovementUtils;

@ModuleInterface(name = "Damage", description = "Damage yourself.", category = Category.Player)
public class Damage extends Module {


    @Override
    public void onEnable() {
        super.onEnable();
        MovementUtils.damagePlayer();
    }

}
