package me.jinthium.straight.api.clickgui;

import imgui.*;
import imgui.flag.ImGuiCol;
import me.jinthium.straight.api.util.MinecraftInstance;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.Objects;

public class IImGuiTheme implements MinecraftInstance {

    public ImFontConfig applyFont(ImGuiIO io){
        final ImFontConfig fontConfig = new ImFontConfig();
        final ImFontAtlas fontAtlas = io.getFonts();
        final ImFontGlyphRangesBuilder rangesBuilder = new ImFontGlyphRangesBuilder(); // Glyphs ranges provide

        rangesBuilder.addRanges(fontAtlas.getGlyphRangesCyrillic());
        rangesBuilder.addRanges(fontAtlas.getGlyphRangesDefault());
        rangesBuilder.addRanges(fontAtlas.getGlyphRangesJapanese());
        rangesBuilder.addRanges(FontAwesomeIcons._IconRange);

        try {
            final short[] glyphRanges = rangesBuilder.buildRanges();
            fontAtlas.addFontFromMemoryTTF(loadFromResources("straight/fonts/circular-std-medium-500.ttf"), 17, fontConfig, glyphRanges);
            fontConfig.setMergeMode(true); // When enabled, all fonts added with this config would be merged with the previously added font
            fontAtlas.addFontFromMemoryTTF(loadFromResources("straight/fonts/fa-regular-400.ttf"), 14, fontConfig, glyphRanges);
            fontAtlas.addFontFromMemoryTTF(loadFromResources("straight/fonts/fa-solid-900.ttf"), 14, fontConfig, glyphRanges);
        } catch (Exception e) {
            e.printStackTrace();
            fontAtlas.addFontDefault();
        }

        return fontConfig;
    }

    public void applyStyle(ImGuiStyle guiStyle){
        guiStyle.setWindowRounding(6.5f);
        guiStyle.setTabRounding(6.5f);
        guiStyle.setGrabRounding(6.5f);
        guiStyle.setPopupRounding(6.5f);
        guiStyle.setScrollbarRounding(6.5f);
        guiStyle.setChildRounding(6.5f);
        guiStyle.setFrameRounding(6.5f);
        guiStyle.setWindowTitleAlign(0.5f, 0.5f);
        guiStyle.setAntiAliasedLines(true);
        guiStyle.setColor(ImGuiCol.Header, ImGui.getColorU32(0.1765f, 0.1765f, 0.1765f, 1.0f));
        guiStyle.setColor(ImGuiCol.SliderGrab, ImGui.getColorU32(0.2f, 0.3059f, 0.3216f, 1.0f));
        guiStyle.setColor(ImGuiCol.SliderGrabActive, ImGui.getColorU32(0.0157f, 0.5216f, 0.5412f, 1.0f));
        guiStyle.setColor(ImGuiCol.TitleBgActive, ImGui.getColorU32(0.15f, 0.15f, 0.15f, 1.0f));
        guiStyle.setColor(ImGuiCol.TitleBgCollapsed, ImGui.getColorU32(0.09f, 0.09f, 0.09f, 1.0f));

        //changes background color for frames (ex. checkmarks)
        guiStyle.setColor(ImGuiCol.FrameBg, ImGui.getColorU32(0.1569f, 0.1569f, 0.1569f,1.0f));
        guiStyle.setColor(ImGuiCol.FrameBgHovered, ImGui.getColorU32(0.0039f, 0.298f, 0.3098f,1.0f));
        guiStyle.setColor(ImGuiCol.FrameBgActive, ImGui.getColorU32(0.0039f, 0.298f, 0.3098f,1.0f));
        //changes color for checkmarks when toggled
        guiStyle.setColor(ImGuiCol.CheckMark, ImGui.getColorU32(0.0157f, 0.5216f, 0.5412f, 1.0f));

        //changes color for buttons at the top
        guiStyle.setColor(ImGuiCol.Button, ImGui.getColorU32(0.0157f, 0.5216f, 0.5412f, 1.0f));
        guiStyle.setColor(ImGuiCol.ButtonHovered, ImGui.getColorU32(0.0078f, 0.4275f, 0.4392f,1.0f));
    }

    byte[] loadFromResources(String path) {
        try {
            return IOUtils.toByteArray(mc.getResourceManager().getResource(new ResourceLocation(path)).getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
