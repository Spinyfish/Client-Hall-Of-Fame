/*
 * Decompiled with CFR 0_132.
 *
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package Reality.Realii.mods.modules.render;

import libraries.optifine.Config;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import org.lwjgl.opengl.GL11;
import Reality.Realii.event.EventHandler;
import Reality.Realii.event.events.rendering.EventRender3D;
import Reality.Realii.event.value.Numbers;
import Reality.Realii.event.value.Option;
import Reality.Realii.managers.FriendManager;
import Reality.Realii.mods.Module;
import Reality.Realii.mods.ModuleType;
import Reality.Realii.mods.modules.player.Teams;
import Reality.Realii.utils.render.RenderUtil;
import Reality.Realii.event.EventHandler;
import Reality.Realii.event.events.world.EventMove;
import Reality.Realii.event.events.world.EventPreUpdate;
import Reality.Realii.event.value.Numbers;
import Reality.Realii.event.value.Option;
import Reality.Realii.mods.Module;
import Reality.Realii.mods.ModuleType;
import Reality.Realii.utils.cheats.player.PlayerUtils;
import Reality.Realii.event.EventHandler;
import Reality.Realii.event.events.world.EventMove;
import Reality.Realii.event.events.world.EventPreUpdate;
import Reality.Realii.event.value.Mode;
import Reality.Realii.managers.ModuleManager;
import Reality.Realii.mods.Module;
import Reality.Realii.mods.ModuleType;
import Reality.Realii.utils.cheats.world.TimerUtil;
import Reality.Realii.utils.math.MathUtil;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;


public class OldNametags
        extends Module {
    private Option<Boolean> dura = new Option<Boolean>("Durability", "durability", true);
    private Numbers<Number> scale = new Numbers<Number>("Scale", "scale", 3.0, 1.0, 5.0, 0.1);
    private ArrayList<Entity> entities = new ArrayList();
    private Option<Boolean> players = new Option<Boolean>("Players", "players", true);
    private Option<Boolean> animals = new Option<Boolean>("Animals", "animals", true);
    private Option<Boolean> mobs = new Option<Boolean>("Mobs", "mobs", false);
    private Option<Boolean> invis = new Option<Boolean>("Invisibles", "invisibles", false);
    private int i = 0;

    public OldNametags() {
        super("OldNametags", ModuleType.Render);

        this.addValues(this.players, this.animals, this.invis, mobs, this.dura, this.invis, this.scale);
    }
    
    

    @EventHandler
    private void onRender(EventRender3D render) {
        ArrayList<EntityPlayer> validEnt = new ArrayList<EntityPlayer>();
        if (validEnt.size() > 100) {
            validEnt.clear();
        }
        for (EntityLivingBase player2 : this.mc.theWorld.playerEntities) {
            if (player2.isEntityAlive()) {
                if (player2.isInvisible() && !this.invis.getValue().booleanValue()) {
                    if (!validEnt.contains(player2)) continue;
                    validEnt.remove(player2);
                    continue;
                }
                if (player2 == this.mc.thePlayer) {
                    if (!validEnt.contains(player2)) continue;
                    validEnt.remove(player2);
                    continue;
                }
                if (validEnt.size() > 100) break;
                if (validEnt.contains(player2)) continue;
                validEnt.add((EntityPlayer) player2);
                continue;
            }
            if (!validEnt.contains(player2)) continue;
            validEnt.remove(player2);
        }
        validEnt.forEach(player -> {
            float x = (float) (player.lastTickPosX + (player.posX - player.lastTickPosX) * (double) render.getPartialTicks() - RenderManager.renderPosX);
            float y = (float) (player.lastTickPosY + (player.posY - player.lastTickPosY) * (double) render.getPartialTicks() - RenderManager.renderPosY);
            float z = (float) (player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double) render.getPartialTicks() - RenderManager.renderPosZ);
            this.renderNametag((EntityPlayer) player, x, y, z);
        });
    }

    private String getHealth(EntityPlayer player) {
        DecimalFormat numberFormat = new DecimalFormat("0.#");
        return numberFormat.format(player.getHealth() * 5.0f + player.getAbsorptionAmount() * 5.0f);
    }

    private void drawNames(EntityPlayer player) {
        float xP = 2.2f;
        float width = (float) this.getWidth(this.getPlayerName(player)) / 2.0f + xP;
        float w = width = (float) ((double) width + ((double) (this.getWidth(" " + this.getHealth(player)) / 2) + 2.5));
        float nw = -width - xP;
        float offset = this.getWidth(this.getPlayerName(player)) + 4;
        RenderUtil.drawRect(nw, -3.0f, width, 10.0f, new Color(10, 10, 10, 170).getRGB());
        offset =  (offset += (float) (this.getWidth(this.getHealth(player)) + this.getWidth(" %") - 1));
        this.drawString(this.getPlayerName(player), w - offset, 0.0f, 16777215);
        if (player.getHealth() == 10.0f) {
            int n = 16776960;
        }
        int color = player.getHealth() > 10.0f ? RenderUtil.blend(new Color(-16711936), new Color(-256), 1.0f / player.getHealth() / 2.0f * (player.getHealth() - 10.0f)).getRGB() : RenderUtil.blend(new Color(-256), new Color(-65536), 0.1f * player.getHealth()).getRGB();
        this.drawString(String.valueOf(this.getHealth(player)) + "%", w - (float) this.getWidth(String.valueOf(this.getHealth(player)) + " %"), 0.0f, color);

    }

    private void drawString(String text, float x, float y, int color) {
        this.mc.fontRendererObj.drawStringWithShadow(text, x, y, color);
    }

    private int getWidth(String text) {
        return this.mc.fontRendererObj.getStringWidth(text);
    }

    private void startDrawing(float x, float y, float z, EntityPlayer player) {
        float var10001 = this.mc.gameSettings.thirdPersonView == 2 ? -1.0f : 1.0f;
        double size = Config.zoomMode ? (double) (this.getSize(player) / 10.0f) * this.scale.getValue().floatValue() * 0.5 : (double) (this.getSize(player) / 10.0f) * this.scale.getValue().floatValue() * 1.5;
        GL11.glPushMatrix();
        RenderUtil.startDrawing();
        GL11.glTranslatef((float) x, (float) y, (float) z);
        GL11.glNormal3f((float) 0.0f, (float) 1.0f, (float) 0.0f);
        GL11.glRotatef((float) (-this.mc.getRenderManager().playerViewY), (float) 0.0f, (float) 1.0f, (float) 0.0f);
        GL11.glRotatef((float) this.mc.getRenderManager().playerViewX, (float) var10001, (float) 0.0f, (float) 0.0f);
        GL11.glScaled((double) (-0.01666666753590107 * size), (double) (-0.01666666753590107 * size), (double) (0.01666666753590107 * size));
    }

    private void stopDrawing() {
        RenderUtil.stopDrawing();
        GlStateManager.color(1.0f, 1.0f, 1.0f);
        GlStateManager.popMatrix();
    }

    private void renderNametag(EntityPlayer player, float x, float y, float z) {
        y = (float) ((double) y + (1.55 + (player.isSneaking() ? 0.5 : 0.7)));
        this.startDrawing(x, y, z, player);
        this.drawNames(player);
        GL11.glColor4d((double) 1.0, (double) 1.0, (double) 1.0, (double) 1.0);
        this.renderArmor(player);
        this.stopDrawing();
    }

    private void renderArmor(EntityPlayer player) {
        ItemStack armourStack;
        ItemStack[] renderStack = player.inventory.armorInventory;
        int length = renderStack.length;
        int xOffset = 0;
        ItemStack[] arritemStack = renderStack;
        int n = arritemStack.length;
        int n2 = 0;
        while (n2 < n) {
            ItemStack aRenderStack = arritemStack[n2];
            armourStack = aRenderStack;
            if (armourStack != null) {
                xOffset -= 8;
            }
            ++n2;
        }
        if (player.getHeldItem() != null) {
            xOffset -= 8;
            ItemStack stock = player.getHeldItem().copy();
            if (stock.hasEffect() && (stock.getItem() instanceof ItemTool || stock.getItem() instanceof ItemArmor)) {
                stock.stackSize = 1;
            }
            this.renderItemStack(stock, xOffset, -20);
            xOffset += 16;
        }
        renderStack = player.inventory.armorInventory;
        int index = 3;
        while (index >= 0) {
            armourStack = renderStack[index];
            if (armourStack != null) {
                this.renderItemStack(armourStack, xOffset, -20);
                xOffset += 16;
            }
            --index;
        }
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private String getPlayerName(EntityPlayer player) {
        String name = player.getDisplayName().getFormattedText();
        if (FriendManager.isFriend(player.getName())) {
            name = "\u00a7b" + FriendManager.getAlias(player.getName());
        }
        if (Teams.isOnSameTeam(player)) {
            name = "\247a[Teams]" + player.getDisplayName().getFormattedText();
        }
        return name;
    }

    private float getSize(EntityPlayer player) {
        return this.mc.thePlayer.getDistanceToEntity(player) / 4.0f <= 2.0f ? 2.0f : this.mc.thePlayer.getDistanceToEntity(player) / 4.0f;
    }

    private void renderItemStack(ItemStack stack, int x, int y) {
        GlStateManager.pushMatrix();
        GlStateManager.depthMask(true);
        GlStateManager.clear(256);
        RenderHelper.enableStandardItemLighting();
        this.mc.getRenderItem().zLevel = -150.0f;
        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        this.mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
        this.mc.getRenderItem().renderItemOverlays(this.mc.fontRendererObj, stack, x, y);
        this.mc.getRenderItem().zLevel = 0.0f;
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableCull();
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.disableLighting();
        double s = 0.5;
        GlStateManager.scale(s, s, s);
        GlStateManager.disableDepth();
        this.renderEnchantText(stack, x, y);
        GlStateManager.enableDepth();
        GlStateManager.scale(2.0f, 2.0f, 2.0f);
        GlStateManager.popMatrix();
    }

    private void renderEnchantText(ItemStack stack, int x, int y) {
        int unbreakingLevel2;
        int enchantmentY = y - 24;
        if (stack.getEnchantmentTagList() != null && stack.getEnchantmentTagList().tagCount() >= 6) {
            this.mc.fontRendererObj.drawStringWithShadow("god", x * 2, enchantmentY, 16711680);
            return;
        }
        if (stack.getItem() instanceof ItemArmor) {
            int protectionLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack);
            int projectileProtectionLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.projectileProtection.effectId, stack);
            int blastProtectionLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.blastProtection.effectId, stack);
            int fireProtectionLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.fireProtection.effectId, stack);
            int thornsLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, stack);
            int unbreakingLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack);
            int damage = stack.getMaxDamage() - stack.getItemDamage();
            if (this.dura.getValue().booleanValue()) {
                this.mc.fontRendererObj.drawStringWithShadow("" + damage, x * 2, y, 16777215);
            }
            if (protectionLevel > 0) {
                this.mc.fontRendererObj.drawStringWithShadow("prot" + protectionLevel, x * 2, enchantmentY, -1);
                enchantmentY += 8;
            }
            if (projectileProtectionLevel > 0) {
                this.mc.fontRendererObj.drawStringWithShadow("proj" + projectileProtectionLevel, x * 2, enchantmentY, -1);
                enchantmentY += 8;
            }
            if (blastProtectionLevel > 0) {
                this.mc.fontRendererObj.drawStringWithShadow("bp" + blastProtectionLevel, x * 2, enchantmentY, -1);
                enchantmentY += 8;
            }
            if (fireProtectionLevel > 0) {
                this.mc.fontRendererObj.drawStringWithShadow("frp" + fireProtectionLevel, x * 2, enchantmentY, -1);
                enchantmentY += 8;
            }
            if (thornsLevel > 0) {
                this.mc.fontRendererObj.drawStringWithShadow("th" + thornsLevel, x * 2, enchantmentY, -1);
                enchantmentY += 8;
            }
            if (unbreakingLevel > 0) {
                this.mc.fontRendererObj.drawStringWithShadow("unb" + unbreakingLevel, x * 2, enchantmentY, -1);
                enchantmentY += 8;
            }
        }
        if (stack.getItem() instanceof ItemBow) {
            int powerLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);
            int punchLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, stack);
            int flameLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, stack);
            unbreakingLevel2 = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack);
            if (powerLevel > 0) {
                this.mc.fontRendererObj.drawStringWithShadow("pow" + powerLevel, x * 2, enchantmentY, -1);
                enchantmentY += 8;
            }
            if (punchLevel > 0) {
                this.mc.fontRendererObj.drawStringWithShadow("pun" + punchLevel, x * 2, enchantmentY, -1);
                enchantmentY += 8;
            }
            if (flameLevel > 0) {
                this.mc.fontRendererObj.drawStringWithShadow("flame" + flameLevel, x * 2, enchantmentY, -1);
                enchantmentY += 8;
            }
            if (unbreakingLevel2 > 0) {
                this.mc.fontRendererObj.drawStringWithShadow("unb" + unbreakingLevel2, x * 2, enchantmentY, -1);
                enchantmentY += 8;
            }
        }
        if (stack.getItem() instanceof ItemSword) {
            int sharpnessLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack);
            int knockbackLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.knockback.effectId, stack);
            int fireAspectLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack);
            unbreakingLevel2 = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack);
            if (sharpnessLevel > 0) {
                this.mc.fontRendererObj.drawStringWithShadow("sh" + sharpnessLevel, x * 2, enchantmentY, -1);
                enchantmentY += 8;
            }
            if (knockbackLevel > 0) {
                this.mc.fontRendererObj.drawStringWithShadow("kb" + knockbackLevel, x * 2, enchantmentY, -1);
                enchantmentY += 8;
            }
            if (fireAspectLevel > 0) {
                this.mc.fontRendererObj.drawStringWithShadow("fire" + fireAspectLevel, x * 2, enchantmentY, -1);
                enchantmentY += 8;
            }
            if (unbreakingLevel2 > 0) {
                this.mc.fontRendererObj.drawStringWithShadow("unb" + unbreakingLevel2, x * 2, enchantmentY, -1);
            }
        }
        if (stack.getItem() instanceof ItemTool) {
            int unbreakingLevel22 = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack);
            int efficiencyLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, stack);
            int fortuneLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, stack);
            int silkTouch = EnchantmentHelper.getEnchantmentLevel(Enchantment.silkTouch.effectId, stack);
            if (efficiencyLevel > 0) {
                this.mc.fontRendererObj.drawStringWithShadow("eff" + efficiencyLevel, x * 2, enchantmentY, -1);
                enchantmentY += 8;
            }
            if (fortuneLevel > 0) {
                this.mc.fontRendererObj.drawStringWithShadow("fo" + fortuneLevel, x * 2, enchantmentY, -1);
                enchantmentY += 8;
            }
            if (silkTouch > 0) {
                this.mc.fontRendererObj.drawStringWithShadow("silk" + silkTouch, x * 2, enchantmentY, -1);
                enchantmentY += 8;
            }
            if (unbreakingLevel22 > 0) {
                this.mc.fontRendererObj.drawStringWithShadow("ub" + unbreakingLevel22, x * 2, enchantmentY, -1);
            }
        }
        if (stack.getItem() == Items.golden_apple && stack.hasEffect()) {
            this.mc.fontRendererObj.drawStringWithShadow("god", x * 2, enchantmentY, -1);
        }
    }

    static enum HealthMode {
        Hearts,
        Percentage;
    }

}

