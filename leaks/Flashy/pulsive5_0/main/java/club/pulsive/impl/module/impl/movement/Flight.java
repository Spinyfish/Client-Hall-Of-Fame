package club.pulsive.impl.module.impl.movement;

import club.pulsive.api.event.eventBus.handler.EventHandler;
import club.pulsive.api.event.eventBus.handler.Listener;
import club.pulsive.api.main.Pulsive;
import club.pulsive.impl.event.network.PacketEvent;
import club.pulsive.impl.event.player.*;
import club.pulsive.impl.event.player.windowClick.WindowClickRequest;
import club.pulsive.impl.event.render.Render2DEvent;
import club.pulsive.impl.module.Category;
import club.pulsive.impl.module.Module;
import club.pulsive.impl.module.ModuleInfo;
import club.pulsive.impl.property.implementations.EnumProperty;
import club.pulsive.impl.util.client.Logger;
import club.pulsive.impl.util.client.TimerUtil;
import club.pulsive.impl.util.math.MathUtil;
import club.pulsive.impl.util.math.apache.ApacheMath;
import club.pulsive.impl.util.network.PacketUtil;
import club.pulsive.impl.util.player.*;
import club.pulsive.impl.util.player.InventoryUtils;
import lombok.AllArgsConstructor;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockSlime;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.potion.Potion;
import net.minecraft.util.*;
import org.apache.commons.lang3.RandomUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@ModuleInfo(name = "Flight", description = "Flight module", category = Category.MOVEMENT)
public class Flight extends Module {
    private final List<Packet> packets = new ArrayList<>();
    private final EnumProperty<Mode> modeEnumProperty = new EnumProperty<>("Mode", Mode.WATCHDOG);
    private final TimerUtil timerUtil = new TimerUtil();
    private double startPosY, distance, moveSpeed, y, lastX, lastY, lastZ;
    private int stage, bestBlockStack, ticks;
    public static boolean set, doFly;
    private WindowClickRequest lastRequest;
    private BlockPos startPos;
    private DecimalFormat format = new DecimalFormat("0.0");

    @EventHandler
    private final Listener<SpoofItemEvent> spoofItemEventListener = event ->{
        if (!doFly && bestBlockStack != -1 && bestBlockStack >= 36 && modeEnumProperty.getValue() == Mode.WATCHDOG)
            event.setCurrentItem(bestBlockStack - InventoryUtils.ONLY_HOT_BAR_BEGIN);
    };
    

    @EventHandler
    private final Listener<PlayerMoveEvent> movePlayerEventListener = event -> {
        switch(modeEnumProperty.getValue()){
            case YES:{
                if (MovementUtil.isMovingOnGround()) {
                    event.setY(mc.thePlayer.motionY = 0.42F);
                    Logger.printSysLog("e");
                    moveSpeed = MovementUtil.getBaseMoveSpeed();
                }
               // MovementUtil.setSpeed(event, moveSpeed = moveSpeed - 1.0e-9);
                break;
            }
            case KILL:{
                    MovementUtil.setSpeed(event, 0);
                    event.setY(mc.thePlayer.motionY = mc.thePlayer.ticksExisted % 4 == 0 ? 0.001 : -0.001);
                break;
            }
            case WATCHDOG:{
//                if (stage != 2) {
//                    MovementUtil.setSpeed(event, 0);
//                    return;
//                }

                if(!doFly){
                    MovementUtil.setSpeed(event, 0.00001);
                    return;
                }

                MovementUtil.setSpeed(event, MovementUtil.getBaseMoveSpeed());
                break;
            }
            case ZONECRAFT:{
                EntityPlayerSP playerInstance = mc.thePlayer;
                MovementInput movementInput = playerInstance.movementInput;
                if(!doFly){
                    if(mc.thePlayer.hurtTime == 0){
                        doFly = true;
                    }
                    MovementUtil.setSpeed(event, 0.000001);
                    return;
                }
                if(!Pulsive.INSTANCE.getModuleManager().getModule(TargetStrafe.class).shouldStrafe()) {
                    event.setY(playerInstance.motionY = movementInput.jump ? 0.42F : movementInput.sneak ? -0.42f : mc.thePlayer.onGround ? 1.1048F : 0);
                }else{
                    event.setY(mc.thePlayer.motionY = 0);
                }
                if (mc.thePlayer.onGround || mc.thePlayer.isSneaking() || mc.thePlayer.fallDistance > 0) {
                    MovementUtil.setSpeed(event, 0.2873f);
                    mc.thePlayer.fallDistance = 0;
                    bestBlockStack = 0;
                    return;
                }
                double base = 0.32;
                if (mc.thePlayer.isPotionActive(1)) {
                    base *= 1 + 0.2 * (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1);
                }
                if (bestBlockStack % 3 == 0) {
                    base += 0.42;
                } else if (bestBlockStack % 3 == 1) {
                    base += 0.84;
                }
                bestBlockStack++;
               // PacketUtil.sendPacketNoEvent(new C02PacketUseEntity(-1, C02PacketUseEntity.Action.ATTACK));
                MovementUtil.setSpeed(event, base);
                break;
            }
            case VANILLA:{
                EntityPlayerSP playerInstance = mc.thePlayer;
                MovementInput movementInput = playerInstance.movementInput;
                if(!Pulsive.INSTANCE.getModuleManager().getModule(TargetStrafe.class).shouldStrafe()) {
                    event.setY(playerInstance.motionY = movementInput.jump ? 0.41F : movementInput.sneak ? -0.42f : 0);
                }else{
                    event.setY(mc.thePlayer.motionY = mc.thePlayer.ticksExisted % 2 == 0 ? 0.0001 : -0.0001);
                }

                if(mc.thePlayer.ticksExisted % 2 == 0)
                    moveSpeed += 1e-7;

                MovementUtil.setSpeed(event, MovementUtil.getBaseMoveSpeed());
                break;
            }
        }
    };

    @EventHandler
    public final Listener<PacketEvent> packetInboundEventListener = event -> {
        if(modeEnumProperty.getValue() == Mode.VANILLA && event.getEventState() == PacketEvent.EventState.SENDING){
//            if(event.getPacket() instanceof C03PacketPlayer){
//                C03PacketPlayer c03 = event.getPacket();
//                double x = mc.thePlayer.posX;
//                double y = mc.thePlayer.posY;
//                double z = mc.thePlayer.posZ;
//                float yaw = mc.thePlayer.rotationYaw, pitch = mc.thePlayer.rotationPitch;
//
//                if(c03.isMoving()){
//                    x = c03.x;
//                    y = c03.y;
//                    z = c03.z;
//                }
//
//                if(c03.getRotating()){
//                    yaw = c03.getYaw();
//                    pitch = c03.getPitch();
//                }
//                event.setPacket(new C03PacketPlayer.C06PacketPlayerPosLook(x, y, z, yaw, pitch, c03.isOnGround()));
//            }
        }
        if(modeEnumProperty.getValue() == Mode.WATCHDOG) {
            switch (event.getEventState()) {
                case SENDING: {
//                    event.setCancelled(true);
//                    packets.add(event.getPacket());
                    break;
                }
                case RECEIVING: {
//                    if (event.getPacket() instanceof S08PacketPlayerPosLook && stage == -1) {
//                        final S08PacketPlayerPosLook packetPlayerPosLook = event.getPacket();
//                        y = packetPlayerPosLook.getY();
//                        Logger.print(y + "");
//                        PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(packetPlayerPosLook.getX(), packetPlayerPosLook.getY(), packetPlayerPosLook.getZ(), false));
//                        for (int i = 0; i < 30; i++) {
//                            PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(packetPlayerPosLook.getX(), packetPlayerPosLook.getY() - 0.0625, packetPlayerPosLook.getZ(), false));
//                            PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(packetPlayerPosLook.getX(), packetPlayerPosLook.getY(), packetPlayerPosLook.getZ(), false));
//                        }
//                        PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(packetPlayerPosLook.getX(), packetPlayerPosLook.getY(), packetPlayerPosLook.getZ(), true));
//                        //mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + (y - (mc.thePlayer.posY - (mc.thePlayer.posY / 0.015625))), mc.thePlayer.posZ);
//                        mc.thePlayer.motionY = y - mc.thePlayer.posY;
//                        stage = 2;
//                        timerUtil.reset();
//                        event.setCancelled(true);
//                    }
                    break;
                }
            }
        }
        if(modeEnumProperty.getValue() == Mode.ZONECRAFT){
            switch (event.getEventState()) {
                case SENDING:{
                    if (event.getPacket() instanceof C03PacketPlayer && doFly) {
                        stage = 1;
                        C03PacketPlayer packetPlayer = event.getPacket();
                        packetPlayer.setY(packetPlayer.getPositionY() - packetPlayer.getPositionY() % 0.015625);
                        packetPlayer.setOnGround(true);
                    }
                    break;
                }
            }
        }
    };

    @EventHandler
    private final Listener<Render2DEvent> render2DEventListener = event -> {
        //if(doFly) {
        mc.fontRendererObj.drawStringWithShadow("Distance flown: " + (ApacheMath.round(distance * 100.0) / 100.0), (float) (event.getScaledResolution().getScaledWidth() / 2.0 -
                        mc.fontRendererObj.getStringWidth("Distance flown: " + (ApacheMath.round(distance * 100.0) / 100.0)) / 2.0), (float) (event.getScaledResolution().getScaledHeight() / 2.0 + 48), -1);
    };

    @EventHandler
    private final Listener<PlayerMotionEvent> updatePlayerListener = event -> {
        ticks++;
        if(modeEnumProperty.getValue() == Mode.VULCAN){
            mc.thePlayer.motionY = ticks % 2 == 0 ? -0.1 : -0.16;
            mc.thePlayer.jumpMovementFactor = 0.0265f;
        }
        
        if(modeEnumProperty.getValue() == Mode.VANILLA){
//
           if(!event.isUpdate()) {
              // this.putItemInSlot(RandomUtils.nextInt(36, 39), 40);
           }
            if(event.isUpdate() || !MovementUtil.isMoving()) return;
            if(event.isPost()){
                if(MovementUtil.isMoving()) {
                    double x = (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * MathUtil.randomDouble(0.95 + (MovementUtil.getRandomHypixelValuesFloat() / 10), 0.98 + (MovementUtil.getRandomHypixelValuesFloat() / 10));
                    double z = (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * MathUtil.randomDouble(0.95 + (MovementUtil.getRandomHypixelValuesFloat() / 10), 0.98 + (MovementUtil.getRandomHypixelValuesFloat() / 10));

//                    mc.timer.timerSpeed = 0.5f;
//                    for (int i = 0; i < 2; i++) {
//                        PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX + x, mc.thePlayer.posY, mc.thePlayer.posZ + z, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, true));
//                        mc.thePlayer.setPosition(mc.thePlayer.posX + x, mc.thePlayer.posY, mc.thePlayer.posZ + z);
//                    }
                }
            }
        }
        
        if(modeEnumProperty.getValue() == Mode.KILL){
            if (ticks == 1) {
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 9, mc.thePlayer.posZ, false));
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
                mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 9, mc.thePlayer.posZ);
            } else if (MovementUtil.isMoving()) {
//                mc.timer.timerSpeed = 0.75f;
//                if(mc.thePlayer.ticksExisted % 4 == 0) {
//                    PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + MovementUtil.yawPos(5)[0], mc.thePlayer.posY - 0.99, mc.thePlayer.posZ + MovementUtil.yawPos(5)[1], false));
////                    mc.thePlayer.setPosition(mc.thePlayer.posX + MovementUtil.yawPos(5)[0], mc.thePlayer.posY - 0.99, mc.thePlayer.posZ + MovementUtil.yawPos(5)[1]);
//                    PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
//                }
                if (timerUtil.hasElapsed(500)) {
//                        PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + MovementUtil.yawPos(7.99 - MovementUtil.getRandomHypixelValuesFloat())[0], mc.thePlayer.posY - 0.22, mc.thePlayer.posZ + MovementUtil.yawPos(7.99 - MovementUtil.getRandomHypixelValuesFloat())[1], false));
//                        PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
                    mc.thePlayer.setPosition(-999, -999, -999);
                    timerUtil.reset();
                }
            }
        }
        if(modeEnumProperty.getValue() == Mode.WATCHDOG) {
            bestBlockStack = getBestBlockStack(InventoryUtils.ONLY_HOT_BAR_BEGIN, InventoryUtils.END);

            if (bestBlockStack >= 36) {
                if (event.isPre()) {
                    stage++;
                    if (!doFly)
                        event.setPitch(90.f);

                    if (stage == 1) mc.thePlayer.motionY = 0.42F;

                    if (stage == 5) {
                        mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.inventoryContainer.getSlot(bestBlockStack).getStack(),
                                new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 2, mc.thePlayer.posZ),
                                EnumFacing.UP,
                                new Vec3(mc.thePlayer.posX, mc.thePlayer.posY - 2, mc.thePlayer.posZ));
                    }

                    if (!doFly) {
                        if (BlockUtils.getBlock(mc.thePlayer.posX, mc.thePlayer.posY - .1, mc.thePlayer.posZ) instanceof BlockSlime) {
                            doFly = true;
                            PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                        } else {
                            if (stage > 5) stage = 1;
                        }
                    }

                    if (doFly && startPosY == 0)
                        startPosY = 1;

                    // if(stage == 7 || stage == 8) mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.42, mc.thePlayer.posZ);

                    if (doFly) {
                        event.setGround(true);
                        mc.thePlayer.motionY = 0;
                    }
                }

                if (event.isPost()) {
                    if (!mc.thePlayer.onGround && doFly) {
                        double x = (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * MathUtil.randomDouble(0.95 + (MovementUtil.getRandomHypixelValuesFloat() / 10), 0.98 + (MovementUtil.getRandomHypixelValuesFloat() / 10));
                        double z = (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * MathUtil.randomDouble(0.95 + (MovementUtil.getRandomHypixelValuesFloat() / 10), 0.98 + (MovementUtil.getRandomHypixelValuesFloat() / 10));
//
                       mc.timer.timerSpeed = 2f;
                    }
//                    if (stage >= 2 && !mc.thePlayer.onGround) {
////                    mc.timer.timerSpeed = ApacheMath.max(1.0f, mc.timer.timerSpeed - mc.timer.timerSpeed / 159);
////                    mc.timer.timerSpeed = 1.6f;
//                        mc.timer.timerSpeed = 0.6F;
//                        double x = (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * MathUtil.randomDouble(0.95 + (MovementUtil.getRandomHypixelValuesFloat() / 10), 0.98 + (MovementUtil.getRandomHypixelValuesFloat() / 10));
//                        double z = (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * MathUtil.randomDouble(0.95 + (MovementUtil.getRandomHypixelValuesFloat() / 10), 0.98 + (MovementUtil.getRandomHypixelValuesFloat() / 10));
//                        for (int i = 0; i < 2; i++) {
//                            PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + x, mc.thePlayer.posY, mc.thePlayer.posZ + z, true));
//                            mc.thePlayer.setPosition(mc.thePlayer.posX + x, mc.thePlayer.posY, mc.thePlayer.posZ + z);
//                        }
//                    }
                }
            }
        }
        if(modeEnumProperty.getValue() == Mode.ZONECRAFT){
            if(event.isPost()){
                if(!mc.thePlayer.onGround && doFly){
                    mc.timer.timerSpeed = 0.3f;
                }
            }
        }
        
        if(event.isPost()) distance += ApacheMath.hypot(mc.thePlayer.posX - mc.thePlayer.lastTickPosX, mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ);

        if(modeEnumProperty.getValue() == Mode.YES){
            if(!event.isUpdate()){
                if (!mc.thePlayer.movementInput.jump || mc.thePlayer.movementInput.sneak || mc.thePlayer.ticksExisted % 3 != 0) return;
                mc.thePlayer.motionY = 0.42f;
                event.setGround(true);
                startPosY = mc.thePlayer.posY;
            }
        }
    };

    @EventHandler
    private final Listener<PlayerCollideBoiEvent> playerCollideBoiEventListener = event -> {
        if (modeEnumProperty.getValue() == Mode.YES && !mc.thePlayer.isSneaking() && event.getBlock() instanceof BlockAir) {
            event.setAxisAlignedBB(new AxisAlignedBB(-12.0, -1.0, -12.0, 12.0, 0.0, 12.0).offset(event.getBlockPos().getX(), startPosY, event.getBlockPos().getZ()));
        }
    };

    private void putItemInSlot(final int slot, final int slotIn) {
        InventoryUtils.windowClick(this.mc, slotIn,
                slot - 36,
                InventoryUtils.ClickType.SWAP_WITH_HOT_BAR_SLOT);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        startPos = null;
        doFly = false;
        startPosY = mc.thePlayer.posY;
        lastX = mc.thePlayer.posX;
        ticks = 0;
        lastY = mc.thePlayer.posY;
        lastZ = mc.thePlayer.posZ;
        if(modeEnumProperty.getValue() == Mode.YES){
            double x2 = mc.thePlayer.posX;
            double y2 = mc.thePlayer.posY;
            double z2 = mc.thePlayer.posZ;
//            if (mc.thePlayer.onGround) {
//                PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(x2, y2 + 3.5, z2, false));
//                PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(x2, y2, z2, false));
//                PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(x2, y2, z2, true));
//            }
            mc.thePlayer.motionY = 0.42F;
            moveSpeed = 2;
        }
//        if(mc.thePlayer.onGround && modeEnumProperty.getValue() == Mode.Vulcum){
//            mc.gameSettings.keyBindAttack.pressed = true;
//            mc.timer.timerSpeed = 0.2f;
//
//            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 3.001, mc.thePlayer.posZ, false));
//            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
//            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
//            mc.thePlayer.motionY = -1F;
//            mc.gameSettings.keyBindAttack.pressed = false;
//        }
        y = 0;
        if (mc.thePlayer.onGround && modeEnumProperty.getValue() == Mode.ZONECRAFT) {
            PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, (int) mc.thePlayer.posY + 4, mc.thePlayer.posZ, true));
            PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, (int) mc.thePlayer.posY, mc.thePlayer.posZ, false));
            PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, (int) mc.thePlayer.posY, mc.thePlayer.posZ, true));
        }
        moveSpeed = 0;
        timerUtil.reset();
        distance = 0;
      //  moveBlocksIntoHotBar();
        set = false;
        packets.clear();
        stage = 0;
    }

    @AllArgsConstructor
    public enum Mode{
        WATCHDOG("Watchdog Slime"),
        ZONECRAFT("Zonecraft"),
        VANILLA("Vanilla"),
        VULCAN("Vulcan Glide"),
        KILL("Dont Use P2"),
        YES("Yes");

        private final String modeName;

        @Override
        public String toString() {return modeName;}
    }

    @Override
    public void onDisable() {
        super.onDisable();
        mc.thePlayer.speedInAir = 0.02F;
        mc.timer.timerSpeed = 1.0f;
       stage = 0;
       packets.clear();
//        if(mc.thePlayer != null) {
//            mc.thePlayer.motionX *= mc.thePlayer.motionZ *= 0;
//        }
        set = false;
    }

    private int getBestBlockStack(final int start, final int end) {
        int bestSlot = -1, bestSlotStackSize = 0;

        for (int i = start; i < end; i++) {
            final ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();

            if (stack != null &&
                    stack.stackSize > bestSlotStackSize &&
                    stack.getItem() instanceof ItemBlock && stack.getItem().getUnlocalizedName().contains("slime")) {

                bestSlot = i;
                bestSlotStackSize = stack.stackSize;
            }
        }

        return bestSlot;
    }

    private void moveBlocksIntoHotBar() {
        final int bestStackInInv = getBestBlockStack(InventoryUtils.EXCLUDE_ARMOR_BEGIN, InventoryUtils.ONLY_HOT_BAR_BEGIN);
        if (bestStackInInv == -1) return;

        boolean foundEmptySlot = false;

        for (int i = InventoryUtils.END - 1; i >= InventoryUtils.ONLY_HOT_BAR_BEGIN; i--) {
            final ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();

            if (stack == null) {
                if (lastRequest == null || lastRequest.isCompleted()) {
                    final int slotID = i;
                    InventoryUtils.queueClickRequest(lastRequest = new WindowClickRequest() {
                        @Override
                        public void performRequest() {
                            InventoryUtils.windowClick(mc, bestStackInInv,
                                    slotID - InventoryUtils.ONLY_HOT_BAR_BEGIN,
                                    InventoryUtils.ClickType.SWAP_WITH_HOT_BAR_SLOT);
                        }
                    });
                }

                foundEmptySlot = true;
            }
        }

        if (!foundEmptySlot) {
            if (lastRequest == null || lastRequest.isCompleted()) {
                InventoryUtils.queueClickRequest(lastRequest = new WindowClickRequest() {
                    @Override
                    public void performRequest() {
                        final int overrideSlot = 9;
                        // Swap with item in last slot of hot bar
                        InventoryUtils.windowClick(mc, bestStackInInv, overrideSlot,
                                InventoryUtils.ClickType.SWAP_WITH_HOT_BAR_SLOT);
                    }
                });
            }
        }
        //}
    }


}
