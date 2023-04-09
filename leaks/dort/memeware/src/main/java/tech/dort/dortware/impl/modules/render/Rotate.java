package tech.dort.dortware.impl.modules.render;

import com.google.common.eventbus.Subscribe;
import tech.dort.dortware.Client;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.api.property.impl.BooleanValue;
import tech.dort.dortware.impl.events.UpdateEvent;
import tech.dort.dortware.impl.managers.ModuleManager;
import tech.dort.dortware.impl.modules.combat.FastBow;
import tech.dort.dortware.impl.modules.combat.KillAura;
import tech.dort.dortware.impl.modules.player.Derp;
import tech.dort.dortware.impl.modules.player.Nuker;
import tech.dort.dortware.impl.modules.player.Scaffold;

public class Rotate extends Module {

    private final BooleanValue always = new BooleanValue("Always", this, true);

    public Rotate(ModuleData moduleData) {
        super(moduleData);
        register(always);
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        final ModuleManager moduleManager = Client.INSTANCE.getModuleManager();
        final Nuker nuker = moduleManager.get(Nuker.class);
        if (always.getValue() && !moduleManager.get(Scaffold.class).isToggled() && !(moduleManager.get(KillAura.class).isToggled() && KillAura.currentTarget != null) && !moduleManager.get(Derp.class).isToggled() && !(nuker.isToggled() && nuker.silent.getValue()) && !(moduleManager.get(FastBow.class).isToggled() && mc.gameSettings.keyBindUseItem.getIsKeyPressed())) {
            mc.thePlayer.renderPitchHead = mc.thePlayer.rotationPitch;
            mc.thePlayer.renderYawHead = mc.thePlayer.rotationYaw;
            mc.thePlayer.renderYawOffset = mc.thePlayer.rotationYaw;
        }
    }
}