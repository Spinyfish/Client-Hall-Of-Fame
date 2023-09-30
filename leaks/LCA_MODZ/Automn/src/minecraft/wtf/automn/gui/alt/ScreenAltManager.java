package wtf.automn.gui.alt;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import wtf.automn.Automn;
import wtf.automn.fontrenderer.ClientFont;
import wtf.automn.fontrenderer.GlyphPageFontRenderer;
import wtf.automn.gui.Position;
import wtf.automn.gui.Renderable;
import wtf.automn.gui.alt.management.Account;
import wtf.automn.gui.alt.management.AccountManager;
import wtf.automn.gui.buttons.GuiButtonTexture;
import wtf.automn.module.impl.visual.ModuleBlur;
import wtf.automn.utils.render.RenderUtils;

import java.awt.*;
import java.io.IOException;

public class ScreenAltManager extends GuiScreen {

  private final GuiScreen parent;
  private final GlyphPageFontRenderer fontRenderer = ClientFont.font(40, "Calibri", true);
  private ScreenAdd screenAdd;
  private int scrollIndex;
  private int selectedIndex = -1;
  private boolean next;

  public ScreenAltManager(GuiScreen parent) {
    this.parent = parent;
  }

  @Override
  public void initGui() {
    int buttonsX = 40, buttonsY = 50;
    int buttonSize = 40;

    this.buttonList.add(new GuiButtonTexture(0, buttonsX, buttonsY + 20, buttonSize, buttonSize,
      "automn/alts/add.png"));
    this.buttonList.add(new GuiButtonTexture(1, buttonsX, buttonsY + buttonSize + 40, buttonSize, buttonSize,
      "automn/alts/refresh.png"));
    this.buttonList.add(new GuiButtonTexture(2, buttonsX, buttonsY + buttonSize + buttonSize + 60, buttonSize,
      buttonSize, "automn/alts/login.png"));
    this.buttonList.add(new GuiButtonTexture(3, buttonsX, buttonsY + buttonSize + buttonSize + buttonSize + 80,
      buttonSize, buttonSize, "automn/alts/remove.png"));
    this.screenAdd = new ScreenAdd();
  }

  @Override
  protected void actionPerformed(GuiButton button) throws IOException {
    if (button.id == 6) {
      mc.displayGuiScreen(parent);
    }
    if (button.id == 0) {
      this.screenAdd.visible = true;
    }
    if (button.id == 1) {
      AccountManager manager = Automn.instance().accountManager();
      manager.shutdown();
      manager.init();
    }
    if (button.id == 2) {
      if (selectedIndex > -1) {
        Account acc = Automn.instance().accountManager().getAccounts().get(selectedIndex);
        acc.login();
        selectedIndex = -1;
      }
    }
    if (button.id == 3) {
      if (selectedIndex > -1) {
        Account acc = Automn.instance().accountManager().getAccounts().get(selectedIndex);
        Automn.instance().accountManager().removeAccount(acc);
        selectedIndex = -1;
      }
    }
    super.actionPerformed(button);
  }

  @Override
  protected void keyTyped(char typedChar, int keyCode) throws IOException {
    if (!screenAdd.visible) {
      mc.displayGuiScreen(parent);
    }
  }

  @Override
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {

    RenderUtils.drawImage(0, 0, this.mc.displayWidth / 2f, this.mc.displayHeight / 2f, new ResourceLocation("textures/background.jpg"));



    int listX = 120, listY = 50, listRight = 25, listBottom = 10; // coords
    int buttonsX = 20, buttonsY = 50;

    drawRect(buttonsX, 10, width - listRight, buttonsY - 10, Integer.MIN_VALUE); // draws the title rect;
    fontRenderer.drawString("Accounts", buttonsX + 4, 11, Color.white.getRGB());
    String loggedInAs = "§fCurrently Logged in as: §a" + mc.session.getUsername();
    fontRenderer.drawScaledString(loggedInAs,
      (width - listRight - fontRenderer.getStringWidth(loggedInAs) * 0.65F) - 3, 11, 0.65F,
      Color.white.getRGB());
    boolean b = !mc.session.getPlayerID().equalsIgnoreCase(mc.session.getUsername());
    String s1 = "§fAccount type: §a" + (b ? "Online" : "Offline");
    fontRenderer.drawScaledString(s1, (width - listRight - fontRenderer.getStringWidth(s1) * 0.65F) - 3,
      buttonsY - 10 - fontRenderer.getFontHeight() * 0.65F + 2, 0.65F, Color.white.getRGB());

    drawRect(buttonsX, buttonsY, listX - 20, height - listBottom, Integer.MIN_VALUE); // button list

    AccountManager accountManager = Automn.instance().accountManager();
    int accountAmount = accountManager.getAccounts().size();

    int mouseDir = MathHelper.clamp_int(Mouse.getDWheel(), -20, 20);

    scrollIndex += mouseDir;
    if (scrollIndex > 0) {
      scrollIndex = 0;
    }
    if (accountAmount > 8) {
      if (scrollIndex < -(50 * accountAmount) + height - listY - 13) {
        scrollIndex = -(50 * accountAmount) + height - listY - 13;
      }
    } else {
      scrollIndex = 0;
    }

    drawRect(listX, listY, width - listRight, height - listBottom, Integer.MIN_VALUE); // account list
    Renderable list = new Renderable() {

      private final int listWidth = width - listRight;
      private final int listHeight = height - listBottom;

      @Override
      public void render(float x, float y, int mouseX, int mouseY) {


        GlStateManager.pushMatrix();
        RenderUtils.scissor((int) x, (int) y, listWidth - (int) x, listHeight - (int) y);
        y = y + scrollIndex;
        int boxHeight = 50;

        for (int i = 0; i < accountManager.getAccounts().size(); i++) {
          float spaceX = x + 3;
          float spaceY = y + 3 + (i * boxHeight);
          float spaceRight = listWidth - 3;
          float spaceBottom = y + (i * boxHeight) + boxHeight;

          Position pos = new Position(spaceX, spaceY, spaceRight - spaceX - 1, spaceBottom - spaceY);

          drawRect(spaceX, spaceY, spaceRight, spaceBottom,
            selectedIndex == i ? Integer.MAX_VALUE / 2 : (pos.isHovered(mouseX, mouseY) && !screenAdd.visible) ? new Color(10, 10, 15, 100).getRGB() : Integer.MIN_VALUE);

          Account acc = accountManager.getAccounts().get(i);

          try {
            mc.getTextureManager().bindTexture(acc.getHeadTexture());
          } catch (Exception ignored) {
          }
          RenderUtils.setGLColor(Color.white.getRGB());
          drawModalRectWithCustomSizedTexture(spaceX, spaceY, 0, 0, boxHeight - 3, boxHeight - 3, boxHeight - 3,
            boxHeight - 3);

          fontRenderer.drawScaledString(acc.getOwner(), spaceX + boxHeight - 1, spaceY + 2, 0.75F,
            Color.white.getRGB());
          fontRenderer.drawScaledString("Last Updated: " + acc.getLastUpdated(), spaceX + boxHeight - 1,
            spaceY + boxHeight - fontRenderer.getFontHeight() * 0.5F - 2, 0.5F, Color.white.getRGB());
          fontRenderer.drawScaledString("Type: " + acc.getTypeString(), spaceX + boxHeight - 1,
            spaceY + boxHeight / 1.3F - fontRenderer.getFontHeight() * 0.5F - 2, 0.5F, Color.white.getRGB());

          if (pos.isHovered(mouseX, mouseY) && Mouse.isButtonDown(0) && !screenAdd.visible) {
            selectedIndex = i;
          }

          Position trashPos = new Position(spaceRight - 55, spaceY + 5, 50, 50 - 12);
          mc.getTextureManager().bindTexture(new ResourceLocation("automn/alts/remove.png"));
          if (trashPos.isHovered(mouseX, mouseY) && !screenAdd.visible) {
            RenderUtils.setGLColor(new Color(255, 50, 50).getRGB());
          }


          drawModalRectWithCustomSizedTexture(spaceRight - 55, spaceY + 5, 0, 0, 50, 50 - 12, 50, 50 - 12);
          if (!screenAdd.visible) {
            if (trashPos.isHovered(mouseX, mouseY) && Mouse.isButtonDown(0) && next) {
              try {
                accountManager.removeAccount(acc);
                next = false;
              } catch (Exception ignored) {
              }
            }
          }
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GlStateManager.popMatrix();
      }
    };
    if (!Mouse.isButtonDown(0)) {
      next = true;
    }
    list.render(listX, listY, mouseX, mouseY);
    try {
      screenAdd.render(width / 2f - 100, height / 2f - 75, mouseX, mouseY);
    } catch (Exception ignored) {
    }
    super.drawScreen(mouseX, mouseY, partialTicks);
  }

}
