package me.jinthium.straight.impl.utils.player;

import me.jinthium.straight.api.util.MinecraftInstance;
import me.jinthium.straight.impl.event.movement.CollisionEvent;
import net.minecraft.block.Block;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

import java.util.HashMap;
import java.util.List;

public class PlayerUtil implements MinecraftInstance {


    private static final HashMap<Integer, Integer> GOOD_POTIONS = new HashMap<Integer, Integer>() {{
        put(6, 1); // Instant Health
        put(10, 2); // Regeneration
        put(11, 3); // Resistance
        put(21, 4); // Health Boost
        put(22, 5); // Absorption
        put(23, 6); // Saturation
        put(5, 7); // Strength
        put(1, 8); // Speed
        put(12, 9); // Fire Resistance
        put(14, 10); // Invisibility
        put(3, 11); // Haste
        put(13, 12); // Water Breathing
    }};

    public static void updateItemForBlock(Block block) {
        float strength = 1.0F;
        int bestItem = -1;
        for (int i = 0; i < 9; i++) {
            ItemStack itemStack = mc.thePlayer.inventory.mainInventory[i];
            if (itemStack == null) {
                continue;
            }
            float strVsBlock = itemStack.getStrVsBlock(block);
            if (strVsBlock > strength) {
                strength = strVsBlock;
                bestItem = i;
            }
        }
        if (bestItem != -1) {
            mc.thePlayer.inventory.currentItem = bestItem;
        }
    }

    public static void setCollisionGround(CollisionEvent event) {
        if (event.getBlock() == Blocks.air) {
            event.setCollisionBox(new AxisAlignedBB(-100, -2, -100, 100, 1, 100)
                    .offset(event.getBlockPos().getX(), event.getBlockPos().getY(), event.getBlockPos().getZ()));
        }
    }


    public static boolean isBlockUnderNoCollisions() {
        for (int offset = 0; offset < mc.thePlayer.posY + mc.thePlayer.getEyeHeight(); offset += 2) {
            BlockPos blockPos = new BlockPos(mc.thePlayer.posX, offset, mc.thePlayer.posZ);

            if (mc.theWorld.getBlockState(blockPos).getBlock() != Blocks.air) {
                return true;
            }
        }
        return false;
    }

    public static void sendClick(final int button, final boolean state) {
        final int keyBind = button == 0 ? mc.gameSettings.keyBindAttack.getKeyCode() : mc.gameSettings.keyBindUseItem.getKeyCode();

        KeyBinding.setKeyBindState(keyBind, state);

        if (state)
            KeyBinding.onTick(keyBind);
    }

    public static boolean isTeammate(EntityPlayer entityPlayer) {
        if (entityPlayer != null) {
            String text = entityPlayer.getDisplayName().getFormattedText();
            String playerText = mc.thePlayer.getDisplayName().getFormattedText();
            if (text.length() < 2 || playerText.length() < 2) return false;
            if (!text.startsWith("\u00A7") || !playerText.startsWith("\u00A7")) return false;
            return text.charAt(1) == playerText.charAt(1);
        }
        return false;
    }

    public static boolean goodPotion(final int id) {
        return GOOD_POTIONS.containsKey(id);
    }

    public static boolean isBlockUnder(final double height) {
        return isBlockUnder(height, true);
    }

    public static boolean isBlockUnder(final double height, final boolean boundingBox) {
        if (boundingBox) {
            for (int offset = 0; offset < height; offset += 2) {
                final AxisAlignedBB bb = mc.thePlayer.getEntityBoundingBox().offset(0, -offset, 0);

                if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb).isEmpty()) {
                    return true;
                }
            }
        } else {
            for (int offset = 0; offset < height; offset++) {
                if (MovementUtil.blockRelativeToPlayer(0, -offset, 0).isFullBlock()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isBlockUnder() {
        return isBlockUnder(mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
    }

    public static double getEffectiveHealth(EntityLivingBase entity) {
        return entity.getHealth() * (entity.getMaxHealth() / entity.getTotalArmorValue());
    }

    public static boolean isHoldingSword() {
        return mc.thePlayer != null && mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
    }
}
