package rip.athena.client.gui.clickgui.pages;

import rip.athena.client.gui.clickgui.components.macros.*;
import rip.athena.client.gui.clickgui.components.cosmetics.*;
import net.minecraft.client.*;
import rip.athena.client.gui.clickgui.*;
import rip.athena.client.gui.clickgui.pages.cosmetics.*;
import net.minecraft.client.gui.*;
import rip.athena.client.gui.framework.*;
import java.util.*;
import rip.athena.client.gui.framework.draw.*;
import rip.athena.client.gui.clickgui.components.mods.*;
import rip.athena.client.utils.font.*;

public class CosmeticsPage extends Page
{
    private CosmeticType cosmeticType;
    private CosmeticGenericButton capes;
    private CosmeticGenericButton bandanas;
    private CosmeticGenericButton emotes;
    private CosmeticGenericButton flags;
    private MenuModKeybind emoteWheelBind;
    private CosmeticBindButton bindButton1;
    private CosmeticBindButton bindButton2;
    private CosmeticBindButton bindButton3;
    private CosmeticBindButton bindButton4;
    private CosmeticBindButton lastButton;
    private CosmeticList capeLocation;
    private CosmeticList bandanaLocation;
    private MacroButton resetButton;
    private CosmeticRainbowButton openCrateButton;
    private CosmeticGenericButton resetCape;
    private CosmeticToggleButton capeEnabled;
    private CosmeticUserPreview userPreview;
    private ModScrollPane scrollpane;
    
    public CosmeticsPage(final Minecraft mc, final Menu menu, final IngameMenu parent) {
        super(mc, menu, parent);
        this.cosmeticType = CosmeticType.CAPES;
    }
    
    @Override
    public void onInit() {
        int x = 15;
        int y = 109;
        final int typeWidth = 125;
        final int typeHeight = 20;
        int width = 300;
        final int compWidth = width - 6 - 40;
        this.capes = new CosmeticGenericButton(CosmeticType.CAPES.toString(), x, y, typeWidth, typeHeight, false) {
            @Override
            public void onAction() {
                this.setActive(false);
                CosmeticsPage.this.cosmeticType = CosmeticType.CAPES;
                CosmeticsPage.this.populateScrollPane();
            }
        };
        this.bandanas = new CosmeticGenericButton(CosmeticType.BANDANAS.toString(), x + typeWidth + 20, y, typeWidth, typeHeight, false) {
            @Override
            public void onAction() {
                this.setActive(false);
                CosmeticsPage.this.cosmeticType = CosmeticType.BANDANAS;
                CosmeticsPage.this.populateScrollPane();
            }
        };
        this.emotes = new CosmeticGenericButton(CosmeticType.EMOTES.toString(), x, y + typeHeight + 10, typeWidth, typeHeight, false) {
            @Override
            public void onAction() {
                this.setActive(false);
                CosmeticsPage.this.cosmeticType = CosmeticType.EMOTES;
                CosmeticsPage.this.populateScrollPane();
            }
        };
        this.flags = new CosmeticGenericButton(CosmeticType.FLAGS.toString(), x + typeWidth + 20, y + typeHeight + 10, typeWidth, typeHeight, false) {
            @Override
            public void onAction() {
                this.setActive(false);
                CosmeticsPage.this.cosmeticType = CosmeticType.FLAGS;
                CosmeticsPage.this.populateScrollPane();
            }
        };
        this.scrollpane = new ModScrollPane(x, y + typeHeight * 2 + 20, width - x - 2, this.menu.getHeight() - y - typeHeight * 2 - 20 - 1, true);
        x = this.menu.getWidth() - width + 20;
        y -= 30;
        this.emoteWheelBind = new MenuModKeybind(x - 21 + width / 2 - compWidth / 2, y + 50, compWidth, 22) {
            @Override
            public void onAction() {
            }
        };
        this.bindButton1 = new CosmeticBindButton(x - 21 + width / 2 - compWidth / 2, y + 110, compWidth, 22) {
            @Override
            public void onAction() {
                CosmeticsPage.this.lastButton = this;
                CosmeticsPage.this.unpressAll(CosmeticsPage.this.lastButton);
            }
        };
        this.bindButton2 = new CosmeticBindButton(x - 21 + width / 2 - compWidth / 2, y + 140, compWidth, 22) {
            @Override
            public void onAction() {
                CosmeticsPage.this.lastButton = this;
                CosmeticsPage.this.unpressAll(CosmeticsPage.this.lastButton);
            }
        };
        this.bindButton3 = new CosmeticBindButton(x - 21 + width / 2 - compWidth / 2, y + 170, compWidth, 22) {
            @Override
            public void onAction() {
                CosmeticsPage.this.lastButton = this;
                CosmeticsPage.this.unpressAll(CosmeticsPage.this.lastButton);
            }
        };
        this.bindButton4 = new CosmeticBindButton(x - 21 + width / 2 - compWidth / 2, y + 200, compWidth, 22) {
            @Override
            public void onAction() {
                CosmeticsPage.this.lastButton = this;
                CosmeticsPage.this.unpressAll(CosmeticsPage.this.lastButton);
            }
        };
        this.capeLocation = new CosmeticList(CapeType.class, x - 21 + width / 2 - compWidth / 2, y + 260, compWidth, 22) {
            @Override
            public void onAction() {
            }
        };
        this.bandanaLocation = new CosmeticList(BandanaSize.class, x - 21 + width / 2 - compWidth / 2, y + 320, compWidth, 22) {
            @Override
            public void onAction() {
            }
        };
        this.resetButton = new MacroButton("RESET TO DEFAULT", x - 21 + width / 2 - compWidth / 2, this.menu.getHeight() - 22 - 20, compWidth, 22, false) {
            @Override
            public void onAction() {
                this.setActive(false);
                CosmeticsPage.this.emoteWheelBind.setBind(0);
                CosmeticsPage.this.bindButton1.setBind("");
                CosmeticsPage.this.bindButton2.setBind("");
                CosmeticsPage.this.bindButton3.setBind("");
                CosmeticsPage.this.bindButton4.setBind("");
                CosmeticsPage.this.bandanaLocation.setValue(BandanaSize.MEDIUM.toString());
            }
        };
        x = this.menu.getWidth() / 2;
        width = 350;
        this.openCrateButton = new CosmeticRainbowButton("OPEN A CRATE", x - width / 2, y, width, 40) {
            @Override
            public void onAction() {
                this.setActive(false);
                Minecraft.getMinecraft().displayGuiScreen(null);
            }
        };
        this.resetCape = new CosmeticGenericButton("SET CAPE TO NONE", x - width / 2, this.menu.getHeight() - 40 - 40, width, 20, true) {
            @Override
            public void onAction() {
                this.setActive(false);
            }
        };
        this.userPreview = new CosmeticUserPreview(x - width / 2, y + 45, width, 275);
        this.bandanaLocation.setValue(BandanaSize.MEDIUM.toString());
        this.populateScrollPane();
    }
    
    private void updateMenuStates(final boolean load) {
        for (final MenuComponent comp : this.menu.getComponents()) {
            if (comp instanceof CosmeticGenericButton) {
                final CosmeticGenericButton button = (CosmeticGenericButton)comp;
                if (!button.getText().equalsIgnoreCase(this.cosmeticType.toString())) {
                    button.setActive(false);
                }
                else {
                    button.setActive(true);
                    if (!load) {
                        continue;
                    }
                    button.onAction();
                }
            }
        }
    }
    
    private void populateScrollPane() {
        this.scrollpane.getComponents().clear();
        this.updateMenuStates(false);
    }
    
    @Override
    public void onRender() {
        final int width = 300;
        int x = this.menu.getX() + this.menu.getWidth() - width + 20;
        int y = this.menu.getY() + 59;
        final int height = 32;
        DrawImpl.drawRect(this.menu.getX() + this.menu.getWidth() - width, this.menu.getY() + 58, width, this.menu.getHeight() - 58, MacrosPage.MENU_SIDE_BG_COLOR);
        DrawImpl.drawRect(this.menu.getX() + this.menu.getWidth() - width, this.menu.getY() + 58, width, height + 1, ModCategoryButton.MAIN_COLOR);
        this.drawShadowDown(this.menu.getX() + this.menu.getWidth() - width, y + height, width);
        FontManager.getProductSansRegular(30).drawString("COSMETIC SETTINGS", this.menu.getX() + this.menu.getWidth() - width / 2 - FontManager.getProductSansRegular(30).width("COSMETIC SETTINGS") / 2, y + height / 2 - rip.athena.client.font.FontManager.baloo17.getHeight("COSMETIC SETTINGS") / 2.0f, IngameMenu.MENU_HEADER_TEXT_COLOR);
        DrawImpl.drawRect(this.menu.getX(), this.menu.getY() + 58, width, this.menu.getHeight() - 58, MacrosPage.MENU_SIDE_BG_COLOR);
        DrawImpl.drawRect(this.menu.getX(), this.menu.getY() + 58, width, height + 1, ModCategoryButton.MAIN_COLOR);
        this.drawShadowDown(this.menu.getX(), y + height, width);
        FontManager.getProductSansRegular(30).drawString("COSMETICS", this.menu.getX() + width / 2 - FontManager.getProductSansRegular(30).width("COSMETICS") / 2, y + height / 2 - rip.athena.client.font.FontManager.baloo17.getHeight("COSMETICS") / 2.0f, IngameMenu.MENU_HEADER_TEXT_COLOR);
        this.drawShadowDown(this.menu.getX(), y - 1, width);
        this.drawShadowDown(this.menu.getX() + this.menu.getWidth() - width, y - 1, width);
        x += 3;
        y += 50;
        FontManager.getProductSansRegular(30).drawString("EMOTE WHEEL KEYBIND", x, y, IngameMenu.MENU_HEADER_TEXT_COLOR);
        y += 60;
        FontManager.getProductSansRegular(30).drawString("ENABLED EMOTES", x, y, IngameMenu.MENU_HEADER_TEXT_COLOR);
        y += 150;
        FontManager.getProductSansRegular(30).drawString("CAPE LOCATION", x, y, IngameMenu.MENU_HEADER_TEXT_COLOR);
        y += 60;
        FontManager.getProductSansRegular(30).drawString("BANDANA LOCATION", x, y, IngameMenu.MENU_HEADER_TEXT_COLOR);
    }
    
    @Override
    public void onLoad() {
        this.menu.addComponent(this.capes);
        this.menu.addComponent(this.bandanas);
        this.menu.addComponent(this.emotes);
        this.menu.addComponent(this.flags);
        this.menu.addComponent(this.emoteWheelBind);
        this.menu.addComponent(this.bindButton1);
        this.menu.addComponent(this.bindButton2);
        this.menu.addComponent(this.bindButton3);
        this.menu.addComponent(this.bindButton4);
        this.menu.addComponent(this.capeLocation);
        this.menu.addComponent(this.bandanaLocation);
        this.menu.addComponent(this.resetButton);
        this.menu.addComponent(this.openCrateButton);
        this.menu.addComponent(this.resetCape);
        this.menu.addComponent(this.userPreview);
        this.menu.addComponent(this.scrollpane);
        this.updateMenuStates(false);
    }
    
    @Override
    public void onUnload() {
    }
    
    @Override
    public void onOpen() {
    }
    
    @Override
    public void onClose() {
    }
    
    private void pressEmote(final String emote) {
        if (this.lastButton != null) {
            this.lastButton.setBind(emote);
            this.unpressAll(this.lastButton = null);
        }
    }
    
    private void unpressAll(final CosmeticBindButton b) {
        for (final MenuComponent comp : this.menu.getComponents()) {
            if (comp instanceof CosmeticBindButton) {
                final CosmeticBindButton button = (CosmeticBindButton)comp;
                if (button == b) {
                    continue;
                }
                button.setActive(false);
            }
        }
    }
}
