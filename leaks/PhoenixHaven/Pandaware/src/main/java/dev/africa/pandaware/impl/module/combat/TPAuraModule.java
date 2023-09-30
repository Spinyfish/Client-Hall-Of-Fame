package dev.africa.pandaware.impl.module.combat;

import com.google.common.collect.Lists;
import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.event.Event;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.interfaces.Category;
import dev.africa.pandaware.api.module.interfaces.ModuleInfo;
import dev.africa.pandaware.impl.event.player.AttackEvent;
import dev.africa.pandaware.impl.event.player.MotionEvent;
import dev.africa.pandaware.impl.event.render.RenderEvent;
import dev.africa.pandaware.impl.setting.BooleanSetting;
import dev.africa.pandaware.impl.setting.EnumSetting;
import dev.africa.pandaware.impl.setting.NumberRangeSetting;
import dev.africa.pandaware.impl.setting.NumberSetting;
import dev.africa.pandaware.impl.ui.UISettings;
import dev.africa.pandaware.utils.math.TimeHelper;
import dev.africa.pandaware.utils.math.random.RandomUtils;
import dev.africa.pandaware.utils.math.vector.Vec2f;
import dev.africa.pandaware.utils.network.ProtocolUtils;
import dev.africa.pandaware.utils.player.RotationUtils;
import dev.africa.pandaware.utils.player.path.PathUtils;
import dev.africa.pandaware.utils.render.RenderUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

@Getter
@ModuleInfo(name = "TP Aura", description = "Attacks from long distance", category = Category.COMBAT)
public class TPAuraModule extends Module {
    private final EnumSetting<Mode> mode = new EnumSetting<>("Mode", Mode.NORMAL);
    private final EnumSetting<SortMode> sortMode = new EnumSetting<>("SortMode", SortMode.DISTANCE);
    private final EnumSetting<ClickMode> clickMode
            = new EnumSetting<>("Click Mode", ClickMode.SECURE_RANDOM);

    private final NumberSetting maxTargets = new NumberSetting("Targets", 50, 1, 1, 1);
    private final NumberSetting pointsDistance = new NumberSetting("Points distance", 10, 0, 6);
    public final NumberSetting range
            = new NumberSetting("Range", 200, 1, 100);
    private final NumberRangeSetting aps =
            new NumberRangeSetting("APS", 20, 0.5, 9, 11, 0.5);

    private final BooleanSetting players = new BooleanSetting("Players", true);
    private final BooleanSetting mobs = new BooleanSetting("Mobs", false);
    private final BooleanSetting swing = new BooleanSetting("Swing", true);
    private final BooleanSetting drawPath = new BooleanSetting("Draw Path", true).setSaveConfig(false);
    private final BooleanSetting esp = new BooleanSetting("ESP", true).setSaveConfig(false);

    private final BooleanSetting aim = new BooleanSetting("Aim", false);
    private final BooleanSetting teams
            = new BooleanSetting("Teams", false,
            this.players::getValue);

    private EntityLivingBase target;
    private final TimeHelper timer = new TimeHelper();
    private final List<EntityLivingBase> entities = new ArrayList<>();
    private final List<List<Vec3>> pathToEntities = new ArrayList<>();

    public TPAuraModule() {
        this.registerSettings(
                this.mode,
                this.sortMode,
                this.clickMode,
                this.aps,
                this.range,
                this.maxTargets,
                this.pointsDistance,
                this.swing,
                this.drawPath,
                this.esp,
                this.aim,
                this.teams,
                this.players,
                this.mobs
        );
    }

    @Override
    public void onEnable() {
        this.entities.clear();
        this.target = null;

        this.pathToEntities.clear();
    }

    @Override
    public void onDisable() {
        this.entities.clear();
        this.target = null;

        this.pathToEntities.clear();
    }

    @EventHandler
    EventCallback<RenderEvent> onRender = event -> {
        if (event.getType() == RenderEvent.Type.RENDER_3D) {
            this.entities.forEach(entityLivingBase -> {
                if (entityLivingBase != ((KillAuraModule)
                        Client.getInstance().getModuleManager().getByClass(KillAuraModule.class))
                        .getTarget() && this.esp.getValue()) {

                    RenderUtils.drawCircle(entityLivingBase, target.width, UISettings.CURRENT_COLOR.getRGB(), false);
                    RenderUtils.drawCircle(entityLivingBase, target.width, UISettings.CURRENT_COLOR.getRGB(), true);
                }
            });

            if (this.drawPath.getValue() && !this.pathToEntities.isEmpty()) {
                this.pathToEntities.forEach(RenderUtils::drawLines);
            }
        }
    };

    @EventHandler
    EventCallback<MotionEvent> onMotion = event -> {
        if (event.getEventState() == Event.EventState.PRE) {
            if (target != null && aim.getValue()) {
                Vec2f rotations = RotationUtils.getRotations(target);
                double f = mc.gameSettings.mouseSensitivity * 0.6 + 0.2f;
                double gcd = f * f * f * 0.8;
                float b1 = (float) (rotations.getY() - rotations.getY() % gcd);
                float a1 = (float) (rotations.getX() - rotations.getX() % gcd);
                event.setYaw(a1);
                event.setPitch(b1);
            }

            if (this.shouldClickMouse()) {
                this.pathToEntities.clear();

                List<EntityLivingBase> entities = getNearEntities(range.getValue().intValue(),
                        maxTargets.getValue().intValue());
                Iterator<EntityLivingBase> entitiesIterator = entities.iterator();

                if (!entitiesIterator.hasNext()) {
                    this.target = null;
                    this.pathToEntities.clear();
                }

                switch (this.mode.getValue()) {
                    case NORMAL: {
                        while (entitiesIterator.hasNext()) {
                            EntityLivingBase entity = entitiesIterator.next();

                            List<Vec3> path = PathUtils.computePath(new Vec3(event.getX(), event.getY(), event.getZ()),
                                    new Vec3(entity.posX, entity.posY, entity.posZ), pointsDistance.getValue().doubleValue());
                            List<Vec3> backwards = Lists.reverse(path);
                            Iterator<Vec3> pathIterator = path.iterator();

                            Vec3 vector;

                            while (pathIterator.hasNext()) {
                                vector = pathIterator.next();
                                mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C03PacketPlayer
                                        .C04PacketPlayerPosition(vector.getX(), vector.getY(), vector.getZ(), true));
                            }

                            this.target = entity;
                            this.attack(entity, path);

                            pathIterator = backwards.iterator();

                            while (pathIterator.hasNext()) {
                                vector = pathIterator.next();
                                mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C03PacketPlayer
                                        .C04PacketPlayerPosition(vector.getX(), vector.getY(), vector.getZ(), true));
                            }
                        }
                        break;
                    }

                    case ONE_PACKET: {
                        while (entitiesIterator.hasNext()) {
                            EntityLivingBase entity = entitiesIterator.next();

                            mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C03PacketPlayer
                                    .C04PacketPlayerPosition(entity.posX, entity.posY, entity.posZ, entity.onGround));

                            List<Vec3> list = new ArrayList<>();
                            list.add(new Vec3(entity.posX, entity.posY + 1, entity.posZ));

                            this.target = entity;
                            this.attack(entity, list);
                        }
                        break;
                    }
                }

                mc.thePlayer.resetCooldown();
                this.timer.reset();
            }
        }
    };

    private boolean shouldClickMouse() {
        switch (this.clickMode.getValue()) {
            case RANDOM: {
                double time = RandomUtils.nextDouble(this.aps.getFirstValue().doubleValue(),
                        this.aps.getSecondValue().doubleValue());

                return this.timer.reach((float) (1000L / time));
            }

            case SECURE_RANDOM: {
                double min = this.aps.getFirstValue().doubleValue();
                double max = this.aps.getSecondValue().doubleValue();

                double time = MathHelper.clamp_double(
                        min + ((max - min) * new SecureRandom().nextDouble()), min, max);

                return this.timer.reach((float) (1000L / time));
            }

            case ONE_DOT_NINE_PLUS: {
                float delay = mc.thePlayer.getCooledAttackStrength(0.0f);

                return delay > 0.9f;
            }
        }

        return false;
    }

    private void attack(EntityLivingBase entity, List<Vec3> path) {
        AttackEvent attackEvent = new AttackEvent(entity, Event.EventState.PRE);
        Client.getInstance().getEventDispatcher().dispatch(attackEvent);

        if (ProtocolUtils.isOneDotEight() && this.swing.getValue()) {
            mc.thePlayer.swingItem();
        }

        mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));

        if (!ProtocolUtils.isOneDotEight() && this.swing.getValue()) {
            mc.thePlayer.swingItem();
        }

        attackEvent.setEventState(Event.EventState.POST);
        Client.getInstance().getEventDispatcher().dispatch(attackEvent);

        this.pathToEntities.add(path);
    }

    private List<EntityLivingBase> getNearEntities(int range, int maxEntities) {
        this.entities.clear();

        List<Entity> sortedEntities = mc.theWorld.loadedEntityList;

        if (this.sortMode.getValue() == SortMode.DISTANCE) {
            sortedEntities.sort(Comparator.comparingDouble(mc.thePlayer::getDistanceSqToEntity));
        }

        if (this.sortMode.getValue() == SortMode.HEALTH) {
            sortedEntities.sort(Comparator.comparingDouble(ent -> {
                if (ent instanceof EntityLivingBase) {
                    return ((EntityLivingBase) ent).getHealth();
                } else {
                    return mc.thePlayer.getDistanceToEntity(ent);
                }
            }));
        }

        if (this.sortMode.getValue() == SortMode.AIM) {
            sortedEntities.sort(Comparator.comparingDouble(ent -> {
                if (ent instanceof EntityLivingBase) {
                    return RotationUtils.getRotationDifference((EntityLivingBase) ent);
                } else {
                    return mc.thePlayer.getDistanceToEntity(ent);
                }
            }));
        }

        for (Entity entity : sortedEntities) {
            if (isValidEntity(entity)) {
                if (entity.getDistanceToEntity(mc.thePlayer) >= range || this.entities.size() >= maxEntities) {
                    continue;
                }

                this.entities.add((EntityLivingBase) entity);
            }
        }

        return this.entities;
    }

    private boolean isValidEntity(Entity entity) {
        if (entity instanceof EntityArmorStand)
            return false;

        if ((entity instanceof EntityIronGolem ||
                entity instanceof EntitySnowman ||
                entity instanceof EntityMob ||
                entity instanceof EntityAnimal ||
                entity instanceof EntityVillager ||
                entity instanceof EntitySquid ||
                entity instanceof EntityBat ||
                entity instanceof EntitySlime) && !this.mobs.getValue())
            return false;

        if (!this.players.getValue() && (entity instanceof EntityPlayer))
            return false;

        if (this.teams.getValue() && mc.thePlayer.isOnSameTeam((EntityLivingBase) entity))
            return false;

        if (entity instanceof EntityPlayer &&
                Client.getInstance().getIgnoreManager().isIgnoreBoth((EntityPlayer) entity))
            return false;

        return entity != mc.thePlayer && entity instanceof EntityLivingBase;
    }

    @AllArgsConstructor
    enum Mode {
        NORMAL("Normal"),
        ONE_PACKET("One Packet");

        private final String label;

        @Override
        public String toString() {
            return label;
        }
    }

    @AllArgsConstructor
    enum SortMode {
        DISTANCE("Distance"),
        HEALTH("Health"),
        AIM("Aim");

        private final String label;

        @Override
        public String toString() {
            return label;
        }
    }

    @AllArgsConstructor
    private enum ClickMode {
        RANDOM("Random"),
        SECURE_RANDOM("Secure Random"),
        ONE_DOT_NINE_PLUS("1.9+");

        private final String label;

        @Override
        public String toString() {
            return label;
        }
    }

    @Override
    public String getSuffix() {
        return this.mode.getValue().label + " §7" + this.entities.size();
    }
}
