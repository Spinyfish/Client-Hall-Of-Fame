package dev.eternal.client.ui.window;

import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WindowManager {

  public static List<WindowBase> windows = new ArrayList<>();

  public static void render(int mouseX, int mouseY) {
    windows.forEach(windowBase -> windowBase.render(mouseX, mouseY));
  }

  public static void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    windows.forEach(windowBase -> windowBase.mouseClicked(mouseX, mouseY, mouseButton));
  }

  public static void mouseReleased(int mouseX, int mouseY, int mouseButton) {
    windows.forEach(windowBase -> windowBase.mouseReleased(mouseX, mouseY, mouseButton));
  }

}
