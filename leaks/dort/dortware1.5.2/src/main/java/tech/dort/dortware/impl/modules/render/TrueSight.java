package tech.dort.dortware.impl.modules.render;

import com.google.common.eventbus.Subscribe;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.api.property.impl.BooleanValue;
import tech.dort.dortware.impl.events.UpdateEvent;

import java.util.stream.Collectors;

public class TrueSight extends Module {

    private final BooleanValue players = new BooleanValue("Players", this, true);
    private final BooleanValue animals = new BooleanValue("Animals", this, false);
    private final BooleanValue neutral = new BooleanValue("Neutral", this, false);
    private final BooleanValue self = new BooleanValue("Self", this, false);
    private final BooleanValue mobs = new BooleanValue("Mobs", this, false);

    public TrueSight(ModuleData moduleData) {
        super(moduleData);
        register(players, animals, neutral, self, mobs);
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        for (Entity entity : mc.theWorld.loadedEntityList.stream().filter(this::isValidEntity).collect(Collectors.toList())) {
            entity.setInvisible(false);
        }
    }

    private boolean isValidEntity(Entity entity) {
        if (entity == mc.thePlayer) return self.getValue() && players.getValue();
        if (entity instanceof EntityPlayer && players.getValue()) return true;
        if (entity instanceof EntityAnimal && animals.getValue()) return true;
        if (entity instanceof EntityVillager && neutral.getValue()) return true;
        return entity instanceof EntityMob && mobs.getValue();
    }
}