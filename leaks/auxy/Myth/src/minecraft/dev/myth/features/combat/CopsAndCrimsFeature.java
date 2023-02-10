/**
 * @project Myth
 * @author CodeMan
 * @at 17.09.22, 14:18
 */
package dev.myth.features.combat;

import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import dev.myth.api.event.EventState;
import dev.myth.api.feature.Feature;
import dev.myth.api.utils.EvictingList;
import dev.myth.api.utils.rotation.RotationUtil;
import dev.myth.api.utils.StringUtil;
import dev.myth.api.utils.TeamUtil;
import dev.myth.events.PacketEvent;
import dev.myth.events.UpdateEvent;
import dev.myth.settings.BooleanSetting;
import dev.myth.settings.NumberSetting;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

@Feature.Info(
        name = "Cops and Crims",
        description = "Aimbot for cops and crims",
        category = Feature.Category.COMBAT
)
public class CopsAndCrimsFeature extends Feature {

    public NumberSetting range = new NumberSetting("Range", 50, 0, 200, 1).setSuffix("m");
    public NumberSetting lagCompensation = new NumberSetting("Lag Compensation", 0, 0, 501, 1).setSuffix("ms").addValueAlias(0, "off").addValueAlias(501, "Auto");
    public BooleanSetting silentAim = new BooleanSetting("Silent Aim", true);
    public BooleanSetting recoilControl = new BooleanSetting("Recoil Control", true);
    public BooleanSetting autoShoot = new BooleanSetting("Auto Shoot", true);
    public BooleanSetting autoScope = new BooleanSetting("Auto Scope", true);

    private EvictingList<Long> delays = new EvictingList<>(10);
    private ArrayList<Long> clientSideTimes = new ArrayList<>(),
            serverSideTimes = new ArrayList<>();
    private long lastServerSideShot, lastClientSideShot;
    private float recoil;
    private boolean shouldShoot;

    @Handler
    public final Listener<UpdateEvent> updateEventListener = event -> {
        Weapon weapon = null;
        if(event.getState() == EventState.PRE) {
            shouldShoot = false;

            if(System.currentTimeMillis() - lastServerSideShot > 500) {
                recoil = 0;
            }

            weapon = getWeapon(getPlayer().getHeldItem());
            if(weapon == null) return;

            if(getPlayer().getHeldItem().isItemDamaged()) {
                recoil = 0;
                return;
            }

            if(!getPlayer().onGround) return;

            Weapon finalWeapon = weapon;

            Vec3 target = MC.theWorld.loadedEntityList.stream()
                    .filter(entity -> entity instanceof EntityOtherPlayerMP && entity.isEntityAlive() && !entity.isInvisible() && getPlayer().getDistanceToEntity(entity) <= range.getValue() && !TeamUtil.isOnSameTeamName((EntityPlayer) entity))
                    .map(entity -> (EntityOtherPlayerMP) entity)
                    .sorted(Comparator.comparingDouble(entity -> {
                                double distanceWeight = 1.0 - (double) getPlayer().getDistanceToEntity(entity) / this.range.getValue();
                                double heightWeight = 1.0 - (double) (entity.getHealth() / entity.getMaxHealth());
                                return -distanceWeight - heightWeight;
                            }))
                    .map(this::getTargetVector).filter(Objects::nonNull).findFirst().orElse(null);

            if(target != null) {
                shouldShoot = true;
                if (autoScope.getValue() && (finalWeapon == Weapon.AUG || finalWeapon == Weapon.AWP)) {
                    event.setSneaking(true);
                }
                float[] rotations = RotationUtil.getRotationsToVector(target, getPlayer().getPositionEyes(1F));
                RotationUtil.doRotation(event, rotations, 180, silentAim.getValue());
            }

            if(recoilControl.getValue()) {
                event.setPitch(event.getPitch() + recoil);
            }
        } else if(autoShoot.getValue() && shouldShoot) {
            weapon = getWeapon(getPlayer().getHeldItem());
            if(weapon == null) return;

            if(System.currentTimeMillis() - lastClientSideShot >= weapon.delay) {
                sendPacket(new C08PacketPlayerBlockPlacement(getPlayer().getHeldItem()));
            }
        }
    };

    @Handler
    public final Listener<PacketEvent> packetEventListener = event -> {
        if(event.getState() == EventState.SENDING) {
            if(event.getPacket() instanceof C08PacketPlayerBlockPlacement) {
                C08PacketPlayerBlockPlacement packet = event.getPacket();
                if(packet.getStack() != null && packet.getStack().getItem() != null) {
                    Weapon weapon = getWeapon(packet.getStack());
                    if(weapon != null) {
                        lastClientSideShot = System.currentTimeMillis();
                        clientSideTimes.add(lastClientSideShot);
                        recoil += weapon.recoil * 0.15F;
                    }
                }
            }
        } else {
            if (event.getPacket() instanceof S2FPacketSetSlot) {
                S2FPacketSetSlot setSlotPacket = event.getPacket();
                int windowID = setSlotPacket.getWindowId();
                int slot = setSlotPacket.getSlot();
                ItemStack stack = setSlotPacket.getStack();
                if (windowID == 0 && slot >= 36 && stack != null) {
                    Weapon weaponInSlot = this.getWeapon(stack);
                    if (weaponInSlot == null || stack.isItemDamaged()) return;

                    ItemStack stackInSlot = getPlayer().inventoryContainer.getSlot(slot).getStack();
                    if (this.getWeapon(stackInSlot) == null || stackInSlot.isItemDamaged()) return;

                    if(weaponInSlot != getWeapon(stackInSlot)) return;

                    if (stack.stackSize == stackInSlot.stackSize - 1) {
                        if(clientSideTimes.size() <= serverSideTimes.size()) {
                            return;
                        }
                        lastServerSideShot = System.currentTimeMillis();
                        serverSideTimes.add(System.currentTimeMillis());
                        long delay = lastServerSideShot - lastClientSideShot;
                        doLog("Shot " + weaponInSlot.name() + " Ping: " + delay + "ms");
                        delays.add(delay);
                    }

                }

                if(event.getPacket() instanceof S40PacketDisconnect) {
                    delays.clear();
                    clientSideTimes.clear();
                    serverSideTimes.clear();
                }
            }
        }
    };

    public Vec3 getTargetVector(EntityOtherPlayerMP entity) {
        Vec3 target = new Vec3(entity.posX, entity.posY, entity.posZ);
        Vec3 player = getPlayer().getPositionEyes(1F);
        if(lagCompensation.getValue() > 0) {
            double lagComp = lagCompensation.getValue() / 50;
            if(lagCompensation.getValue() > 500) {
                lagComp = delays.stream().mapToDouble(Long::doubleValue).average().orElse(50) / 50;
            }
            target = target.addVector((entity.posX - entity.prevPosX) * lagComp,
                    0,
                    (entity.posZ - entity.prevPosZ) * lagComp);
        }

        for(int i = 14; i >0; i--){
            MovingObjectPosition raytrace = getWorld().rayTraceBlocks(player, target.addVector(0, i / 10F, 0), false, false, false);
            if (raytrace == null) {
                return target.addVector(0, i / 10F, 0);
            }
        }
        return null;
    }

    private Weapon getWeapon(ItemStack stack) {
        if(stack != null) {
            String stackDisplayName = StringUtil.removeFormatting(stack.getDisplayName());
            return Arrays.stream(Weapon.values()).filter(weapon -> stackDisplayName.startsWith(weapon.name)).findAny().orElse(null);
        }
        return null;
    }

    public enum Weapon {
        USP("USP", 1, 400L),
        HK45("HK45", 1, 250L),
        DEAGLE("Desert Eagle", 3, 400L),
        PUMP_ACTION("Pump Action", 2, 1400L),
        SPAS("SPAS-12", 2, 550L),
        MP5("MP5", 3, 50L),
        P90("P90", 3, 100L),
        AK_47("AK-47", 4, 150L),
        M4("M4", 3, 100L),
        AUG("Steyr AUG", 4, 500L),
        AWP("50 Cal", 1, 950L);

        final String name;
        final float recoil;
        final long delay;

        private Weapon(String name, int recoil, long delay) {
            this.name = name;
            this.recoil = (float) recoil;
            this.delay = delay;
        }
    }

}
