package me.uncodable.srt.impl.modules.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import me.uncodable.srt.Ries;
import me.uncodable.srt.impl.events.api.EventBus;
import me.uncodable.srt.impl.events.api.IListener;
import me.uncodable.srt.impl.modules.api.settings.Setting;
import net.minecraft.client.Minecraft;

public class ModuleManager {
   private final CopyOnWriteArrayList<Module> modules = new CopyOnWriteArrayList<>();
   private final List<IListener> listeners = new ArrayList<>();
   private static final Minecraft MC = Minecraft.getMinecraft();
   private boolean initialized;

   public void finishInitialization() {
      this.sort();
      this.listeners.addAll(this.modules);
      EventBus.setup(this.listeners);
      this.initialized = true;
   }

   public void addModules(Module... suppliedModules) {
      this.modules.addAll(Arrays.asList(suppliedModules));
   }

   public void sort() {
      this.modules
         .sort(
            Comparator.comparing(
               module -> -MC.fontRendererObj
                     .getStringWidth(
                        String.format(
                           "%s%s",
                           module.getInfo().name(),
                           Ries.INSTANCE.getSettingManager().getSetting(module, "INTERNAL_GENERAL_COMBO_BOX", Setting.Type.COMBO_BOX) != null
                              ? " "
                                 + Ries.INSTANCE
                                    .getSettingManager()
                                    .getSetting(module, "INTERNAL_GENERAL_COMBO_BOX", Setting.Type.COMBO_BOX)
                                    .getCurrentCombo()
                              : ""
                        )
                     )
            )
         );
   }

   public ArrayList<Module> getModulesInCategory(Module.Category category) {
      ArrayList<Module> returnModules = new ArrayList<>();
      this.modules.stream().filter(module -> module.getInfo() != null && module.getInfo().category() == category).forEach(returnModules::add);
      return returnModules;
   }

   public Module getModuleByName(String name) {
      for(Module module : this.modules) {
         if (module.getInfo().internalName().equalsIgnoreCase(name)) {
            return module;
         }
      }

      return null;
   }

   public Module getShortestToggled(boolean failIfHidden) {
      for(int i = this.modules.size() - 1; i > 0; --i) {
         Module module = this.modules.get(i);
         if (module.isEnabled()
            && (!failIfHidden || !Ries.INSTANCE.getSettingManager().getSetting(module, "INTERNAL_HIDDEN", Setting.Type.CHECKBOX).isTicked())) {
            return this.modules.get(i);
         }
      }

      return null;
   }

   public CopyOnWriteArrayList<Module> getModules() {
      return this.modules;
   }

   public List<IListener> getListeners() {
      return this.listeners;
   }

   public boolean isInitialized() {
      return this.initialized;
   }

   public void setInitialized(boolean initialized) {
      this.initialized = initialized;
   }
}
