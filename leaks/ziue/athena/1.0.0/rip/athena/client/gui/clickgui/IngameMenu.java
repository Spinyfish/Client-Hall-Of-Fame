package rip.athena.client.gui.clickgui;

import rip.athena.client.gui.framework.draw.*;
import rip.athena.client.modules.*;
import rip.athena.client.gui.framework.*;
import net.minecraft.util.*;
import rip.athena.client.gui.clickgui.components.mods.*;
import rip.athena.client.modules.impl.other.*;
import rip.athena.client.utils.font.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.*;
import rip.athena.client.utils.render.*;
import rip.athena.client.*;
import rip.athena.client.gui.framework.components.*;
import java.util.*;
import rip.athena.client.gui.clickgui.pages.*;
import rip.athena.client.config.save.*;
import java.awt.*;

public class IngameMenu extends MinecraftMenuImpl implements DrawImpl
{
    public static int MENU_ALPHA;
    public static int MENU_TOP_BG_COLOR;
    public static int MENU_PANE_BG_COLOR;
    public static int MENU_HEADER_TEXT_COLOR;
    public static int MENU_LINE_COLOR;
    public static PageManager pageManager;
    public static Category category;
    private static boolean initd;
    private static int savedWidth;
    private static int savedHeight;
    
    public IngameMenu(final Module feature, final Menu menu) {
        super(feature, menu);
        IngameMenu.pageManager = new PageManager(this, menu);
    }
    
    @Override
    public void initGui() {
        if (IngameMenu.initd) {
            this.menu.getComponents().clear();
            IngameMenu.initd = false;
        }
        if (!IngameMenu.initd) {
            for (final IPage page : IngameMenu.pageManager.getPages().values()) {
                page.onInit();
            }
            this.menu.addComponent(new MenuDraggable(0, 0, this.menu.getWidth(), 58));
            int x = 175;
            int y = 119;
            final int height = 32;
            for (final Category category : Category.values()) {
                final MenuButton comp = new CategoryButton(category, new ResourceLocation(category.getIcon()), 0, y, 205, height) {
                    @Override
                    public void onAction() {
                        if (IngameMenu.category != null) {
                            IngameMenu.pageManager.getPage(IngameMenu.category).onUnload();
                        }
                        IngameMenu.category = category;
                        for (final MenuComponent component : IngameMenu.this.menu.getComponents()) {
                            if (component instanceof CategoryButton) {
                                final CategoryButton button = (CategoryButton)component;
                                button.setActive(component == this);
                            }
                        }
                        IngameMenu.this.initPage();
                    }
                };
                if (category == IngameMenu.category) {
                    comp.setActive(true);
                }
                this.menu.addComponent(comp);
                if (Settings.customGuiFont) {
                    x += FontManager.getProductSansRegular(30).width(category.getName()) + 20;
                }
                else {
                    x += this.mc.fontRendererObj.getStringWidth(category.getName()) + 20;
                }
                y += 40;
            }
            this.initPage();
            IngameMenu.initd = true;
        }
        if (IngameMenu.category != null) {
            IngameMenu.pageManager.getPage(IngameMenu.category).onOpen();
        }
        super.initGui();
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        if (IngameMenu.savedWidth != this.mc.displayWidth || IngameMenu.savedHeight != this.mc.displayHeight) {
            IngameMenu.savedWidth = this.mc.displayWidth;
            IngameMenu.savedHeight = this.mc.displayHeight;
            final ScaledResolution sr = new ScaledResolution(this.mc);
            this.menu.setX(sr.getScaledWidth() / 2);
            this.menu.setY(sr.getScaledHeight() / 2);
        }
        GlStateManager.pushMatrix();
        final float value = this.guiScale / new ScaledResolution(this.mc).getScaleFactor();
        GlStateManager.scale(value, value, value);
        this.drawShadowDown(this.menu.getX(), this.menu.getY() + 58, this.menu.getWidth());
        RoundedUtils.drawGradientRound((float)(this.menu.getX() - 1), (float)(this.menu.getY() - 1), (float)(this.menu.getWidth() + 2), (float)(this.menu.getHeight() + 2), 10.0f, ColorUtil.getClientColor(0, 255), ColorUtil.getClientColor(90, 255), ColorUtil.getClientColor(180, 255), ColorUtil.getClientColor(270, 255));
        RoundedUtils.drawRoundedRect((float)this.menu.getX(), (float)this.menu.getY(), (float)(this.menu.getX() + this.menu.getWidth()), (float)(this.menu.getY() + this.menu.getHeight()), 16.0f, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getFirstColor());
        FontManager.getProductSansBold(60).drawString(Athena.INSTANCE.getClientName(), this.menu.getX() + 60, this.menu.getY() + 17, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getTextColor());
        this.drawShadowDown(this.menu.getX(), this.menu.getY() + 58, this.menu.getWidth());
        if (IngameMenu.category != null) {
            IngameMenu.pageManager.getPage(IngameMenu.category).onRender();
        }
        GlStateManager.popMatrix();
        super.drawScreen(mouseX, mouseY, partialTicks);
        GlStateManager.pushMatrix();
        GlStateManager.scale(value, value, value);
        for (final MenuComponent component : this.menu.getComponents()) {
            if (component instanceof MenuScrollPane) {
                final MenuScrollPane scrollpane = (MenuScrollPane)component;
                scrollpane.drawExtras();
            }
        }
        GlStateManager.popMatrix();
        GlStateManager.color(1.0f, 1.0f, 1.0f);
    }
    
    public void initPage() {
        final List<MenuComponent> remove = new ArrayList<MenuComponent>();
        for (final MenuComponent component : this.menu.getComponents()) {
            if (!(component instanceof CategoryButton)) {
                if (component instanceof MenuDraggable) {
                    continue;
                }
                remove.add(component);
            }
        }
        this.menu.getComponents().removeAll(remove);
        IngameMenu.pageManager.getPage(IngameMenu.category).onLoad();
    }
    
    public void openSettings(final Module parent) {
        if (IngameMenu.category != null) {
            IngameMenu.pageManager.getPage(IngameMenu.category).onUnload();
        }
        IngameMenu.category = Category.MODS;
        IngameMenu.pageManager.getPage(ModsPage.class, Category.MODS).activeModule = parent;
        this.initPage();
    }
    
    @Override
    public void onGuiClosed() {
        if (IngameMenu.category != null) {
            IngameMenu.pageManager.getPage(IngameMenu.category).onClose();
        }
        super.onGuiClosed();
        final Config config;
        new Thread(() -> {
            config = Athena.INSTANCE.getConfigManager().getLoadedConfig();
            if (config != null) {
                config.save();
            }
        }).start();
    }
    
    static {
        IngameMenu.MENU_ALPHA = 255;
        IngameMenu.MENU_TOP_BG_COLOR = new Color(30, 30, 30, IngameMenu.MENU_ALPHA).getRGB();
        IngameMenu.MENU_PANE_BG_COLOR = new Color(35, 35, 35, IngameMenu.MENU_ALPHA).getRGB();
        IngameMenu.MENU_HEADER_TEXT_COLOR = new Color(255, 255, 255, IngameMenu.MENU_ALPHA).getRGB();
        IngameMenu.MENU_LINE_COLOR = new Color(25, 25, 28, IngameMenu.MENU_ALPHA).getRGB();
        IngameMenu.category = Category.MODS;
        IngameMenu.savedWidth = -1;
        IngameMenu.savedHeight = -1;
    }
}
