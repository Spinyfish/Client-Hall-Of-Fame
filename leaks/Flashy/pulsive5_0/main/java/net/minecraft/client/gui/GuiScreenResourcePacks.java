package net.minecraft.client.gui;

import club.pulsive.api.font.Fonts;
import club.pulsive.api.main.Pulsive;
import club.pulsive.api.ui.config.CustomButton;
import club.pulsive.client.shader.impls.NewBlur;
import club.pulsive.impl.module.impl.visual.HUD;
import club.pulsive.impl.module.impl.visual.Shaders;
import club.pulsive.impl.util.render.ColorUtil;
import club.pulsive.impl.util.render.RenderUtil;
import club.pulsive.impl.util.render.RoundedUtil;
import club.pulsive.impl.util.render.StencilUtil;
import club.pulsive.impl.util.render.secondary.ShaderRound;
import club.pulsive.impl.util.render.shaders.Blur;
import com.google.common.collect.Lists;
import net.minecraft.client.resources.*;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.Sys;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;

public class GuiScreenResourcePacks extends GuiScreen
{
    private static final Logger logger = LogManager.getLogger();
    private final GuiScreen parentScreen;
    private List<ResourcePackListEntry> availableResourcePacks;
    private List<ResourcePackListEntry> selectedResourcePacks;
    private GuiResourcePackAvailable availableResourcePacksList;
    private GuiResourcePackSelected selectedResourcePacksList;
    private boolean changed = false;

    public GuiScreenResourcePacks(GuiScreen parentScreenIn)
    {
        this.parentScreen = parentScreenIn;
    }

    public void initGui()
    {
        this.buttonList.add(new CustomButton(2, 173 + 75 / 2 - 154, this.height - 48, 150, 20, I18n.format("resourcePack.openFolder")));
        this.buttonList.add(new CustomButton(1, 173 + 75 / 2 + 4, this.height - 48, 150, 20, I18n.format("gui.done")));

        if (!this.changed)
        {
            this.availableResourcePacks = Lists.<ResourcePackListEntry>newArrayList();
            this.selectedResourcePacks = Lists.<ResourcePackListEntry>newArrayList();
            ResourcePackRepository resourcepackrepository = this.mc.getResourcePackRepository();
            resourcepackrepository.updateRepositoryEntriesAll();
            List<ResourcePackRepository.Entry> list = Lists.newArrayList(resourcepackrepository.getRepositoryEntriesAll());
            list.removeAll(resourcepackrepository.getRepositoryEntries());

            for (ResourcePackRepository.Entry resourcepackrepository$entry : list)
            {
                this.availableResourcePacks.add(new ResourcePackListEntryFound(this, resourcepackrepository$entry));
            }

            for (ResourcePackRepository.Entry resourcepackrepository$entry1 : Lists.reverse(resourcepackrepository.getRepositoryEntries()))
            {
                this.selectedResourcePacks.add(new ResourcePackListEntryFound(this, resourcepackrepository$entry1));
            }

            this.selectedResourcePacks.add(new ResourcePackListEntryDefault(this));
        }

        this.availableResourcePacksList = new GuiResourcePackAvailable(this.mc, 200, this.height, this.availableResourcePacks);
        this.availableResourcePacksList.setSlotXBoundsFromLeft(420 / 2 - 4 - 200);
        this.availableResourcePacksList.registerScrollButtons(7, 8);
        this.selectedResourcePacksList = new GuiResourcePackSelected(this.mc, 200, this.height, this.selectedResourcePacks);
        this.selectedResourcePacksList.setSlotXBoundsFromLeft(420 / 2 + 4);
        this.selectedResourcePacksList.registerScrollButtons(7, 8);
    }

    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        this.selectedResourcePacksList.handleMouseInput();
        this.availableResourcePacksList.handleMouseInput();
    }

    public boolean hasResourcePackEntry(ResourcePackListEntry p_146961_1_)
    {
        return this.selectedResourcePacks.contains(p_146961_1_);
    }

    public List<ResourcePackListEntry> getListContaining(ResourcePackListEntry p_146962_1_)
    {
        return this.hasResourcePackEntry(p_146962_1_) ? this.selectedResourcePacks : this.availableResourcePacks;
    }

    public List<ResourcePackListEntry> getAvailableResourcePacks()
    {
        return this.availableResourcePacks;
    }

    public List<ResourcePackListEntry> getSelectedResourcePacks()
    {
        return this.selectedResourcePacks;
    }

    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.enabled)
        {
            if (button.id == 2)
            {
                File file1 = this.mc.getResourcePackRepository().getDirResourcepacks();
                String s = file1.getAbsolutePath();

                if (Util.getOSType() == Util.EnumOS.OSX)
                {
                    try
                    {
                        logger.info(s);
                        Runtime.getRuntime().exec(new String[] {"/usr/bin/open", s});
                        return;
                    }
                    catch (IOException ioexception1)
                    {
                        logger.error((String)"Couldn\'t open file", (Throwable)ioexception1);
                    }
                }
                else if (Util.getOSType() == Util.EnumOS.WINDOWS)
                {
                    String s1 = String.format("cmd.exe /C start \"Open file\" \"%s\"", new Object[] {s});

                    try
                    {
                        Runtime.getRuntime().exec(s1);
                        return;
                    }
                    catch (IOException ioexception)
                    {
                        logger.error((String)"Couldn\'t open file", (Throwable)ioexception);
                    }
                }

                boolean flag = false;

                try
                {
                    Class<?> oclass = Class.forName("java.awt.Desktop");
                    Object object = oclass.getMethod("getDesktop", new Class[0]).invoke((Object)null, new Object[0]);
                    oclass.getMethod("browse", new Class[] {URI.class}).invoke(object, new Object[] {file1.toURI()});
                }
                catch (Throwable throwable)
                {
                    logger.error("Couldn\'t open link", throwable);
                    flag = true;
                }

                if (flag)
                {
                    logger.info("Opening via system class!");
                    Sys.openURL("file://" + s);
                }
            }
            else if (button.id == 1)
            {
                if (this.changed)
                {
                    List<ResourcePackRepository.Entry> list = Lists.<ResourcePackRepository.Entry>newArrayList();

                    for (ResourcePackListEntry resourcepacklistentry : this.selectedResourcePacks)
                    {
                        if (resourcepacklistentry instanceof ResourcePackListEntryFound)
                        {
                            list.add(((ResourcePackListEntryFound)resourcepacklistentry).func_148318_i());
                        }
                    }

                    Collections.reverse(list);
                    this.mc.getResourcePackRepository().setRepositories(list);
                    this.mc.gameSettings.resourcePacks.clear();
                    this.mc.gameSettings.incompatibleResourcePacks.clear();

                    for (ResourcePackRepository.Entry resourcepackrepository$entry : list)
                    {
                        this.mc.gameSettings.resourcePacks.add(resourcepackrepository$entry.getResourcePackName());

                        if (resourcepackrepository$entry.func_183027_f() != 1)
                        {
                            this.mc.gameSettings.incompatibleResourcePacks.add(resourcepackrepository$entry.getResourcePackName());
                        }
                    }

                    this.mc.gameSettings.saveOptions();
                    this.mc.refreshResources();
                }

                this.mc.displayGuiScreen(this.parentScreen);
            }
        }
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.availableResourcePacksList.mouseClicked(mouseX, mouseY, mouseButton);
        this.selectedResourcePacksList.mouseClicked(mouseX, mouseY, mouseButton);
    }

    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        super.mouseReleased(mouseX, mouseY, state);
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        // Blur.renderBlur(50);
        Blur.renderBlur(50);


        Fonts.fontforflashytitle.drawCenteredStringWithShadow("Texture Packs", this.width / 2 + this.width / 4, 20, -1);
        Fonts.fontforflashy.drawCenteredStringWithShadow("Looking good Steve!", this.width / 2 + this.width / 4, 30, Color.lightGray.getRGB());


        StencilUtil.initStencilToWrite();
        RoundedUtil.drawRoundedRect(0, 0, 420,
                6000, 3,  new Color(40, 40, 40, 220).getRGB());
        StencilUtil.readStencilBuffer(1);
        Blur.renderBlur(15);
        StencilUtil.uninitStencilBuffer();
        RoundedUtil.drawRoundedRect(0, 0, 420,
                6000, 3,  new Color(40, 40, 40, 220).getRGB());

        this.availableResourcePacksList.drawScreen(mouseX, mouseY, partialTicks);
        this.selectedResourcePacksList.drawScreen(mouseX, mouseY, partialTicks);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public void markChanged()
    {
        this.changed = true;
    }
}