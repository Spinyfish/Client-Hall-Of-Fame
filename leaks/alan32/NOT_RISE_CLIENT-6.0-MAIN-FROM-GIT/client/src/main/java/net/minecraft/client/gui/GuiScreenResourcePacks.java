package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import net.minecraft.client.resources.*;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.Sys;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;

public class GuiScreenResourcePacks extends GuiScreen {
    private static final Logger logger = LogManager.getLogger();
    private final GuiScreen parentScreen;
    private List<ResourcePackListEntry> availableResourcePacks;
    private List<ResourcePackListEntry> selectedResourcePacks;

    /**
     * List component that contains the available resource packs
     */
    private GuiResourcePackAvailable availableResourcePacksList;

    /**
     * List component that contains the selected resource packs
     */
    private GuiResourcePackSelected selectedResourcePacksList;
    private boolean changed = false;

    public GuiScreenResourcePacks(final GuiScreen parentScreenIn) {
        this.parentScreen = parentScreenIn;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui() {
        this.buttonList.add(new GuiOptionButton(2, this.width / 2 - 154, this.height - 48, I18n.format("resourcePack.openFolder")));
        this.buttonList.add(new GuiOptionButton(1, this.width / 2 + 4, this.height - 48, I18n.format("gui.done")));

        if (!this.changed) {
            this.availableResourcePacks = Lists.newArrayList();
            this.selectedResourcePacks = Lists.newArrayList();
            final ResourcePackRepository resourcepackrepository = this.mc.getResourcePackRepository();
            resourcepackrepository.updateRepositoryEntriesAll();
            final List<ResourcePackRepository.Entry> list = Lists.newArrayList(resourcepackrepository.getRepositoryEntriesAll());
            list.removeAll(resourcepackrepository.getRepositoryEntries());

            for (final ResourcePackRepository.Entry resourcepackrepository$entry : list) {
                this.availableResourcePacks.add(new ResourcePackListEntryFound(this, resourcepackrepository$entry));
            }

            for (final ResourcePackRepository.Entry resourcepackrepository$entry1 : Lists.reverse(resourcepackrepository.getRepositoryEntries())) {
                this.selectedResourcePacks.add(new ResourcePackListEntryFound(this, resourcepackrepository$entry1));
            }

            this.selectedResourcePacks.add(new ResourcePackListEntryDefault(this));
        }

        this.availableResourcePacksList = new GuiResourcePackAvailable(this.mc, 200, this.height, this.availableResourcePacks);
        this.availableResourcePacksList.setSlotXBoundsFromLeft(this.width / 2 - 4 - 200);
        this.availableResourcePacksList.registerScrollButtons(7, 8);
        this.selectedResourcePacksList = new GuiResourcePackSelected(this.mc, 200, this.height, this.selectedResourcePacks);
        this.selectedResourcePacksList.setSlotXBoundsFromLeft(this.width / 2 + 4);
        this.selectedResourcePacksList.registerScrollButtons(7, 8);
    }

    /**
     * Handles mouse input.
     */
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.selectedResourcePacksList.handleMouseInput();
        this.availableResourcePacksList.handleMouseInput();
    }

    public boolean hasResourcePackEntry(final ResourcePackListEntry p_146961_1_) {
        return this.selectedResourcePacks.contains(p_146961_1_);
    }

    public List<ResourcePackListEntry> getListContaining(final ResourcePackListEntry p_146962_1_) {
        return this.hasResourcePackEntry(p_146962_1_) ? this.selectedResourcePacks : this.availableResourcePacks;
    }

    public List<ResourcePackListEntry> getAvailableResourcePacks() {
        return this.availableResourcePacks;
    }

    public List<ResourcePackListEntry> getSelectedResourcePacks() {
        return this.selectedResourcePacks;
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(final GuiButton button) throws IOException {
        if (button.enabled) {
            if (button.id == 2) {
                final File file1 = this.mc.getResourcePackRepository().getDirResourcepacks();
                final String s = file1.getAbsolutePath();

                if (Util.getOSType() == Util.EnumOS.OSX) {
                    try {
                        logger.info(s);
                        Runtime.getRuntime().exec(new String[]{"/usr/bin/open", s});
                        return;
                    } catch (final IOException ioexception1) {
                        logger.error("Couldn't open file", ioexception1);
                    }
                } else if (Util.getOSType() == Util.EnumOS.WINDOWS) {
                    final String s1 = String.format("cmd.exe /C start \"Open file\" \"%s\"", s);

                    try {
                        Runtime.getRuntime().exec(s1);
                        return;
                    } catch (final IOException ioexception) {
                        logger.error("Couldn't open file", ioexception);
                    }
                }

                boolean flag = false;

                try {
                    final Class<?> oclass = Class.forName("java.awt.Desktop");
                    final Object object = oclass.getMethod("getDesktop", new Class[0]).invoke(null);
                    oclass.getMethod("browse", new Class[]{URI.class}).invoke(object, file1.toURI());
                } catch (final Throwable throwable) {
                    logger.error("Couldn't open link", throwable);
                    flag = true;
                }

                if (flag) {
                    logger.info("Opening via system class!");
                    Sys.openURL("file://" + s);
                }
            } else if (button.id == 1) {
                if (this.changed) {
                    final List<ResourcePackRepository.Entry> list = Lists.newArrayList();

                    for (final ResourcePackListEntry resourcepacklistentry : this.selectedResourcePacks) {
                        if (resourcepacklistentry instanceof ResourcePackListEntryFound) {
                            list.add(((ResourcePackListEntryFound) resourcepacklistentry).func_148318_i());
                        }
                    }

                    Collections.reverse(list);
                    this.mc.getResourcePackRepository().setRepositories(list);
                    this.mc.gameSettings.resourcePacks.clear();
                    this.mc.gameSettings.field_183018_l.clear();


                    for (final ResourcePackRepository.Entry resourcepackrepository$entry : list) {
                        this.mc.gameSettings.resourcePacks.add(resourcepackrepository$entry.getResourcePackName());

                        if (resourcepackrepository$entry.func_183027_f() != 1) {
                            this.mc.gameSettings.field_183018_l.add(resourcepackrepository$entry.getResourcePackName());
                        }
                    }

                    this.mc.gameSettings.saveOptions();

                    this.mc.refreshResources();
                }

                this.mc.displayGuiScreen(this.parentScreen);
            }
        }
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.availableResourcePacksList.mouseClicked(mouseX, mouseY, mouseButton);
        this.selectedResourcePacksList.mouseClicked(mouseX, mouseY, mouseButton);
    }

    /**
     * Called when a mouse button is released.  Args : mouseX, mouseY, releaseButton
     */
    protected void mouseReleased(final int mouseX, final int mouseY, final int state) {
        super.mouseReleased(mouseX, mouseY, state);
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.drawBackground(0);
        this.availableResourcePacksList.drawScreen(mouseX, mouseY, partialTicks);
        this.selectedResourcePacksList.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRendererObj, I18n.format("resourcePack.title"), this.width / 2, 16, 16777215);
        this.drawCenteredString(this.fontRendererObj, I18n.format("resourcePack.folderInfo"), this.width / 2 - 77, this.height - 26, 8421504);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    /**
     * Marks the selected resource packs list as changed to trigger a resource reload when the screen is closed
     */
    public void markChanged() {
        this.changed = true;
    }
}
