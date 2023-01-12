package dev.eternal.client.ui.targethud;

import com.google.common.collect.Lists;
import dev.eternal.client.Client;
import dev.eternal.client.util.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class EternalTargetHUD extends TargetHUD {

  private static final int WIDTH = 125;
  private static final int HEIGHT = 45;

  public EternalTargetHUD(EntityLivingBase target) {
    super(target);
  }

  @Override
  public void render() {
    final double x = hudPosition.getX();
    final double y = hudPosition.getY();
    GlStateManager.pushMatrix();
    healthBar.interpolate((target.getHealth() / target.getMaxHealth()) * (WIDTH - 10));
    Gui.drawRect(x, y, x + WIDTH, y + HEIGHT, Client.singleton().scheme().getBackground());
    Gui.drawRect(x, y, x + WIDTH, y + 1, Client.singleton().scheme().getPrimary());
    for (int i = 0; i < 5; i++) {
      Gui.drawRect(x + 32 + (18 * i), y + 15, x + 48 + (18 * i), y + 31, Client.singleton().scheme().getScrim());
    }
    if (target instanceof EntityPlayer player) {
      GlStateManager.color(1, 1, 1, 1);
      RenderUtil.drawPlayerFace(player, (float) x + 5, (float) y + 7, 24, 24);
      List<ItemStack> items = new ArrayList<>(Stream.of(player.inventory.armorInventory).filter(Objects::nonNull).toList());
      items.add(player.getHeldItem());
      items = Lists.reverse(items);
      int i = 0;
      for (ItemStack itemStack : items) {
        if (itemStack != null) {
          Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(itemStack, (float) x + 32 + (18 * i), (float) y + 15);
          Minecraft.getMinecraft().getRenderItem().renderItemOverlays(Minecraft.getMinecraft().fontRendererObj, itemStack, (int) x + 32 + (18 * i), (int) y + 15);
          i++;
        }
      }
    }
    var x1 = target instanceof EntityPlayer ? x + 30 : x + 4;

    title.drawStringWithShadow(target.getName(), x1 + 1, y + 5, -1);

    Gui.drawRect(x + 5, y + HEIGHT - 9, x + WIDTH - 5, y + HEIGHT - 4, 0xFF0C0C0C);
    RenderUtil.drawGradientRect(
        x + 5,
        y + HEIGHT - 9,
        healthBar.getValue(),
        5,
        new Color(Client.singleton().scheme().getPrimary()),
        new Color(Client.singleton().scheme().getPrimary()).darker());
    GlStateManager.popMatrix();
  }
}
