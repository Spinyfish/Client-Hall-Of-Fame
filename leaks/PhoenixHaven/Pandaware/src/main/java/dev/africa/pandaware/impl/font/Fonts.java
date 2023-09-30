package dev.africa.pandaware.impl.font;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.interfaces.Initializable;
import dev.africa.pandaware.api.interfaces.MinecraftInstance;
import dev.africa.pandaware.impl.font.renderer.TTFFontRenderer;
import dev.africa.pandaware.utils.client.Printer;
import lombok.Getter;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.main.Main;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.util.Objects;

@Getter
public class Fonts implements Initializable, MinecraftInstance {
    @Getter
    private static final Fonts instance = new Fonts();

    private TTFFontRenderer arialBdSmall, arialBdNormal, arialBdMedium, arialBdBig, arialBdVeryBig;
    private TTFFontRenderer comfortaSmall, comfortaNormal, comfortaMedium, comfortaBig, comfortaVeryBig;
    private TTFFontRenderer iconsSmall, iconsNormal, iconsMedium, iconsBig, iconsVeryBig;
    private TTFFontRenderer productSansSmall, productSansNormal, productSansMedium, productSansBig, productSansVeryBig;
    private TTFFontRenderer epocaSmall, epocaNormal, epocaMedium, epocaBig, epocaVeryBig;
    private TTFFontRenderer tahomaSmall, tahomaNormal, tahomaMedium, tahomaBig, tahomaVeryBig;

    private FontRenderer bit;

    @Override
    public void init() {
        try {
            if (System.getProperty("24c6ba8a-4e4f-4bad-906a-8eff47f36e15") == null) {
                return;
            }

            this.arialBdSmall = this.createFont("arialbd.ttf", 16);
            this.arialBdNormal = this.createFont("arialbd.ttf", 19);
            this.arialBdMedium = this.createFont("arialbd.ttf", 20);
            this.arialBdBig = this.createFont("arialbd.ttf", 24);
            this.arialBdVeryBig = this.createFont("arialbd.ttf", 75);

            this.comfortaSmall = this.createFont("Comfortaa-Regular.ttf", 16);
            this.comfortaNormal = this.createFont("Comfortaa-Regular.ttf", 19);
            this.comfortaMedium = this.createFont("Comfortaa-Regular.ttf", 20);
            this.comfortaBig = this.createFont("Comfortaa-Regular.ttf", 24);
            this.comfortaVeryBig = this.createFont("Comfortaa-Regular.ttf", 30);

            this.iconsSmall = this.createFont("Icons.ttf", 16);
            this.iconsNormal = this.createFont("Icons.ttf", 19);
            this.iconsMedium = this.createFont("Icons.ttf", 20);
            this.iconsBig = this.createFont("Icons.ttf", 24);
            this.iconsVeryBig = this.createFont("Icons.ttf", 34);

            this.productSansSmall = this.createFont("productsans.ttf", 16);
            this.productSansNormal = this.createFont("productsans.ttf", 19);
            this.productSansMedium = this.createFont("productsans.ttf", 20);
            this.productSansBig = this.createFont("productsans.ttf", 24);
            this.productSansVeryBig = this.createFont("productsans.ttf", 30);

            this.epocaSmall = this.createFont("epoca.ttf", 16);
            this.epocaNormal = this.createFont("epoca.ttf", 19);
            this.epocaMedium = this.createFont("epoca.ttf", 20);
            this.epocaBig = this.createFont("epoca.ttf", 24);
            this.epocaVeryBig = this.createFont("epoca.ttf", 30);

            this.tahomaSmall = this.createFont("tahoma.ttf", 16);
            this.tahomaNormal = this.createFont("tahoma.ttf", 19);
            this.tahomaMedium = this.createFont("tahoma.ttf", 20);
            this.tahomaBig = this.createFont("tahoma.ttf", 24);
            this.tahomaVeryBig = this.createFont("tahoma.ttf", 30);

            this.createBit();
            if (Client.getInstance().isKillSwitch()) {
                throw new RuntimeException();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Printer.consoleError("Failed to load fonts, StackTrace: " + e.getMessage());
            if (!Client.getInstance().isKillSwitch()) {
                System.exit(2);
            }
        }
    }

    TTFFontRenderer createFont(String fontName, int size) throws Exception {
        String fontPath = "assets/minecraft/" + Client.getInstance().getManifest()
                .getClientName().toLowerCase() + "/fonts/";

        boolean classExists = true;

        /*try {
            Class.forName("me.Utility");
        } catch (ClassNotFoundException e) {
            classExists = false;
        }*/
        //TODO: uncum on release

        if (System.getProperty("a755e611-6014-4ffa-8ab8-7204b31a840e") == null || !classExists) {
            return null;
        }

        return new TTFFontRenderer(Font.createFont(Font.PLAIN,
                Objects.requireNonNull(Main.class.getClassLoader()
                        .getResourceAsStream(fontPath + fontName))).deriveFont(Font.PLAIN, size));
    }

    void createBit() {
        String fontPath = Client.getInstance().getManifest().getClientName().toLowerCase() + "/fonts/bit.png";

        this.bit = new FontRenderer(mc.gameSettings, new ResourceLocation(fontPath), mc.renderEngine, false);

        if (mc.gameSettings.language != null) {
            this.bit.setUnicodeFlag(mc.isUnicode());
            this.bit.setBidiFlag(mc.getLanguageManager().isCurrentLanguageBidirectional());
        }

        mc.mcResourceManager.registerReloadListener(this.bit);
    }
}
