// 
// Decompiled by Procyon v0.5.36
// 

package today.getbypass.utils;

import net.minecraft.util.Vec3;
import net.minecraft.client.gui.GuiButton;
import com.mojang.authlib.GameProfile;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.network.Packet;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.StringUtils;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.block.Block;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.world.World;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;
import java.lang.reflect.Field;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.client.Minecraft;

public class Invoker
{
    private static String entityLivingBaseLoc;
    
    static {
        Invoker.entityLivingBaseLoc = "net.minecraft.entity.EntityLivingBase";
    }
    
    public static void sendChatMessage(final String msg) {
        Minecraft.getMinecraft().thePlayer.sendChatMessage(msg);
    }
    
    public static void addChatMessage(final String str) {
        final Object chat = new ChatComponentText(str);
        if (str != null) {
            Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage((IChatComponent)chat);
        }
    }
    
    public static float getRotationYaw() {
        return Minecraft.getMinecraft().thePlayer.rotationYaw;
    }
    
    public static float getRotationPitch() {
        return Minecraft.getMinecraft().thePlayer.rotationPitch;
    }
    
    public static void setRotationYaw(final float yaw) {
        Minecraft.getMinecraft().thePlayer.rotationYaw = yaw;
    }
    
    public static void setRotationPitch(final float pitch) {
        Minecraft.getMinecraft().thePlayer.rotationPitch = pitch;
    }
    
    public static void setSprinting(final boolean sprinting) {
        Minecraft.getMinecraft().thePlayer.setSprinting(sprinting);
    }
    
    public static boolean isOnLadder() {
        return Minecraft.getMinecraft().thePlayer.isOnLadder();
    }
    
    public static float moveForward() {
        return Minecraft.getMinecraft().thePlayer.moveForward;
    }
    
    public static boolean isCollidedHorizontally() {
        return Minecraft.getMinecraft().thePlayer.isCollidedHorizontally;
    }
    
    public static void setMotionX(final double x) {
        Minecraft.getMinecraft().thePlayer.motionX = x;
    }
    
    public static void setMotionY(final double y) {
        Minecraft.getMinecraft().thePlayer.motionY = y;
    }
    
    public static void setMotionZ(final double z) {
        Minecraft.getMinecraft().thePlayer.motionZ = z;
    }
    
    public static double getMotionX() {
        return Minecraft.getMinecraft().thePlayer.motionX;
    }
    
    public static double getMotionY() {
        return Minecraft.getMinecraft().thePlayer.motionY;
    }
    
    public static double getMotionZ() {
        return Minecraft.getMinecraft().thePlayer.motionZ;
    }
    
    public static void setLandMovementFactor(final float newFactor) {
        try {
            final Class elb = Class.forName(Invoker.entityLivingBaseLoc);
            final Field landMovement = elb.getDeclaredField("landMovementFactor");
            landMovement.setAccessible(true);
            landMovement.set(Minecraft.getMinecraft().thePlayer, newFactor);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void setJumpMovementFactor(final float newFactor) {
        try {
            final Class elb = Class.forName(Invoker.entityLivingBaseLoc);
            final Field landMovement = elb.getDeclaredField("jumpMovementFactor");
            landMovement.setAccessible(true);
            landMovement.set(Minecraft.getMinecraft().thePlayer, newFactor);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static float getGammaSetting() {
        return Minecraft.getMinecraft().gameSettings.gammaSetting;
    }
    
    public static void setGammaSetting(final float newSetting) {
        Minecraft.getMinecraft().gameSettings.gammaSetting = newSetting;
    }
    
    public static int getJumpCode() {
        return Minecraft.getMinecraft().gameSettings.keyBindJump.getKeyCode();
    }
    
    public static int getForwardCode() {
        return Minecraft.getMinecraft().gameSettings.keyBindForward.getKeyCode();
    }
    
    public static void setJumpKeyPressed(final boolean pressed) {
        Minecraft.getMinecraft().gameSettings.keyBindJump.pressed = pressed;
    }
    
    public static void setForwardKeyPressed(final boolean pressed) {
        Minecraft.getMinecraft().gameSettings.keyBindForward.pressed = pressed;
    }
    
    public static void setUseItemKeyPressed(final boolean pressed) {
        Minecraft.getMinecraft().gameSettings.keyBindUseItem.pressed = pressed;
    }
    
    public int getSneakCode() {
        return Minecraft.getMinecraft().gameSettings.keyBindSneak.getKeyCode();
    }
    
    public synchronized void displayScreen(final GuiScreen screen) {
        Minecraft.getMinecraft().displayGuiScreen(screen);
    }
    
    public List getEntityList() {
        return Minecraft.getMinecraft().theWorld.loadedEntityList;
    }
    
    public static float getDistanceToEntity(final Entity from, final Entity to) {
        return from.getDistanceToEntity(to);
    }
    
    public boolean isEntityDead(final Entity e) {
        return e.isDead;
    }
    
    public boolean canEntityBeSeen(final Entity e) {
        return Minecraft.getMinecraft().thePlayer.canEntityBeSeen(e);
    }
    
    public static void attackEntity(final Entity e) {
        Minecraft.getMinecraft().playerController.attackEntity(Minecraft.getMinecraft().thePlayer, e);
    }
    
    public static void swingItem() {
        Minecraft.getMinecraft().thePlayer.swingItem();
    }
    
    public static float getEyeHeight() {
        return Minecraft.getMinecraft().thePlayer.getEyeHeight();
    }
    
    public static float getEyeHeight(final Entity e) {
        return e.getEyeHeight();
    }
    
    public double getPosX() {
        return Minecraft.getMinecraft().thePlayer.posX;
    }
    
    public double getPosY() {
        return Minecraft.getMinecraft().thePlayer.posY;
    }
    
    public double getPosZ() {
        return Minecraft.getMinecraft().thePlayer.posZ;
    }
    
    public double getPosX(final Entity e) {
        return e.posX;
    }
    
    public double getPosY(final Entity e) {
        return e.posY;
    }
    
    public double getPosZ(final Entity e) {
        return e.posZ;
    }
    
    public static void setInvSlot(final int slot) {
        Minecraft.getMinecraft().thePlayer.inventory.currentItem = slot;
    }
    
    public int getCurInvSlot() {
        return Minecraft.getMinecraft().thePlayer.inventory.currentItem;
    }
    
    public static ItemStack getCurrentItem() {
        return Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem();
    }
    
    public ItemStack getItemAtSlot(final int slot) {
        return Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(slot).getStack();
    }
    
    public static ItemStack getItemAtSlotHotbar(final int slot) {
        return Minecraft.getMinecraft().thePlayer.inventory.getStackInSlot(slot);
    }
    
    public int getIdFromItem(final Item item) {
        return Item.getIdFromItem(item);
    }
    
    public static void clickWindow(final int slot, final int mode, final int button, final EntityPlayer player) {
        Minecraft.getMinecraft().playerController.windowClick(player.inventoryContainer.windowId, slot, button, mode, player);
    }
    
    public static void clickWindow(final int id, final int slot, final int mode, final int button, final EntityPlayer player) {
        Minecraft.getMinecraft().playerController.windowClick(id, slot, button, mode, player);
    }
    
    public static void sendUseItem(final ItemStack itemStack, final EntityPlayer player) {
        Minecraft.getMinecraft().playerController.sendUseItem(player, Minecraft.getMinecraft().theWorld, itemStack);
    }
    
    public Item getItemById(final int id) {
        return Item.getItemById(id);
    }
    
    public static void dropItemStack(final int slot) {
        for (int i = 0; i < Minecraft.getMinecraft().thePlayer.inventory.getStackInSlot(slot).stackSize; ++i) {
            Minecraft.getMinecraft().thePlayer.dropOneItem(false);
        }
    }
    
    public int getPacketVelocityEntityId(final S12PacketEntityVelocity p) {
        return p.getEntityID();
    }
    
    public Entity getEntityById(final int id) {
        return Minecraft.getMinecraft().theWorld.getEntityByID(id);
    }
    
    public int getXMovePacketVel(final S12PacketEntityVelocity p) {
        return p.getMotionX();
    }
    
    public int getYMovePacketVel(final S12PacketEntityVelocity p) {
        return p.getMotionY();
    }
    
    public int getZMovePacketVel(final S12PacketEntityVelocity p) {
        return p.getMotionZ();
    }
    
    public static void rightClick() {
        Minecraft.getMinecraft().rightClickMouse();
    }
    
    public static void leftClick() {
        Minecraft.getMinecraft().clickMouse();
    }
    
    public static void setKeyBindAttackPressed(final boolean flag) {
        Minecraft.getMinecraft().gameSettings.keyBindAttack.pressed = flag;
    }
    
    public static MovingObjectPosition getObjectMouseOver() {
        return Minecraft.getMinecraft().objectMouseOver;
    }
    
    public static float getStrVsBlock(final ItemStack item, final Block block) {
        return item.getStrVsBlock(block);
    }
    
    public static void useItemRightClick(final ItemStack item) {
        item.useItemRightClick(Minecraft.getMinecraft().theWorld, Minecraft.getMinecraft().thePlayer);
    }
    
    public ItemStack[] getArmourInventory() {
        return Minecraft.getMinecraft().thePlayer.inventory.armorInventory;
    }
    
    public static void enableStandardItemLighting() {
        RenderHelper.enableStandardItemLighting();
    }
    
    public static void disableStandardItemLighting() {
        RenderHelper.disableStandardItemLighting();
    }
    
    public String stripControlCodes(final String s) {
        return StringUtils.stripControlCodes(s);
    }
    
    public String getSessionUsername() {
        return Minecraft.getMinecraft().getSession().getUsername();
    }
    
    public double getBoundboxMinY(final Entity e) {
        return e.boundingBox.minY;
    }
    
    public double getBoundboxMaxY(final Entity e) {
        return e.boundingBox.maxY;
    }
    
    public double getBoundboxMinX(final Entity e) {
        return e.boundingBox.minX;
    }
    
    public double getBoundboxMaxX(final Entity e) {
        return e.boundingBox.maxX;
    }
    
    public double getBoundboxMinZ(final Entity e) {
        return e.boundingBox.minZ;
    }
    
    public double getBoundboxMaxZ(final Entity e) {
        return e.boundingBox.maxZ;
    }
    
    public int getDisplayHeight() {
        Minecraft.getMinecraft();
        return Minecraft.getMinecraft().displayHeight;
    }
    
    public int getDisplayWidth() {
        Minecraft.getMinecraft();
        return Minecraft.getMinecraft().displayWidth;
    }
    
    public GuiScreen getCurrentScreen() {
        Minecraft.getMinecraft();
        return Minecraft.getMinecraft().currentScreen;
    }
    
    public int getScaledWidth(final ScaledResolution sr) {
        return sr.getScaledWidth();
    }
    
    public int getScaledHeight(final ScaledResolution sr) {
        return sr.getScaledHeight();
    }
    
    public ServerData getCurrentServerData() {
        Minecraft.getMinecraft();
        return Minecraft.getMinecraft().currentServerData;
    }
    
    public boolean isInCreativeMode() {
        return Minecraft.getMinecraft().playerController.isInCreativeMode();
    }
    
    public static void setStackDisplayName(final ItemStack is, final String s) {
        is.setStackDisplayName(s);
    }
    
    public String getItemDisplayName(final ItemStack is) {
        return is.getDisplayName();
    }
    
    public static void sendPacket(final Packet p) {
        Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(p);
    }
    
    public Enchantment[] getEnchantList() {
        return Enchantment.enchantmentsList;
    }
    
    public static void addEnchantment(final ItemStack is, final Enchantment e, final int level) {
        is.addEnchantment(e, 127);
    }
    
    public double getLastTickPosX(final Entity e) {
        return e.lastTickPosX;
    }
    
    public double getLastTickPosY(final Entity e) {
        return e.lastTickPosY;
    }
    
    public double getLastTickPosZ(final Entity e) {
        return e.lastTickPosZ;
    }
    
    public static float getEntityHeight(final Entity e) {
        return e.height;
    }
    
    public static void loadRenderers() {
        Minecraft.getMinecraft();
        if (Minecraft.getMinecraft().renderGlobal != null) {
            Minecraft.getMinecraft();
            Minecraft.getMinecraft().renderGlobal.loadRenderers();
        }
    }
    
    public static void setSmoothLighting(final int mode) {
        Minecraft.getMinecraft().gameSettings.ambientOcclusion = mode;
    }
    
    public int getSmoothLighting() {
        return (Minecraft.getMinecraft().gameSettings == null) ? 2 : Minecraft.getMinecraft().gameSettings.ambientOcclusion;
    }
    
    public int getIdFromBlock(final Block block) {
        return Block.getIdFromBlock(block);
    }
    
    public List getTileEntityList() {
        return Minecraft.getMinecraft().theWorld.loadedTileEntityList;
    }
    
    public boolean isBurning() {
        return Minecraft.getMinecraft().thePlayer.isBurning();
    }
    
    public static void setRightDelayTimer(final int delay) {
        Minecraft.getMinecraft();
        Minecraft.getMinecraft().rightClickDelayTimer = delay;
    }
    
    public int getItemInUseDuration() {
        return Minecraft.getMinecraft().thePlayer.getItemInUseDuration();
    }
    
    public boolean isOnGround() {
        return Minecraft.getMinecraft().thePlayer.onGround;
    }
    
    public boolean isInWater() {
        return Minecraft.getMinecraft().thePlayer.isInWater();
    }
    
    public static void setFallDistance(final float f) {
        Minecraft.getMinecraft().thePlayer.fallDistance = f;
    }
    
    public static void setOnGround(final boolean b) {
        Minecraft.getMinecraft().thePlayer.onGround = b;
    }
    
    public int getFoodLevel() {
        return Minecraft.getMinecraft().thePlayer.getFoodStats().getFoodLevel();
    }
    
    public static float getHealth() {
        return Minecraft.getMinecraft().thePlayer.getHealth();
    }
    
    public static void removeEntityFromWorld(final int id) {
        Minecraft.getMinecraft().theWorld.removeEntityFromWorld(id);
    }
    
    public static void addEntityToWorld(final Entity e, final int id) {
        Minecraft.getMinecraft().theWorld.addEntityToWorld(id, e);
    }
    
    public static void setTimerSpeed(final float speed) {
        Minecraft.getMinecraft();
        Minecraft.getMinecraft().timer.timerSpeed = speed;
    }
    
    public static void jump() {
        Minecraft.getMinecraft().thePlayer.jump();
    }
    
    public GameProfile getGameProfile(final EntityPlayer ep) {
        return ep.gameProfile;
    }
    
    public static void setGameProfile(final GameProfile profile, final EntityPlayer ep) {
        ep.gameProfile = profile;
    }
    
    public static void setPositionAndRotation(final Entity e, final double x, final double y, final double z, final float yaw, final float pitch) {
        e.setPositionAndRotation(x, y, z, yaw, pitch);
    }
    
    public static void copyInventory(final EntityPlayer from, final EntityPlayer to) {
        from.inventory.copyInventory(to.inventory);
    }
    
    public static void setNoClip(final boolean state) {
        Minecraft.getMinecraft().thePlayer.noClip = state;
    }
    
    public static void setSneakKeyPressed(final boolean pressed) {
        Minecraft.getMinecraft().gameSettings.keyBindSneak.pressed = pressed;
    }
    
    public boolean isSneaking() {
        return Minecraft.getMinecraft().thePlayer.isSneaking();
    }
    
    public static void setStepHeight(final float value) {
        Minecraft.getMinecraft().thePlayer.stepHeight = value;
    }
    
    public int getWidth() {
        return this.getDisplayWidth() / 2;
    }
    
    public int getHeight() {
        return this.getDisplayHeight() / 2;
    }
    
    public int getId(final GuiButton btn) {
        return btn.id;
    }
    
    public static void addButton(final GuiScreen screen, final GuiButton btn) {
        screen.buttonList.add(btn);
    }
    
    public static void clearButtons(final GuiScreen screen) {
        screen.buttonList.clear();
    }
    
    public MovingObjectPosition rayTraceBlocks(final Vec3 vecFromPool, final Vec3 vecFromPool2) {
        return Minecraft.getMinecraft().theWorld.rayTraceBlocks(vecFromPool, vecFromPool2);
    }
    
    public static float getFallDistance(final Entity e) {
        return e.fallDistance;
    }
    
    public boolean isInvisible(final Entity e) {
        return e.isInvisible();
    }
    
    public static Block getBlock() {
        return getBlock();
    }
}
