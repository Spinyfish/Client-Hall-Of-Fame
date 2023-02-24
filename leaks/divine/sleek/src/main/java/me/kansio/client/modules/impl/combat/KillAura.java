package me.kansio.client.modules.impl.combat;

import com.google.common.eventbus.Subscribe;
import me.kansio.client.Client;
import me.kansio.client.event.impl.PacketEvent;
import me.kansio.client.event.impl.RenderOverlayEvent;
import me.kansio.client.event.impl.UpdateEvent;
import me.kansio.client.gui.notification.Notification;
import me.kansio.client.gui.notification.NotificationManager;
import me.kansio.client.modules.api.ModuleCategory;
import me.kansio.client.modules.api.ModuleData;
import me.kansio.client.modules.impl.Module;
import me.kansio.client.value.value.BooleanValue;
import me.kansio.client.value.value.ModeValue;
import me.kansio.client.value.value.NumberValue;
import me.kansio.client.utils.combat.FightUtil;
import me.kansio.client.utils.math.Stopwatch;
import me.kansio.client.utils.network.PacketUtil;
import me.kansio.client.utils.rotations.AimUtil;
import me.kansio.client.utils.rotations.Rotation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.apache.commons.lang3.RandomUtils;

import javax.vecmath.Vector2f;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@ModuleData(
        name = "Killaura",
        category = ModuleCategory.COMBAT,
        description = "Automatically attacks nearby entities"
)
public class KillAura extends Module {

    public static EntityLivingBase target;
    public static boolean isBlocking, swinging;
    public final Stopwatch attackTimer = new Stopwatch();
    public ModeValue mode = new ModeValue("Mode", this, /*"Switch",*/ "Smart");
    public ModeValue targetPriority = new ModeValue("Target Priority", this, "None", "Distance", "Armor", "HurtTime", "Health");
    public ModeValue rotatemode = new ModeValue("Rotation Mode", this, "None", "Default", "Down", "NCP", "AAC", "GWEN");
    public NumberValue<Double> swingrage = new NumberValue<>("Swing Range", this, 3.0, 1.0, 9.0, 0.1);
    public NumberValue<Double> autoblockRange = new NumberValue<>("Block Range", this, 3.0, 1.0, 12.0, 0.1);
    public NumberValue<Double> cps = new NumberValue<>("CPS", this, 12.0, 1.0, 20.0, 1.0);
    public NumberValue<Double> cprandom = new NumberValue<>("Randomize CPS", this, 3.0, 0.0, 10.0, 1.0);
    public NumberValue chance = new NumberValue<>("Hit Chance", this, 100, 0, 100, 1);
    public ModeValue swingmode = new ModeValue("Swing Mode", this, "Client", "Server");
    public ModeValue attackMethod = new ModeValue("Attack Method", this, "Packet", "Legit");
    public ModeValue autoblockmode = new ModeValue("Autoblock Mode", this, "None", "Real", "Verus", "Fake");
    public BooleanValue gcd = new BooleanValue("GCD", this, false);
    public BooleanValue targethud = new BooleanValue("TargetHud", this, false);
    public ModeValue targethudmode = new ModeValue("TargetHud Mode", this, targethud, "Sleek", "Moon");
    public BooleanValue players = new BooleanValue("Players", this, true);
    public BooleanValue friends = new BooleanValue("Friends", this, true);
    public BooleanValue animals = new BooleanValue("Animals", this, true);
    public BooleanValue monsters = new BooleanValue("Monsters", this, true);
    public BooleanValue invisible = new BooleanValue("Invisibles", this, true);
    public BooleanValue walls = new BooleanValue("Walls", this, true);
    public Vector2f currentRotation = null;
    private int index;
    private boolean canBlock;
    private Rotation lastRotation;

    public static boolean isSwinging() {
        return swinging;
    }

    @Override
    public void onEnable() {
        index = 0;
        lastRotation = null;
        target = null;
        this.attackTimer.resetTime();
    }

    @Override
    public void onDisable() {
        if (isBlocking) unblock();
        isBlocking = false;
        mc.gameSettings.keyBindUseItem.pressed = false;
        swinging = false;
        currentRotation = null;
        target = null;

        if (!mc.thePlayer.isBlocking()) {
            isBlocking = false;
            mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
        }
    }

    @Subscribe
    public void doHoldBlock(UpdateEvent event) {
        if (autoblockmode.getValue().equalsIgnoreCase("Hold")) {
            mc.gameSettings.keyBindUseItem.pressed = KillAura.target != null;
        }
    }

    @Subscribe
    public void onMotion(UpdateEvent event) {
        List<EntityLivingBase> entities = FightUtil.getMultipleTargets(swingrage.getValue(), players.getValue(), friends.getValue(), animals.getValue(), walls.getValue(), monsters.getValue(), invisible.getValue());

        if (mc.currentScreen != null) return;

        if (isBlocking && target == null) {
            unblock();
            mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
        }


        if (mc.thePlayer.ticksExisted < 5) {
            if (isToggled()) {
                NotificationManager.getNotificationManager().show(new Notification(Notification.NotificationType.INFO, "World Change!", "Killaura disabled", 1));
                toggle();
            }
        }

        List<EntityLivingBase> blockRangeEntites = FightUtil.getMultipleTargets(autoblockRange.getValue(), players.getValue(), friends.getValue(), animals.getValue(), walls.getValue(), monsters.getValue(), invisible.getValue());

        entities.removeIf(e -> e.getName().contains("[NPC]"));

        ItemStack heldItem = mc.thePlayer.getHeldItem();

        canBlock = !blockRangeEntites.isEmpty()
                && heldItem != null
                && heldItem.getItem() instanceof ItemSword;

        if (event.isPre()) {
            target = null;
        }

        if (entities.isEmpty()) {
            index = 0;

            isBlocking = false;
        } else {
            if (index >= entities.size()) index = 0;

            if (canBlock) {
                switch (autoblockmode.getValue()) {
                    case "Real":
                        if (!event.isPre()) {
                            mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem());
                            isBlocking = true;
                        }
                        break;
                    case "Fake": {
                        isBlocking = true;
                        break;
                    }
                }
            }


            if (event.isPre()) {
                switch (mode.getValue()) {
                    case "Smart": {
                        switch (targetPriority.getValue().toLowerCase()) {
                            case "distance": {
                                entities.sort(Comparator.comparingInt(e -> (int) -e.getDistanceToEntity(mc.thePlayer)));
                                break;
                            }
                            case "armor": {
                                entities.sort(Comparator.comparingInt(e -> -e.getTotalArmorValue()));
                                break;
                            }
                            case "hurttime": {
                                entities.sort(Comparator.comparingInt(e -> -e.hurtResistantTime));
                                break;
                            }
                            case "health": {
                                entities.sort(Comparator.comparingInt(e -> (int) -e.getHealth()));
                                break;
                            }
                        }
                        Collections.reverse(entities);
                        target = entities.get(0);

                        //set the targetted players as main targets.
                        entities.forEach(entityLivingBase -> {
                            if (entityLivingBase instanceof EntityPlayer && Client.getInstance().getTargetManager().isTarget((EntityPlayer) target)) {
                                target = entities.get(entities.indexOf(entityLivingBase));
                            }
                        });
                        break;
                    }
                    case "switch": {
                        target = entities.get(index);
                        if (target == null) {
                            target = entities.get(0);
                        }
                        break;
                    }
                }
                aimAtTarget(event, rotatemode.getValue(), target);
            }

            if (event.isPre()) {

                boolean canIAttack = attackTimer.timeElapsed((long) (1000L / cps.getValue()));

                if (canIAttack) {
                    if (cps.getValue() > 9) {
                        cps.setValue(cps.getValue() - RandomUtils.nextInt(0, cprandom.getValue().intValue()));
                    } else {
                        cps.setValue(cps.getValue() + RandomUtils.nextInt(0, cprandom.getValue().intValue()));
                    }
                    switch (mode.getValue()) {
                        case "Switch": {
                            if (canIAttack && attack(target, chance.getValue().intValue(), autoblockmode.getValue())) {
                                index++;
                                attackTimer.resetTime();
                            }
                            break;
                        }
                        case "Smart": {
                            if (canIAttack && attack(target, chance.getValue().intValue(), autoblockmode.getValue())) {
                                attackTimer.resetTime();
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean attack(EntityLivingBase entity, double chance, String blockMode) {
        if (FightUtil.canHit(chance / 100)) {
            if (swingmode.getValue().equalsIgnoreCase("client")) {
                mc.thePlayer.swingItem();
            }
            else if (swingmode.getValue().equalsIgnoreCase("server")) {
                mc.getNetHandler().addToSendQueue(new C0APacketAnimation());
            }

            //sending the attack directly through a packet prevents you from getting slowed down when hitting
            if (attackMethod.getValue().equalsIgnoreCase("Packet"))
                PacketUtil.sendPacket(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));
            else
                mc.playerController.attackEntity(mc.thePlayer, entity);

            if (!isBlocking && autoblockmode.getValue().equalsIgnoreCase("verus")) {
                PacketUtil.sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
                isBlocking = true;
            }

            return true;
        } else {
            mc.thePlayer.swingItem();
        }
        return false;
    }

    public void aimAtTarget(UpdateEvent event, String mode, Entity target) {
        Rotation rotation = AimUtil.getRotationsRandom((EntityLivingBase) target);

        if (lastRotation == null) {
            lastRotation = rotation;
            attackTimer.resetTime();
            return;
        }

        Rotation temp = rotation;

        rotation = lastRotation;

        switch (mode.toUpperCase()) {
            case "DEFAULT":
                event.setRotationYaw(rotation.getRotationYaw());
                event.setRotationPitch(rotation.getRotationPitch());
                break;
            case "DOWN":
                temp = new Rotation(mc.thePlayer.rotationYaw, 90.0f);
                event.setRotationPitch(90.0F);
                break;
            case "NCP":
                lastRotation = temp = rotation = Rotation.fromFacing((EntityLivingBase) target);
                event.setRotationYaw(rotation.getRotationYaw());
                break;
            case "AAC":
                rotation = new Rotation(mc.thePlayer.rotationYaw, temp.getRotationPitch());
                event.setRotationPitch(rotation.getRotationPitch());
                break;
            case "GWEN":
                temp = mc.thePlayer.ticksExisted % 5 == 0 ? AimUtil.getRotationsRandom((EntityLivingBase) target) : lastRotation;
                event.setRotationYaw(temp.getRotationYaw());
                event.setRotationPitch(temp.getRotationPitch());
                break;
        }
        lastRotation = temp;
    }

    @Subscribe
    public void onPacket(PacketEvent event) {
        Packet packet = event.getPacket();
        if (isBlocking && ((packet instanceof C07PacketPlayerDigging && ((C07PacketPlayerDigging) packet).getStatus() == C07PacketPlayerDigging.Action.RELEASE_USE_ITEM) || packet instanceof C08PacketPlayerBlockPlacement)) {
            event.setCancelled(true);
        }
        if (packet instanceof C09PacketHeldItemChange) {
            isBlocking = false;
        }

        if (gcd.getValue() && target != null && event.getPacket() instanceof C03PacketPlayer && ((C03PacketPlayer) event.getPacket()).getRotating()) {
            C03PacketPlayer p = event.getPacket();
            float m = (float) (0.005 * mc.gameSettings.mouseSensitivity / 0.005);
            double f = m * 0.6 + 0.2;
            double gcd = m * m * m * 1.2;
            p.pitch -= p.pitch % gcd;
            p.yaw -= p.yaw % gcd;
        }
    }

    @Subscribe
    public void onRender(RenderOverlayEvent event) {
        if (target == null) {
            return;
        }
        if (targethud.getValue()) {
            TargetHUD.draw(event, target);
        }
    }

    private void unblock() {
        isBlocking = false;
    }

    @Override
    public String getSuffix() {
        return " " + mode.getValueAsString();
    }
}
