package me.uncodable.srt.impl.gui.clickgui;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.ArrayList;
import me.uncodable.srt.impl.gui.clickgui.components.RenderTab;
import me.uncodable.srt.impl.modules.api.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

public class ClickGUIScreen extends GuiScreen {
   private static final Minecraft MC = Minecraft.getMinecraft();
   private final ArrayList<RenderTab> renderTabs = new ArrayList<>();
   private boolean setup;
   private final int secret;

   public ClickGUIScreen(int secret) {
      this.secret = secret;
      this.setup();
   }

   public void setup() {
      if (!this.setup) {
         for(int i = 0; i < Module.Category.values().length; ++i) {
            this.renderTabs.add(new RenderTab(Module.Category.values()[i], 10 + 200 * i, 50, 100 + 200 * i, 70, this.secret));
         }

         this.setup = true;
      }
   }

   @Override
   public boolean doesGuiPauseGame() {
      return false;
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.renderTabs.forEach(renderTab -> renderTab.render(mouseX, mouseY, this.width, this.height));
      this.renderTabs
         .forEach(
            renderTab -> renderTab.getRenderModules()
                  .forEach(
                     renderModule -> {
                        if (renderModule.hoverOver(mouseX, mouseY)) {
                           String message = String.format(
                              "§7Description:\n\n%s\n\n§7Primary Bind:§f %s\n\n§7Secondary Bind:§f %s",
                              renderModule.getModule().getInfo().desc(),
                              Keyboard.getKeyName(renderModule.getModule().getPrimaryKey()),
                              "N/A"
                           );
                           this.drawHoveringText(Lists.newArrayList(Splitter.on('\n').split(message)), mouseX, mouseY);
                        }
                     }
                  )
         );
      MC.fontRendererObj.drawStringWithShadow("I promise a new Click GUI will be made eventually... <3", 2.0F, (float)(this.height - 12), 16777215);
      MC.gameSettings.keyBindJump.setKeyDown(Keyboard.isKeyDown(MC.gameSettings.keyBindJump.getKeyCode()));
      MC.gameSettings.keyBindRight.setKeyDown(Keyboard.isKeyDown(MC.gameSettings.keyBindRight.getKeyCode()));
      MC.gameSettings.keyBindLeft.setKeyDown(Keyboard.isKeyDown(MC.gameSettings.keyBindLeft.getKeyCode()));
      MC.gameSettings.keyBindForward.setKeyDown(Keyboard.isKeyDown(MC.gameSettings.keyBindForward.getKeyCode()));
      MC.gameSettings.keyBindBack.setKeyDown(Keyboard.isKeyDown(MC.gameSettings.keyBindBack.getKeyCode()));
      MC.gameSettings.keyBindSprint.setKeyDown(Keyboard.isKeyDown(MC.gameSettings.keyBindSprint.getKeyCode()));
      MC.gameSettings.keyBindSneak.setKeyDown(Keyboard.isKeyDown(MC.gameSettings.keyBindSneak.getKeyCode()));
   }

   @Override
   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
      this.renderTabs.forEach(renderTab -> renderTab.onClicked(mouseX, mouseY, mouseButton));
   }

   @Override
   protected void mouseReleased(int mouseX, int mouseY, int state) {
      this.renderTabs.forEach(RenderTab::mouseReleased);
   }

   @Override
   protected void keyTyped(char typedChar, int keyCode) throws IOException {
      this.renderTabs.forEach(renderTab -> renderTab.keyTyped(keyCode));
      if (keyCode == 1) {
         super.keyTyped(typedChar, keyCode);
      }
   }

   public ArrayList<RenderTab> getRenderTabs() {
      return this.renderTabs;
   }

   public boolean isSetup() {
      return this.setup;
   }

   public int getSecret() {
      return this.secret;
   }
}
