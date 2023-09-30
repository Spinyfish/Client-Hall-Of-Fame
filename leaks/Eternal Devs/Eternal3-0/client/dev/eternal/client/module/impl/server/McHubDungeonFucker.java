package dev.eternal.client.module.impl.server;

import dev.eternal.client.event.Subscribe;;
import dev.eternal.client.Client;
import dev.eternal.client.event.events.EventPostRenderGui;
import dev.eternal.client.event.events.EventUpdate;
import dev.eternal.client.module.Module;
import dev.eternal.client.module.api.ModuleInfo;
import dev.eternal.client.ui.notification.Notification;
import dev.eternal.client.ui.notification.NotificationManager;
import dev.eternal.client.ui.notification.NotificationType;
import dev.eternal.client.util.math.MathUtil;
import dev.eternal.client.util.network.PacketUtil;
import dev.eternal.client.util.server.ServerUtil;
import lombok.SneakyThrows;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

@ModuleInfo(name = "mcdf", description = "McHubDungeonFucker - for more info ask Eternal.", category = Module.Category.SERVER)
public class McHubDungeonFucker extends Module {

  private int stage = 0;
  private double initialY;
  private boolean working, pathed;
  private final List<EntityArmorStand> armorStands = new ArrayList<>();
  private final Map<Long, BlockPos> chestMap = new HashMap<>();
  private final Map<EntityArmorStand, Long> chestMapFirst = new HashMap<>();
  private final Executor exec = Executors.newSingleThreadExecutor();

  @Override
  public void onEnable() {
    if (!ServerUtil.isOnServer("mchub.com")) {
      NotificationManager.pushNotification(new Notification("McHubDungeonFucker", "This module is made for the serevr mchub.com!", 5000, NotificationType.INFO));
      toggle();
      return;
    }
    initialY = mc.thePlayer.posY;
    pathed = working = false;
    stage = 0;
    armorStands.clear();
    chestMap.clear();
    chestMapFirst.clear();
    pathAndCollectInfo();
  }

  @Subscribe
  public void onUpdate(EventUpdate eventUpdate) {
    robgrave();
    if (stage == 1) {
      if (chestMap.isEmpty()) {
        NotificationManager.pushNotification(new Notification(
            "McHubDungeonFucker",
            "Process complete!",
            5000,
            NotificationType.SUCCESS
        ));
      }
      AtomicReference<Long> bp = new AtomicReference<>(null);
      chestMap.forEach((aLong, blockPos) -> {
        float f = (float) (aLong - System.currentTimeMillis()) / 60000F;
        if (f < 0.5) System.out.println(f + ": " + blockPos);
        if (System.currentTimeMillis() > aLong && !working) {
          working = true;
          Client.singleton().displayMessage("Teleporting to chest at " + blockPos.toString());
          bp.set(aLong);
          exec.execute(() -> {
            try {
              pathTo(blockPos.getX(), blockPos.getZ(), 50);

              Thread.sleep(500);

              mc.thePlayer.setPosition(blockPos.getX() + 0.5, Math.ceil(blockPos.getY()) + 1, blockPos.getZ() + 0.5);

              Thread.sleep(500);

              mc.thePlayer.setPosition(blockPos.getX() + 0.5, initialY, blockPos.getZ() + 0.5);
              working = false;
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          });
        }
      });
      if (bp.get() != null) {
        chestMap.remove(bp.get());
      }
    }
  }

  @Subscribe
  public void onRender2D(EventPostRenderGui eventPostRenderGui) {
    int itr = 0;
    List<Double> list = new ArrayList<>();
    if (pathed)
      for (long l : chestMap.keySet()) {
        float f = (float) (l - System.currentTimeMillis()) / 60000F;
        list.add(MathUtil.roundToPlace(f, 2));
        itr++;
      }
    list.sort(Comparator.comparingDouble(value -> value));

    itr = 0;
    for (double d : list) {
      fr.drawString("Time left: " + d, 2, 200 + (itr * 12), -1);
      itr++;
    }
  }

  private void robgrave() {
    List<BlockPos> list = new ArrayList<>();
    for (int x = -1; x < 1; ++x) {
      for (int y = 3; y > -3; --y) {
        for (int z = -1; z < 1; ++z) {
          int xPos = (int) this.mc.thePlayer.posX + x;
          int yPos = (int) this.mc.thePlayer.posY + y;
          int zPos = (int) this.mc.thePlayer.posZ + z;
          BlockPos blockPos = new BlockPos(xPos, yPos, zPos);
          list.add(blockPos);
//          breakBlock(blockPos);
        }
      }
    }
    if (list.stream().anyMatch(blockPos -> mc.theWorld.getBlockState(blockPos).getBlock() instanceof BlockChest)) {
      BlockPos blockPos = list.stream().filter(bPos -> mc.theWorld.getBlockState(bPos).getBlock() instanceof BlockChest).findFirst().orElse(null);
      if (blockPos == null) return;
      Block block = mc.theWorld.getBlockState(blockPos).getBlock();
      if (block != null) {
        mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), blockPos, EnumFacing.NORTH, new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ));
      }
    }
  }

  private void pathAndCollectInfo() {
    exec.execute(() -> {
      boolean completed = false;
      int x = 0;
      int z = 0;
      boolean direction = true;
      while (!completed) {
        pathTo(x * 10, z * 25, 1);
        try {
          Thread.sleep(150);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        mc.theWorld.getLoadedEntityList().stream()
            .filter(EntityArmorStand.class::isInstance)
            .map(entity -> (EntityArmorStand) entity)
            .forEach(armorStand -> {
              if (!armorStands.contains(armorStand)) {
                armorStands.add(armorStand);
                if (armorStand.getName().contains("Unlocks")) {


                  NotificationManager.pushNotification(new Notification(
                      "McHubDungeonFucker",
                      String.format("Chest located at %s.", armorStand.posX + ":" + armorStand.posZ),
                      5000,
                      NotificationType.INFO
                  ));
                  ;
                  if (armorStand.getName().contains("minute")) {
                    String[] args = armorStand.getName().split(" ");
                    System.out.println(Arrays.toString(args));
                    if (args.length > 4) {
                      double mins = Double.parseDouble(args[2].substring(2));
                      double secs = Double.parseDouble(args[4]);
                      long milisTime = (long) (((mins * 60L) * 1000L) + secs * 1000L);
                      chestMap.put(System.currentTimeMillis() + milisTime, new BlockPos(armorStand.posX, armorStand.posY, armorStand.posZ));
                    }
                  }
                } else if (armorStand.getName().contains("Unlocked") && !armorStand.getName().contains(" ")) {
                  chestMap.put(System.currentTimeMillis(), new BlockPos(armorStand.posX, armorStand.posY, armorStand.posZ));
                }
              }
            });

        if (direction) x++;
        else x--;
        if (x == 34 || x == 0) {
          z++;
          direction = !direction;
        }
        if (z == 340 / 25) {
          completed = true;
          stage = 1;
        }
      }
      pathed = true;
      NotificationManager.pushNotification(new Notification(
          "McHubDungeonFucker",
          "Found " + chestMap.size() + " chests",
          5000,
          NotificationType.INFO
      ));
    });
  }

  /**
   * @param toX  self-explanatory
   * @param toZ  self-explanatory
   * @param time the time it takes to change position
   * @author some retard
   */
  @SneakyThrows
  private void pathTo(int toX, int toZ, int time) {
    int currX = (int) mc.thePlayer.posX;
    int currZ = (int) mc.thePlayer.posZ;

    if (toX > currX) {
      while (currX < toX) {
        currX += 3;
        currX = MathHelper.clamp_int(currX, currX, toX);
        PacketUtil.sendSilent(new C03PacketPlayer.C04PacketPlayerPosition(
            currX,
            mc.thePlayer.posY,
            mc.thePlayer.posZ,
            true
        ));
        mc.thePlayer.setPosition(currX, mc.thePlayer.posY, currZ);
        Thread.sleep(time);
      }
    } else {
      while (currX > toX) {
        currX -= 3;
        currX = MathHelper.clamp_int(currX, toX, currX);
        PacketUtil.sendSilent(new C03PacketPlayer.C04PacketPlayerPosition(
            currX,
            mc.thePlayer.posY,
            mc.thePlayer.posZ,
            true
        ));
        mc.thePlayer.setPosition(currX, mc.thePlayer.posY, currZ);
        Thread.sleep(time);
      }
    }
    Thread.sleep(time * 10L);
    if (toZ > currZ) {
      while (currZ < toZ) {
        currZ += 3;
        currZ = MathHelper.clamp_int(currZ, currZ, toZ);
        PacketUtil.sendSilent(new C03PacketPlayer.C04PacketPlayerPosition(
            currX,
            initialY,
            currZ,
            true
        ));
        mc.thePlayer.setPosition(currX, mc.thePlayer.posY, currZ);
        Thread.sleep(time);
      }
    } else {
      while (currZ > toZ) {
        currZ -= 3;
        currZ = MathHelper.clamp_int(currZ, toZ, currZ);
        PacketUtil.sendSilent(new C03PacketPlayer.C04PacketPlayerPosition(
            currX,
            initialY,
            currZ,
            true
        ));
        mc.thePlayer.setPosition(currX, mc.thePlayer.posY, currZ);
        Thread.sleep(time);
      }
    }
    mc.thePlayer.setPosition(currX, mc.thePlayer.posY, currZ);
  }
}