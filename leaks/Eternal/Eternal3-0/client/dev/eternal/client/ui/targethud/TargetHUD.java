package dev.eternal.client.ui.targethud;

import dev.eternal.client.Client;
import dev.eternal.client.font.FontManager;
import dev.eternal.client.font.FontType;
import dev.eternal.client.font.renderer.TrueTypeFontRenderer;
import dev.eternal.client.util.animate.Animate;
import dev.eternal.client.util.animate.Position;
import dev.eternal.client.util.animate.Transition;
import dev.eternal.client.util.math.MathUtil;
import dev.eternal.client.util.render.RenderUtil;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@Getter
public abstract class TargetHUD {

  protected final EntityLivingBase target;
  protected final Animate healthBar;
  protected final Position hudPosition;
  protected final Transition transition;
  protected final TrueTypeFontRenderer title, small, titleRoboto, smallRoboto;

  public TargetHUD(final EntityLivingBase target) {
    final ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
    this.target = target;
    this.healthBar = new Animate(0, 1);
    this.hudPosition = new Position(0, 0, 1);
    this.transition = new Transition(0.6F).scale(true);
    this.transition.setValue(0.1F);
    this.title = FontManager.getFontRenderer(FontType.ICIEL, 20);
    this.titleRoboto = FontManager.getFontRenderer(FontType.ROBOTO_REGULAR, 18);
    this.small = FontManager.getFontRenderer(FontType.ICIEL, 16);
    this.smallRoboto = FontManager.getFontRenderer(FontType.ROBOTO_REGULAR, 14);
  }

  public abstract void render();

}
