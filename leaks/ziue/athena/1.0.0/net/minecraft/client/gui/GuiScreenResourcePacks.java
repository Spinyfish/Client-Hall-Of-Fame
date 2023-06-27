package net.minecraft.client.gui;

import com.google.common.collect.*;
import net.minecraft.client.resources.*;
import net.minecraft.util.*;
import java.net.*;
import org.lwjgl.*;
import java.util.*;
import java.io.*;
import org.apache.logging.log4j.*;

public class GuiScreenResourcePacks extends GuiScreen
{
    private static final Logger logger;
    private final GuiScreen parentScreen;
    private List<ResourcePackListEntry> availableResourcePacks;
    private List<ResourcePackListEntry> selectedResourcePacks;
    private GuiResourcePackAvailable availableResourcePacksList;
    private GuiResourcePackSelected selectedResourcePacksList;
    private boolean changed;
    
    public GuiScreenResourcePacks(final GuiScreen parentScreenIn) {
        this.changed = false;
        this.parentScreen = parentScreenIn;
    }
    
    @Override
    public void initGui() {
        this.buttonList.add(new GuiOptionButton(2, this.width / 2 - 154, this.height - 48, I18n.format("resourcePack.openFolder", new Object[0])));
        this.buttonList.add(new GuiOptionButton(1, this.width / 2 + 4, this.height - 48, I18n.format("gui.done", new Object[0])));
        if (!this.changed) {
            this.availableResourcePacks = (List<ResourcePackListEntry>)Lists.newArrayList();
            this.selectedResourcePacks = (List<ResourcePackListEntry>)Lists.newArrayList();
            final ResourcePackRepository resourcepackrepository = this.mc.getResourcePackRepository();
            resourcepackrepository.updateRepositoryEntriesAll();
            final List<ResourcePackRepository.Entry> list = (List<ResourcePackRepository.Entry>)Lists.newArrayList((Iterable)resourcepackrepository.getRepositoryEntriesAll());
            list.removeAll(resourcepackrepository.getRepositoryEntries());
            for (final ResourcePackRepository.Entry resourcepackrepository$entry : list) {
                this.availableResourcePacks.add(new ResourcePackListEntryFound(this, resourcepackrepository$entry));
            }
            for (final ResourcePackRepository.Entry resourcepackrepository$entry2 : Lists.reverse((List)resourcepackrepository.getRepositoryEntries())) {
                this.selectedResourcePacks.add(new ResourcePackListEntryFound(this, resourcepackrepository$entry2));
            }
            this.selectedResourcePacks.add(new ResourcePackListEntryDefault(this));
        }
        (this.availableResourcePacksList = new GuiResourcePackAvailable(this.mc, 200, this.height, this.availableResourcePacks)).setSlotXBoundsFromLeft(this.width / 2 - 4 - 200);
        this.availableResourcePacksList.registerScrollButtons(7, 8);
        (this.selectedResourcePacksList = new GuiResourcePackSelected(this.mc, 200, this.height, this.selectedResourcePacks)).setSlotXBoundsFromLeft(this.width / 2 + 4);
        this.selectedResourcePacksList.registerScrollButtons(7, 8);
    }
    
    @Override
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
    
    @Override
    protected void actionPerformed(final GuiButton button) throws IOException {
        if (button.enabled) {
            if (button.id == 2) {
                final File file1 = this.mc.getResourcePackRepository().getDirResourcepacks();
                final String s = file1.getAbsolutePath();
                Label_0135: {
                    if (Util.getOSType() == Util.EnumOS.OSX) {
                        try {
                            GuiScreenResourcePacks.logger.info(s);
                            Runtime.getRuntime().exec(new String[] { "/usr/bin/open", s });
                            return;
                        }
                        catch (IOException ioexception1) {
                            GuiScreenResourcePacks.logger.error("Couldn't open file", (Throwable)ioexception1);
                            break Label_0135;
                        }
                    }
                    if (Util.getOSType() == Util.EnumOS.WINDOWS) {
                        final String s2 = String.format("cmd.exe /C start \"Open file\" \"%s\"", s);
                        try {
                            Runtime.getRuntime().exec(s2);
                            return;
                        }
                        catch (IOException ioexception2) {
                            GuiScreenResourcePacks.logger.error("Couldn't open file", (Throwable)ioexception2);
                        }
                    }
                }
                boolean flag = false;
                try {
                    final Class<?> oclass = Class.forName("java.awt.Desktop");
                    final Object object = oclass.getMethod("getDesktop", (Class<?>[])new Class[0]).invoke(null, new Object[0]);
                    oclass.getMethod("browse", URI.class).invoke(object, file1.toURI());
                }
                catch (Throwable throwable) {
                    GuiScreenResourcePacks.logger.error("Couldn't open link", throwable);
                    flag = true;
                }
                if (flag) {
                    GuiScreenResourcePacks.logger.info("Opening via system class!");
                    Sys.openURL("file://" + s);
                }
            }
            else if (button.id == 1) {
                if (this.changed) {
                    final List<ResourcePackRepository.Entry> list = (List<ResourcePackRepository.Entry>)Lists.newArrayList();
                    for (final ResourcePackListEntry resourcepacklistentry : this.selectedResourcePacks) {
                        if (resourcepacklistentry instanceof ResourcePackListEntryFound) {
                            list.add(((ResourcePackListEntryFound)resourcepacklistentry).func_148318_i());
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
    
    @Override
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.availableResourcePacksList.mouseClicked(mouseX, mouseY, mouseButton);
        this.selectedResourcePacksList.mouseClicked(mouseX, mouseY, mouseButton);
    }
    
    @Override
    protected void mouseReleased(final int mouseX, final int mouseY, final int state) {
        super.mouseReleased(mouseX, mouseY, state);
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.drawBackground(0);
        this.availableResourcePacksList.drawScreen(mouseX, mouseY, partialTicks);
        this.selectedResourcePacksList.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRendererObj, I18n.format("resourcePack.title", new Object[0]), this.width / 2, 16, 16777215);
        this.drawCenteredString(this.fontRendererObj, I18n.format("resourcePack.folderInfo", new Object[0]), this.width / 2 - 77, this.height - 26, 8421504);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    
    public void markChanged() {
        this.changed = true;
    }
    
    static {
        logger = LogManager.getLogger();
    }
}
