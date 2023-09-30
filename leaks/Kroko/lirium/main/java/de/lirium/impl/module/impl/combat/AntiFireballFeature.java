package de.lirium.impl.module.impl.combat;

import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import de.lirium.base.helper.TimeHelper;
import de.lirium.base.setting.Value;
import de.lirium.base.setting.impl.CheckBox;
import de.lirium.base.setting.impl.SliderSetting;
import de.lirium.impl.events.AttackEvent;
import de.lirium.impl.module.ModuleFeature;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.util.EnumHand;

import java.util.ArrayList;
import java.util.List;

@ModuleFeature.Info(name = "Anti Fireball", description = "Disables achievement notifications", category = ModuleFeature.Category.COMBAT)
public class AntiFireballFeature extends ModuleFeature {

    @Value(name = "Range")
    private final SliderSetting<Double> range = new SliderSetting<>(3.0, 3.0, 6.0);

    @Value(name = "Delay")
    private final SliderSetting<Long> delay = new SliderSetting<>(0L, 0L, 1000L);

    @Value(name = "Multiple attacks")
    private final CheckBox multipleAttacks = new CheckBox(false);

    @Value(name = "Swing")
    private final CheckBox swing = new CheckBox(true);

    private final TimeHelper timeHelper = new TimeHelper();

    private final List<Entity> alreadyAttacked = new ArrayList<>();

    @EventHandler
    public Listener<AttackEvent> attackEventListener = e -> {
        for(Entity entity : getWorld().loadedEntityList) {
            if(entity instanceof EntityFireball) {
                if(getRange(entity) <= range.getValue()) {
                    if (!getPlayer().isRowingBoat()) {
                        if (timeHelper.hasReached(delay.getValue())) {
                            if (multipleAttacks.getValue() || !alreadyAttacked.contains(entity)) {
                                mc.playerController.attackEntity(getPlayer(), entity);
                                if (swing.getValue())
                                    getPlayer().swingArm(EnumHand.MAIN_HAND);
                            }
                            alreadyAttacked.add(entity);
                        }
                    }
                }
            }
        }
    };
}
