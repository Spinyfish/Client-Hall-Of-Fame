package dev.eternal.client.module.impl.render;

import com.google.common.collect.Lists;
import dev.eternal.client.event.EventPriority;
import dev.eternal.client.event.Subscribe;;
import dev.eternal.client.Client;
import dev.eternal.client.event.events.EventRender3D;
import dev.eternal.client.font.FontManager;
import dev.eternal.client.font.FontType;
import dev.eternal.client.font.renderer.TrueTypeFontRenderer;
import dev.eternal.client.module.Module;
import dev.eternal.client.module.api.ModuleInfo;
import dev.eternal.client.property.impl.BooleanSetting;
import dev.eternal.client.util.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Vector3d;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector4f;

import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.lwjgl.opengl.GL11.*;

@ModuleInfo(name = "NameTags", description = "Renders name tags through walls, and whilst sneaking", category = Module.Category.RENDER)
public class NameTags extends Module {

  private final FloatBuffer screen_coords = GLAllocation.createDirectFloatBuffer(4);
  private final IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);
  private final FloatBuffer modelview = GLAllocation.createDirectFloatBuffer(16);
  private final FloatBuffer projection = GLAllocation.createDirectFloatBuffer(16);
  private final TrueTypeFontRenderer fr = FontManager.getFontRenderer(FontType.ICIEL, 20);

  private final BooleanSetting unformatted = new BooleanSetting(this, "Unformatted", true),
      players = new BooleanSetting(this, "Players", true),
      showHealth = new BooleanSetting(this, "Show Health", true),
      self = new BooleanSetting(this, "Self", false),
      animals = new BooleanSetting(this, "Animals", false),
      mobs = new BooleanSetting(this, "Mobs", false),
      neutral = new BooleanSetting(this, "Neutrals", false),
      invisibleEntities = new BooleanSetting(this, "Invisible Entities", true);

  private final DecimalFormat decimalFormat = new DecimalFormat("#.#");

  @Subscribe(priority = EventPriority.LAST)
  public void handleRender3D(EventRender3D er) {
    ScaledResolution scaledResolution = new ScaledResolution(mc);
    mc.theWorld.loadedEntityList.stream()
        .filter(this::isValidEntity)
        .sorted(Comparator.comparingDouble(mc.thePlayer::getDistanceToEntity).reversed())
        .forEach(ent -> {
          float x = (float) ((float) (ent.lastTickPosX + (ent.posX - ent.lastTickPosX) * er.partialTicks())
              - RenderManager.renderPosX);
          float y = (float) ((float) (ent.lastTickPosY + (ent.posY - ent.lastTickPosY) * er.partialTicks())
              - RenderManager.renderPosY);
          float z = (float) ((float) (ent.lastTickPosZ + (ent.posZ - ent.lastTickPosZ) * er.partialTicks())
              - RenderManager.renderPosZ);
          double width = ent.width / 1.25;
          double height = ent.height;

          mc.entityRenderer.setupCameraTransform(mc.timer.renderPartialTicks, 0);

          AxisAlignedBB aabb = new AxisAlignedBB(x - width, y, z - width, x + width, y + height,
              z + width);

          Vector3d[] vectors = new Vector3d[]{new Vector3d(aabb.minX, aabb.minY, aabb.minZ),
              new Vector3d(aabb.minX, aabb.maxY, aabb.minZ),
              new Vector3d(aabb.maxX, aabb.minY, aabb.minZ),
              new Vector3d(aabb.maxX, aabb.maxY, aabb.minZ),
              new Vector3d(aabb.minX, aabb.minY, aabb.maxZ),
              new Vector3d(aabb.minX, aabb.maxY, aabb.maxZ),
              new Vector3d(aabb.maxX, aabb.minY, aabb.maxZ),
              new Vector3d(aabb.maxX, aabb.maxY, aabb.maxZ)};

          Vector4f position = null;

          for (Vector3d vector : vectors) {
            vector = gluProject2D(vector.x, vector.y, vector.z, scaledResolution.getScaleFactor());

            if (vector != null && vector.z >= 0.0D && vector.z < 1.0D) {
              if (position == null)
                position = new Vector4f((float) vector.x, (float) vector.y, (float) vector.z, 0.0F);

              position.x = (float) Math.min(vector.x, position.x);
              position.y = (float) Math.min(vector.y, position.y);
              position.z = (float) Math.max(vector.x, position.z);
              position.w = (float) Math.max(vector.y, position.w);

            }
          }

          if (position != null) {
            mc.entityRenderer.setupOverlayRendering();
            double posX = position.x;
            double endPosX = position.z;
            double endPosY = position.w;
            double posY = position.y - Math.abs(position.y - endPosY) / 3;
            double middle = (posX + endPosX) / 2d;

            glPushMatrix();
            renderNametags(ent, (float) middle, (float) posY);
            glPopMatrix();
          }
        });
    glEnable(GL_DEPTH_TEST);
  }

  private void renderNametags(Entity entity, float middle, float yPos) {
    var entityLivingBase = (EntityLivingBase) entity;
    var entityName = unformatted.value() ? entity.getName() : entity.getDisplayName().getUnformattedText();

    var health = entityLivingBase.getHealth() / 2;
    var maxHealth = (entityLivingBase).getMaxHealth() / 2;
    var percentage = 100 * (health / maxHealth);
    var healthColor = getHealthColor(percentage);
    var healthDisplay = decimalFormat.format(health * 2);
    entityName = showHealth.value() ? String.format(String.format("%s %s- %s%s", "%s", EnumChatFormatting.GRAY, "%s", "%s"), entityName, healthColor, healthDisplay) : entityName;

    var distance = mc.thePlayer.getDistanceToEntity(entity);
    var heightOffset = yPos;
    if (entity.isSneaking()) {
      heightOffset += 4;
    }

    heightOffset -= distance / 5;
    if (heightOffset < -8) {
      heightOffset = -8;
    }
    var width = (fr.getWidth(entityName) / 2) + 2;

    if (entity instanceof EntityPlayer player) {
      List<ItemStack> items = new ArrayList<>(Stream.of(player.inventory.armorInventory).filter(Objects::nonNull).toList());
      if (player.getHeldItem() != null) items.add(player.getHeldItem());
      items = Lists.reverse(items);
      int i = 0;
      for (ItemStack itemStack : items) {
        if (itemStack == null) continue;

        final float xPos = middle - (items.size() * 12) + (i * 12) + 5;
        final float offset = (12 * i);

        RenderUtil.drawRoundedRect(xPos + offset - 3,
            heightOffset + 12 - 32 - 3,
            xPos + offset + 16 + 1,
            heightOffset + 12 - 32 + 16 + 1,
            4,
            client.scheme().getPrimary());

        RenderUtil.drawRoundedRect(xPos + offset - 2,
            heightOffset + 12 - 32 - 2,
            xPos + offset + 16,
            heightOffset + 12 - 32 + 16,
            4,
            client.scheme().getBackground());

        Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(itemStack, xPos + offset - 1, heightOffset + 12 - 32 - 1);
        i++;
      }
    }

    RenderUtil.drawRoundedRect(middle - width - 1,
        heightOffset - 1,
        middle + width + 1,
        heightOffset + 13,
        4,
        client.scheme().getPrimary());

    RenderUtil.drawRoundedRect(middle - width,
        heightOffset,
        middle + width,
        heightOffset + 12,
        4,
        client.scheme().getBackground());
    fr.drawStringWithShadow(entityName, middle - width + 1, heightOffset + 2, -1);
  }

  /**
   * @param percentage The health percentage
   * @return A single-character {@link String} with a color code prefixed with unicode character U+00A7
   */
  private String getHealthColor(float percentage) {
    percentage -= percentage % 25;
    return "\247%s".formatted(switch ((int) percentage) {
      case 100, 75 -> "2";
      case 50 -> "e";
      case 25 -> "6";
      default -> "4";
    });
  }

  /**
   * Gets a list of enchantments and returns a {@link Set} with their simplified names
   * with the level appended at the end.
   *
   * @param itemStack The {@link ItemStack} to check for enchantments
   * @return A {@link Set} of simple 2-letter enchantment names with their levels appended to the end.
   */
  private Set<String> getItemDisplayNameSet(ItemStack itemStack) {
    if (itemStack.getEnchantmentTagList() != null) {
      Set<String> itemDisplayNameSet = new HashSet<>();
      var tagList = itemStack.getEnchantmentTagList();
      for (int i = 0; i < tagList.tagCount(); i++) {
        var nbtTagCompound = tagList.getCompoundTagAt(i);
        var enchantmentID = nbtTagCompound.getInteger("id");
        var enchantmentLevel = nbtTagCompound.getInteger("lvl");
        var enchantment = Objects.requireNonNull(Enchantment.getEnchantmentById(enchantmentID));
        itemDisplayNameSet.add(String.join(" ",
            enchantment.getTranslatedName(0).substring(0, 2).toLowerCase(),
            Integer.toString(enchantmentLevel)));
      }
      return itemDisplayNameSet;
    }
    return Set.of();
  }

  private boolean isValidEntity(Entity entity) {
    if (entity.isBot || !(entity instanceof EntityLivingBase)) return false;
    if (entity == mc.thePlayer) return self.value() && players.value();
    if (entity.isInvisible() && !invisibleEntities.value()) return false;
    if (entity instanceof EntityPlayer && players.value()) return true;
    if (entity instanceof EntityAnimal && animals.value()) return true;
    if (entity instanceof EntityVillager && neutral.value()) return true;
    return entity instanceof EntityMob && mobs.value();
  }


  public Vector3d gluProject2D(double x, double y, double z, double scaleFactor) {

    GL11.glGetFloat(2982, modelview);
    GL11.glGetFloat(2983, projection);
    GL11.glGetInteger(2978, viewport);

    if (GLU.gluProject((float) x, (float) y, (float) z, modelview, projection, viewport, screen_coords))
      return new Vector3d((screen_coords.get(0) / scaleFactor),
          ((Display.getHeight() - screen_coords.get(1)) / scaleFactor), screen_coords.get(2));
    return null;
  }

}
