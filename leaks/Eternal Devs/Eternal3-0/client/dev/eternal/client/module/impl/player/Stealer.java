package dev.eternal.client.module.impl.player;

import dev.eternal.client.event.Subscribe;;
import dev.eternal.client.event.events.EventPacket;
import dev.eternal.client.event.events.EventUpdate;
import dev.eternal.client.module.Module;
import dev.eternal.client.module.api.ModuleInfo;
import dev.eternal.client.property.impl.BooleanSetting;
import dev.eternal.client.property.impl.EnumSetting;
import dev.eternal.client.property.impl.NumberSetting;
import dev.eternal.client.property.impl.interfaces.INameable;
import dev.eternal.client.util.network.PacketUtil;
import dev.eternal.client.util.time.Stopwatch;
import lombok.Getter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

/**
 * please for the love of god replace this with something better, this is from dortware 1.5
 * - Dort
 */
@ModuleInfo(name = "Stealer", description = "Steals from chests", category = Module.Category.PLAYER, defaultKey = Keyboard.KEY_K)
public class Stealer extends Module {

  private final Stopwatch stealerTimer = new Stopwatch();
  private final Stopwatch nearbyTimer = new Stopwatch();
  private final List<BlockPos> emptiedChests = new ArrayList<>();

  private final EnumSetting<Mode> enumValue = new EnumSetting<>(this, "Mode", Stealer.Mode.values());
  private final NumberSetting delay = new NumberSetting(this, "Delay", 25, 25, 500, 25);
  private final BooleanSetting checkChest = new BooleanSetting(this, "Check Chest", true);
  private final BooleanSetting autoClose = new BooleanSetting(this, "Auto Close", true);

  private boolean isValidName(Container c) {
    ContainerChest container = (ContainerChest) c;
    String name = EnumChatFormatting.getTextWithoutFormattingCodes(container.getLowerChestInventory().getName());
    return name.contains(I18n.format("container.chest"));
  }

  @Subscribe
  public void onUpdate(EventUpdate event) {
    if (event.pre()) {
      if (mc.currentScreen instanceof GuiChest) {
        if (!isEmpty(mc.thePlayer.openContainer)) {
          if (checkChest.value() && !isValidName(mc.thePlayer.openContainer)) {
            return;
          }
          for (Slot slot : mc.thePlayer.openContainer.inventorySlots) {
            if (slot != null && slot.getStack() != null && stealerTimer.hasElapsed(delay.value().longValue() / 2)) {
              mc.playerController.windowClick(mc.thePlayer.openContainer.windowId, slot.slotNumber, 0, 1, mc.thePlayer);
              mc.playerController.updateController();
              stealerTimer.reset();
            }
          }
        } else {
          if (enumValue.value().equals(Mode.NEARBY) || (stealerTimer.hasElapsed(delay.value().longValue()) && autoClose.value())) {
            mc.thePlayer.closeScreen();
          }
        }
        return;
      }
      if (enumValue.value().equals(Mode.NEARBY)) {
        int maxDist = 3;
        if (mc.currentScreen == null) {
          if (nearbyTimer.hasElapsed(delay.value().longValue() + 100)) {
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
                        PacketUtil.sendSilent(new C0APacketAnimation());
                        mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, null, new BlockPos(posX, posY, posZ), EnumFacing.DOWN, new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ));
                      }
                    }
                  }
                }
              }
            }
            nearbyTimer.reset();
          }
        }
      }
    }
  }

  @Subscribe
  public void onPacket(EventPacket event) {
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
    return enumValue.value().getName();
  }

  @Getter
  public enum Mode implements INameable {
    NORMAL("Normal"), NEARBY("Nearby");
    private final String getName;

    Mode(String name) {
      this.getName = name;
    }
  }
}
