package net.minecraft.client.gui;

import intent.AquaDev.aqua.Aqua;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.SaveFormatComparator;
import net.minecraft.world.storage.WorldInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

public class GuiSelectWorld extends GuiScreen implements GuiYesNoCallback {
   private static final Logger logger = LogManager.getLogger();
   private final DateFormat field_146633_h = new SimpleDateFormat();
   protected GuiScreen parentScreen;
   protected String screenTitle = "Select world";
   private boolean field_146634_i;
   private int selectedIndex;
   private java.util.List<SaveFormatComparator> field_146639_s;
   private GuiSelectWorld.List availableWorlds;
   private String field_146637_u;
   private String field_146636_v;
   private String[] field_146635_w = new String[4];
   private boolean confirmingDelete;
   private GuiButton deleteButton;
   private GuiButton selectButton;
   private GuiButton renameButton;
   private GuiButton recreateButton;

   public GuiSelectWorld(GuiScreen parentScreenIn) {
      this.parentScreen = parentScreenIn;
   }

   @Override
   public void initGui() {
      Aqua.moduleManager.getModuleByName("Arraylist").setState(true);
      Aqua.moduleManager.getModuleByName("HUD").setState(true);
      Aqua.moduleManager.getModuleByName("Blur").setState(true);
      Aqua.moduleManager.getModuleByName("Shadow").setState(true);
      Aqua.moduleManager.getModuleByName("Disabler").setState(false);
      Aqua.INSTANCE.lastConnection = System.currentTimeMillis();
      this.screenTitle = I18n.format("selectWorld.title");

      try {
         this.loadLevelList();
      } catch (AnvilConverterException var2) {
         logger.error("Couldn't load level list", (Throwable)var2);
         this.mc.displayGuiScreen(new GuiErrorScreen("Unable to load worlds", var2.getMessage()));
         return;
      }

      this.field_146637_u = I18n.format("selectWorld.world");
      this.field_146636_v = I18n.format("selectWorld.conversion");
      this.field_146635_w[WorldSettings.GameType.SURVIVAL.getID()] = I18n.format("gameMode.survival");
      this.field_146635_w[WorldSettings.GameType.CREATIVE.getID()] = I18n.format("gameMode.creative");
      this.field_146635_w[WorldSettings.GameType.ADVENTURE.getID()] = I18n.format("gameMode.adventure");
      this.field_146635_w[WorldSettings.GameType.SPECTATOR.getID()] = I18n.format("gameMode.spectator");
      this.availableWorlds = new GuiSelectWorld.List(this.mc);
      this.availableWorlds.registerScrollButtons(4, 5);
      this.addWorldSelectionButtons();
   }

   @Override
   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      this.availableWorlds.handleMouseInput();
   }

   private void loadLevelList() throws AnvilConverterException {
      ISaveFormat isaveformat = this.mc.getSaveLoader();
      this.field_146639_s = isaveformat.getSaveList();
      Collections.sort(this.field_146639_s);
      this.selectedIndex = -1;
   }

   protected String func_146621_a(int p_146621_1_) {
      return this.field_146639_s.get(p_146621_1_).getFileName();
   }

   protected String func_146614_d(int p_146614_1_) {
      String s = this.field_146639_s.get(p_146614_1_).getDisplayName();
      if (StringUtils.isEmpty(s)) {
         s = I18n.format("selectWorld.world") + " " + (p_146614_1_ + 1);
      }

      return s;
   }

   public void addWorldSelectionButtons() {
      this.buttonList.add(this.selectButton = new GuiButton(1, width / 2 - 154, height - 52, 150, 20, I18n.format("selectWorld.select")));
      this.buttonList.add(new GuiButton(3, width / 2 + 4, height - 52, 150, 20, I18n.format("selectWorld.create")));
      this.buttonList.add(this.renameButton = new GuiButton(6, width / 2 - 154, height - 28, 72, 20, I18n.format("selectWorld.rename")));
      this.buttonList.add(this.deleteButton = new GuiButton(2, width / 2 - 76, height - 28, 72, 20, I18n.format("selectWorld.delete")));
      this.buttonList.add(this.recreateButton = new GuiButton(7, width / 2 + 4, height - 28, 72, 20, I18n.format("selectWorld.recreate")));
      this.buttonList.add(new GuiButton(0, width / 2 + 82, height - 28, 72, 20, I18n.format("gui.cancel")));
      this.selectButton.enabled = false;
      this.deleteButton.enabled = false;
      this.renameButton.enabled = false;
      this.recreateButton.enabled = false;
   }

   @Override
   protected void actionPerformed(GuiButton button) throws IOException {
      Aqua.moduleManager.getModuleByName("Disabler").setState(false);
      if (button.enabled) {
         if (button.id == 2) {
            String s = this.func_146614_d(this.selectedIndex);
            if (s != null) {
               this.confirmingDelete = true;
               GuiYesNo guiyesno = makeDeleteWorldYesNo(this, s, this.selectedIndex);
               this.mc.displayGuiScreen(guiyesno);
            }
         } else if (button.id == 1) {
            this.func_146615_e(this.selectedIndex);
         } else if (button.id == 3) {
            this.mc.displayGuiScreen(new GuiCreateWorld(this));
         } else if (button.id == 6) {
            this.mc.displayGuiScreen(new GuiRenameWorld(this, this.func_146621_a(this.selectedIndex)));
         } else if (button.id == 0) {
            this.mc.displayGuiScreen(this.parentScreen);
         } else if (button.id == 7) {
            GuiCreateWorld guicreateworld = new GuiCreateWorld(this);
            ISaveHandler isavehandler = this.mc.getSaveLoader().getSaveLoader(this.func_146621_a(this.selectedIndex), false);
            WorldInfo worldinfo = isavehandler.loadWorldInfo();
            isavehandler.flush();
            guicreateworld.recreateFromExistingWorld(worldinfo);
            this.mc.displayGuiScreen(guicreateworld);
         } else {
            this.availableWorlds.actionPerformed(button);
         }
      }
   }

   public void func_146615_e(int p_146615_1_) {
      this.mc.displayGuiScreen((GuiScreen)null);
      if (!this.field_146634_i) {
         this.field_146634_i = true;
         String s = this.func_146621_a(p_146615_1_);
         if (s == null) {
            s = "World" + p_146615_1_;
         }

         String s1 = this.func_146614_d(p_146615_1_);
         if (s1 == null) {
            s1 = "World" + p_146615_1_;
         }

         if (this.mc.getSaveLoader().canLoadWorld(s)) {
            this.mc.launchIntegratedServer(s, s1, (WorldSettings)null);
         }
      }
   }

   @Override
   public void confirmClicked(boolean result, int id) {
      if (this.confirmingDelete) {
         this.confirmingDelete = false;
         if (result) {
            ISaveFormat isaveformat = this.mc.getSaveLoader();
            isaveformat.flushCache();
            isaveformat.deleteWorldDirectory(this.func_146621_a(id));

            try {
               this.loadLevelList();
            } catch (AnvilConverterException var5) {
               logger.error("Couldn't load level list", (Throwable)var5);
            }
         }

         this.mc.displayGuiScreen(this);
      }
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      if (!GL11.glGetString(7937).contains("AMD") && !GL11.glGetString(7937).contains("RX ")) {
         Aqua.INSTANCE.shaderBackgroundMM.renderShader();
      } else {
         this.drawDefaultBackground();
      }

      Aqua.moduleManager.getModuleByName("Disabler").setState(false);
      this.availableWorlds.drawScreen(mouseX, mouseY, partialTicks);
      Aqua.INSTANCE.comfortaa3.drawCenteredString(this.screenTitle, (float)width / 2.0F, 20.0F, 16777215);
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   public static GuiYesNo makeDeleteWorldYesNo(GuiYesNoCallback selectWorld, String name, int id) {
      String s = I18n.format("selectWorld.deleteQuestion");
      String s1 = "'" + name + "' " + I18n.format("selectWorld.deleteWarning");
      String s2 = I18n.format("selectWorld.deleteButton");
      String s3 = I18n.format("gui.cancel");
      return new GuiYesNo(selectWorld, s, s1, s2, s3, id);
   }

   class List extends GuiSlot {
      public List(Minecraft mcIn) {
         super(mcIn, GuiSelectWorld.width, GuiSelectWorld.height, 32, GuiSelectWorld.height - 64, 36);
      }

      @Override
      protected int getSize() {
         return GuiSelectWorld.this.field_146639_s.size();
      }

      @Override
      protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
         GuiSelectWorld.this.selectedIndex = slotIndex;
         boolean flag = GuiSelectWorld.this.selectedIndex >= 0 && GuiSelectWorld.this.selectedIndex < this.getSize();
         GuiSelectWorld.this.selectButton.enabled = flag;
         GuiSelectWorld.this.deleteButton.enabled = flag;
         GuiSelectWorld.this.renameButton.enabled = flag;
         GuiSelectWorld.this.recreateButton.enabled = flag;
         if (isDoubleClick && flag) {
            GuiSelectWorld.this.func_146615_e(slotIndex);
         }
      }

      @Override
      protected boolean isSelected(int slotIndex) {
         return slotIndex == GuiSelectWorld.this.selectedIndex;
      }

      @Override
      protected int getContentHeight() {
         return GuiSelectWorld.this.field_146639_s.size() * 36;
      }

      @Override
      protected void drawBackground() {
         if (!GL11.glGetString(7937).contains("AMD") && !GL11.glGetString(7937).contains("RX ")) {
            Aqua.INSTANCE.shaderBackgroundMM.renderShader();
         } else {
            GuiSelectWorld.this.drawDefaultBackground();
         }
      }

      @Override
      protected void drawSlot(int entryID, int p_180791_2_, int p_180791_3_, int p_180791_4_, int mouseXIn, int mouseYIn) {
         SaveFormatComparator saveformatcomparator = GuiSelectWorld.this.field_146639_s.get(entryID);
         String s = saveformatcomparator.getDisplayName();
         if (StringUtils.isEmpty(s)) {
            s = GuiSelectWorld.this.field_146637_u + " " + (entryID + 1);
         }

         String s1 = saveformatcomparator.getFileName();
         s1 = s1 + " (" + GuiSelectWorld.this.field_146633_h.format(new Date(saveformatcomparator.getLastTimePlayed()));
         s1 = s1 + ")";
         String s2 = "";
         if (saveformatcomparator.requiresConversion()) {
            s2 = GuiSelectWorld.this.field_146636_v + " " + s2;
         } else {
            s2 = GuiSelectWorld.this.field_146635_w[saveformatcomparator.getEnumGameType().getID()];
            if (saveformatcomparator.isHardcoreModeEnabled()) {
               s2 = EnumChatFormatting.DARK_RED + I18n.format("gameMode.hardcore") + EnumChatFormatting.RESET;
            }

            if (saveformatcomparator.getCheatsEnabled()) {
               s2 = s2 + ", " + I18n.format("selectWorld.cheats");
            }
         }

         Aqua.INSTANCE.comfortaa3.drawString(s, (float)(p_180791_2_ + 2), (float)(p_180791_3_ + 1), 16777215);
         Aqua.INSTANCE.comfortaa3.drawString(s1, (float)(p_180791_2_ + 2), (float)(p_180791_3_ + 12), 8421504);
         Aqua.INSTANCE.comfortaa3.drawString(s2, (float)(p_180791_2_ + 2), (float)(p_180791_3_ + 12 + 10), 8421504);
      }
   }
}
