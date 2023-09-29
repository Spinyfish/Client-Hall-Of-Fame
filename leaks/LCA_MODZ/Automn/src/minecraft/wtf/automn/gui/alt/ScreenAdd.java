package wtf.automn.gui.alt;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import wtf.automn.Automn;
import wtf.automn.gui.Position;
import wtf.automn.gui.Renderable;
import wtf.automn.gui.alt.management.Account;
import wtf.automn.gui.alt.utils.MicrosoftAuthentication;
import wtf.automn.gui.alt.utils.MicrosoftLoginWindow;
import wtf.automn.utils.render.RenderUtils;

import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ScreenAdd extends Gui implements Renderable {

  public boolean visible = false;
  public Position pos;

  public boolean next = true;

  @Override
  public void render(float x, float y, int mouseX, int mouseY) {
    if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
      visible = false;
    }
    if (visible) {
      pos = new Position(x, y, 200, 150);
      drawRect(pos.x, pos.y, pos.x + pos.width, pos.y + pos.height, new Color(25, 25, 25, 255).getRGB());
      drawCenteredString(Minecraft.getMinecraft().fontRendererObj, "Account Type", pos.x + pos.width / 2, pos.y + 5, Color.white.getRGB());

      Position windows = new Position(pos.x + 25, pos.y + 35, pos.width - 25 - 25, 40);
      Position mojang = new Position(pos.x + 25, pos.y + 85, pos.width - 25 - 25, 40);

      /*windows*/
      {
        boolean b = windows.isHovered(mouseX, mouseY);
        drawRect(windows.x, windows.y, windows.x + windows.width, windows.y + windows.height, b ? Integer.MAX_VALUE / 3 : Integer.MAX_VALUE / 2);
        ResourceLocation loc = new ResourceLocation("automn/alts/type/microsoft.png");
        RenderUtils.drawImage(windows.x, windows.y, windows.width, windows.height, loc);
        if (Mouse.isButtonDown(0) && b && next) {
          MicrosoftAuthentication.instance().loginWithPopUpWindow();
          next = false;
          visible = false;
        }
      }

      /*mojang*/
      {
        boolean b = mojang.isHovered(mouseX, mouseY);
        drawRect(mojang.x, mojang.y, mojang.x + mojang.width, mojang.y + mojang.height, b ? Integer.MAX_VALUE / 3 : Integer.MAX_VALUE / 2);
        ResourceLocation loc = new ResourceLocation("automn/alts/type/mojang.png");
        RenderUtils.drawImage(mojang.x, mojang.y - 10, mojang.width, mojang.height + 20, loc);
        if (Mouse.isButtonDown(0) && b && next) {
          try {
            String email = GuiScreen.getClipboardString().split(":")[0];
            String password = GuiScreen.getClipboardString().split(":")[1];
            Session s = MicrosoftLoginWindow.loginMojang(email, password);
            assert s != null;
            Account account = new Account(s);
            account.setType(Account.Type.MOJANG);
            if (Automn.instance().accountManager().getAccountByName(account.getOwner()) != null) {
              Automn.instance().accountManager().removeAccount(account);
            }
            account.setLastUpdated(LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
            Automn.instance().accountManager().addAccount(account);
            Automn.instance().accountManager().save();
          } catch (Exception ignored) {
          }
          next = false;
          visible = false;
        }
      }
    } else {
      return;
    }
    if (!Mouse.isButtonDown(0)) next = true;
  }

}
