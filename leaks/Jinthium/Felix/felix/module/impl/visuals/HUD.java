package felix.module.impl.visuals;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.ToDoubleFunction;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.VanillaFontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.mojang.realmsclient.gui.ChatFormatting;

import felix.Client;
import felix.api.annotations.Handler;
import felix.events.player.EventKeyPress;
import felix.events.render.EventRender2D;
import felix.gui.click.Panel;
import felix.module.Module;
import felix.util.font.FontRenderer;
import felix.util.other.PlayerUtil;
import felix.util.visual.RenderUtil;
import felix.util.visual.Translate;
import felix.value.impl.BooleanValue;
import felix.value.impl.ColorValue;
import felix.value.impl.EnumValue;
import felix.value.impl.NumberValue;

public class HUD extends Module {
 
private float hue = 1.0F;
  
  private EnumValue<ColorMode> arrayListColor = new EnumValue<>("Module List Color", ColorMode.Rainbow);
  
  private EnumValue<FontMode> fontMode = new EnumValue<>("Font Mode", FontMode.Normal);

  private BooleanValue pulsing = new BooleanValue("Pulsing", true);
  private BooleanValue background = new BooleanValue("Background", false);
  private BooleanValue tabgui = new BooleanValue("TabGUI", true);
  private BooleanValue radar = new BooleanValue("Radar", true);
  private BooleanValue arrayList = new BooleanValue("Module List", true);
  private BooleanValue info = new BooleanValue("Info", true);
  private BooleanValue armorStatus = new BooleanValue("Armor Status", true);
  private BooleanValue potionStatus = new BooleanValue("Potion Status", true);
  private BooleanValue toggleSounds = new BooleanValue("Toggle Sounds", true);
  private BooleanValue watermark = new BooleanValue("Watermark", true);
  private BooleanValue sideLine = new BooleanValue("Bar", true);
  
  private NumberValue<Float> rainbowSaturation = new NumberValue<>("Rainbow Saturation", 0.6f, 0.1f, 1.0f);
  
  private NumberValue<Float> modListBackgroundAlpha;
  
  private final NumberValue<Integer> X = new NumberValue<>("Radar X", 1, 1, 1920, 5);
  private final NumberValue<Integer> Y = new NumberValue<>("Radar Y", 15, 1, 1920, 2);
  private NumberValue<Integer> size = new NumberValue<>("Radar Size", 60, 50, 130, 5);
  
  private ColorValue colorValue = new ColorValue("Custom Color", new Color(255, 255, 255).getRGB());
  
  private float xOffset;
  private float yOffset;
  private boolean dragging;
  
  public HUD() {
	  super("HUD", 0, ModuleCategory.VISUALS);
      modListBackgroundAlpha = new NumberValue("BG Alpha", 0.2f, 0.0f, 1.0f, 0.05f);
      addValues(fontMode, arrayListColor, modListBackgroundAlpha, rainbowSaturation, X, Y, size, colorValue, arrayList, watermark, tabgui, sideLine, radar, info, armorStatus, potionStatus, pulsing, toggleSounds, background);
      setHidden(true);
  }
  
  private enum ColorMode {
	  Custom, Rainbow, Felix, Astolfo;
  }
  
  private enum FontMode {
	  Normal, Tahoma, Vanilla;
  }
  
  @Override
  public void onEnable() {
	  super.onEnable();
  }

  @Override
  public void onDisable() {
	  super.onDisable();
  }
  
  @Handler
  public void onRender(final EventRender2D event) {
	  if (arrayList.isEnabled()) {
		  draw(event.getScaledResolution());
	  }
	  if (armorStatus.isEnabled()) {
		  drawArmorStatus(event.getScaledResolution());
	  }
	  if (potionStatus.isEnabled()) {
		  drawPotionStatus(event.getScaledResolution());
	  }
	  if (radar.isEnabled()) {
		  drawRadar(event.getScaledResolution());
	  }
	  boolean tahoma = fontMode.getValue().equals(FontMode.Tahoma);
	  EntityPlayerSP player = mc.thePlayer;
      double xDist = player.posX - player.lastTickPosX;
      double zDist = player.posZ - player.lastTickPosZ;
      float d = (float) StrictMath.sqrt(xDist * xDist + zDist * zDist);
      final String bps = String.format(ChatFormatting.GRAY + " [" + ChatFormatting.WHITE + "%.2f" + " BPS" + ChatFormatting.GRAY + "]", d * 20 * mc.timer.timerSpeed);
      final String fps = info.isEnabled() ? ChatFormatting.GRAY + "[" + ChatFormatting.WHITE + Minecraft.getDebugFPS() + " FPS" + ChatFormatting.GRAY + "]" : "";
	  final FontRenderer fr = Client.INSTANCE.getFontManager().getFont(tahoma ? "Tahoma 20" : "Display 20", false);
	  final FontRenderer font = Client.INSTANCE.getFontManager().getFont(tahoma ? "Tahoma 20" : "Display 20", false);
	  final String text = ChatFormatting.GRAY + "X" + ChatFormatting.WHITE + ": " + MathHelper.floor_double(mc.thePlayer.posX) + " " + ChatFormatting.GRAY + "Y" + ChatFormatting.WHITE + ": " + MathHelper.floor_double(mc.thePlayer.posY) + " " + ChatFormatting.GRAY + "Z" + ChatFormatting.WHITE + ": " + MathHelper.floor_double(mc.thePlayer.posZ);
      final int ychat = mc.ingameGUI.getChatGUI().getChatOpen() ? 25 : 10;
      int color = 0;
      switch (arrayListColor.getValue()) {
		case Custom:
			color = new Color(colorValue.getValue()).getRGB();
			break;
		case Rainbow:
			color = RenderUtil.getRainbow(6000, (int) (1 * 30), rainbowSaturation.getValue());
			break;
		case Felix:
			color = RenderUtil.getGradientOffset(new Color(Panel.color2), new Color(Panel.color), (Math.abs(((System.currentTimeMillis()) / 10)) / 100D) + (1 / (50))).getRGB();
			break;
		case Astolfo:
			color = RenderUtil.getGradientOffset(new Color(0, 255, 255), new Color(255,105,180), (Math.abs(((System.currentTimeMillis()) / 10)) / 100D) + (1 / (50))).getRGB();
			break;
		default:
			break;
      }
      boolean vanilla = fontMode.getValue().equals(FontMode.Vanilla);
      if (watermark.isEnabled()) {
    	  if (vanilla) {
    		  mc.fontRendererObj.drawStringWithShadow("F" + ChatFormatting.WHITE + "elix " + fps + bps, 1, 1, color);
    	  } else {
    		  font.drawStringWithShadow("F" + ChatFormatting.WHITE + "elix " + fps + bps, 1, 1, color);
    	  }
      }
      if (info.isEnabled()) {
    	  if (vanilla) {
    		  mc.fontRendererObj.drawStringWithShadow(text, 1, new ScaledResolution(mc).getScaledHeight() - ychat, new Color(255, 255, 255).getRGB());
    	  } else {
    		  font.drawStringWithShadow(text, 1, new ScaledResolution(mc).getScaledHeight() - ychat, new Color(255, 255, 255).getRGB());
    	  }
      }
  }
  
  public void drawRadar(final ScaledResolution sr) {
	  int size = this.size.getValue().intValue();
      xOffset = this.X.getValue().floatValue();
      yOffset = this.Y.getValue().floatValue();
      float playerOffsetX = (float) mc.thePlayer.posX;
      float playerOffSetZ = (float) mc.thePlayer.posZ;
      int var141 = sr.getScaledWidth();
      int var151 = sr.getScaledHeight();
      int mouseX = Mouse.getX() * var141 / mc.displayWidth;
      int mouseY = var151 - Mouse.getY() * var151 / mc.displayHeight - 1;
      if ((float) mouseX >= xOffset && (float) mouseX <= xOffset + (float) size && (float) mouseY >= yOffset - 3.0F && (float) mouseY <= yOffset + 10.0F && Mouse.getEventButton() == 0) {
          this.dragging = !this.dragging;
      }

      if (mc.currentScreen instanceof GuiChat) {
      }

      if (this.hue > 255.0F) {
          this.hue = 0.0F;
      }

      float h = this.hue;
      float h2 = this.hue + 85.0F;
      float h3 = this.hue + 170.0F;
      if (h > 255.0F) {
          h = 0.0F;
      }

      if (h2 > 255.0F) {
          h2 -= 255.0F;
      }

      if (h3 > 255.0F) {
          h3 -= 255.0F;
      }

      Color color33 = Color.getHSBColor(h / 255.0F, 0.9F, 1.0F);
      Color color332 = Color.getHSBColor(h2 / 255.0F, 0.9F, 1.0F);
      Color color333 = Color.getHSBColor(h3 / 255.0F, 0.9F, 1.0F);
      int color1 = color33.getRGB();
      int color2 = color332.getRGB();
      int color3 = color333.getRGB();
      this.hue = (float) ((double) this.hue + 0.1D);
      RenderUtil.drawBorderedRect((double) (xOffset + 3.0F), (double) (yOffset + 3.0F), (double) ((float) size - 6.0F), (double) ((float) size - 6.0F), 1.2, Panel.black195, Panel.black100);
      //Gui.drawRect((double) (xOffset + 4F), (double) (yOffset + 5F), (double) (xOffset + (float) (size / 2)), (double) yOffset + 4d, Panel.color);
      //Gui.drawRect((double) (xOffset + (float) (size / 3)), (double) (yOffset + 5F), (double) (xOffset + (float) size - 4F), (double) yOffset + 4d, Panel.color);
      Gui.drawRect((double) xOffset + ((double) (size / 2) + 0.7D), (double) yOffset + 4.0D, (double) xOffset + (double) (size / 2) + 1.5, (double) (yOffset + (float) size) - 3.8D, Panel.black195);
      Gui.drawRect((double) xOffset + 3.5D, (double) yOffset + ((double) (size / 2) - 0.5D), (double) (xOffset + (float) size) - 4.0D, (double) yOffset + (double) (size / 2) + 0.56, Panel.black195);
      Iterator var21 = mc.theWorld.getLoadedEntityList().iterator();

      while (var21.hasNext()) {
          Object o = var21.next();
          if (o instanceof EntityPlayer) {
              EntityPlayer ent = (EntityPlayer) o;
              if (ent.isEntityAlive() && ent != mc.thePlayer && !ent.isInvisible() && !ent.isInvisibleToPlayer(mc.thePlayer)) {
                  float pTicks = mc.timer.renderPartialTicks;
                  float posX = (float) ((ent.lastTickPosX + (ent.posX - ent.lastTickPosX) * (double) pTicks - (double) playerOffsetX));
                  float posZ = (float) ((ent.lastTickPosZ + (ent.posZ - ent.lastTickPosZ) * (double) pTicks - (double) playerOffSetZ));

                  float cos = (float) Math.cos((double) mc.thePlayer.rotationYaw * 0.017453292519943295D);
                  float sin = (float) Math.sin((double) mc.thePlayer.rotationYaw * 0.017453292519943295D);
                  float rotY = -(posZ * cos - posX * sin);
                  float rotX = -(posX * cos + posZ * sin);
                  if (rotY > (float) (size / 2 - 5)) {
                      rotY = (float) (size / 2) - 5.0F;
                  } else if (rotY < (float) (-(size / 2 - 5))) {
                      rotY = (float) (-(size / 2 - 5));
                  }

                  if (rotX > (float) (size / 2) - 5.0F) {
                      rotX = (float) (size / 2 - 5);
                  } else if (rotX < (float) (-(size / 2 - 5))) {
                      rotX = -((float) (size / 2) - 5.0F);
                  }

                  int color = PlayerUtil.isOnSameTeam((EntityLivingBase) o) ? new Color(255, 255, 255).getRGB() : new Color(200, 0, 0).getRGB();
                  RenderUtil.drawCircle((float) (xOffset + (float) (size / 2) + rotX) - 1.0f, (float) (yOffset + (float) (size / 2) + rotY) - 1.0f, 2.2f, new Color(0, 0, 0).getRGB());
                  RenderUtil.drawCircle((float) (xOffset + (float) (size / 2) + rotX) - 1.0f, (float) (yOffset + (float) (size / 2) + rotY) - 1.0f, 2, color);
              }
          }
      }
  }
  
  private void drawArmorStatus(ScaledResolution sr) {
      GL11.glPushMatrix();
      List stuff = new ArrayList();
      boolean onwater = mc.thePlayer.isEntityAlive() && mc.thePlayer.isInsideOfMaterial(Material.water);
      int split = -3;

      ItemStack errything;
      for(int index = 3; index >= 0; --index) {
         errything = mc.thePlayer.inventory.armorInventory[index];
         if (errything != null) {
            stuff.add(errything);
         }
      }

      if (mc.thePlayer.getCurrentEquippedItem() != null) {
         stuff.add(mc.thePlayer.getCurrentEquippedItem());
      }

      Iterator var8 = stuff.iterator();

      while(var8.hasNext()) {
         errything = (ItemStack)var8.next();
         if (mc.theWorld != null) {
            RenderHelper.enableGUIStandardItemLighting();
            split += 16;
         }

         GlStateManager.pushMatrix();
         GlStateManager.disableAlpha();
         GlStateManager.clear(256);
         mc.getRenderItem().zLevel = -150.0F;
         int s = mc.thePlayer.capabilities.isCreativeMode ? 15 : 0;
		 mc.getRenderItem().renderItemAndEffectIntoGUI(errything, split + sr.getScaledWidth() / 2 - 4, sr.getScaledHeight() - (onwater ? 65 : 55) + s);
         mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, errything, split + sr.getScaledWidth() / 2 - 4, sr.getScaledHeight() - (onwater ? 65 : 55) + s);
         mc.getRenderItem().zLevel = 0.0F;
         GlStateManager.disableBlend();
         GlStateManager.scale(0.5D, 0.5D, 0.5D);
         GlStateManager.disableDepth();
         GlStateManager.disableLighting();
         GlStateManager.enableDepth();
         GlStateManager.scale(2.0F, 2.0F, 2.0F);
         GlStateManager.enableAlpha();
         GlStateManager.popMatrix();
         errything.getEnchantmentTagList();
      }

      GL11.glPopMatrix();
  }
  
  private void drawPotionStatus(ScaledResolution sr) {
      int y = 0;
      for (final PotionEffect effect : (Collection<PotionEffect>) this.mc.thePlayer.getActivePotionEffects()) {
          Potion potion = Potion.potionTypes[effect.getPotionID()];
          String PType = I18n.format(potion.getName());
          switch (effect.getAmplifier()) {
              case 1:
                  PType = PType + " II";
                  break;
              case 2:
                  PType = PType + " III";
                  break;
              case 3:
                  PType = PType + " IV";
                  break;
              default:
                  break;
          }
          if (effect.getDuration() < 600 && effect.getDuration() > 300) {
              PType = PType + "\2477:\2476 " + Potion.getDurationString(effect);
          } else if (effect.getDuration() < 300) {
              PType = PType + "\2477:\247c " + Potion.getDurationString(effect);
          } else if (effect.getDuration() > 600) {
              PType = PType + "\2477:\2477 " + Potion.getDurationString(effect);
          }
          int ychat = mc.ingameGUI.getChatGUI().getChatOpen() ? 5 : 2;
          mc.fontRendererObj.drawStringWithShadow(PType, sr.getScaledWidth() - mc.fontRendererObj.getStringWidth(PType) - 1, sr.getScaledHeight() - mc.fontRendererObj.FONT_HEIGHT + y - 12 - ychat, new Color(255, 255, 255).getRGB());
          y -= 10;
      }
  }
  
  public void draw(ScaledResolution sr) {
    int width = sr.getScaledWidth();
    int height = sr.getScaledHeight();
    
    boolean tahoma = fontMode.getValue().equals(FontMode.Tahoma);
    final FontRenderer fr = tahoma ? Client.INSTANCE.getFontManager().getFont("Tahoma 18", true) : Client.INSTANCE.getFontManager().getFont("Display 18", true);
    
    final VanillaFontRenderer fro = mc.fontRendererObj;
    
    final boolean bottom = false;
    
    boolean vanilla = fontMode.getValue().equals(FontMode.Vanilla);
    
    List<Module> sortedList = getSortedModules(fr, fro);
    float translationFactor = 40.4F / Minecraft.getDebugFPS();
    double listOffset = 11, y = bottom ? (height - listOffset) : 0;
    hue += translationFactor / 255.0F;
    if (hue > 1.0F)
      hue = 0.0F; 
    float h = hue;
    GL11.glEnable(3042);
    for (int i = 0, sortedListSize = sortedList.size(); i < sortedListSize; i++) {
      Module module = sortedList.get(i);
      Translate translate = module.translate;
      String moduleLabel = module.getName() + (module.getSuffix() != null ? ChatFormatting.GRAY + " " + module.getSuffix() : "");
      float length = vanilla ? fro.getStringWidth(moduleLabel) : fr.getWidth(moduleLabel);
      float featureX = width - length;
      boolean enable = module.isEnabled();
      if (bottom) {
        if (enable) {
          translate.interpolate(featureX, (y + 1), translationFactor);
        } 
        else {
          translate.interpolate(width, (height + 1), translationFactor);
        } 
      } 
      else if (enable) {
        translate.interpolate(featureX, (y + 1), translationFactor);
      } 
      else {
        translate.interpolate(width * width, (-listOffset - 1), translationFactor);
      } 
      double translateX = translate.getX();
      double translateY = translate.getY();
      boolean visible = bottom ? ((translateY < height)) : ((translateY > -listOffset));
      if (visible) {
        int color;
        color = 0;
		switch (arrayListColor.getValue()) {
		case Custom:
			color = new Color(colorValue.getValue()).getRGB();
			boolean pulsing = this.pulsing.isEnabled();
			if (pulsing) {
				float colorWidth = vanilla ? fro.getStringWidth(moduleLabel) * 2 + 10 : fr.getWidth(moduleLabel) * 2 + 10;
				color = RenderUtil.fade(new Color(this.colorValue.getValue()), 100, (int) colorWidth).getRGB();
			}
			break;
		case Rainbow:
			color = RenderUtil.getRainbow(6000, (int) (y * 30), rainbowSaturation.getValue());
			break;
		case Felix:
			color = RenderUtil.getGradientOffset(new Color(Panel.color2), new Color(Panel.color), (Math.abs(((System.currentTimeMillis()) / 10)) / 100D) + (y / (50))).getRGB();
			break;
		case Astolfo:
			color = RenderUtil.getGradientOffset(new Color(0, 255, 255), new Color(255,105,180), (Math.abs(((System.currentTimeMillis()) / 10)) / 100D) + (y / (50))).getRGB();
			break;
		default:
			break;
        }
        int nextIndex = sortedList.indexOf(module) + 1;
        Module nextModule = null;
        final int outlineColor = arrayListColor.getValue().equals(ColorMode.Custom) ? new Color(colorValue.getValue()).getRGB()  : color;
        if (sortedList.size() > nextIndex)
          nextModule = getNextEnabledModule(sortedList, nextIndex); 
        if (background.isEnabled()) {
          Gui.drawRect(translateX - 2.0D, translateY - 1.0D, width, translateY + listOffset - 1.0D, (new Color(13, 13, 13, (int)(255.0F * modListBackgroundAlpha.getValue()))).getRGB());
        }
        if (sideLine.isEnabled()) {
        	Gui.drawRect(translateX + length - 1.0, translateY - 1.0D, width, translateY + listOffset - 1.0D, color);
        	Gui.drawRect(translateX + length - 1.0, translateY - 1.0D, width, translateY + listOffset - 1.0D, Color.TRANSLUCENT);
        } else {
        	Gui.drawRect(translateX + length - 1.0, translateY - 1.0D, width, translateY + listOffset - 1.0D, Color.TRANSLUCENT);
        }
        if (vanilla) {
        	fro.drawStringWithShadow(moduleLabel, (float)translateX - 1.4f, (float)translateY + 1, color);
        }
        else {
        	fr.drawStringWithShadow(moduleLabel, (float)translateX - 1f, (float)translateY, color);
        }
        if (module.isEnabled())
          y += bottom ? -listOffset : listOffset; 
        h += translationFactor / 6.0F;
      } 
    } 
  }
  
  public boolean hasToggleSoundsEnabled() {
	  return toggleSounds.isEnabled();
  }

  private Module getNextEnabledModule(List<Module> sortedList, int startingIndex) {
    for (int i = startingIndex, modulesSize = sortedList.size(); i < modulesSize; i++) {
    	Module module = sortedList.get(i);
      if (module.isEnabled())
        return module; 
    } 
    return null;
  }
  
  private List<Module> getSortedModules(final FontRenderer fr, VanillaFontRenderer fro) {
	boolean vanilla = fontMode.getValue().equals(FontMode.Vanilla);
    List<Module> sortedList = new ArrayList(Client.INSTANCE.getModuleManager().getModules());
    sortedList.removeIf(Module::isHidden);
    sortedList.sort(Comparator.comparingDouble(e -> vanilla ? -fro.getStringWidth(e.getName() + (e.getSuffix() != null ? ChatFormatting.GRAY + " " + e.getSuffix() : "")) : -fr.getWidth(e.getName() + (e.getSuffix() != null ? ChatFormatting.GRAY + " " + e.getSuffix() : ""))));
    return sortedList;
  }
}
