package me.uncodable.srt.impl.gui.clickgui2;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.ArrayList;
import me.uncodable.srt.impl.gui.clickgui2.components.RenderTab;
import me.uncodable.srt.impl.utils.EasingUtils;
import me.uncodable.srt.impl.utils.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class ClickGUIScreen extends GuiScreen {
   private static final Minecraft MC = Minecraft.getMinecraft();
   private final ArrayList<RenderTab> renderTabs;
   private final Timer timer = new Timer();
   private static final long ANIMATION_MILLISECONDS = 500L;

   public ClickGUIScreen(ArrayList<RenderTab> renderTabs) {
      this.renderTabs = renderTabs;
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      float progress = (float)this.timer.difference() / 500.0F;
      this.drawDefaultBackground();
      GL11.glPushMatrix();
      if (progress <= 1.0F) {
         float animationProgress = EasingUtils.easeOutBack(progress);
         GL11.glTranslatef((float)this.width * (1.0F - animationProgress) / 2.0F, (float)this.height * (1.0F - animationProgress) / 2.0F, 0.0F);
         GL11.glScalef(animationProgress, animationProgress, animationProgress);
      }

      this.renderTabs.forEach(renderTab -> renderTab.render(mouseX, mouseY, this.width, this.height));
      this.renderTabs
         .forEach(
            renderTab -> renderTab.getRenderModules()
                  .forEach(
                     renderModule -> {
                        if (renderModule.hoverOver(mouseX, mouseY) && renderTab.isDropped()) {
                           String message = String.format(
                              "§dDescription:\n\n§5%s\n\n§dPrimary Bind:§5 %s\n\n§dSecondary Bind:§5 %s",
                              renderModule.getModule().getInfo().desc().replaceAll("", "§5"),
                              Keyboard.getKeyName(renderModule.getModule().getPrimaryKey()),
                              "N/A"
                           );
                           this.drawHoveringText(Lists.newArrayList(Splitter.on('\n').split(message)), mouseX, mouseY);
                        }
                     }
                  )
         );
      GL11.glDisable(2896);
      GL11.glPopMatrix();
      this.mc.fontRendererObj.drawStringWithShadow("Prototype Click GUI", 0.0F, (float)(this.height - 12), 16777215);
      MC.gameSettings.keyBindJump.setKeyDown(Keyboard.isKeyDown(MC.gameSettings.keyBindJump.getKeyCode()));
      MC.gameSettings.keyBindRight.setKeyDown(Keyboard.isKeyDown(MC.gameSettings.keyBindRight.getKeyCode()));
      MC.gameSettings.keyBindLeft.setKeyDown(Keyboard.isKeyDown(MC.gameSettings.keyBindLeft.getKeyCode()));
      MC.gameSettings.keyBindForward.setKeyDown(Keyboard.isKeyDown(MC.gameSettings.keyBindForward.getKeyCode()));
      MC.gameSettings.keyBindBack.setKeyDown(Keyboard.isKeyDown(MC.gameSettings.keyBindBack.getKeyCode()));
      MC.gameSettings.keyBindSprint.setKeyDown(Keyboard.isKeyDown(MC.gameSettings.keyBindSprint.getKeyCode()));
      MC.gameSettings.keyBindSneak.setKeyDown(Keyboard.isKeyDown(MC.gameSettings.keyBindSneak.getKeyCode()));
   }

   @Override
   public boolean doesGuiPauseGame() {
      return false;
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
}
