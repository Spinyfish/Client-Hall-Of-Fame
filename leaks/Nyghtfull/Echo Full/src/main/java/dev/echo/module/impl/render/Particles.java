package dev.echo.module.impl.render;

import dev.echo.listener.Link;
import dev.echo.listener.Listener;
import dev.echo.listener.event.impl.player.AttackEvent;
import dev.echo.module.Category;
import dev.echo.module.Module;
import dev.echo.module.settings.impl.BooleanSetting;
import dev.echo.module.settings.impl.NumberSetting;
import dev.echo.utils.player.AlwaysUtil;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;

public class Particles extends Module {
    public Particles() {
        super("Particles", Category.RENDER, "Renders Particles");
        addSettings(multiplier, alwaysCrit, alwaysSharpness);
    }

    private final NumberSetting multiplier = new NumberSetting("Multiplier", 1, 10, 0, 0.1);
    private final BooleanSetting alwaysCrit = new BooleanSetting("Always Crit", true);

    private final BooleanSetting alwaysSharpness = new BooleanSetting("Always Sharpness", true);

    @Link
    public final Listener<AttackEvent> attackEventListener = event -> {
        if (!AlwaysUtil.isPlayerInGame()) {
            return;
        }

        Entity target = event.getTargetEntity();

        if (mc.thePlayer.fallDistance > 0 || alwaysCrit.isEnabled() || alwaysSharpness.isEnabled()) {
            for (int i = 0; i <= multiplier.getValue().intValue(); i++) {
                if (alwaysCrit.isEnabled()) {
                    mc.thePlayer.onCriticalHit(target);
                }

                if (alwaysSharpness.isEnabled()) {
                    mc.effectRenderer.emitParticleAtEntity(target, EnumParticleTypes.CRIT_MAGIC);
                }
            }
        }
    };
}
