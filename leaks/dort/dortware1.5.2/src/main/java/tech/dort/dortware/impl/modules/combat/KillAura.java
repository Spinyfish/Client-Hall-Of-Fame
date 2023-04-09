package tech.dort.dortware.impl.modules.combat;

import com.google.common.eventbus.Subscribe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.*;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StringUtils;
import org.apache.commons.lang3.RandomUtils;
import tech.dort.dortware.Client;
import tech.dort.dortware.api.font.CustomFontRenderer;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.api.property.SliderUnit;
import tech.dort.dortware.api.property.impl.BooleanValue;
import tech.dort.dortware.api.property.impl.EnumValue;
import tech.dort.dortware.api.property.impl.NumberValue;
import tech.dort.dortware.api.property.impl.interfaces.INameable;
import tech.dort.dortware.impl.events.PacketEvent;
import tech.dort.dortware.impl.events.RenderHUDEvent;
import tech.dort.dortware.impl.events.StrafeEvent;
import tech.dort.dortware.impl.events.UpdateEvent;
import tech.dort.dortware.impl.gui.click.GuiUtils;
import tech.dort.dortware.impl.modules.movement.Speed;
import tech.dort.dortware.impl.modules.player.Scaffold;
import tech.dort.dortware.impl.modules.render.Hud;
import tech.dort.dortware.impl.modules.render.Rotate;
import tech.dort.dortware.impl.utils.combat.AimUtil;
import tech.dort.dortware.impl.utils.combat.FightUtil;
import tech.dort.dortware.impl.utils.combat.extras.Rotation;
import tech.dort.dortware.impl.utils.movement.MotionUtils;
import tech.dort.dortware.impl.utils.networking.PacketUtil;
import tech.dort.dortware.impl.utils.render.ColorUtil;
import tech.dort.dortware.impl.utils.time.Stopwatch;

import java.awt.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class KillAura extends Module {

    public static boolean blocking;
    public static EntityLivingBase currentTarget;
    private final Stopwatch attackTimer = new Stopwatch();
    private final BooleanValue modeSettings = new BooleanValue("Mode Settings", this, false);
    private final BooleanValue attackSettings = new BooleanValue("Attack Settings", this, false);
    private final BooleanValue targetSettings = new BooleanValue("Target Settings", this, false);
    private final EnumValue<Mode> mode = new EnumValue<>("Mode", this, Mode.values(), modeSettings);
    private final EnumValue<BlockMode> blockMode = new EnumValue<>("Block Mode", this, BlockMode.values(), modeSettings);
    private final EnumValue<AttackMode> attackMode = new EnumValue<>("Attack Mode", this, AttackMode.values(), modeSettings);
    private final EnumValue<RotationMode> rotationMode = new EnumValue<>("Rotation Mode", this, RotationMode.values(), modeSettings);
    private final EnumValue<SortingMode> sortingMode = new EnumValue<>("Sorting Mode", this, SortingMode.values(), modeSettings);
    /* attack settings */
    private final NumberValue range = new NumberValue("Range", this, 3, 1, 8, SliderUnit.BLOCKS, attackSettings);
    private final NumberValue blockRange = new NumberValue("Block Range", this, 3, 1, 12, SliderUnit.BLOCKS, attackSettings);
    private final NumberValue attackSpeed = new NumberValue("Attack Speed", this, 10, 1, 20, SliderUnit.CPS, true, attackSettings);
    private final NumberValue speedRandomization = new NumberValue("Speed Randomization", this, 0, 0, 5, SliderUnit.CPS, true, attackSettings);
    private final NumberValue hitRate = new NumberValue("Hitrate", this, 100, 0, 100, SliderUnit.PERCENT, true, attackSettings);
    public final BooleanValue sprintCheck = new BooleanValue("Sprint Check", this, false, attackSettings);
    /* attack settings */
    private final BooleanValue correctStrafe = new BooleanValue("Correct Movement", this, false, attackSettings);
    private final BooleanValue silentCorrection = new BooleanValue("Silent Correction", this, false, attackSettings);
    /* target settings */
    private final BooleanValue invis = new BooleanValue("Invisibles", this, false, targetSettings);
    private final BooleanValue friends = new BooleanValue("Friends", this, false, targetSettings);
    private final BooleanValue players = new BooleanValue("Players", this, true, targetSettings);
    private final BooleanValue animals = new BooleanValue("Animals", this, false, targetSettings);
    private final BooleanValue teams = new BooleanValue("Teams", this, false, targetSettings);
    private final BooleanValue walls = new BooleanValue("Walls", this, false, targetSettings);
    private final BooleanValue mobs = new BooleanValue("Mobs", this, false, targetSettings);
    /* target settings */
    public boolean canBlock;
    private int index;
    private float animated;
    private Rotation lastRotation;

    public KillAura(ModuleData moduleData) {
        super(moduleData);
        register(modeSettings, attackSettings, targetSettings, mode, blockMode, attackMode, rotationMode, sortingMode,
                attackSpeed, speedRandomization, hitRate, range, blockRange, correctStrafe, silentCorrection, sprintCheck, invis,
                friends, players, animals, teams, walls, mobs);
    }

    public double[] getInfo() {
        double range = this.range.getValue();
        double attackSpeed = this.attackSpeed.getValue();
        return new double[]{attackSpeed, range};
    }

    @Subscribe
    public void onStrafe(StrafeEvent event) {
        if (currentTarget != null && lastRotation != null && correctStrafe.getValue()) {
            event.setYaw(lastRotation.getRotationYaw());
            event.setPitch(lastRotation.getRotationPitch());
            event.setSilent(silentCorrection.getValue());
        }
    }

    @Subscribe
    public void onRender(RenderHUDEvent event) {
        if (currentTarget == null || Client.INSTANCE.getModuleManager().get(TPAura.class).isToggled())
            return;

        final CustomFontRenderer font = Client.INSTANCE.getFontManager().getFont("Small1").getRenderer();

        final ScaledResolution sr = event.getSr();
        final Hud hud = Client.INSTANCE.getModuleManager().get(Hud.class);

        final float x = ((sr.getScaledWidth() / 2.0f) + hud.targetX.getValue().floatValue()) - 80.0F - font.getWidth(String.valueOf(Math.round(currentTarget.getHealth())));
        final float y = (sr.getScaledHeight() / 2.0f) + hud.targetY.getValue().floatValue();

        if (hud.targetHUD.getValue() && mc.thePlayer != null) {
            switch (hud.targetHudMode.getValue()) {
                case SIMPLE: {
                    final Color healthColor = new Color(ColorUtil.getHealthColor(currentTarget));
                    final float xSpeed = 100F / (Minecraft.func_175610_ah() * 1.05F);
                    final float desiredWidth = (150.0F / currentTarget.getMaxHealth()) * Math.min(currentTarget.getHealth(), currentTarget.getMaxHealth());

                    GuiUtils.drawRect1(x - 1, y - 1, 150.0F, 12.0F, new Color(0, 0, 0, 100).getRGB());

                    if (desiredWidth < animated || desiredWidth > animated) {
                        if (Math.abs(desiredWidth - animated) <= xSpeed) {
                            animated = desiredWidth;
                        } else {
                            animated += (animated < desiredWidth ? xSpeed * 3 : -xSpeed);
                        }
                    }

                    GuiUtils.drawRect1(x - 1, y - 1, animated, 12.0F, healthColor.getRGB());

                    font.drawStringWithShadow(currentTarget.getName(), x, y + 2, -1);
                    font.drawStringWithShadow(String.valueOf(Math.round(currentTarget.getHealth())), x + 149.0F - font.getWidth(String.valueOf(Math.round(currentTarget.getHealth()))), y + 2, -1);
                }
                break;

                case HELIUM: {
                    final Color healthColor = new Color(ColorUtil.getHealthColor(currentTarget));
                    final String playerName = "Name: " + StringUtils.stripControlCodes(currentTarget.getName());
                    final int distance = (int) ((mc.thePlayer.getDistanceToEntity(currentTarget)));
                    final float xSpeed = 133F / (Minecraft.func_175610_ah() * 1.05F);
                    final float desiredWidth = (140F / currentTarget.getMaxHealth()) * Math.min(currentTarget.getHealth(), currentTarget.getMaxHealth());

                    GuiUtils.drawRect1(x - 1, y - 1, 142F, 44F, new Color(0, 0, 0, 100).getRGB());
                    GuiUtils.drawRect1(x, y, 140F, 40F, new Color(0, 0, 0, 75).getRGB());
                    GuiUtils.drawRect1(x, y + 40, 140, 2, new Color(0, 0, 0).getRGB());

                    mc.fontRendererObj.drawStringWithShadow(playerName, x + 25.5F, y + 4F, -1);
                    mc.fontRendererObj.drawStringWithShadow("Distance: " + distance + "m", x + 25.5F, y + 15F, -1);
                    mc.fontRendererObj.drawStringWithShadow("Armor: " + Math.round(currentTarget.getTotalArmorValue()), x + 25.5F, y + 25F, -1);

                    if (mc.currentScreen == null)
                        GuiInventory.drawEntityOnScreen((int) x + 12, (int) y + 31, 13, currentTarget.rotationYaw, -currentTarget.rotationPitch, currentTarget);
                    if (desiredWidth < animated || desiredWidth > animated) {
                        if (Math.abs(desiredWidth - animated) <= xSpeed) {
                            animated = desiredWidth;
                        } else {
                            animated += (animated < desiredWidth ? xSpeed * 3 : -xSpeed);
                        }
                    }

                    GuiUtils.drawRect1(x, y + 40F, animated, 2F, healthColor.getRGB());
                }
                break;
            }
        }
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        try {

            if (Client.INSTANCE.getModuleManager().isEnabled(Scaffold.class))
                return;

            if (mc.thePlayer.ticksExisted <= 10 && event.isPre()) {
                toggle();
            }
//            AutoUse autoUse = Client.INSTANCE.getModuleManager().get(AutoUse.class);
            if (AutoPot.isPotting()) {
                blocking = false;
                return;
            }

            List<EntityLivingBase> entities = FightUtil.getMultipleTargets(range.getValue(), players.getValue(),
                    animals.getValue(), walls.getValue(), mobs.getValue(), invis.getValue());

            List<EntityLivingBase> blockRangeEntites = FightUtil.getMultipleTargets(blockRange.getValue(), players.getValue(),
                    animals.getValue(), walls.getValue(), mobs.getValue(), invis.getValue());

            if (!friends.getValue()) {
                entities.removeIf(e -> Client.INSTANCE.getFriendManager().getObjects().contains(e.getName().toLowerCase()));
                blockRangeEntites.removeIf(e -> Client.INSTANCE.getFriendManager().getObjects().contains(e.getName().toLowerCase()));
            }
            entities.removeIf(e -> e.getName().contains("[NPC]"));
            if (teams.getValue()) {
                entities.removeIf(FightUtil::isOnSameTeam);
                blockRangeEntites.removeIf(FightUtil::isOnSameTeam);
            }

            ItemStack heldItem = mc.thePlayer.getHeldItem();

            canBlock = !blockRangeEntites.isEmpty() && blockMode.getValue() != BlockMode.NONE
                    && heldItem != null
                    && heldItem.getItem() instanceof ItemSword;

            mc.thePlayer.setBlocking(canBlock);

            if (event.isPre())
                currentTarget = null;
            if (entities.isEmpty()) {
                index = 0;
                if (blocking && blockMode.getValue() != BlockMode.VANILLA)
                    unblock();
            } else {
                if (index >= entities.size()) {
                    index = 0;
                }
                if (attackMode.getValue() == AttackMode.PRE && canBlock && blockMode.getValue() != BlockMode.FAKE) {
                    if (event.isPre() && blockMode.getValue() != BlockMode.VANILLA) {
                        unblock();
                    } else if (!event.isPre()) {
                        block(entities.get(index));
                    }
                }
                if (event.isPre()) {
                    switch (mode.getValue()) {
                        case SMART:
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
                            currentTarget = entities.get(0);
                            break;
                        case SWITCH:
                            currentTarget = entities.get(index);
                            break;
                        case MULTI:
                            currentTarget = entities.get(0);
                            break;
                    }
                    rotateToEntity(currentTarget, rotationMode.getValue(), event);
                }

                if (event.isPre() && attackMode.getValue() == AttackMode.PRE || !event.isPre() && attackMode.getValue() == AttackMode.POST) {
                    boolean canAttack = attackTimer.timeElapsed((long) (1000L / attackSpeed.getValue()));
                    if (canAttack) {
                        if (attackSpeed.getValue() > 9) {
                            attackSpeed.setValue(attackSpeed.getValue() - RandomUtils.nextInt(0, speedRandomization.getValue().intValue()));
                        } else {
                            attackSpeed.setValue(attackSpeed.getValue() + RandomUtils.nextInt(0, speedRandomization.getValue().intValue()));
                        }
                    }
                    switch (this.mode.getValue()) {
                        case MULTI: {
                            if (canAttack) {
                                for (EntityLivingBase entity : entities) {
                                    attack(entity, hitRate.getValue(), blockMode.getValue());
                                }
                                attackTimer.resetTime();
                            }
                            break;
                        }
                        case SWITCH: {
                            if (canAttack && attack(currentTarget, hitRate.getValue(), blockMode.getValue())) {
                                index++;
                                attackTimer.resetTime();
                            }
                            break;
                        }
                        case SMART: {
                            if (canAttack && attack(currentTarget, hitRate.getValue(), blockMode.getValue())) {
                                attackTimer.resetTime();
                            }
                            break;
                        }
                    }
                }
                if (attackMode.getValue() == AttackMode.POST && canBlock && blockMode.getValue() != BlockMode.FAKE) {
                    if (event.isPre() && blockMode.getValue() != BlockMode.VANILLA) {
                        unblock();
                    } else if (!event.isPre()) {
                        block(entities.get(index));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onPacket(PacketEvent event) {
        event.forceCancel(event.getPacket() instanceof C03PacketPlayer && silentCorrection.getValue() && lastRotation != null && currentTarget != null);
    }

    public void block(Entity target) {
        mc.playerController.interactWithEntitySendPacket(mc.thePlayer, target);
        PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.getHeldItem(), 0, 0, 0));
        blocking = true;
    }

    private void unblock() {
        PacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
        blocking = false;
    }

    private void rotateToEntity(EntityLivingBase entity, RotationMode type, UpdateEvent event) {
        Rotation rotation = AimUtil.getRotationsRandom(entity);

        if (lastRotation == null) {
            lastRotation = rotation;
            attackTimer.resetTime();
            return;
        }

        Rotation temp = rotation;

        rotation = lastRotation;

        switch (type) {
            case NCP:
                lastRotation = temp = rotation = Rotation.fromFacing(entity);
                event.setRotationYaw(rotation.getRotationYaw());
                break;
            case NORMAL:
                event.setRotationYaw(rotation.getRotationYaw());
                event.setRotationPitch(rotation.getRotationPitch());
                break;
            case DOWN:
                temp = new Rotation(mc.thePlayer.rotationYaw, 90.0f);
                event.setRotationPitch(90.0F);
                break;
            case NON_SILENT:
                AimUtil.turnToEntityClient(temp);
                break;
            case GWEN:
                temp = mc.thePlayer.ticksExisted % 5 == 0 ? AimUtil.getRotationsRandom(entity) : lastRotation;
                event.setRotationYaw(temp.getRotationYaw());
                event.setRotationPitch(temp.getRotationPitch());
                break;
            case AAC:
                rotation = new Rotation(mc.thePlayer.rotationYaw, temp.getRotationPitch());
                event.setRotationPitch(rotation.getRotationPitch());
                break;
        }
        lastRotation = temp;

        if (Client.INSTANCE.getModuleManager().get(Rotate.class).isToggled() && !type.equals(RotationMode.NONE)) {
            mc.thePlayer.renderPitchHead = temp.getRotationPitch();
            mc.thePlayer.renderYawOffset = temp.getRotationYaw();
            mc.thePlayer.renderYawHead = temp.getRotationYaw();
        }
    }

    private boolean attack(EntityLivingBase entity, double chance, KillAura.BlockMode blockMode) {
        Criticals criticals = Client.INSTANCE.getModuleManager().get(Criticals.class);

        if (criticals.isToggled()) {
            switch (criticals.mode.getValue()) {
                case HYPIXEL:
                    if (mc.thePlayer.onGround && entity.hurtResistantTime != -1 && !Client.INSTANCE.getModuleManager().get(Speed.class).isToggled()) {
                        for (double o : criticals.hypixelValues) {
                            PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + o, mc.thePlayer.posZ, false));
                        }
                        entity.hurtResistantTime = -1;
                    }
                    break;

                case PACKET:
                    if (mc.thePlayer.onGround && entity.hurtResistantTime != -1) {
                        for (double o : criticals.packetValues) {
                            PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + o, mc.thePlayer.posZ, false));
                        }
                        entity.hurtResistantTime = -1;
                    }
                    break;
                case MINEPLEX:
                    if (mc.thePlayer.onGround && entity.hurtResistantTime != -1) {
                        for (double o : criticals.mineplexValues) {
                            PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + o, mc.thePlayer.posZ, false));
                        }
                        entity.hurtResistantTime = -1;
                    }
                    break;
                case JUMP:
                    if (mc.thePlayer.onGround) {
                        mc.thePlayer.motionY = MotionUtils.getMotion(0.42F);
                    }
                    break;
            }
        }

        if (FightUtil.canHit(chance / 100)) {
            mc.thePlayer.swingItem();

            if (sprintCheck.getValue()) {
                PacketUtil.sendPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
                PacketUtil.sendPacket(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
                PacketUtil.sendPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
            } else {
                PacketUtil.sendPacket(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
            }

            if ((blockMode == KillAura.BlockMode.VANILLA) && canBlock) {
                block(entity);
            }

            return true;
        } else {
            mc.thePlayer.swingItemNoEvent();
        }
        return false;
    }

    @Override
    public void onEnable() {
        lastRotation = null;
        currentTarget = null;
        index = 0;
    }

    @Override
    public void onDisable() {
        if (blocking) {
            unblock();
        }
        currentTarget = null;
    }

    @Override
    public String getSuffix() {
        String mode = this.mode.getValue().getDisplayName();
        return " \2477" + mode;
    }

    private enum Mode implements INameable {
        SMART("Smart"), SWITCH("Switch"), MULTI("Multi");

        private final String name;

        Mode(String name) {
            this.name = name;
        }

        @Override
        public String getDisplayName() {
            return this.name;
        }
    }

    private enum AttackMode implements INameable {
        PRE("Pre"), POST("Post");

        private final String name;

        AttackMode(String name) {
            this.name = name;
        }

        @Override
        public String getDisplayName() {
            return this.name;
        }
    }

    private enum RotationMode implements INameable {
        NORMAL("Normal"), NCP("NCP"), NON_SILENT("Non Silent"), GWEN("GWEN"), AAC("AAC"), DOWN("Down"), NONE("None");

        private final String name;

        RotationMode(String name) {
            this.name = name;
        }

        @Override
        public String getDisplayName() {
            return this.name;
        }
    }

    public enum BlockMode implements INameable {
        NONE("None"), FAKE("Fake"), VANILLA("Vanilla"), NCP("NCP");

        private final String name;

        BlockMode(String name) {
            this.name = name;
        }

        @Override
        public String getDisplayName() {
            return this.name;
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
