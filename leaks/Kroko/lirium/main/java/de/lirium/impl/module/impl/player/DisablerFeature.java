/**
 * @project LiriumV4
 * @author Skush/Duzey
 * @at 06.07.2022
 */
package de.lirium.impl.module.impl.player;

import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import com.mojang.realmsclient.gui.ChatFormatting;
import de.lirium.base.helper.TimeHelper;
import de.lirium.base.setting.Dependency;
import de.lirium.base.setting.Value;
import de.lirium.base.setting.impl.CheckBox;
import de.lirium.base.setting.impl.ComboBox;
import de.lirium.impl.events.BuildToHighEvent;
import de.lirium.impl.events.PacketEvent;
import de.lirium.impl.events.UpdateEvent;
import de.lirium.impl.module.ModuleFeature;
import de.lirium.impl.module.ModuleManager;
import de.lirium.util.misc.ServerUtil;
import god.buddy.aot.BCompiler;
import io.netty.buffer.Unpooled;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@ModuleFeature.Info(name = "Disabler", description = "Anti cheat say good bye", category = ModuleFeature.Category.PLAYER)
public class DisablerFeature extends ModuleFeature {

    @Value(name = "Mode")
    private final ComboBox<String> mode = new ComboBox<>("Sentinel", new String[]{"WatchDog", "Block Drop", "MushMC", "WatchCat"});

    @Value(name = "WatchDog - Position Disabler", displayName = "Position Disabler")
    private final CheckBox watchDogPositionDisabler = new CheckBox(true, new Dependency<>(mode, "WatchDog"));

    @Value(name = "WatchDog - Timer Disabler", displayName = "Timer Disabler")
    private final CheckBox watchDogTimerDisabler = new CheckBox(true, new Dependency<>(mode, "WatchDog"));

    @Value(name = "WatchDog - Timer Disabler V2 | Bi-Turbo", displayName = "Timer Disabler V2 | Bi-Turbo")
    private final CheckBox watchDogTimerDisablerV2 = new CheckBox(true, new Dependency<>(mode, "WatchDog"));

    @Value(name = "Terrorismus Disabler", displayName = "Terrorismus Disabler")
    private final CheckBox watchDogTerrorismusDisabler = new CheckBox(true, new Dependency<>(mode, "WatchDog"));

    @Value(name = "Experimental Disabler", displayName = "Experimental Disabler")
    private final CheckBox watchDogExperimentalDisabler = new CheckBox(true, new Dependency<>(mode, "WatchDog"));

    @Value(name = "Israel Diasbler", displayName = "Israel Disabler")
    private final CheckBox watchDogIsraelDisabler = new CheckBox(true, new Dependency<>(mode, "WatchDog"));

    @Value(name = "Hack Disabler", displayName = "Hack Diasbler")
    private final CheckBox watchDogHackDisabler = new CheckBox(true, new Dependency<>(mode, "WatchDog"));

    @Value(name = "Sentinel - Rotation Disabler", displayName = "Rotation Disabler")
    private final CheckBox sentinelRotationDisabler = new CheckBox(false, new Dependency<>(mode, "Sentinel"));

    @Value(name = "Sentinel - Movement Disabler", displayName = "Movement Disabler")
    private final CheckBox sentinelMovementDisabler = new CheckBox(true, new Dependency<>(mode, "Sentinel"));

    @Value(name = "Block Drop - Smart")
    private final CheckBox blockDropSmart = new CheckBox(false, new Dependency<>(mode, "Block Drop"));

    /**
     * Watchdog
     */
    long lastKey;
    int receivedConfirmTP;
    final List<Packet<? extends INetHandler>> watchDogPackets = new ArrayList<>();

    final List<Packet<?>> packets = new ArrayList<>();

    private final TimeHelper timer = new TimeHelper();

    private int ticks;

    @EventHandler
    public final Listener<UpdateEvent> updateEvent = e -> {
        setSuffix(mode.getValue());
        if (mc.isSingleplayer()) return;
        switch (mode.getValue()) {
            case "WatchCat":
                sendPacketUnlogged(new CPacketClientStatus(CPacketClientStatus.State.PERFORM_RESPAWN));
                break;
            case "WatchDog":
                doWatchDog(e);
                break;
            case "Sentinel":
                doSentinel(null);
                break;
        }
    };

    @EventHandler
    public final Listener<PacketEvent> packetEvent = e -> {
        if (mc.isSingleplayer()) return;
        switch (mode.getValue()) {
            case "Sentinel":
                doSentinel(e);
                break;
            case "WatchDog":
                doWatchDog(e);
                break;
            case "MushMC":
                doMushMC(e);
                break;
            case "Block Drop":
                doBlockDrop(e);
                break;
        }

        if (e.packet instanceof SPacketChat) {
            final SPacketChat packet = (SPacketChat) e.packet;
            if (ChatFormatting.stripFormatting(packet.getChatComponent().getFormattedText()).equals("Height limit for building is 256 blocks"))
                e.setCancelled(true);
        }
    };

    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    private void doBlockDrop(final PacketEvent e) {
        if(blockDropSmart.getValue() && (!ModuleManager.getFlight().isEnabled() || !targets.isEmpty())) return;
        if (e.packet instanceof CPacketKeepAlive || e.packet instanceof CPacketConfirmTransaction) {
            e.setCancelled(true);
            packets.add(e.packet);
        }
        if (e.packet instanceof CPacketPlayer && timer.hasReached(5000, true)) {
            while (!packets.isEmpty())
                sendPacketUnlogged(packets.remove(0));
        }
    }

    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    private void doMushMC(final PacketEvent e) {
        if (e.packet instanceof SPacketPlayerPosLook) {
            final SPacketPlayerPosLook s08 = (SPacketPlayerPosLook) e.packet;
            if (mc.player.getDistance(s08.x, s08.y, s08.z) < 15) {
                e.setCancelled(true);
                if (mc.getConnection() != null && !mc.getConnection().doneLoadingTerrain) {
                    getPlayer().prevPosX = getPlayer().posX;
                    getPlayer().prevPosY = getPlayer().posY;
                    getPlayer().prevPosZ = getPlayer().posZ;
                    mc.getConnection().doneLoadingTerrain = true;
                    mc.displayGuiScreen(null);
                }
            }
        }
        if (e.packet instanceof CPacketPlayer) {
            final CPacketPlayer c03 = (CPacketPlayer) e.packet;
            if (mc.player.motionY == 0 && mc.player.ticksExisted % 3 == 0) {
                c03.y += Math.random() * 0.1;
                c03.onGround = true;
            }
            sendPacketUnlogged(c03);
            sendPacketUnlogged(new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 100, mc.player.posZ, true));
            e.setCancelled(true);
        }
    }

    @EventHandler
    public final Listener<BuildToHighEvent> buildToHighEvent = e -> {
        e.setCancelled(true);
    };

    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    public void doSentinel(PacketEvent e) {
        if (e != null) {
            if (!ServerUtil.isCubeCraft())
                return;
            if (sentinelRotationDisabler.getValue() && e.packet instanceof CPacketPlayer) {
                final CPacketPlayer c = (CPacketPlayer) e.packet;
                if (!mc.playerController.getIsHittingBlock()) {
                    c.yaw += 4.5E+6F;
                    c.pitch += 4.5E+6F;
                }
            }
            if (sentinelMovementDisabler.getValue()) {
                if (e.packet instanceof SPacketPlayerPosLook) {
                    final SPacketPlayerPosLook packetIn = (SPacketPlayerPosLook) e.packet;
                    e.setCancelled(true);
                    EntityPlayer entityplayer = getPlayer();
                    double d0 = packetIn.getX();
                    double d1 = packetIn.getY();
                    double d2 = packetIn.getZ();

                    if (packetIn.getFlags().contains(SPacketPlayerPosLook.EnumFlags.X)) d0 += entityplayer.posX;
                    else entityplayer.motionX = 0.0D;

                    if (packetIn.getFlags().contains(SPacketPlayerPosLook.EnumFlags.Y)) d1 += entityplayer.posY;
                    else entityplayer.motionY = 0.0D;

                    if (packetIn.getFlags().contains(SPacketPlayerPosLook.EnumFlags.Z)) d2 += entityplayer.posZ;
                    else entityplayer.motionZ = 0.0D;

                    entityplayer.setPosition(d0, d1, d2);
                    sendPacket(new CPacketConfirmTeleport(packetIn.getTeleportId()));
                    sendPacket(new CPacketPlayer.Position(entityplayer.posX, entityplayer.getEntityBoundingBox().minY, entityplayer.posZ, false));

                    if (mc.getConnection() != null && !mc.getConnection().doneLoadingTerrain) {
                        getPlayer().prevPosX = getPlayer().posX;
                        getPlayer().prevPosY = getPlayer().posY;
                        getPlayer().prevPosZ = getPlayer().posZ;
                        mc.getConnection().doneLoadingTerrain = true;
                        mc.displayGuiScreen(null);
                    }
                }
                if (e.packet instanceof CPacketKeepAlive) e.setCancelled(true);
                if (e.packet instanceof CPacketPlayer) {
                    if (ticks++ % 2 != 0)
                        return;
                    final IBlockState state = mc.world.getBlockState(new BlockPos(getX(), getY(), getZ()));
                    final Block block = state.getBlock();
                    if (!state.isFullBlock() && !(block instanceof BlockCarpet || block instanceof BlockSnow || block instanceof BlockDoor || block instanceof BlockTrapDoor))
                        sendPacketUnlogged(new CPacketPlayerTryUseItemOnBlock(new BlockPos(mc.player.posX, mc.player.posY + 0.5, mc.player.posZ), EnumFacing.UP, EnumHand.MAIN_HAND, 0.0F, (float) (Math.random() / 5f), 0.0F));
                }
            }
        } else {
            if (!ServerUtil.isCubeCraft())
                return;
            if (sentinelMovementDisabler.getValue()) {
                sendPacketUnlogged(new CPacketInput(0, 0, false, false));
                sendPacketUnlogged(new CPacketInput((float) getSpeed(), (float) getSpeed(), false, false));
                sendPacketUnlogged(new CPacketPlayerTryUseItemOnBlock(new BlockPos(getX(), getY() + 256, getZ()), EnumFacing.DOWN, EnumHand.MAIN_HAND, 0.5F, 0.94F, 0.5F));

                sendPacketUnlogged(new CPacketConfirmTransaction());
            }
        }
    }

    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    public void doWatchDog(UpdateEvent e) {
        if (watchDogTerrorismusDisabler.getValue()) {
            if (getPlayer().ticksExisted <= 1) {
                watchDogPackets.clear();
                receivedConfirmTP = -1;
                lastKey = -1;

                sendMessage("Reset Values!");
            }

            try {
                ByteArrayOutputStream b = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(b);
                out.writeUTF(getPlayer().getGameProfile().getName());
                PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
                buf.writeBytes(b.toByteArray());
                String value = "TheOnePiece";
                sendPacketUnlogged(new CPacketCustomPayload(value, buf));
            } catch (Exception ignored) {
            }

            final long l = ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE - 1);
            sendPacketUnlogged(new CPacketPlayerTryUseItemOnBlock(new BlockPos(mc.player.posX, mc.player.posY + 0.5, mc.player.posZ), EnumFacing.UP, EnumHand.OFF_HAND, 0.0F, (float) (Math.random() / 5f), 0.0F));
            sendPacketUnlogged(new CPacketSpectate(new UUID(l, l + 1)));
            sendPacketUnlogged(new CPacketConfirmTeleport(Integer.MIN_VALUE));
            sendPacketUnlogged(new CPacketConfirmTeleport(Integer.MAX_VALUE));
            sendPacketUnlogged(new CPacketSteerBoat(false, false));
        }

        if (watchDogTimerDisabler.getValue()) {
            if (getPlayer().ticksExisted == 105)
                getPlayer().setPosition(999, 999, 999);

            if (this.packets.isEmpty()) return;

            if (getPlayer().ticksExisted % 25 == 0) {
                sendPacketUnlogged(new CPacketPlayerTryUseItemOnBlock(new BlockPos(getX(), getY() + 256, getZ()), EnumFacing.DOWN, EnumHand.OFF_HAND, 0.5F, 0.94F, 0.5F));
                sendPacketUnlogged(new CPacketPlayerTryUseItemOnBlock(new BlockPos(mc.player.posX, mc.player.posY + 0.5, mc.player.posZ), EnumFacing.UP, EnumHand.OFF_HAND, 0.0F, (float) (Math.random() / 5f), 0.0F));
                sendPacketUnlogged(new CPacketKeepAlive());
                this.packets.forEach(this::sendPacketUnlogged);
                this.packets.clear();
                sendPacketUnlogged(new CPacketKeepAlive());
                sendPacketUnlogged(new CPacketPlayerTryUseItemOnBlock(new BlockPos(mc.player.posX, mc.player.posY + 0.5, mc.player.posZ), EnumFacing.UP, EnumHand.OFF_HAND, 0.0F, (float) (Math.random() / 5f), 0.0F));
                sendPacketUnlogged(new CPacketPlayerTryUseItemOnBlock(new BlockPos(getX(), getY() + 256, getZ()), EnumFacing.DOWN, EnumHand.OFF_HAND, 0.5F, 0.94F, 0.5F));
            }
        }
        if (watchDogTimerDisablerV2.getValue()) {
            if (getPlayer().ticksExisted == 50 || getPlayer().ticksExisted == 105)
                getPlayer().setPosition(0, 0, 0);
            try {
                ByteArrayOutputStream b = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(b);
                out.writeUTF(getPlayer().getGameProfile().getName());
                PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
                buf.writeBytes(b.toByteArray());
                String value = "Fritz";
                for (int i = 0; i < 3; i++)
                    sendPacketUnlogged(new CPacketCustomPayload(value, buf));
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            sendPacketUnlogged(new CPacketEnchantItem(Integer.MAX_VALUE, Integer.MIN_VALUE));

            sendPacketUnlogged(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.SUCCESSFULLY_LOADED));
            sendPacketUnlogged(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.ACCEPTED));
            sendPacketUnlogged(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.DECLINED));
            sendPacketUnlogged(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.FAILED_DOWNLOAD));

            final long l = ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE - 1);
            sendPacketUnlogged(new CPacketSpectate(new UUID(l, l + 1)));

            sendPacketUnlogged(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.ACCEPTED));
            sendPacketUnlogged(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.FAILED_DOWNLOAD));
            sendPacketUnlogged(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.SUCCESSFULLY_LOADED));
            sendPacketUnlogged(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.DECLINED));

            sendPacketUnlogged(new CPacketConfirmTeleport(Integer.MAX_VALUE));
        }
        if (watchDogExperimentalDisabler.getValue()) {
            try {
                ByteArrayOutputStream b = new ByteArrayOutputStream();
                b.write(getPlayer().getName().getBytes());

                DataOutputStream out = new DataOutputStream(b);
                out.writeUTF(getPlayer().getGameProfile().getName());
                out.writeUTF("باكستان");

                PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
                buf.writeBytes(b.toByteArray());

                //String value = "Fritz";
                String value = "باكستان";
                for (int i = 0; i < 3; i++) sendPacketUnlogged(new CPacketCustomPayload(value, buf));
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            int i = 0;

            for (EnumPlayerModelParts enumplayermodelparts : mc.gameSettings.getModelParts()) {
                i |= enumplayermodelparts.getPartMask();
            }

            sendPacketUnlogged(new CPacketClientSettings("", Integer.MAX_VALUE, mc.gameSettings.chatVisibility, mc.gameSettings.chatColours, i, mc.gameSettings.mainHand));

            final long l = ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE - 1);
            sendPacketUnlogged(new CPacketSpectate(new UUID(l, l + 1)));

            sendPacketUnlogged(new CPacketConfirmTeleport(Integer.MAX_VALUE));
        }
    }

    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    public void doWatchDog(PacketEvent e) {
        if (e.packet instanceof SPacketChat) {
            final SPacketChat chat = (SPacketChat) e.packet;
            if (chat.getChatComponent().getUnformattedText().contains("Resource Pack Declined! Make sure you have resource packs enabled in your multiplayer server settings!"))
                e.setCancelled(true);
        }

        if (watchDogPositionDisabler.getValue()) {
            if (e.packet instanceof SPacketPlayerPosLook) {
                final SPacketPlayerPosLook packet = (SPacketPlayerPosLook) e.packet;
                if (getPlayer().ticksExisted < 50) {
                    final BlockPos pos = getWorld().getSpawnPoint();
                    sendPacketUnlogged(new CPacketPlayer.Position(pos.getX(), pos.getY(), pos.getZ(), false));
                    e.setCancelled(true);
                }
            }
        }

        if (watchDogHackDisabler.getValue()) {
            if (e.packet instanceof SPacketPlayerPosLook && getPlayer().ticksExisted <= 150)
                ((SPacketPlayerPosLook) e.packet).y += 20.0D;

            if (e.packet instanceof CPacketPlayer) {
                if (getPlayer().ticksExisted > 25 && getPlayer().ticksExisted % 5 != 0) {
                    ((CPacketPlayer) e.packet).yaw = Float.MAX_VALUE;
                    ((CPacketPlayer) e.packet).pitch = Float.MAX_VALUE;
                }
            }
        }

        if (watchDogIsraelDisabler.getValue()) {
            if (e.packet instanceof CPacketConfirmTransaction && getPlayer().ticksExisted <= 100) {
                ((CPacketConfirmTransaction) e.packet).windowId = 0;
                ((CPacketConfirmTransaction) e.packet).uid = 0;
                ((CPacketConfirmTransaction) e.packet).accepted = true;
            }
            if (e.packet instanceof CPacketPlayer && getPlayer().ticksExisted <= 150) e.setCancelled(true);
            if (e.packet instanceof CPacketPlayer && getPlayer().ticksExisted <= 200) {
                packets.clear();
                ((CPacketPlayer) e.packet).x = -1.0D;
                ((CPacketPlayer) e.packet).y = -1.0D;
                ((CPacketPlayer) e.packet).z = -1.0D;
                ((CPacketPlayer) e.packet).yaw = Float.MAX_VALUE;
                ((CPacketPlayer) e.packet).pitch = Float.MAX_VALUE;
            }

            if (e.packet instanceof CPacketPlayer && !e.isCancelled() && !packets.contains(e.packet)) {
                if (mc.player.isHandActive()) {
                    e.setCancelled(true);
                    packets.add(e.packet);
                }
                if (packets.size() > 3 || !mc.player.isHandActive()) {
                    for (Packet<?> packet : packets) {
                        if (packet instanceof CPacketPlayer)
                            sendMessage(((CPacketPlayer) packet).yaw);
                        sendPacket(packet);
                    }
                    packets.clear();
                }
            }

            if (e.packet instanceof CPacketPlayer && !e.isCancelled()) {
                sendPacketUnlogged(new CPacketSeenAdvancements(CPacketSeenAdvancements.Action.CLOSED_SCREEN, new ResourceLocation("ابن العاهرة")));

                final long l = ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE - 1);
                sendPacketUnlogged(new CPacketSpectate(new UUID(l, l + 1)));
                sendPacketUnlogged(new CPacketSteerBoat(true, false));
                sendPacketUnlogged(new CPacketCreativeInventoryAction(Integer.MAX_VALUE, new ItemStack(getWorld().getBlockState(new BlockPos(getX(), getY() - 1.0D, getZ())).getBlock())));
                sendPacketUnlogged(new CPacketConfirmTeleport(getPlayer().ticksExisted % 2 == 0 ? Integer.MAX_VALUE : Integer.MIN_VALUE));

                final CPacketVehicleMove bim = new CPacketVehicleMove();
                {
                    bim.x = getX();
                    bim.y = getY();
                    bim.z = getZ();
                    bim.yaw = getYaw();
                    bim.pitch = Float.MIN_VALUE;
                }
                sendPacketUnlogged(bim);

                sendPacketUnlogged(new CPacketClientStatus(CPacketClientStatus.State.REQUEST_STATS));
            }
        }

        if (watchDogTerrorismusDisabler.getValue()) {

            if ((e.packet instanceof CPacketPlayer || e.packet instanceof CPacketConfirmTransaction || e.packet instanceof CPacketKeepAlive) && getPlayer().ticksExisted <= 100)
                e.setCancelled(true);

            if (e.packet instanceof CPacketPlayer && !e.isCancelled()) {
                this.watchDogPackets.add(e.packet);
                e.setCancelled(true);
            }

            if (e.packet instanceof CPacketConfirmTransaction && !e.isCancelled()) {
                this.watchDogPackets.forEach(this::sendPacketUnlogged);
                this.watchDogPackets.clear();

                sendMessage("Cleared Queue!");
            }

            if (e.packet instanceof CPacketConfirmTeleport) {
                if (this.receivedConfirmTP == -1) {
                    this.receivedConfirmTP = ((CPacketConfirmTeleport) e.packet).telportId;
                    sendMessage("Set Teleport ID to -> " + this.receivedConfirmTP + " : " + getPlayer().ticksExisted);
                }
                ((CPacketConfirmTeleport) e.packet).telportId = this.receivedConfirmTP;
                sendMessage("Send Confirm Teleport with Teleport ID -> " + this.receivedConfirmTP + " " + getPlayer().ticksExisted);
            }

            /*if (e.packet instanceof CPacketKeepAlive && !mc.isSingleplayer()) {
                if (getPlayer().ticksExisted % 25 == 0) {
                    final long key = ((CPacketKeepAlive) e.packet).key;

                    ((CPacketKeepAlive) e.packet).key = lastKey;
                    sendMessage("Set Key from | " + key + " -> " + lastKey);
                } else {
                    lastKey = ((CPacketKeepAlive) e.packet).key;
                    sendMessage("Set Last Key to -> " + lastKey);
                }
            }*/
        }

        if (watchDogTimerDisablerV2.getValue()) {
            if ((e.packet instanceof CPacketPlayer || e.packet instanceof CPacketConfirmTransaction || e.packet instanceof CPacketKeepAlive || e.packet instanceof CPacketConfirmTeleport) && getPlayer().ticksExisted <= 100)
                e.setCancelled(true);
            if (e.packet instanceof CPacketCustomPayload)
                e.setCancelled(true);

            if (e.packet instanceof CPacketPlayer.Rotation) {
                ((CPacketPlayer.Rotation) e.packet).x = Float.MAX_VALUE;
                ((CPacketPlayer.Rotation) e.packet).y = Float.MAX_VALUE;
                ((CPacketPlayer.Rotation) e.packet).z = Float.MAX_VALUE;
            } else if (e.packet instanceof CPacketPlayer.Position) {
                ((CPacketPlayer.Position) e.packet).yaw = Float.MAX_VALUE;
                ((CPacketPlayer.Position) e.packet).pitch = Float.MAX_VALUE;
            }

            /*if (e.packet instanceof CPacketKeepAlive) {
                ((CPacketKeepAlive) e.packet).key -= 1L;
                this.hypixelQueue.add(e.packet);
                e.setCancelled(true);
            }*/
        }

        if (watchDogExperimentalDisabler.getValue()) {
            if ((e.packet instanceof CPacketPlayer || e.packet instanceof CPacketConfirmTransaction || e.packet instanceof CPacketKeepAlive || e.packet instanceof CPacketConfirmTeleport) && getPlayer().ticksExisted <= 100)
                e.setCancelled(true);

            if (e.packet instanceof CPacketPlayer && !e.isCancelled() && getPlayer().ticksExisted <= 200)
                ((CPacketPlayer) e.packet).y -= 0.5D;

            if (e.packet instanceof SPacketPlayerPosLook && getPlayer().ticksExisted <= 200 && getPlayer().ticksExisted >= 100)
                ((SPacketPlayerPosLook) e.packet).y += 20.0D;

            if (e.packet instanceof CPacketPlayer.Rotation) {
                ((CPacketPlayer.Rotation) e.packet).x = Float.MAX_VALUE;
                ((CPacketPlayer.Rotation) e.packet).y = Float.MAX_VALUE;
                ((CPacketPlayer.Rotation) e.packet).z = Float.MAX_VALUE;
            } else if (e.packet instanceof CPacketPlayer.Position) {
                ((CPacketPlayer.Position) e.packet).yaw = Float.MAX_VALUE;
                ((CPacketPlayer.Position) e.packet).pitch = Float.MAX_VALUE;
            }

            if (e.packet instanceof CPacketPlayer) {
                final CPacketVehicleMove vehicleMove = new CPacketVehicleMove();
                vehicleMove.x = getX();
                vehicleMove.y = getY();
                vehicleMove.z = getZ();
                vehicleMove.yaw = Float.MAX_VALUE;
                vehicleMove.pitch = Float.MAX_VALUE;
                sendPacketUnlogged(vehicleMove);
            }
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();

        this.watchDogPackets.clear();
    }

    @Override
    public void onDisable() {
        super.onDisable();

        this.packets.clear();
    }
}
