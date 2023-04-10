package crispy.features.hacks.impl.player;

import crispy.features.event.Event;
import crispy.features.event.impl.player.EventUpdate;
import crispy.features.event.impl.render.EventRenderGui;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.HackInfo;
import crispy.util.time.TimeHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.*;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.superblaubeere27.valuesystem.BooleanValue;
import net.superblaubeere27.valuesystem.NumberValue;

import java.util.ArrayList;

@HackInfo(name = "ChestStealer", category = Category.PLAYER)
public class ChestStealer extends Hack {
    BooleanValue close = new BooleanValue("AutoClose", true);
    BooleanValue spoof = new BooleanValue("Spoof", false);
    NumberValue<Long> CleanerDelay = new NumberValue<Long>("Delay", 100L, 0L, 1000L);
    BooleanValue ignore = new BooleanValue("Ignore Names", false);
    BooleanValue aura = new BooleanValue("Aura", true);
    private final TimeHelper timer = new TimeHelper();
    private final int On = 0;
    private ContainerChest chest;
    private final ArrayList<BlockPos> chestPos = new ArrayList<>();

    @Override
    public void onEnable() {
        chestPos.clear();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        chestPos.clear();
        super.onDisable();
    }

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventRenderGui) {
            if ((this.mc.thePlayer.openContainer != null) && ((this.mc.thePlayer.openContainer instanceof ContainerChest))) {
                if(spoof.getObject()) {
                    mc.mouseHelper.grabMouseCursor();
                    mc.currentScreen = null;
                }
            }
        }
        if (e instanceof EventUpdate) {
            setDisplayName(getName() + " \2477" + CleanerDelay.getObject());
            if (aura.getObject()) {

                TileEntityChest chest = getClosestChest(4);
                if (chest == null)
                    return;
                if (!chest.getDisplayName().getUnformattedText().contains("Chest") && ignore.getObject())
                    return;

                if(!chestPos.contains(chest.getPos())) {
                    chest.openInventory(mc.thePlayer);
                    Vec3 vec3 = new Vec3(chest.getPos().getX(), chest.getPos().getY(), chest.getPos().getZ());
                    chestPos.add(chest.getPos());
                    mc.playerController.onPlayerRightClick(mc.thePlayer, Minecraft.theWorld, mc.thePlayer.getHeldItem(), chest.getPos(), EnumFacing.fromAngle(mc.thePlayer.rotationYaw), vec3);
                }
            }
            if ((this.mc.thePlayer.openContainer != null) && ((this.mc.thePlayer.openContainer instanceof ContainerChest))) {
                this.chest = (ContainerChest) this.mc.thePlayer.openContainer;
                if (!chest.getLowerChestInventory().getDisplayName().getUnformattedText().contains("Chest") && ignore.getObject())
                    return;

                int i2 = 0;
                for (int i = 0; i < chest.getLowerChestInventory().getSizeInventory(); i++) {
                    ItemStack stack = chest.getLowerChestInventory().getStackInSlot(i);
                    if ((this.timer.hasReached(CleanerDelay.getObject()))) {
                        i2++;
                        if (chest.getLowerChestInventory().getStackInSlot(i) != null) {
                            if (!isBad(stack)) {

                                this.mc.playerController.windowClick(chest.windowId, i, 0, 1, this.mc.thePlayer);
                                this.timer.reset();
                            }
                        }
                        if (i2 == chest.getLowerChestInventory().getSizeInventory() && close.getObject()) {
                            mc.thePlayer.closeScreen();
                        }
                        if (spoof.getObject() && !close.getObject()) {
                            mc.thePlayer.closeScreen();
                        }


                    }
                }
            }
        }
    }
    //private EntityLivingBase getClosest(double range) {
    //    double dist = range;
    //    EntityLivingBase target = null;
    //    for (Object object : Minecraft.theWorld.loadedEntityList) {
//
    //        Entity entity = (Entity) object;
    //        if (entity instanceof EntityLivingBase) {
    //            EntityLivingBase player = (EntityLivingBase) entity;
    //            if (canAttack(player)) {
    //                double currentDist = mc().thePlayer.getDistanceToEntity(player);
    //                if (currentDist <= dist) {
    //                    dist = currentDist;
    //                    target = player;
    //                }
    //            }
    //        }
    //    }
    //    return target;
    //}

    public TileEntityChest getClosestChest(double range) {
        double dist = range;
        TileEntityChest target = null;
        for (Object object : Minecraft.theWorld.loadedTileEntityList) {
            if (object instanceof TileEntityChest) {
                TileEntityChest entity = (TileEntityChest) object;
                double currentDist = mc.thePlayer.getDistance(entity.getPos().getX(), entity.getPos().getY(), entity.getPos().getZ());
                if (currentDist <= dist) {
                    dist = currentDist;
                    target = entity;
                }
            }
        }
        return target;
    }
    public TileEntityEnderChest getClosestEnderChest(double range) {
        double dist = range;
        TileEntityEnderChest target = null;
        for (Object object : Minecraft.theWorld.loadedTileEntityList) {
            if (object instanceof TileEntityChest) {
                TileEntityEnderChest entity = (TileEntityEnderChest) object;
                double currentDist = mc.thePlayer.getDistance(entity.getPos().getX(), entity.getPos().getY(), entity.getPos().getZ());
                if (currentDist <= dist) {
                    dist = currentDist;
                    target = entity;
                }
            }
        }
        return target;
    }

    private boolean isBad(ItemStack item) {
        ItemStack is = null;
        float lastDamage = -1;
        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is1 = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (is1.getItem() instanceof ItemSword && item.getItem() instanceof ItemSword) {
                    if (lastDamage < getDamage(is1)) {
                        lastDamage = getDamage(is1);
                        is = is1;
                    }
                }
            }
        }


        if (is != null && is.getItem() instanceof ItemSword && item.getItem() instanceof ItemSword) {
            float currentDamage = getDamage(is);
            float itemDamage = getDamage(item);
            if (itemDamage > currentDamage) {
                return false;
            }
        }


        return item != null &&
                ((item.getItem().getUnlocalizedName().contains("tnt")) ||
                        (item.getItem().getUnlocalizedName().contains("stick")) ||
                        (item.getItem().getUnlocalizedName().contains("egg") && !item.getItem().getUnlocalizedName().contains("leg")) ||
                        (item.getItem().getUnlocalizedName().contains("string")) ||
                        (item.getItem().getUnlocalizedName().contains("flint")) ||
                        (item.getItem().getUnlocalizedName().contains("compass")) ||
                        (item.getItem().getUnlocalizedName().contains("feather")) ||
                        (item.getItem().getUnlocalizedName().contains("bucket")) ||
                        (item.getItem().getUnlocalizedName().contains("snow")) ||
                        (item.getItem().getUnlocalizedName().contains("fish")) ||
                        (item.getItem().getUnlocalizedName().contains("enchant")) ||
                        (item.getItem().getUnlocalizedName().contains("exp")) ||
                        (item.getItem().getUnlocalizedName().contains("shears")) ||
                        (item.getItem().getUnlocalizedName().contains("anvil")) ||
                        (item.getItem().getUnlocalizedName().contains("torch")) ||
                        (item.getItem().getUnlocalizedName().contains("seeds")) ||
                        (item.getItem().getUnlocalizedName().contains("leather")) ||

                        ((item.getItem() instanceof ItemGlassBottle)) ||
                        ((item.getItem() instanceof ItemTool)) ||


                        (item.getItem().getUnlocalizedName().contains("piston")) ||
                        ((item.getItem().getUnlocalizedName().contains("potion"))
                                && (isBadPotion(item))));
    }

    private boolean isBadPotion(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemPotion) {
            final ItemPotion potion = (ItemPotion) stack.getItem();
            if (ItemPotion.isSplash(stack.getItemDamage())) {
                for (final Object o : potion.getEffects(stack)) {
                    final PotionEffect effect = (PotionEffect) o;
                    if (effect.getPotionID() == Potion.poison.getId() || effect.getPotionID() == Potion.harm.getId() || effect.getPotionID() == Potion.moveSlowdown.getId() || effect.getPotionID() == Potion.weakness.getId()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private float getDamage(ItemStack stack) {
        if (!(stack.getItem() instanceof ItemSword)) {
            return 0;
        }
        return EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack) * 1.25f + ((ItemSword) stack.getItem()).getDamageVsEntity();
    }
}
