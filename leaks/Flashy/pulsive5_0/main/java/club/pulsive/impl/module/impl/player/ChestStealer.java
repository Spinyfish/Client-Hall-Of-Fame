package club.pulsive.impl.module.impl.player;

import club.pulsive.api.event.eventBus.handler.EventHandler;
import club.pulsive.api.event.eventBus.handler.Listener;
import club.pulsive.impl.event.network.PacketEvent;
import club.pulsive.impl.event.player.PlayerMotionEvent;
import club.pulsive.impl.event.player.WindowClickEvent;
import club.pulsive.impl.module.Category;
import club.pulsive.impl.module.Module;
import club.pulsive.impl.module.ModuleInfo;
import club.pulsive.impl.property.Property;
import club.pulsive.impl.property.implementations.DoubleProperty;
import club.pulsive.impl.util.client.TimerUtil;
import club.pulsive.impl.util.math.apache.ApacheMath;
import club.pulsive.impl.util.player.InventoryUtils;
import club.pulsive.impl.util.player.PlayerUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import org.lwjgl.input.Mouse;

@ModuleInfo(name = "Stealer", renderName = "Stealer", aliases = "Stealer", description = "Automatically take items from chest.", category = Category.PLAYER)
public class ChestStealer extends Module {

    private final Property<Boolean> autoDelayProperty = new Property<>("Auto Delay", true);
    private final DoubleProperty delayProperty = new DoubleProperty("Delay", 100, 0, 500, 1, 
            () -> !this.autoDelayProperty.getValue());

    private int lastColumn, lastRow;
    private long lastClickTime, nextDelay = 100L;
    
    @EventHandler
    private final Listener<PacketEvent> onReceivePacket = event -> {
        
        if(event.getEventState() == PacketEvent.EventState.RECEIVING){
            if(event.getPacket() instanceof S2DPacketOpenWindow){
                S2DPacketOpenWindow openWindow = event.getPacket();
                if(openWindow.getGuiId().equals("minecraft:container")){
                    this.reset();
                }
            }
        }
    };

    private void reset() {
        this.lastClickTime = System.currentTimeMillis();
        this.nextDelay = 100L;
        this.lastColumn = 0;
        this.lastRow = 0;
    }

    @EventHandler
    private final Listener<WindowClickEvent> windowClickEventListener = event -> {
        this.lastClickTime = System.currentTimeMillis();
    };

    @EventHandler
    private final Listener<PlayerMotionEvent> playerMotionEventListener = event -> {
        if (event.isPre()) {
            final long timeSinceLastClick = System.currentTimeMillis() - this.lastClickTime;
            if (timeSinceLastClick < this.nextDelay) return;

            final GuiScreen current = mc.currentScreen;

            if (current instanceof GuiChest) {
                final GuiChest guiChest = (GuiChest) current;

                final IInventory lowerChestInventory = guiChest.lowerChestInventory;

                final String chestName = lowerChestInventory.getDisplayName().getUnformattedText();

                if (!chestName.equals(I18n.format("container.chest")) && !chestName.equals(I18n.format("container.chestDouble")))
                    return;
                if (!InventoryUtils.hasFreeSlots(mc.thePlayer)) {
                    // Close delay
                    if (timeSinceLastClick > 100L)
                        mc.thePlayer.closeScreen();
                    return;
                }

                final int nSlots = lowerChestInventory.getSizeInventory();
                for (int i = 0; i < nSlots; i++) {
                    final ItemStack stack = lowerChestInventory.getStackInSlot(i);

                    if (InventoryUtils.isValidStack(mc.thePlayer, stack)) {
                        final int column = i % 9;
                        final int row = i % (nSlots / 9);

                        final int columnDif = this.lastColumn - column;
                        final int rowDif = this.lastRow - row;

                        this.nextDelay = this.autoDelayProperty.getValue() ?
                                (long) ApacheMath.ceil(50.0 * ApacheMath.max(1.0, ApacheMath.sqrt(columnDif * columnDif + rowDif * rowDif))) :
                                this.delayProperty.getValue().longValue();

                        if (timeSinceLastClick < this.nextDelay) return;

                        InventoryUtils.windowClick(mc, mc.thePlayer.openContainer.windowId, i, 0, InventoryUtils.ClickType.SHIFT_CLICK);

                        this.lastColumn = column;
                        this.lastRow = row;
                        return;
                    }
                }

                if (timeSinceLastClick > 100L)
                    mc.thePlayer.closeScreen();
            }
        }
    };

    @Override
    public void onEnable() {
        this.reset();
        super.onEnable();
    }
}