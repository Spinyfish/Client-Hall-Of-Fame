package me.uncodable.srt.impl.modules.impl.visual;

import me.uncodable.srt.impl.events.api.EventTarget;
import me.uncodable.srt.impl.events.events.render.Event2DRender;
import me.uncodable.srt.impl.modules.api.Module;
import me.uncodable.srt.impl.modules.api.ModuleInfo;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import org.lwjgl.input.Keyboard;

@ModuleInfo(
   internalName = "Keystrokes",
   name = "Keystrokes",
   desc = "Allows you to actually know which keys you're pressing.",
   category = Module.Category.VISUAL,
   legit = true
)
public class Keystrokes extends Module {
   int getHeight;

   public Keystrokes(int key, boolean enabled) {
      super(key, enabled);
   }

   @EventTarget(
      target = Event2DRender.class
   )
   public void onRender(Event2DRender e) {
      if (!(MC.currentScreen instanceof GuiChat) && !MC.gameSettings.showDebugInfo) {
         this.getHeight = e.getScaledResolution().getScaledHeight();
         Gui.drawRect(
            29,
            (int)((float)this.getHeight / 2.0F - 25.0F),
            49,
            (int)((float)this.getHeight / 2.0F - 5.0F),
            MC.gameSettings.keyBindForward.isKeyDown() ? -855638017 : -872415232
         );
         MC.fontRendererObj
            .drawString(Keyboard.getKeyName(MC.gameSettings.keyBindForward.getKeyCode()), 36, (int)((float)this.getHeight / 2.0F - 19.0F), -855638017);
         Gui.drawRect(
            4,
            (int)((float)this.getHeight / 2.0F),
            24,
            (int)((float)this.getHeight / 2.0F + 20.0F),
            MC.gameSettings.keyBindLeft.isKeyDown() ? -855638017 : -872415232
         );
         MC.fontRendererObj
            .drawString(Keyboard.getKeyName(MC.gameSettings.keyBindLeft.getKeyCode()), 11, (int)((float)this.getHeight / 2.0F + 6.0F), -855638017);
         Gui.drawRect(
            29,
            (int)((float)this.getHeight / 2.0F),
            49,
            (int)((float)this.getHeight / 2.0F + 20.0F),
            MC.gameSettings.keyBindBack.isKeyDown() ? -855638017 : -872415232
         );
         MC.fontRendererObj
            .drawString(Keyboard.getKeyName(MC.gameSettings.keyBindBack.getKeyCode()), 36, (int)((float)this.getHeight / 2.0F + 6.0F), -855638017);
         Gui.drawRect(
            54,
            (int)((float)this.getHeight / 2.0F),
            74,
            (int)((float)this.getHeight / 2.0F + 20.0F),
            MC.gameSettings.keyBindRight.isKeyDown() ? -855638017 : -872415232
         );
         MC.fontRendererObj
            .drawString(Keyboard.getKeyName(MC.gameSettings.keyBindRight.getKeyCode()), 61, (int)((float)this.getHeight / 2.0F + 6.0F), -855638017);
         Gui.drawRect(
            4,
            (int)((float)this.getHeight / 2.0F + 25.0F),
            37,
            (int)((float)this.getHeight / 2.0F + 45.0F),
            MC.gameSettings.keyBindAttack.isKeyDown() ? -855638017 : -872415232
         );
         MC.fontRendererObj.drawString("LMB", 12, (int)((float)this.getHeight / 2.0F + 31.0F), -855638017);
         Gui.drawRect(
            42,
            (int)((float)this.getHeight / 2.0F + 25.0F),
            74,
            (int)((float)this.getHeight / 2.0F + 45.0F),
            MC.gameSettings.keyBindUseItem.isKeyDown() ? -855638017 : -872415232
         );
         MC.fontRendererObj.drawString("RMB", 49, (int)((float)this.getHeight / 2.0F + 31.0F), -855638017);
      }
   }
}
