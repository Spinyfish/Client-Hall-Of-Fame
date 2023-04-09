package tech.dort.dortware.impl.modules.player;

import com.google.common.eventbus.Subscribe;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.api.property.SliderUnit;
import tech.dort.dortware.api.property.impl.BooleanValue;
import tech.dort.dortware.api.property.impl.EnumValue;
import tech.dort.dortware.api.property.impl.NumberValue;
import tech.dort.dortware.api.property.impl.interfaces.INameable;
import tech.dort.dortware.impl.events.PacketEvent;
import tech.dort.dortware.impl.events.UpdateEvent;
import tech.dort.dortware.impl.utils.networking.PacketUtil;
import tech.dort.dortware.impl.utils.pathfinding.Vec3d;
import tech.dort.dortware.impl.utils.time.Stopwatch;

import java.util.ArrayList;
import java.util.List;


public class Stealer extends Module {

    private final Stopwatch stealerTimer = new Stopwatch();
    private final Stopwatch nearbyTimer = new Stopwatch();
    private final List<BlockPos> emptiedChests = new ArrayList<>();

    private final EnumValue<Mode> enumValue = new EnumValue<>("Mode", this, Stealer.Mode.values());
    private final NumberValue delay = new NumberValue("Delay", this, 150, 25, 500, SliderUnit.MS, true);
    private final BooleanValue checkChest = new BooleanValue("Check Chest", this, true);
    private final BooleanValue autoClose = new BooleanValue("Auto Close", this, true);

    public Stealer(ModuleData moduleData) {
        super(moduleData);
        register(enumValue, delay, checkChest, autoClose);
    }

    private boolean nameValid(Container c) {
        ContainerChest container = (ContainerChest) c;
        String name = ChatFormatting.stripFormatting(container.getLowerChestInventory().getName());
        return name.contains("Chest");
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (event.isPre()) {
            if (mc.currentScreen instanceof GuiChest) {
                if (!isEmpty(mc.thePlayer.openContainer)) {
                    if (checkChest.getValue() && !nameValid(mc.thePlayer.openContainer)) {
                        return;
                    }
                    for (Slot slot : mc.thePlayer.openContainer.inventorySlots) {
                        if (slot != null && slot.getStack() != null && stealerTimer.timeElapsed(delay.getCastedValue().longValue())) {
                            mc.playerController.windowClick(mc.thePlayer.openContainer.windowId, slot.slotNumber, 0, 1, mc.thePlayer);
                            mc.playerController.updateController();
                            stealerTimer.resetTime();
                        }
                    }
                } else {
                    if (enumValue.getValue().equals(Mode.NEARBY) || (stealerTimer.timeElapsed(delay.getCastedValue().longValue()) && autoClose.getValue())) {
                        mc.thePlayer.closeScreen();
                        PacketUtil.sendPacketNoEvent(new C0DPacketCloseWindow());
                    }
                }
                return;
            }
            if (enumValue.getValue().equals(Mode.NEARBY)) {
                int maxDist = 3;
                if (mc.currentScreen == null) {
                    if (nearbyTimer.timeElapsed(delay.getCastedValue().longValue() + 100)) {
                        for (int y = maxDist; y >= -maxDist; y--) {
                            for (int x = -maxDist; x < maxDist; x++) {
                                for (int z = -maxDist; z < maxDist; z++) {
                                    int posX = ((int) Math.floor(mc.thePlayer.posX) + x);
                                    int posY = ((int) Math.floor(mc.thePlayer.posY) + y);
                                    int posZ = ((int) Math.floor(mc.thePlayer.posZ) + z);
                                    if (mc.thePlayer.getDistanceSq(mc.thePlayer.posX + x, mc.thePlayer.posY + y, mc.thePlayer.posZ + z) <= 26.0) {
                                        Block block = mc.theWorld.getBlockState(new BlockPos(posX, posY, posZ)).getBlock();
                                        if (block instanceof BlockChest) {
                                            BlockPos pos = new BlockPos(posX, posY, posZ);
                                            if (!emptiedChests.contains(pos)) {
                                                emptiedChests.add(pos);
                                                PacketUtil.sendPacket(new C0APacketAnimation());
                                                mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, null, new BlockPos(posX, posY, posZ), EnumFacing.DOWN, new Vec3d(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        nearbyTimer.resetTime();
                    }
                }
            }
        }
    }

    @Subscribe
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof S08PacketPlayerPosLook && mc.getNetHandler() != null && mc.getNetHandler().doneLoadingTerrain) {
            emptiedChests.clear();
        }
    }

    @Override
    public void onEnable() {
        emptiedChests.clear();
    }

    public boolean isEmpty(final Container container) {
        boolean isEmpty = true;
        int maxSlot = (container.inventorySlots.size() == 90) ? 54 : 27;
        for (int i = 0; i < maxSlot; ++i) {
            if (container.getSlot(i).getHasStack()) {
                isEmpty = false;
            }
        }
        return isEmpty;
    }

    @Override
    public String getSuffix() {
        return " \2477" + enumValue.getValue().getDisplayName();
    }

    public enum Mode implements INameable {
        NORMAL("Normal"), NEARBY("Nearby");
        private final String name;

        Mode(String name) {
            this.name = name;
        }

        @Override
        public String getDisplayName() {
            return name;
        }
    }
}
