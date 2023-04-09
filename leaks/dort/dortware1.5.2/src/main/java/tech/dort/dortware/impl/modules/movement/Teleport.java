package tech.dort.dortware.impl.modules.movement;

import com.google.common.eventbus.Subscribe;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovementInput;
import tech.dort.dortware.Client;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.api.property.SliderUnit;
import tech.dort.dortware.api.property.impl.EnumValue;
import tech.dort.dortware.api.property.impl.NumberValue;
import tech.dort.dortware.api.property.impl.interfaces.INameable;
import tech.dort.dortware.impl.events.BlockCollisionEvent;
import tech.dort.dortware.impl.events.PacketEvent;
import tech.dort.dortware.impl.events.UpdateEvent;
import tech.dort.dortware.impl.modules.render.FreeCam;
import tech.dort.dortware.impl.utils.combat.FightUtil;
import tech.dort.dortware.impl.utils.movement.MotionUtils;
import tech.dort.dortware.impl.utils.networking.PacketUtil;
import tech.dort.dortware.impl.utils.networking.ServerUtils;
import tech.dort.dortware.impl.utils.pathfinding.DortPathFinder;
import tech.dort.dortware.impl.utils.pathfinding.Vec3;
import tech.dort.dortware.impl.utils.player.ChatUtil;
import tech.dort.dortware.impl.utils.time.Stopwatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Auth
 */

public class Teleport extends Module {

    final Stopwatch stopwatch = new Stopwatch();
    final Stopwatch stopwatchDeath = new Stopwatch();
    private double startX, startY, startZ, lastSecondPosX, lastSecondPosY, lastSecondPosZ;
    private EntityOtherPlayerMP otherPlayerMP;
    private boolean shouldDeathTP;

    public final EnumValue<Mode> mode = new EnumValue<>("Mode", this, Mode.values());
    private final NumberValue speed = new NumberValue("Speed", this, 1, 0.1, 10, SliderUnit.BPT);

    public Teleport(ModuleData moduleData) {
        super(moduleData);
        register(mode, speed);
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        final FreeCam freeCam = Client.INSTANCE.getModuleManager().get(FreeCam.class);
        switch (mode.getValue()) {
            case CLICK: {
                ArrayList<Vec3> path;

                if (mc.gameSettings.keyBindUseItem.getIsKeyPressed() && stopwatch.timeElapsed(500L)) {
                    BlockPos blockPos = mc.objectMouseOver.func_178782_a();
                    Block block = mc.theWorld.getBlockState(blockPos).getBlock();

                    if (block instanceof BlockAir)
                        return;

                    path = DortPathFinder.computePath(freeCam.isToggled() ? new tech.dort.dortware.impl.utils.pathfinding.Vec3(freeCam.startX, freeCam.startY, freeCam.startZ) : new tech.dort.dortware.impl.utils.pathfinding.Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ), new Vec3(blockPos.getX(), blockPos.getY() + 1.0D, blockPos.getZ()));

                    int i = 0;
                    for (Vec3 vector : path) {
                        i++;
                        PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(vector.getX(), vector.getY(), vector.getZ(), true));
                    }
                    mc.thePlayer.setPosition(blockPos.getX(), blockPos.getY() + 1.0D, blockPos.getZ());

                    ChatUtil.displayChatMessage("Teleported to " + blockPos.getX() + " " + blockPos.getY() + " " + blockPos.getZ() + " in " + i + " teleports.");
                    stopwatch.resetTime();
                }
            }
            break;

            case FREECAM: {
                final MovementInput movementInput = mc.thePlayer.movementInput;
                MotionUtils.setMotion(this.speed.getCastedValue().floatValue());
                float newSpeed = this.speed.getCastedValue().floatValue() * 0.5F;
                mc.thePlayer.motionY = movementInput.jump ? newSpeed : movementInput.sneak ? -newSpeed : 0;
            }
            break;

            case DEATH: {
                ArrayList<Vec3> path;

                if (stopwatch.timeElapsed(1000L)) {
                    if (mc.thePlayer.posY < 110) {
                        lastSecondPosX = mc.thePlayer.lastTickPosX;
                        lastSecondPosY = mc.thePlayer.lastTickPosY;
                        lastSecondPosZ = mc.thePlayer.lastTickPosZ;
                    }

                    stopwatch.resetTime();
                }

                if (shouldDeathTP) {
                    if (stopwatchDeath.timeElapsed(250L)) {
                        path = DortPathFinder.computePath(new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ), new Vec3(lastSecondPosX, lastSecondPosY, lastSecondPosZ));

                        int i = 0;
                        for (Vec3 vector : path) {
                            i++;
                            PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(vector.getX(), vector.getY(), vector.getZ(), true));
                        }

                        mc.thePlayer.setPosition(lastSecondPosX, lastSecondPosY, lastSecondPosZ);

                        ChatUtil.displayChatMessage("Teleported to " + Math.round(lastSecondPosX) + " " + Math.round(lastSecondPosY) + " " + Math.round(lastSecondPosZ) + " in " + i + " teleports.");
                        MotionUtils.setMotion(0);

                        shouldDeathTP = false;
                    }
                }
            }
            break;

            case PLAYER: {
                this.toggle();
            }
            break;
        }
    }

    @Subscribe
    public void onCollide(BlockCollisionEvent event) {
        if (mode.getValue() == Mode.FREECAM) {
            event.setAxisAlignedBB(null);
            mc.thePlayer.noClip = true;
        }
    }

    @Subscribe
    public void onPacket(PacketEvent event) {
        switch (mode.getValue()) {
            case FREECAM:
                if (event.getPacket() instanceof S08PacketPlayerPosLook || event.getPacket() instanceof C03PacketPlayer || event.getPacket() instanceof C0BPacketEntityAction || event.getPacket() instanceof C0APacketAnimation || event.getPacket() instanceof C02PacketUseEntity) {
                    event.setCancelled(true);
                }
                break;

            case DEATH:
                if (event.getPacket() instanceof S02PacketChat) {
                    final S02PacketChat packetChat = event.getPacket();

                    if (packetChat.getChatComponent().getUnformattedText().startsWith("�c�lDEATH! �7by") || packetChat.getChatComponent().getUnformattedText().startsWith(mc.thePlayer.getName() + " was killed by")) {
                        shouldDeathTP = true;
                        stopwatchDeath.resetTime();
                    }
                }
                break;
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        stopwatch.resetTime();
        stopwatchDeath.resetTime();

        if (mode.getValue() == Mode.FREECAM) {
            if (otherPlayerMP != null) {
                mc.theWorld.removeEntityFromWorld(otherPlayerMP.getEntityId());
            }
            MotionUtils.setMotion(0);
            mc.thePlayer.noClip = false;

            ArrayList<Vec3> path;

            path = DortPathFinder.computePath(new Vec3(startX, startY, startZ), new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ));

            int i = 0;
            for (Vec3 vector : path) {
                i++;
                PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(vector.getX(), vector.getY(), vector.getZ(), true));
            }

            ChatUtil.displayChatMessage("Teleported to " + Math.round(mc.thePlayer.posX) + " " + Math.round(mc.thePlayer.posY) + " " + Math.round(mc.thePlayer.posZ) + " in " + i + " teleports.");
        }
    }

    @Override
    public void onEnable() {
        super.onDisable();
        switch (mode.getValue()) {
            case FREECAM:
                otherPlayerMP = new EntityOtherPlayerMP(mc.theWorld, new GameProfile(mc.thePlayer.getUniqueID(), mc.thePlayer.getDisplayName().getUnformattedText()));
                otherPlayerMP.setPositionAndRotation(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, 0, 0);
                otherPlayerMP.inventoryContainer = mc.thePlayer.inventoryContainer;
                otherPlayerMP.inventory = mc.thePlayer.inventory;
                mc.theWorld.addEntityToWorld(otherPlayerMP.getEntityId(), otherPlayerMP);
                PacketUtil.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
                break;

            case DEATH:
                ChatUtil.displayChatMessage("This mode is only for Hypixel's Pit gamemode.");
                break;

            case PLAYER:
                List<EntityLivingBase> targets = FightUtil.getMultipleTargets(300, true, false, true, false, false);

                targets.removeIf(e -> Client.INSTANCE.getFriendManager().getObjects().contains(e.getName().toLowerCase()));

                if (ServerUtils.onHypixel()) {
                    targets.removeIf(e -> e.posY > mc.thePlayer.posY - 3.0D);
                }

                targets.removeIf((EntityLivingBase entity) -> {
                    for (int offset = 0; offset < entity.posY + entity.getEyeHeight(); offset += 2) {
                        final AxisAlignedBB boundingBox = entity.getEntityBoundingBox().offset(0, -offset, 0);

                        if (!mc.theWorld.getCollidingBoundingBoxes(entity, boundingBox).isEmpty())
                            return false;
                    }
                    return true;
                });

                targets.sort(Comparator.comparingInt(e -> (int) -e.getHealth()));
                Collections.reverse(targets);

                if (targets.size() == 0) {
                    ChatUtil.displayChatMessage("No target found.");
                    return;
                }

                EntityLivingBase target = targets.get(0);

                ArrayList<Vec3> path = DortPathFinder.computePath(new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ), new Vec3(target.posX, target.posY, target.posZ));

                int i = 0;
                for (Vec3 vector : path) {
                    i++;
                    PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(vector.getX(), vector.getY(), vector.getZ(), true));
                }
                mc.thePlayer.setPosition(target.posX, target.posY, target.posZ);

                ChatUtil.displayChatMessage("Teleported to " + target.getName() + " in " + i + " teleports.");
                this.toggle();
                break;
        }
        startX = mc.thePlayer.posX;
        startY = mc.thePlayer.posY;
        startZ = mc.thePlayer.posZ;
    }

    public enum Mode implements INameable {
        CLICK("Click"), FREECAM("Free Cam"), DEATH("Death"), PLAYER("Player");
        public final String name;

        Mode(String name) {
            this.name = name;
        }

        @Override
        public String getDisplayName() {
            return name;
        }
    }

    @Override
    public String getSuffix() {
        return " \2477" + mode.getValue().getDisplayName();
    }
}
