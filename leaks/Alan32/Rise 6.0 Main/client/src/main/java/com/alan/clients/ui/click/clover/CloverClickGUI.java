package com.alan.clients.ui.click.clover;

import com.alan.clients.Client;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.input.GuiClickEvent;
import com.alan.clients.newevent.impl.input.GuiMouseReleaseEvent;
import com.alan.clients.ui.click.clover.button.impl.ModuleButton;
import com.alan.clients.ui.click.clover.button.impl.OptionsButton;
import com.alan.clients.ui.click.clover.setting.api.SettingComp;
import com.alan.clients.util.dragging.Drag;
import com.alan.clients.util.font.FontManager;
import com.alan.clients.util.gui.ScrollUtil;
import com.alan.clients.util.gui.textbox.TextAlign;
import com.alan.clients.util.gui.textbox.TextBox;
import com.alan.clients.util.render.ColorUtil;
import com.alan.clients.util.render.RenderUtil;
import com.alan.clients.util.render.StencilUtil;
import com.alan.clients.util.vector.Vector2d;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class CloverClickGUI extends GuiScreen {

    //Dimensions
    public double round = 20;
    public double actionButtonWidth = 60;
    public Vector2d scale = new Vector2d(530, 345);
    public Vector2d searchScale = new Vector2d(100, round * 0.8);

    //Colors
    public Color accentLines = new Color(255, 255, 255, 20);
    public Color backgroundShade = ColorUtil.withAlpha(Color.BLACK, 120);
    public Color dropShadow = ColorUtil.withAlpha(Color.BLACK, 120);
    public Color deSelected = ColorUtil.withAlpha(Color.WHITE, 50);

    //Positions
    public Vector2d actionBarBottom = new Vector2d(0, 5 + round);
    public Vector2d actionBarMiddle = new Vector2d(scale.x / 2, actionBarBottom.y);
    public Vector2d actionBarSearch = new Vector2d(scale.x - searchScale.x - 40, actionBarBottom.y);
    public Vector2d actionBarOptions = new Vector2d(scale.x - (scale.x - (actionBarSearch.x + searchScale.x)) / 2, actionBarBottom.y);

    //Dimensions
    public Vector2d optionsScale = new Vector2d(160, scale.y - actionBarBottom.y);
    public double padding = 15;

    //Positions
    public Vector2d moduleIcon = actionBarBottom.offset(optionsScale.x / 2, 15);
    public Vector2d categoryMiddle = actionBarBottom.offset(optionsScale.x + (scale.x - optionsScale.x) / 2, actionBarBottom.y);

    //Parts
    public Drag drag = new Drag(new Vector2d(0, 0), new Vector2d(scale.x, round));
    public ScrollUtil scroll = new ScrollUtil();
    public OptionsButton mods = new OptionsButton(new Vector2d(0, 0), new Vector2d(actionButtonWidth, actionBarBottom.y - 1), null, "Mods");
    public OptionsButton settings = new OptionsButton(new Vector2d(0, 0), new Vector2d(actionButtonWidth, actionBarBottom.y - 1), null, "Settings");
    public OptionsButton profiles = new OptionsButton(new Vector2d(0, 0), new Vector2d(actionButtonWidth, actionBarBottom.y - 1), null, "Profiles");
    public TextBox searchBox = new TextBox(new Vector2d(0, 0), FontManager.getNunito(15), ColorUtil.withAlpha(Color.WHITE, 100), TextAlign.LEFT, "Search", (float) searchScale.x - 18);

    public OptionsButton game = new OptionsButton(new Vector2d(0, 0), new Vector2d(actionButtonWidth, actionBarBottom.y - 1), null, "Game");
    public OptionsButton hud = new OptionsButton(new Vector2d(0, 0), new Vector2d(actionButtonWidth, actionBarBottom.y - 1), null, "Hud");
    public OptionsButton enhancements = new OptionsButton(new Vector2d(0, 0), new Vector2d(actionButtonWidth, actionBarBottom.y - 1), null, "Enhancements");

    public ArrayList<ModuleButton> moduleButtons = new ArrayList<>();

    //Other
    public ModuleButton selected;

    public CloverClickGUI() {
        Client.INSTANCE.getModuleManager().getAll().forEach(module -> {
            ModuleButton moduleButton = new ModuleButton(new Vector2d(0, 0), new Vector2d(160, 85), null, module);

            moduleButton.click = () -> {
                selected = moduleButton;
                moduleButton.module.toggle();
            };

            moduleButtons.add(moduleButton);
        });

        selected = moduleButtons.get(0);
    }


    public void render() {
        drag.render();

        NORMAL_RENDER_RUNNABLES.add(() -> {
            //Background
            RenderUtil.dropShadow(10, drag.position.x, drag.position.y, scale.x, scale.y, 60, round);
            RenderUtil.roundedRectangle(drag.position.x, drag.position.y, scale.x, scale.y, round, backgroundShade);
            RenderUtil.roundedOutlineRectangle(drag.position.x, drag.position.y, scale.x, scale.y, round, 0.5, backgroundShade);

            GlStateManager.pushMatrix();
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            RenderUtil.scissor(drag.position.x, drag.position.y, scale.x, actionBarBottom.y * 2 + 1);
            RenderUtil.roundedRectangle(drag.position.x, drag.position.y, scale.x, scale.y, round, ColorUtil.withAlpha(Color.WHITE, 10));
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
            GlStateManager.popMatrix();

            GlStateManager.pushMatrix();
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            RenderUtil.scissor(drag.position.x, drag.position.y + actionBarBottom.y * 2 + 1, optionsScale.x, optionsScale.y - 1);
            RenderUtil.roundedRectangle(drag.position.x, drag.position.y, scale.x, scale.y, round, ColorUtil.withAlpha(Color.WHITE, 10));
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
            GlStateManager.popMatrix();

            //Lines
            RenderUtil.rectangle(drag.position.x, drag.position.y + actionBarBottom.y - 0.5, scale.x, 0.5, accentLines);
            RenderUtil.rectangle(drag.position.x + optionsScale.x, drag.position.y + actionBarBottom.y, 0.5, scale.y - actionBarBottom.y, accentLines);
            RenderUtil.rectangle(drag.position.x + optionsScale.x, drag.position.y + actionBarBottom.y * 2 - 0.5, scale.x - optionsScale.x, 0.5, accentLines);

            //Logo
            double divider = 18;
            RenderUtil.image(new ResourceLocation("clover/images/Clover Logo.png"), drag.position.x + 15, drag.position.y - 7, 1178 / divider, 662 / divider);

            //Action bar buttons
            mods.position = drag.position.offset(actionBarMiddle).offset(-actionButtonWidth / 2 - actionButtonWidth, -mods.scale.y - 1);
            mods.selected = true;
            mods.render();

            settings.position = drag.position.offset(actionBarMiddle).offset(-actionButtonWidth / 2, -mods.scale.y - 1);
            settings.render();

            profiles.position = drag.position.offset(actionBarMiddle).offset(-actionButtonWidth / 2 + actionButtonWidth, -mods.scale.y - 1);
            profiles.render();

            //Category buttons
            game.position = drag.position.offset(categoryMiddle).offset(-actionButtonWidth / 2 - actionButtonWidth, -mods.scale.y - 1);
            game.selected = true;
            game.render();

            hud.position = drag.position.offset(categoryMiddle).offset(-actionButtonWidth / 2, -mods.scale.y - 1);
            hud.render();

            enhancements.position = drag.position.offset(categoryMiddle).offset(-actionButtonWidth / 2 + actionButtonWidth, -mods.scale.y - 1);
            enhancements.render();

            //Search
            Vector2d searchPosition = drag.position.offset(actionBarSearch).offset(0, -searchScale.y - actionBarBottom.y / 2 + searchScale.y / 2 + 0.5f);
            RenderUtil.dropShadow(5, searchPosition.x, searchPosition.y, searchScale.x, searchScale.y, 40, round);
            RenderUtil.roundedRectangle(searchPosition.x, searchPosition.y, searchScale.x, searchScale.y, 4.5, backgroundShade);
//            FontManager.getIconsThree(12).drawString("U", searchPosition.x + 6, searchPosition.y + 8, ColorUtil.withAlpha(Color.WHITE, 70).hashCode());

            if (searchBox.selected)
            searchBox.position = searchPosition.offset(16, 7);
            searchBox.draw();

            //More options
            double radius = 1.5;
            double distance = 2 + radius;
            Vector2d moreOptionsPosition = drag.position.offset(actionBarOptions).offset(-radius / 2f, -actionBarBottom.y + 9);
            for (double y = moreOptionsPosition.y; y < moreOptionsPosition.y + distance * 3; y += distance) {
                RenderUtil.roundedRectangle(moreOptionsPosition.x, y, radius, radius, radius / 2, Color.WHITE);
            }

            //Module settings
            Vector2d moduleInfo = drag.position.offset(moduleIcon);
            Vector2d logo = moduleInfo.offset(-FontManager.getNunito(160).width("?") / 2f, 0);
            FontManager.getNunito(160).drawString("?", logo.x, logo.y, Color.WHITE.hashCode());
            NORMAL_POST_BLOOM_RUNNABLES.add(() -> FontManager.getNunito(160).drawString("?", logo.x, logo.y, Color.WHITE.hashCode()));

            FontManager.getNunito(30).drawCenteredString(selected.module.getModuleInfo().name(), moduleInfo.x, moduleInfo.y + 90, Color.WHITE.hashCode());
            FontManager.getNunito(17).drawCenteredString(selected.module.getModuleInfo().description(), moduleInfo.x, moduleInfo.y + 115, deSelected.hashCode());

            //Modules
            Vector2d position = drag.position.offset(actionBarBottom).offset(optionsScale.x + padding, actionBarBottom.y + padding);
            scroll.max = position.y;
            position = position.offset(0, scroll.scroll);

            StencilUtil.initStencil();
            StencilUtil.bindWriteStencilBuffer();

            RenderUtil.rectangle(drag.position.x, drag.position.y + actionBarBottom.y * 2, scale.x, scale.y - actionBarBottom.y * 2, Color.BLACK);

            StencilUtil.bindReadStencilBuffer(1);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
            GlStateManager.enableTexture2D();

            for (int index = 0; index < moduleButtons.size(); index++) {
                ModuleButton moduleButton = moduleButtons.get(index);

                moduleButton.position = position;
                if (index % 2 == 0) {
                    position = position.offset(moduleButton.scale.x + padding, 0);
                } else {
                    position = position.offset(-moduleButton.scale.x - padding, moduleButton.scale.y + padding);
                }

                moduleButton.render();
            }

            GlStateManager.disableBlend();
            StencilUtil.uninitStencilBuffer();

            scroll.setMax(-((85 + 15) * moduleButtons.size() / 2f) + scale.y - actionBarBottom.y * 2 - padding);
            scroll.onRender();

            //Module settings
            Vector2d settingPosition = drag.position.offset(actionBarBottom).offset(padding, 170);
            for (SettingComp settingComp : selected.settings) {
                settingComp.position = settingPosition;
                settingComp.render();

                settingPosition = settingPosition.offset(0, settingComp.scale);
            }
        });

        NORMAL_BLUR_RUNNABLES.add(() -> RenderUtil.roundedRectangle(drag.position.x, drag.position.y, scale.x, scale.y, round, Color.BLACK));

    }

    @EventLink()
    public final Listener<GuiMouseReleaseEvent> onMouse = event -> {

        drag.release();

        mods.release();
        settings.release();
        profiles.release();

        hud.release();
        game.release();
        enhancements.release();

        //Modules
        for (ModuleButton moduleButton : moduleButtons) {
            moduleButton.release();
        }
    };

    @EventLink()
    public final Listener<GuiClickEvent> onGuiClick = event -> {

        drag.onClick(event.getMouseButton());

        mods.click(event.getMouseButton());
        settings.click(event.getMouseButton());
        profiles.click(event.getMouseButton());

        hud.click(event.getMouseButton());
        game.click(event.getMouseButton());
        enhancements.click(event.getMouseButton());

        searchBox.click(event.getMouseX(), event.getMouseY(), event.getMouseButton());

        //Modules
        for (ModuleButton moduleButton : moduleButtons) {
            moduleButton.click(event.getMouseButton());
        }
    };

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        searchBox.key(typedChar, keyCode);
    }
}
