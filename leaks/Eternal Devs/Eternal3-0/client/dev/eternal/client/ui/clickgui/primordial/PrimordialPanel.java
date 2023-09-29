package dev.eternal.client.ui.clickgui.primordial;

import com.google.common.collect.ImmutableMap;
import dev.eternal.client.Client;
import dev.eternal.client.font.FontManager;
import dev.eternal.client.font.FontType;
import dev.eternal.client.module.Module.Category;
import dev.eternal.client.ui.clickgui.ClickGui;
import dev.eternal.client.ui.clickgui.primordial.frames.AbstractPrimordialFrame;
import dev.eternal.client.ui.clickgui.primordial.frames.ModulePrimordialFrame;
import dev.eternal.client.util.animate.Animate;
import dev.eternal.client.util.client.MouseUtils;
import dev.eternal.client.util.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class PrimordialPanel extends ClickGui {

  private double scroll;
  private double posX, posY;
  private double addX, addY;
  private double prevX, prevY;

  private boolean dragging, resizing;

  private final Animate scrollAnim = new Animate(0, 1F);
  private final Minecraft MC = Minecraft.getMinecraft();
  private final FontRenderer FR = MC.fontRendererObj;
  private final List<AbstractPrimordialFrame> frameList = new ArrayList<>();
  private final ImmutableMap<Category, ResourceLocation> icons =
      new ImmutableMap.Builder<Category, ResourceLocation>()
          .put(Category.COMBAT, new ResourceLocation("tetra/combat.png"))
          .put(Category.MOVEMENT, new ResourceLocation("tetra/movement.png"))
          .put(Category.PLAYER, new ResourceLocation("tetra/player.png"))
          .put(Category.RENDER, new ResourceLocation("tetra/render.png"))
          .put(Category.MISC, new ResourceLocation("tetra/misc.png"))
          .build();

  public PrimordialPanel() {
    Client.singleton()
        .moduleManager()
        .stream()
        .map(ModulePrimordialFrame::new)
        .forEach(frameList::add);
  }

  @Override
  public void initGui() {
    this.posX = this.posY = 50;
    this.addX = 703 / 2f;
    this.addY = 668 / 2F;
  }

  @Override
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    drawPanel();

    scroll += Mouse.getDWheel() / 10f;
    scroll = MathHelper.clamp_double(scroll, -6969, 36);
    scrollAnim.interpolate(scroll);

    drawModules(mouseX, mouseY);

    if (dragging) {
      posX += (mouseX - prevX);
      posY += (mouseY - prevY);
    }

    if (resizing) {
      addX += (mouseX - prevX);
      addY += (mouseY - prevY);
    }

    addX = MathHelper.clamp_double(addX, 703 / 2d, Double.MAX_VALUE);
    addY = MathHelper.clamp_double(addY, 668 / 2d, Double.MAX_VALUE);

    prevX = mouseX;
    prevY = mouseY;
  }

  @Override
  public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    if (MouseUtils.isInArea(mouseX, mouseY, posX, posY, addX, 10)) {
      dragging = true;
      return;
    }
    if (MouseUtils.isInArea(mouseX, mouseY, posX + addX - 10, posY + addY - 10, 10, 10)) resizing = true;
    frameList.forEach(abstractPrimordialFrame -> abstractPrimordialFrame.mouseClicked(mouseX, mouseY, mouseButton));
  }

  @Override
  protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
    dragging = resizing = false;
    frameList.forEach(abstractPrimordialFrame -> abstractPrimordialFrame.mouseReleased(mouseX, mouseY, mouseButton));
  }

  private void drawPanel() {
    RenderUtil.drawRoundedRect(posX, posY, posX + addX, posY + addY, 12, 0xFF0C0C0C);
    RenderUtil.drawRoundedRect(posX + 1, posY + 1, posX + addX - 1, posY + addY - 1, 12, 0xFF2A2A2B);
    drawRect(posX + 1, posY + 26, posX + addX - 1, posY + addY - 27.5, 0xFF161616);
    drawRect(posX + 0.5, posY + 25, posX + addX - 0.5, posY + 26, Client.singleton().scheme().getPrimary());
    FontManager.getFontRenderer(FontType.ICIEL, 16)
        .drawCenteredString(Client.singleton().clientSettings().name(), posX + addX / 2, posY + 12, -1);

//    GlStateManager.enableBlend();
//    MC.getTextureManager().bindTexture(icons);
//    GlStateManager.scale(0.5, 0.5, 1);
//    GlStateManager.color(1, 1, 1, 1);
//    Gui.drawModalRectWithCustomSizedTexture((int)posX*2, (int)posY*2 + (int) addY*2 - 27*2, 0, 0, 2640/5, 50, 2640/5f, 416/5f);
//    GlStateManager.scale(2, 2, 1);

  }

  public void drawModules(int mouseX, int mouseY) {
    int maxPanelsWidth = MathHelper.floor_double(addX / 165);
    GL11.glEnable(3089);
    RenderUtil.prepareScissorBox(posX, posY + 27, posX + addX, posY + addY - 27);
    int itrDown = 0;
    int itrAcross = 0;
    double[][] positions = new double[maxPanelsWidth][frameList.size()];
    for (AbstractPrimordialFrame frame : frameList) {
      double yOffset = 0;
      double difference = (addX / maxPanelsWidth) * itrAcross;
      double xPosition = difference + (addX % (163 * maxPanelsWidth)) / maxPanelsWidth / 2;
      if (itrDown == 0) {
        positions[itrAcross][itrDown] = frame.getHeight();
      } else {
        positions[itrAcross][itrDown] = positions[itrAcross][itrDown - 1] + frame.getHeight();
        yOffset = positions[itrAcross][itrDown - 1] + itrDown * 8;
      }
      yOffset += scrollAnim.getValue();
      if (yOffset < addY) frame.draw(mouseX, mouseY, posX + xPosition, posY + yOffset);
      itrAcross++;
      if (itrAcross % maxPanelsWidth == 0) {
        itrDown++;
        itrAcross = 0;
      }
    }
    GL11.glDisable(3089);
    drawGradientRect(posX + 1, posY + 26, posX + addX - 1, posY + 41, 0xFF000000, 0x000000);
    drawGradientRect(posX + 1, posY + addY - 42.5, posX + addX - 1, posY + addY - 27.5, 0x000000, 0xFF000000);
  }

  @Override
  public void save() {

  }

  @Override
  public void load() {

  }
}
