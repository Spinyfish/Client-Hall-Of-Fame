package crispy.features.hacks.impl.combat;

import crispy.Crispy;
import crispy.features.commands.impl.ClickPatternCommand;
import crispy.features.event.Event;
import crispy.features.event.impl.player.EventUpdate;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.HackInfo;
import crispy.util.pathfinder.PathFinder;
import crispy.util.pathfinder.PathPos;
import crispy.util.pathfinder.PathProcessor;
import crispy.util.rotation.LookUtils;
import crispy.util.rotation.Rotation;
import crispy.util.rotation.VecRotation;
import crispy.util.time.TimeHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.*;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.superblaubeere27.valuesystem.BooleanValue;
import net.superblaubeere27.valuesystem.ModeValue;
import net.superblaubeere27.valuesystem.NumberValue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Random;


@HackInfo(name = "PvpBot", category = Category.COMBAT)
public class PvpBot extends Hack {


    public static float sYaw, sPitch;
    private Rotation rotation = new Rotation(0,0);
    EntityLivingBase target;
    /*
     * I'm bored and I want to win mineplex survival games without touching a thing.
     */
    TimeHelper stopwatch = new TimeHelper();
    ModeValue clickPattern = new ModeValue("Click Pattern", "Normal", "Normal", "Custom");
    ModeValue pvpbot = new ModeValue("Server Mode", "EnhanceX", "EnhanceX", "Hollond");
    BooleanValue pathFind = new BooleanValue("Path Find", true);
    BooleanValue invisibles = new BooleanValue("Invisibles", false);
    BooleanValue players = new BooleanValue("Players", true);
    BooleanValue others = new BooleanValue("Others", false);
    BooleanValue teams = new BooleanValue("Teams", true);
    NumberValue<Integer> cps = new NumberValue<Integer>("CPS", 10, 1, 20);
    BooleanValue legit = new BooleanValue("Refill Legit", true);
    TimeHelper timer = new TimeHelper();
    boolean runAway;
    int ticksProcessing;
    int[] randoms = {0, 1, 0};
    boolean Stop;
    TimeHelper timer3 = new TimeHelper();
    TimeHelper timer2 = new TimeHelper();
    private boolean inventoryOpen;
    private EntityPathFinder pathFinder;
    private PathProcessor processor;
    private int clickDelayIndex;
    private final TimeHelper clickDelay = new TimeHelper();

    public static int randomNumber(float max, float min) {
        return Math.round(min + (float) Math.random() * ((max - min)));
    }

    @Override
    public void onEnable() {
        setForward(false);
        setRight(false);
        setLeft(false);
        clickDelay.reset();
        ticksProcessing = 0;
        if (Minecraft.theWorld != null) {
            pathFinder = new EntityPathFinder(mc.thePlayer);
        }
        super.onEnable();
    }

    @Override
    public void onDisable() {
        timer3.reset();
        runAway = false;
        timer.reset();
        stopwatch.reset();
        ticksProcessing = 0;
        pathFinder = null;
        super.onDisable();
    }

    @Override
    public void onEvent(Event e) {

        if (e instanceof EventUpdate) {
            ClickPatternCommand pattern = Crispy.INSTANCE.getCommandManager().getCommand(ClickPatternCommand.class);
            target = getClosest(9999999);
            if (target == null)
                return;
            if (target.isDead)
                return;

            if (mc.thePlayer.getHealth() < 8) {
                //BRO I NEED TO POT

                runAway = true;

            }
            if(pvpbot.getMode().equalsIgnoreCase("EnhanceX")) {
                if (mc.thePlayer.posY >= 68) {
                    runAway = true;
                    mc.thePlayer.rotationYaw = 90;
                    setBack(false);
                    setLeft(false);
                    setForward(true);
                    setRight(false);
                    Stop = true;
                } else {
                    Stop = false;
                    runAway = false;
                }
            } else if(pvpbot.getMode().equalsIgnoreCase("Hollond")) {
                if(mc.thePlayer.posY >= 104) {
                    runAway = true;
                    mc.thePlayer.rotationYaw = 0;
                    setBack(false);
                    setLeft(false);
                    setForward(true);
                    setRight(false);
                    Stop = true;
                } else {
                    Stop = false;
                    runAway = false;
                }
            }

            if (!runAway) {
                double y = 0;
                int minran = cps.getObject();
                int maxran = cps.getObject();

                int rand = randomNumber(minran, maxran);
                int cpsdel = 0 + rand <= 0 ? 1 : 0 + rand;
                float del = 1000 / (cpsdel);
                if (clickPattern.getObject() == 1 && !pattern.delays.isEmpty()) {
                    if (clickDelayIndex > pattern.delays.size() - 1) {
                        clickDelayIndex = 0;

                    }

                    del = pattern.delays.get(clickDelayIndex);
                    clickDelayIndex++;
                }
                if (timer2.hasReached((long) del)) {
                    timer2.reset();


                    if (mc.thePlayer.getDistanceToEntity(target) < 8.0) {
                        mc.thePlayer.swingItem();
                        if(mc.thePlayer.getDistanceToEntity(target) <= 3) {
                            mc.playerController.attackEntity(mc.thePlayer, target);
                        }
                    }

                }
            }


            if (target.getDistanceToEntity(mc.thePlayer) > 1.5 && runAway) {
                //POT NOW BRO
//
               // if (getPotionCount() != 0) {
               //     if (timer.hasReached(80)) {
               //         timer.reset();
//
//
               //         if (hotbarHasPots()) {
               //             mc.thePlayer.rotationPitch = 90;
//
               //             splashPot();
//
               //             mc.thePlayer.rotationPitch = 90;
//
               //         } else {
               //             Stop = true;
               //             getPotsFromInventory();
               //         }
               //     } else {
               //     }
               // } else {
               //     runAway = false;
               // }
            } else {
                Stop = false;
                mc.thePlayer.inventory.currentItem = 0;
                if (!runAway && !pathFind.getObject()) {
                    try {
                        if (target != null) {
                            AxisAlignedBB bb = target.getEntityBoundingBox();
                            //jitter fixed??

                            VecRotation rotation = LookUtils.searchCenter(bb, false, true, mc.thePlayer.getDistanceToEntity(target));
                            Rotation limit = LookUtils.limitAngleChange(this.rotation, rotation.getRotation(), randomNumber(20, 1));
                            float[] fixedRotation = LookUtils.fixedSensitivity(mc.gameSettings.mouseSensitivity, limit.getYaw(), limit.getPitch());
                            if (fixedRotation[1] > 90)
                                return;

                            this.rotation = new Rotation(fixedRotation[0], fixedRotation[1]);
                            mc.thePlayer.rotationYaw = this.rotation.getYaw();
                            mc.thePlayer.rotationPitch = this.rotation.getPitch();
                        }
                    } catch (Exception e2) {
                    }

                }
            }
            if (runAway && mc.thePlayer.getHealth() > 6) {
                runAway = false;
            }

            if (!Stop) {
                if (!runAway) {
                    if (!pathFind.getObject()) {
                        setForward(true);
                        float distance = Aura.randomNumber(10, -200);
                        if (distance > 0) {

                            int dist = Aura.randomNumber(2, 1);
                            if (dist == 1) {
                                setLeft(true);
                                setRight(false);
                            } else {

                                setRight(true);
                                setLeft(false);
                            }


                        }
                    } else {
                        // reset pathfinder
                        // reset pathfinder
                        if((processor == null || processor.isDone() || ticksProcessing >= 10)
                                && (pathFinder.isDone() || pathFinder.isFailed()))
                        {


                            pathFinder = new EntityPathFinder(target);
                            processor = null;
                            ticksProcessing = 0;
                        }

                        // find path
                        if(!pathFinder.isDone() && !pathFinder.isFailed())
                        {
                            PathProcessor.lockControls();
                            aim();
                            pathFinder.think();
                            pathFinder.formatPath();
                            processor = pathFinder.getProcessor();
                        }

                        // process path
                        if(!processor.isDone())
                        {
                            processor.process();
                            ticksProcessing++;
                        }
                    }


                }
            }
        }
    }

    private void smoothAim() {
        float xz = 0.05f;
        float y = 0;
        double pitchFactor = 3;
        double yawFactor = 10;

        float targetYaw = LookUtils.getYawChange(mc.thePlayer.rotationYaw, target.posX, target.posZ);

        if (targetYaw > 0 && targetYaw > yawFactor) {
            mc.thePlayer.rotationYaw += yawFactor;
        } else if (targetYaw < 0 && targetYaw < -yawFactor) {
            mc.thePlayer.rotationYaw -= yawFactor;
        } else {
            mc.thePlayer.rotationYaw += targetYaw;
        }

        float targetPitch = LookUtils.getPitchChange(mc.thePlayer.rotationPitch, target, target.posY);


    }

    private void aim() {
        double randomYaw = 0.05;
        double randomPitch = 0.05;
        Random random = new Random();
        double randomX = random.nextDouble() * 0.1;
        double randomZ = random.nextDouble() * 0.1;
        double randomY = random.nextDouble() * 0.1;
        float targetYaw = LookUtils.getYawChange(mc.thePlayer.rotationYaw, target.posX + randomX, target.posZ + randomZ);
        float yawFactor = targetYaw / 2;
        if (Math.abs(yawFactor) > 5) {

            yawFactor = yawFactor * yawFactor / (2f * yawFactor);
        } else {
            yawFactor = targetYaw;


        }
        float[] fixedYaw = LookUtils.fixedSensitivity(mc.gameSettings.mouseSensitivity, mc.thePlayer.rotationYaw + yawFactor, 0);
        mc.thePlayer.rotationYaw += yawFactor;
        float targetPitch = LookUtils.getPitchChange(mc.thePlayer.rotationPitch, target, target.posY + randomY);
        float pitchFactor = targetPitch / 5;
        float[] fixedPitch = LookUtils.fixedSensitivity(mc.gameSettings.mouseSensitivity, 0, mc.thePlayer.rotationPitch + pitchFactor);
        mc.thePlayer.rotationYaw = fixedYaw[0];
        if (fixedPitch[1] > 90F) {
            fixedPitch[1] = 90f;
        }
        mc.thePlayer.rotationPitch = fixedPitch[1];
    }

    public void openInvPacket() {

        if (!inventoryOpen)
            mc.thePlayer.sendQueue.addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));

        inventoryOpen = true;
    }

    public void closeInvPacket() {
        if (inventoryOpen)
            mc.thePlayer.sendQueue.addToSendQueue(new C0DPacketCloseWindow(mc.thePlayer.inventoryContainer.windowId));

        inventoryOpen = false;
    }

    private void getPotsFromInventory() {
        if (legit.getObject()) {
            openInvPacket();
        }
        int item = -1;
        boolean found = false;
        boolean splash = false;
        for (int i1 = 36; i1 >= 9; i1--) {
            ItemStack itemStack = minecraft.thePlayer.inventoryContainer.getSlot(i1).getStack();
            if (itemStack != null) {
                if (isItemHealthPotion(itemStack)) {
                    item = i1;
                    found = true;
                    splash = ItemPotion.isSplash(itemStack.getItemDamage());
                }
            }
        }
        if (found) {
            if (!splash)
                for (int i1 = 0; i1 < 45; i1++) {
                    ItemStack itemStack = minecraft.thePlayer.inventoryContainer.getSlot(i1).getStack();
                    if (itemStack != null)
                        if ((itemStack.getItem() == Items.glass_bottle) && (i1 >= 36) && (i1 <= 44)) {
                            minecraft.playerController.windowClick(0, i1, 0, 0, minecraft.thePlayer);
                            minecraft.playerController.windowClick(0, -999, 0, 0, minecraft.thePlayer);
                        }
                }
            minecraft.playerController.windowClick(0, item, 0, 1, minecraft.thePlayer);

        }
    }

    private void setForward(boolean b) {
        KeyBinding sneakBinding = mc.gameSettings.keyBindForward;
        try {
            Field field = sneakBinding.getClass().getDeclaredField("pressed");
            field.setAccessible(true);
            field.setBoolean(sneakBinding, b);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    private void setRight(boolean b) {
        KeyBinding sneakBinding = mc.gameSettings.keyBindRight;
        try {
            Field field = sneakBinding.getClass().getDeclaredField("pressed");
            field.setAccessible(true);
            field.setBoolean(sneakBinding, b);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    private void setLeft(boolean b) {
        KeyBinding sneakBinding = mc.gameSettings.keyBindLeft;
        try {
            Field field = sneakBinding.getClass().getDeclaredField("pressed");
            field.setAccessible(true);
            field.setBoolean(sneakBinding, b);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    private void openInventory(boolean b) {
        KeyBinding sneakBinding = mc.gameSettings.keyBindInventory;
        try {
            Field field = sneakBinding.getClass().getDeclaredField("pressed");
            field.setAccessible(true);
            field.setBoolean(sneakBinding, b);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    private void closeInventory(boolean b) {
        KeyBinding sneakBinding = mc.gameSettings.keyBindBack;
        try {
            Field field = sneakBinding.getClass().getDeclaredField("pressed");
            field.setAccessible(true);
            field.setBoolean(sneakBinding, b);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    private void setBack(boolean b) {
        KeyBinding sneakBinding = mc.gameSettings.keyBindBack;
        try {
            Field field = sneakBinding.getClass().getDeclaredField("pressed");
            field.setAccessible(true);
            field.setBoolean(sneakBinding, b);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    private void setRightClick(boolean b) {
        KeyBinding sneakBinding = mc.gameSettings.keyBindRight;
        try {
            Field field = sneakBinding.getClass().getDeclaredField("pressed");
            field.setAccessible(true);
            field.setBoolean(sneakBinding, b);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    private void splashPot() {

        int item = -1;
        boolean found = false;
        boolean splash = false;
        for (int i1 = 36; i1 < 45; i1++) {
            ItemStack itemStack = minecraft.thePlayer.inventoryContainer.getSlot(i1).getStack();
            if (itemStack != null)
                if (isItemHealthPotion(itemStack)) {
                    item = i1;
                    found = true;
                    splash = ItemPotion.isSplash(itemStack.getItemDamage());
                    break;
                }
        }
        if (found) {
            if (splash) {
                if (stopwatch.hasReached(250)) {
                    minecraft.thePlayer.inventory.currentItem = (item - 36);
                    minecraft.playerController.updateController();
                    minecraft.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1),
                            -1, minecraft.thePlayer.inventory.getCurrentItem(), 0.0F, 0.0F, 0.0F));

                    stopwatch.reset();
                    if (legit.getObject())
                        closeInvPacket();
                }
            } else if (minecraft.thePlayer.onGround) {
                if (legit.getObject())
                    mc.thePlayer.closeScreen();
                for (int i1 = 0; i1 < 45; i1++) {
                    ItemStack itemStack = minecraft.thePlayer.inventoryContainer.getSlot(i1).getStack();
                    if (itemStack != null)
                        if ((itemStack.getItem() == Items.glass_bottle) && (i1 >= 36) && (i1 <= 44)) {
                            minecraft.playerController.windowClick(0, i1, 0, 0, minecraft.thePlayer);
                            minecraft.playerController.windowClick(0, -999, 0, 0, minecraft.thePlayer);
                        }
                }

                minecraft.thePlayer.inventory.currentItem = (item - 36);
                minecraft.playerController.updateController();
                minecraft.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), -1,
                        minecraft.thePlayer.inventory.getCurrentItem(), 0.0F, 0.0F, 0.0F));
                minecraft.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(minecraft.thePlayer.inventory.currentItem));
                for (int index = 0; index < 32; index++)
                    minecraft.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(minecraft.thePlayer.onGround));
                minecraft.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(
                        C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(0, 0, 0), EnumFacing.DOWN));
                minecraft.thePlayer.stopUsingItem();
            }
        }
    }


    private boolean canAttack(EntityLivingBase player) {
        if (player instanceof EntityPlayer || player instanceof EntityAnimal || player instanceof EntityMob || player instanceof EntityVillager) {
            if (player instanceof EntityPlayer && !players.getObject())
                return false;
            if (player instanceof EntityAnimal && !others.getObject())
                return false;
            if (player instanceof EntityMob && !others.getObject())
                return false;
            if (player instanceof EntityVillager && !others.getObject())
                return false;


        }
        if(player.posY > 104)
            return false;
        if (player.isOnSameTeam(mc.thePlayer) && teams.getObject())
            return false;
        if (player.isInvisible() && !invisibles.getObject())
            return false;
        if(Crispy.INSTANCE.getFriendManager().isFriend(player.getName()))
            return false;
        if (AntiBot.getInvalid().contains(player) || player.isPlayerSleeping())
            return false;
        //if (Crispy.INSTANCE.isFriend(player.getCommandSenderName()))
        //    return false;


        return player != mc.thePlayer && mc.thePlayer.getDistanceToEntity(player) <= 999999;
    }

    public int getPotionCount() {
        int i = 0;
        for (int i1 = 9; i1 < 45; i1++) {
            ItemStack itemStack = minecraft.thePlayer.inventoryContainer.getSlot(i1).getStack();
            if (itemStack != null)
                if (isItemHealthPotion(itemStack))
                    i += itemStack.stackSize;
        }
        return i;
    }

    private EntityLivingBase getClosest(double range) {
        double dist = range;
        EntityLivingBase target = null;
        for (Object object : Minecraft.theWorld.loadedEntityList) {

            Entity entity = (Entity) object;
            if (entity instanceof EntityLivingBase) {
                EntityLivingBase player = (EntityLivingBase) entity;
                if (canAttack(player)) {
                    double currentDist = mc.thePlayer.getDistanceToEntity(player);
                    if (currentDist <= dist) {
                        dist = currentDist;
                        target = player;
                    }
                }
            }
        }
        return target;
    }

    private boolean isItemHealthPotion(ItemStack itemStack) {
        if (itemStack.getItem() instanceof ItemPotion) {
            ItemPotion potion = (ItemPotion) itemStack.getItem();
            if (potion.hasEffect(itemStack))
                for (Object o : potion.getEffects(itemStack)) {
                    PotionEffect effect = (PotionEffect) o;
                    if (effect.getEffectName().equals("potion.heal"))
                        return true;
                }
        }
        return false;
    }

    private boolean isItemSpeedPotion(ItemStack itemStack) {
        if (itemStack.getItem() instanceof ItemPotion) {
            ItemPotion potion = (ItemPotion) itemStack.getItem();
            if (potion.hasEffect(itemStack))
                for (Object o : potion.getEffects(itemStack)) {
                    PotionEffect effect = (PotionEffect) o;
                    if (effect.getEffectName().equals("potion.speed"))
                        return true;
                }
        }
        return false;
    }

    private boolean hotbarHasPots() {
        boolean found = false;
        for (int i1 = 36; i1 < 45; i1++) {
            ItemStack itemStack = minecraft.thePlayer.inventoryContainer.getSlot(i1).getStack();
            if (itemStack != null)
                if (isItemHealthPotion(itemStack))
                    found = true;
        }
        return found;
    }

    private class EntityPathFinder extends PathFinder {
        private final Entity entity;

        public EntityPathFinder(Entity entity) {
            super(new BlockPos(entity.getPosition()));
            this.entity = entity;
            setThinkTime(10);
        }

        @Override
        protected boolean checkDone() {
            return done = entity.getDistanceSqToEntity(mc.thePlayer) <= 3;
        }

        @Override
        public ArrayList<PathPos> formatPath() {
            if (!done)
                failed = true;

            return super.formatPath();
        }
    }


}
