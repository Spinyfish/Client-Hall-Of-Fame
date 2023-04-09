package tech.dort.dortware.impl.modules.combat;

import com.google.common.eventbus.Subscribe;
import net.minecraft.entity.EntityLivingBase;
import tech.dort.dortware.Client;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.api.property.SliderUnit;
import tech.dort.dortware.api.property.impl.BooleanValue;
import tech.dort.dortware.api.property.impl.EnumValue;
import tech.dort.dortware.api.property.impl.NumberValue;
import tech.dort.dortware.api.property.impl.interfaces.INameable;
import tech.dort.dortware.impl.events.UpdateEvent;
import tech.dort.dortware.impl.modules.render.Rotate;
import tech.dort.dortware.impl.utils.combat.AimUtil;
import tech.dort.dortware.impl.utils.combat.FightUtil;
import tech.dort.dortware.impl.utils.combat.extras.Rotation;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AimBot extends Module {

    private final EnumValue<SortingMode> sortingMode = new EnumValue<>("Sorting Mode", this, SortingMode.values());
    private final NumberValue range = new NumberValue("Range", this, 3, 1, 8, SliderUnit.BLOCKS);
    private final BooleanValue invis = new BooleanValue("Invisibles", this, false);
    private final BooleanValue friends = new BooleanValue("Friends", this, false);
    private final BooleanValue players = new BooleanValue("Players", this, true);
    private final BooleanValue animals = new BooleanValue("Animals", this, false);
    private final BooleanValue teams = new BooleanValue("Teams", this, false);
    private final BooleanValue walls = new BooleanValue("Walls", this, false);
    private final BooleanValue mobs = new BooleanValue("Mobs", this, false);

    public AimBot(ModuleData moduleData) {
        super(moduleData);
        register(sortingMode, range, invis, friends, players, animals, teams, walls, mobs);
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        try {
            if (AutoPot.isPotting()) {
                return;
            }

            List<EntityLivingBase> entities = FightUtil.getMultipleTargets(range.getValue(), players.getValue(),
                    animals.getValue(), walls.getValue(), mobs.getValue(), invis.getValue());

            if (!friends.getValue()) {
                entities.removeIf(e -> Client.INSTANCE.getFriendManager().getObjects().contains(e.getName().toLowerCase()));
            }

            if (teams.getValue()) {
                entities.removeIf(FightUtil::isOnSameTeam);
            }

            if (event.isPre()) {
                switch (sortingMode.getValue()) {
                    case RANGE:
                        entities.sort(Comparator.comparingInt(e -> (int) -e.getDistanceToEntity(mc.thePlayer)));
                        break;

                    case HURTTIME:
                        entities.sort(Comparator.comparingInt(e -> -e.hurtResistantTime));
                        break;

                    case HEALTH:
                        entities.sort(Comparator.comparingInt(e -> (int) -e.getHealth()));
                        break;

                    case ARMOR:
                        entities.sort(Comparator.comparingInt(e -> -e.getTotalArmorValue()));
                        break;
                }
                Collections.reverse(entities);

                if (entities.isEmpty())
                    return;

                EntityLivingBase currentTarget = entities.get(0);

                if (currentTarget.isDead || mc.thePlayer.isUsingItem())
                    return;

                rotateToEntity(currentTarget);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void rotateToEntity(EntityLivingBase entity) {
        Rotation rotation = AimUtil.getRotationsRandom(entity);

        mc.thePlayer.rotationYaw = rotation.getRotationYaw();
        mc.thePlayer.rotationPitch = rotation.getRotationPitch();

        if (Client.INSTANCE.getModuleManager().get(Rotate.class).isToggled()) {
            mc.thePlayer.renderPitchHead = rotation.getRotationPitch();
            mc.thePlayer.renderYawOffset = rotation.getRotationYaw();
            mc.thePlayer.renderYawHead = rotation.getRotationYaw();
        }
    }

    private enum SortingMode implements INameable {
        HURTTIME("Hurt Time"), HEALTH("Health"), ARMOR("Armor"), RANGE("Range");

        private final String name;

        SortingMode(String name) {
            this.name = name;
        }

        @Override
        public String getDisplayName() {
            return this.name;
        }
    }
}
