package rip.athena.client.gui.clickgui.pages;

import net.minecraft.util.*;
import rip.athena.client.modules.*;
import rip.athena.client.gui.hud.*;
import net.minecraft.client.*;
import rip.athena.client.gui.clickgui.*;
import java.awt.*;
import net.minecraft.client.renderer.*;
import rip.athena.client.*;
import rip.athena.client.modules.impl.other.*;
import rip.athena.client.utils.font.*;
import rip.athena.client.modules.impl.render.*;
import rip.athena.client.gui.framework.*;
import rip.athena.client.utils.input.*;
import net.minecraft.client.gui.*;
import rip.athena.client.config.*;
import rip.athena.client.config.types.*;
import rip.athena.client.gui.framework.components.*;
import java.util.stream.*;
import java.util.*;
import rip.athena.client.gui.clickgui.components.mods.*;

public class ModsPage extends Page
{
    public final int MENU_HEADER_TEXT_COLOR_MOD;
    public static final int MENU_BG_COLOR_MOD;
    public final int MENU_BG_COLOR_MOD_BORDER;
    public static final int MENU_SIDE_BG_COLOR;
    private final ResourceLocation[] MOD_TABS;
    private Category modCategory;
    public Module activeModule;
    private String search;
    private HUDEditor hudEditor;
    
    public ModsPage(final Minecraft mc, final Menu menu, final IngameMenu parent) {
        super(mc, menu, parent);
        this.MENU_HEADER_TEXT_COLOR_MOD = new Color(129, 129, 129, IngameMenu.MENU_ALPHA).getRGB();
        this.MENU_BG_COLOR_MOD_BORDER = new Color(30, 30, 30, IngameMenu.MENU_ALPHA).getRGB();
        this.MOD_TABS = new ResourceLocation[Category.values().length];
        this.modCategory = Category.ALL_MODS;
        this.hudEditor = new HUDEditor(parent.getFeature());
    }
    
    @Override
    public void onInit() {
        for (final Category category : Category.values()) {
            if (!category.isHidden()) {}
        }
    }
    
    @Override
    public void onRender() {
        int y = this.menu.getY() + 59;
        final int height = 32;
        GlStateManager.color(1.0f, 1.0f, 1.0f);
        this.drawVerticalLine(this.menu.getX() + 215, y + height - 30, height + 432, 3, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getSecondColor());
        y += 50;
        for (final Category category : Category.values()) {
            if (!category.isHidden()) {
                y += height + 2 + 10;
            }
        }
        y = this.menu.getY() + this.menu.getHeight() - height;
        this.drawShadowUp(this.menu.getX(), y - 10, 215);
        if (this.modCategory != null) {
            if (Settings.customGuiFont) {
                FontManager.getProductSansRegular(30).drawString((this.activeModule != null) ? this.activeModule.getName().toUpperCase() : this.modCategory.getText(), this.menu.getX() + 255, this.menu.getY() + 20, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getTextColor());
                FontManager.getProductSansRegular(20).drawString("Configure build-in client mods", this.menu.getX() + 255, this.menu.getY() + 35, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getTextColor());
            }
            else {
                this.mc.fontRendererObj.drawString((this.activeModule != null) ? "SETTINGS | " : this.modCategory.getText(), this.menu.getX() + 255, this.menu.getY() + 25, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getTextColor());
            }
            if (this.activeModule != null) {
                final int offset = FontManager.getProductSansRegular(30).width("SETTINGS | ");
                final String text = this.activeModule.getName().toUpperCase().trim();
                if (this.activeModule instanceof Crosshair) {
                    final Crosshair crosshair = (Crosshair)this.activeModule;
                    final int w = 86;
                    final int h = 76;
                    crosshair.drawPicker(this.menu.getX() + 255 + 25, this.menu.getY() + 290, w, h, this.menu.getMouseX(), this.menu.getMouseY());
                }
            }
        }
    }
    
    @Override
    public void onLoad() {
        final int y = 74;
        int x = 255;
        final int height = 32;
        for (final Category category : Category.values()) {
            if (!category.isHidden()) {
                final MenuButton comp = new ModCategoryButton(category, x - 20, y, 35 + FontManager.getNunitoBold(30).width(category.getText()), height) {
                    @Override
                    public void onAction() {
                        for (final MenuComponent component : ModsPage.this.menu.getComponents()) {
                            if (component instanceof ModCategoryButton) {
                                final ModCategoryButton button = (ModCategoryButton)component;
                                button.setActive(component == this);
                            }
                        }
                        ModsPage.this.modCategory = category;
                        ModsPage.this.activeModule = null;
                        ModsPage.this.parent.initPage();
                    }
                };
                if (category == this.modCategory) {
                    comp.setActive(true);
                }
                this.menu.addComponent(comp);
                if (Settings.customGuiFont) {
                    x += FontManager.getProductSansRegular(30).width(category.getText()) + 20;
                }
                else {
                    x += this.mc.fontRendererObj.getStringWidth(category.getText()) + 20;
                }
            }
        }
        if (this.activeModule == null) {
            final MenuTextField searchbar = new SearchTextfield(TextPattern.NONE, this.menu.getWidth() - 31 - 250 - 5, 72, 250, 30) {
                @Override
                public void onAction() {
                    ModsPage.this.search = this.getText();
                    ModsPage.this.initModPage();
                }
                
                @Override
                public void onClick() {
                    this.setText("");
                    ModsPage.this.search = "";
                    ModsPage.this.initModPage();
                }
            };
            searchbar.setText((this.search != null) ? this.search : "");
            this.menu.addComponent(searchbar);
        }
        else {
            final int w = 150;
            final int h = 20;
            final ModsButton enable = new ModsButton(this.activeModule.isToggled() ? "DISABLE" : "ENABLE", 255, this.menu.getHeight() - h - 6) {
                @Override
                public void onAction() {
                    ModsPage.this.activeModule.setEnabled(this.isActive());
                    this.setText(ModsPage.this.activeModule.isToggled() ? "DISABLE" : "ENABLE");
                }
            };
            enable.setActive(this.activeModule.isToggled());
            this.menu.addComponent(enable);
            final MenuModList list = new MenuModList(BindType.class, this.menu.getWidth() - 182 - 160, this.menu.getHeight() - h - 6, 20) {
                @Override
                public void onAction() {
                    ModsPage.this.activeModule.setBindType(BindType.valueOf(this.getValue().toUpperCase()));
                }
            };
            list.setValue(this.activeModule.getBindType().toString());
            this.menu.addComponent(list);
            final MenuModKeybind btn = new MenuModKeybind(this.menu.getWidth() - 182, this.menu.getHeight() - h - 6, w, h) {
                @Override
                public void onAction() {
                    ModsPage.this.activeModule.setKeyBind(this.getBind());
                    this.setX(ModsPage.this.menu.getWidth() - 182);
                }
            };
            btn.setBind(this.activeModule.getKeyBind());
            this.menu.addComponent(btn);
            this.menu.addComponent(new GoBackButton(this.menu.getWidth() - 154, 72) {
                @Override
                public void onAction() {
                    ModsPage.this.activeModule = null;
                    ModsPage.this.parent.initPage();
                }
            });
        }
        final ModScrollPane pane = new ModScrollPane(255, 140, this.menu.getWidth() - 255 - 32, this.menu.getHeight() - 141, false);
        this.menu.addComponent(pane);
        this.menu.addComponent(new ModCategoryButton("EDIT HUD", new ResourceLocation("Athena/gui/menu/edit.png"), 0, this.menu.getHeight() - height - 5, 225, height) {
            @Override
            public void onAction() {
                this.setActive(false);
                ModsPage.this.mc.displayGuiScreen(ModsPage.this.hudEditor);
            }
        });
        if (this.activeModule == null) {
            this.initModPage(pane);
        }
        else {
            pane.setX(226);
            pane.setY(116);
            pane.setWidth(this.menu.getWidth() - 255 - 33);
            pane.setHeight(this.menu.getHeight() - 110 - 52 - 5);
            pane.setFullHeightScroller(true);
            pane.getComponents().clear();
            final List<MenuComponent> toAdd = new ArrayList<MenuComponent>();
            final int xSpacing = 25;
            final int ySpacing = 15;
            final int sliderWidth = pane.getWidth() - xSpacing * 2;
            for (final ConfigEntry configEntry : this.activeModule.getEntries()) {
                GlStateManager.color(1.0f, 1.0f, 1.0f);
                final String key = configEntry.getKey().toUpperCase();
                FeatureText label;
                if (configEntry.hasDescription()) {
                    toAdd.add(label = new FeatureText(key, 0, 0));
                }
                else {
                    toAdd.add(label = new FeatureText(key, configEntry.getDescription(), 0, 0));
                }
                if (configEntry instanceof BooleanEntry) {
                    final BooleanEntry entry = (BooleanEntry)configEntry;
                    final MenuModCheckbox checkbox = new MenuModCheckbox(0, 0, 15, 15) {
                        @Override
                        public void onAction() {
                            entry.setValue(ModsPage.this.activeModule, this.isChecked());
                        }
                    };
                    checkbox.setChecked((boolean)entry.getValue(this.activeModule));
                    toAdd.add(checkbox);
                }
                else if (configEntry instanceof ColorEntry) {
                    final ColorEntry entry2 = (ColorEntry)configEntry;
                    toAdd.add(new MenuModColorPicker(0, 0, 15, 15, ((Color)entry2.getValue(this.activeModule)).getRGB()) {
                        @Override
                        public void onAction() {
                            entry2.setValue(ModsPage.this.activeModule, this.getColor());
                        }
                    });
                }
                else if (configEntry instanceof DoubleEntry) {
                    final DoubleEntry entry3 = (DoubleEntry)configEntry;
                    final FeatureValueText valueText = new FeatureValueText("", 0, 0);
                    toAdd.add(valueText);
                    final MenuModSlider slider = new MenuModSlider((double)entry3.getValue(this.activeModule), entry3.getMin(), entry3.getMax(), 2, 0, 0, sliderWidth, 15) {
                        @Override
                        public void onAction() {
                            label.setText((entry3.getKey() + " | ").toUpperCase());
                            entry3.setValue(ModsPage.this.activeModule, (double)this.getValue());
                            valueText.setText(this.getValue() + "");
                        }
                    };
                    slider.onAction();
                    toAdd.add(slider);
                }
                else if (configEntry instanceof FloatEntry) {
                    final FloatEntry entry4 = (FloatEntry)configEntry;
                    final FeatureValueText valueText = new FeatureValueText("", 0, 0);
                    toAdd.add(valueText);
                    final MenuModSlider slider = new MenuModSlider((float)entry4.getValue(this.activeModule), entry4.getMin(), entry4.getMax(), 2, 0, 0, sliderWidth, 15) {
                        @Override
                        public void onAction() {
                            label.setText((entry4.getKey() + " | ").toUpperCase());
                            entry4.setValue(ModsPage.this.activeModule, this.getValue());
                            valueText.setText(this.getValue() + "");
                        }
                    };
                    slider.onAction();
                    toAdd.add(slider);
                }
                else if (configEntry instanceof IntEntry) {
                    final IntEntry entry5 = (IntEntry)configEntry;
                    final FeatureValueText valueText = new FeatureValueText("", 0, 0);
                    toAdd.add(valueText);
                    if (entry5.isKeyBind()) {
                        final MenuModKeybind bind = new MenuModKeybind(0, 0, 175, 15) {
                            @Override
                            public void onAction() {
                                entry5.setValue(ModsPage.this.activeModule, this.getBind());
                            }
                        };
                        bind.setBind((int)entry5.getValue(this.activeModule));
                        toAdd.add(bind);
                    }
                    else {
                        final MenuModSlider slider = new MenuModSlider((int)entry5.getValue(this.activeModule), entry5.getMin(), entry5.getMax(), 0, 0, sliderWidth, 15) {
                            @Override
                            public void onAction() {
                                label.setText((entry5.getKey() + " | ").toUpperCase());
                                entry5.setValue(ModsPage.this.activeModule, this.getIntValue());
                                valueText.setText(this.getIntValue() + "");
                            }
                        };
                        slider.onAction();
                        toAdd.add(slider);
                    }
                }
                else if (configEntry instanceof ListEntry) {
                    final ListEntry entry6 = (ListEntry)configEntry;
                    final MenuModList list2 = new MenuModList(entry6.getValues(), 0, 0, 15) {
                        @Override
                        public void onAction() {
                            entry6.setValue(ModsPage.this.activeModule, this.getValue());
                        }
                    };
                    list2.setValue((String)configEntry.getValue(this.activeModule));
                    toAdd.add(list2);
                }
                else {
                    if (!(configEntry instanceof StringEntry)) {
                        continue;
                    }
                    final StringEntry entry7 = (StringEntry)configEntry;
                    final ModTextbox box = new ModTextbox(TextPattern.NONE, 0, 0, 175, 15) {
                        @Override
                        public void onAction() {
                            entry7.setValue(ModsPage.this.activeModule, this.getText());
                        }
                    };
                    box.setText((String)configEntry.getValue(this.activeModule));
                    toAdd.add(box);
                }
            }
            int xPos;
            final int defaultX = xPos = 25;
            int yPos = 25;
            boolean isText = false;
            MenuComponent last = null;
            for (final MenuComponent component : toAdd) {
                if (component instanceof FeatureValueText) {
                    if (last != null) {
                        component.setX(xPos);
                        component.setY(yPos);
                    }
                }
                else if (component instanceof FeatureText) {
                    component.setX(xPos);
                    component.setY(yPos);
                    xPos += component.getWidth();
                    isText = true;
                }
                else {
                    xPos = defaultX;
                    if (isText) {
                        if (component instanceof MenuModSlider) {
                            yPos += ySpacing;
                            component.setX(xPos);
                            component.setY(yPos);
                        }
                        else {
                            if (component instanceof MenuModList) {
                                component.setX(pane.getWidth() - component.getWidth() - xSpacing * 3 + 12);
                            }
                            else {
                                component.setX(pane.getWidth() - component.getWidth() - xSpacing);
                            }
                            component.setY(yPos);
                        }
                        isText = false;
                    }
                    else {
                        component.setX(xPos);
                        component.setY(yPos);
                    }
                    yPos += ySpacing + component.getHeight();
                }
                pane.addComponent(component);
                last = component;
            }
        }
    }
    
    @Override
    public void onUnload() {
        this.activeModule = null;
        this.parent.initPage();
    }
    
    @Override
    public void onOpen() {
        this.updateStates();
    }
    
    @Override
    public void onClose() {
    }
    
    private void updateStates() {
        for (final MenuComponent component : this.menu.getComponents()) {
            if (component instanceof ModsButton) {
                final ModsButton button = (ModsButton)component;
                button.setActive(this.activeModule.isToggled());
                button.onAction();
                break;
            }
        }
    }
    
    private void initModPage() {
        for (final MenuComponent component : this.menu.getComponents()) {
            if (component instanceof ModScrollPane) {
                this.initModPage((ModScrollPane)component);
            }
        }
    }
    
    private void initModPage(final ModScrollPane pane) {
        pane.getComponents().clear();
        for (final MenuComponent c : this.menu.getComponents()) {
            if (c instanceof ModsButton || c instanceof MenuModList) {
                this.menu.getComponents().remove(c);
            }
        }
        int x = 0;
        int y = 5;
        final int width = 170;
        final int height = 150;
        final int spacing = 16;
        final List<Module> modules = Athena.INSTANCE.getModuleManager().getModules().stream().filter(entry -> (this.search == null || this.search.isEmpty() || entry.getName().toLowerCase().contains(this.search.toLowerCase())) && (entry.getCategory() == this.modCategory || this.modCategory == Category.ALL_MODS) && !entry.getCategory().isHidden()).collect((Collector<? super Object, ?, List<Module>>)Collectors.toList());
        modules.sort(Comparator.comparing(module -> module.getName().toLowerCase()));
        for (final Module module2 : modules) {
            pane.addComponent(new ModuleBox(module2, x, y, width, height) {
                @Override
                public void onOpenSettings() {
                    ModsPage.this.activeModule = this.module;
                    ModsPage.this.parent.initPage();
                }
            });
            x += width + spacing;
            if (x + width >= pane.getWidth()) {
                x = 0;
                y += height + spacing;
            }
        }
    }
    
    static {
        MENU_BG_COLOR_MOD = new Color(30, 30, 30, IngameMenu.MENU_ALPHA).getRGB();
        MENU_SIDE_BG_COLOR = new Color(30, 30, 30, IngameMenu.MENU_ALPHA).getRGB();
    }
}
