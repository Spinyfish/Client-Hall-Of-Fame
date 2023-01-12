package dev.eternal.client.ui.alt;

import dev.eternal.client.Client;
import dev.eternal.client.font.FontManager;
import dev.eternal.client.font.FontType;
import dev.eternal.client.ui.mainmenu.GUIMainMenu;
import dev.eternal.client.ui.notification.Notification;
import dev.eternal.client.ui.notification.NotificationManager;
import dev.eternal.client.ui.notification.NotificationType;
import dev.eternal.client.util.Savable;
import dev.eternal.client.util.animate.Position;
import dev.eternal.client.util.client.Alt;
import dev.eternal.client.util.client.MouseUtils;
import dev.eternal.client.util.client.SessionChanger;
import dev.eternal.client.util.files.FileUtils;
import dev.eternal.client.util.render.RenderUtil;
import dev.eternal.client.util.shader.Shader;
import lombok.Getter;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.MathHelper;
import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Mouse;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class AltManager extends GuiScreen implements Savable {

  @Getter
  private final List<AltPart> alts = new ArrayList<>();
  private int scroll;
  private final Position position = new Position(0, 0, 0.5F);
  private GuiButton login;
  private GuiButton importList;
  private GuiButton crackedLogin;
  private GuiButton clipboardLogin;
  private GuiButton back;
  private Shader shader;

  @Override
  public void initGui() {
    shader = new Shader("menu/menu.glsl");
    this.buttonList.add(login = new GuiButton(0, this.width / 2 - 225, this.height - 50, 223, 20, "Add account"));
    this.buttonList.add(crackedLogin = new GuiButton(1, this.width / 2 - 74, this.height - 25, 148, 20, "Generate Cracked"));
    this.buttonList.add(importList = new GuiButton(2, this.width / 2 + 2, this.height - 50, 223, 20, "Import Alt List"));
    this.buttonList.add(clipboardLogin = new GuiButton(3, this.width / 2 + 77, this.height - 25, 148, 20, "Clipboard Login"));
    this.buttonList.add(back = new GuiButton(4, this.width / 2 - 225, this.height - 25, 148, 20, "Back"));
  }

  @Override
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    ScaledResolution sr = new ScaledResolution(mc);
    drawShader(mouseX, mouseY);

    final Client client = Client.singleton();

    scroll += Mouse.getDWheel() / 5;

    int itr = 0;
    for (AltPart alt : alts) {
      if (canRenderAlt(alt, itr))
        alt.render(sr.getScaledWidth() / 2 - 100, itr * 38 + 15 + (int) position.getY());
      itr++;
    }

    int offsetting = itr * 38 + 30;
    position.interpolate(0,
        position.getY() > 11 ? scroll = 0 :
            position.getY() < -offsetting + (38 * MathHelper.clamp_int(itr, 0, 12)) - 10
                ? scroll = -offsetting + (38 * MathHelper.clamp_int(itr, 0, 12))
                : scroll);

    Gui.drawRect(0,
        sr.getScaledHeight() - 65,
        sr.getScaledWidth(),
        sr.getScaledHeight(),
        client.scheme().getBackground());

    FontManager.getFontRenderer(FontType.ICIEL, 20).drawStringWithShadow("Current account: " + mc.session.getUsername(), 3, sr.getScaledHeight() - 12, -1);

    super.drawScreen(mouseX, mouseY, partialTicks);
  }

  private boolean canRenderAlt(AltPart altPart, int itr) {
    ScaledResolution sr = new ScaledResolution(mc);
    return (itr * 38 + 15 + (int) position.getY()) < sr.getScaledHeight() && ((itr + 1) * 38 + 15 + (int) position.getY()) > 0;
  }

  private void drawShader(int mouseX, int mouseY) {
    RenderUtil.drawRect(0, 0, mc.displayWidth, mc.displayHeight, 0xFF000000);
    shader.useShader();
    shader.setUniform1f("time", (System.currentTimeMillis() - Client.singleton().initTime()) / 2000F);
    shader.setUniform2f("resolution", mc.displayWidth, mc.displayHeight);
    shader.setUniform2f("mouse", mouseX, mouseY);
    RenderUtil.drawRect(0, 0, mc.displayWidth, mc.displayHeight, -1);
    shader.stopShader();
  }

  @Override
  protected void actionPerformed(GuiButton button) throws IOException {
    switch (button.id) {
      case 0 -> mc.displayGuiScreen(new GuiAddAccount());
      case 1 -> {
        try {
          SessionChanger.getInstance().setUserOffline(getName());
        } catch (Exception e) {
          NotificationManager.pushNotification(new Notification("AltManager", "Error in name generation, please contact Eternal about this.", 5000, NotificationType.ERROR));
        }
      }
      case 2 -> importAltList();
      case 3 -> {
        try {
          Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
          String[] details = ((String) clipboard.getData(DataFlavor.stringFlavor)).split(":");
          this.alts.add(new AltPart(new Alt(details[0], details[1])));
        } catch (Exception e) {
          NotificationManager.pushNotification(new Notification("AltManager", "Unable to add alt!", 5000, NotificationType.ERROR));
        }
      }
      case 4 -> mc.displayGuiScreen(new GUIMainMenu());
    }
  }

  @Override
  public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
    final ScaledResolution scaledResolution = new ScaledResolution(mc);
    super.mouseClicked(mouseX, mouseY, mouseButton);

//        if(MouseUtils.isInArea(mouseX, mouseY, 0, scaledResolution.getScaledHeight() - 65, scaledResolution.getScaledWidth(), 65)) return;

    alts.forEach(altPart -> altPart.mouseClicked(mouseX, mouseY, mouseButton));
    alts.removeIf(AltPart::shouldRemove);
  }

  public String getName() throws Exception {
    URL wordURL = new URL("https://random-word-api.herokuapp.com/word?number=" + 1);
    BufferedReader bufferedReader =
        new BufferedReader(new InputStreamReader(wordURL.openStream()));
    String in = bufferedReader.readLine();
    in = in.replace("[", "").replace("]", "");
    String[] args = in.replace("\"", "").split(",");
    String word = args[RandomUtils.nextInt(0, args.length)];
    if (word.equals("ERR")) {
      NotificationManager.pushNotification(new Notification("WORD BANK", "ERROR IN WORD BANK", 5000, NotificationType.ERROR));
    }

    switch (new Random().nextInt(6)) {
      case 0 -> {
        if (word.length() > 8) word = word.substring(0, 8);
        word = "xXx_" + word + "_xXx";
      }
      case 1 -> word += RandomUtils.nextInt(1, 999);
      case 2 ->
//          if(word.length() > 7)  word = word.substring(0, 7);
          word = "NoHaxJust" + word.substring(0, 1).toUpperCase(Locale.ROOT) + word.substring(1);
      case 3 -> word = String.format("%s%s%s", "_", word, "_");
      case 4 -> word = word.replaceAll("a", "q").replaceAll("e", "3").replaceAll("o", "0").replace("l", "1");
    }

    if (word.length() > 16) word = word.substring(0, 16);
    return word;
  }

  private void importAltList() {
    final JFileChooser jFileChooser = new JFileChooser();
    jFileChooser.setVisible(true);
    jFileChooser.requestFocus();
    int result = jFileChooser.showOpenDialog(null);

    if (result == JFileChooser.APPROVE_OPTION) {
      File file = jFileChooser.getSelectedFile();
      List<String> fileContents = FileUtils.readFromFile(file);
      fileContents.removeIf(s -> !s.contains(":"));
      fileContents.forEach(s -> {
        String[] details = s.split(":");
        alts.add(new AltPart(new Alt(details[0], details[1])));
      });
    }
  }

  @Override
  protected void keyTyped(char typedChar, int keyCode) throws IOException {
    if (keyCode == 1)
      mc.displayGuiScreen(new GUIMainMenu());
  }

  @Override
  protected void mouseReleased(int mouseX, int mouseY, int state) {
    alts.forEach(altPart -> altPart.mouseReleased(mouseX, mouseY, state));
    super.mouseReleased(mouseX, mouseY, state);
  }

  private List<String> getAltsPlainText() {
    return alts.stream()
        .map(AltPart::alt)
        .map(Alt::toString)
        .toList();
  }

  @Override
  public void save() {
    FileUtils.writeToFile(FileUtils.getFileFromFolder("alt", "alts.txt"), getAltsPlainText());
  }

  @Override
  public void load() {
    FileUtils.readFromFile(FileUtils.getFileFromFolder("alt", "alts.txt")).forEach(s -> {
      try {
        String[] splitString = s.split(":");
        switch (splitString.length) {
          case 2 -> alts.add(new AltPart(new Alt(splitString[0], splitString[1])));
          case 3 -> alts.add(new AltPart(new Alt(splitString[0], splitString[1], splitString[2])));
        }
      } catch (Exception e) {
        System.out.println("Sorry mate, cannot add an alt. Code did an oops!");
      }
    });
  }
}
