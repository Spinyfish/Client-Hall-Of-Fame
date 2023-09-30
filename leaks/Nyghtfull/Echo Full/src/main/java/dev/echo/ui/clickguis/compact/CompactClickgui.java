package dev.echo.ui.clickguis.compact;

import dev.echo.Echo;
import dev.echo.module.Category;
import dev.echo.module.Module;
import dev.echo.module.ModuleCollection;
import dev.echo.module.impl.movement.InventoryMove;
import dev.echo.module.impl.render.ClickGUIMod;
import dev.echo.module.settings.Setting;
import dev.echo.ui.clickguis.compact.impl.ModuleRect;
import dev.echo.ui.searchbar.SearchBar;
import dev.echo.ui.sidegui.SideGUI;
import dev.echo.utils.animations.Animation;
import dev.echo.utils.animations.Direction;
import dev.echo.utils.animations.impl.DecelerateAnimation;
import dev.echo.utils.font.FontUtil;
import dev.echo.utils.misc.HoveringUtil;
import dev.echo.utils.objects.Drag;
import dev.echo.utils.render.ColorUtil;
import dev.echo.utils.render.StencilUtil;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class CompactClickgui extends GuiScreen {

    private final Animation openingAnimation = new DecelerateAnimation(250, 1);
    private final Drag drag = new Drag(40, 40);
    private final ModulePanel modulePanel = new ModulePanel();
    private float rectWidth = 600;
    private float rectHeight = 600;
    public boolean typing;
    private HashMap<Category, ArrayList<ModuleRect>> moduleRects;


    @Override
    public void onDrag(int mouseX, int mouseY) {
        boolean focusedConfigGui = Echo.INSTANCE.getSideGui().isFocused();
        int fakeMouseX = focusedConfigGui ? 0 : mouseX, fakeMouseY = focusedConfigGui ? 0 : mouseY;

        drag.onDraw(fakeMouseX, fakeMouseY);
        Echo.INSTANCE.getSideGui().onDrag(mouseX, mouseY);
    }

    @Override
    public void initGui() {
        openingAnimation.setDirection(Direction.FORWARDS);
        rectWidth = 450;
        rectHeight = 450;
        if (moduleRects != null) {
            moduleRects.forEach((cat, list) -> list.forEach(ModuleRect::initGui));
        }
        modulePanel.initGui();
        Echo.INSTANCE.getSideGui().initGui();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == 1) {
            if (Echo.INSTANCE.getSearchBar().isFocused()) {
                Echo.INSTANCE.getSearchBar().getSearchField().setText("");
                Echo.INSTANCE.getSearchBar().getSearchField().setFocused(false);
                return;
            }

            if (Echo.INSTANCE.getSideGui().isFocused()) {
                Echo.INSTANCE.getSideGui().setFocused(false);
                return;
            }
            
            openingAnimation.setDirection(Direction.BACKWARDS);
        }
        modulePanel.keyTyped(typedChar, keyCode);
        Echo.INSTANCE.getSideGui().keyTyped(typedChar, keyCode);
        Echo.INSTANCE.getSearchBar().keyTyped(typedChar, keyCode);
    }

    private final List<ModuleRect> searchResults = new ArrayList<>();
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (ModuleCollection.reloadModules || moduleRects == null) {
            if (moduleRects == null) {
                moduleRects = new HashMap<>();
            } else moduleRects.clear();
            for (Category category : Category.values()) {
                ArrayList<ModuleRect> modules = new ArrayList<>();

                for (Module module : Echo.INSTANCE.getModuleCollection().getModulesInCategory(category)) {
                    modules.add(new ModuleRect(module));
                }

                moduleRects.put(category, modules);
            }
            moduleRects.forEach((cat, list) -> list.forEach(ModuleRect::initGui));
            ModuleCollection.reloadModules = false;
            return;
        }



        typing = modulePanel.typing || (Echo.INSTANCE.getSideGui().isFocused() && Echo.INSTANCE.getSideGui().isTyping()) || Echo.INSTANCE.getSearchBar().isTyping();

        if (ClickGUIMod.walk.isEnabled() && !typing) {
            InventoryMove.updateStates();
        }

        boolean focusedConfigGui = Echo.INSTANCE.getSideGui().isFocused();
        int fakeMouseX = focusedConfigGui ? 0 : mouseX, fakeMouseY = focusedConfigGui ? 0 : mouseY;

        float x = drag.getX(), y = drag.getY();

        if (!openingAnimation.isDone()) {
            x -= width + rectWidth / 2f;
            x += (width + rectWidth / 2f) * openingAnimation.getOutput().floatValue();
        } else if (openingAnimation.getDirection().equals(Direction.BACKWARDS)) {
            mc.displayGuiScreen(null);
            return;
        }

        Gui.drawRect2(90 + x, y, rectWidth - 90, rectHeight, new Color(0, 0, 0, 150).getRGB());
        Gui.drawRect2(x, y, 90, rectHeight, new Color(0, 0, 0, 100).getRGB());

        FontUtil.echoBoldFont40.drawString(
                Echo.NAME, x + 45 - echoBoldFont40.getStringWidth(Echo.NAME) / 2.0F,
                y + 7,
                Color.WHITE
        );

        boolean searching = Echo.INSTANCE.getSearchBar().isFocused();

        ClickGUIMod clickGUIMod = Echo.INSTANCE.getModuleCollection().getModule(ClickGUIMod.class);
        float catHeight = (rectHeight / 3) / Category.values().length;

        float seperation = 0;
        for (Category category : Category.values()) {
            float catY = y + 32 + seperation;
            boolean hovering = HoveringUtil.isHovering(x, catY + 8, 90, catHeight - 16, fakeMouseX, fakeMouseY);

            Color categoryColor = hovering ? ColorUtil.tripleColor(110).brighter() : ColorUtil.tripleColor(110);
            Color selectColor = (clickGUIMod.getActiveCategory() == category) ? Color.WHITE : categoryColor;

            echoBoldFont22.drawString(category.name, x + 8, catY + echoFont22.getMiddleOfBox(catHeight), selectColor.getRGB());
            seperation += catHeight;
        }

        modulePanel.currentCat = searching ? null : clickGUIMod.getActiveCategory();
        modulePanel.moduleRects = getModuleRects(clickGUIMod.getActiveCategory());
        modulePanel.x = x;
        modulePanel.y = y;
        modulePanel.rectHeight = rectHeight;
        modulePanel.rectWidth = rectWidth;

        StencilUtil.initStencilToWrite();
        Gui.drawRect2(x, y, rectWidth, rectHeight, Color.BLACK.getRGB());
        StencilUtil.readStencilBuffer(1);
        modulePanel.drawScreen(fakeMouseX, fakeMouseY);
        StencilUtil.uninitStencilBuffer();

        modulePanel.drawTooltips(fakeMouseX, fakeMouseY);

        SideGUI sideGUI = Echo.INSTANCE.getSideGui();
        sideGUI.getOpenAnimation().setDirection(openingAnimation.getDirection());
        //sideGUI.drawScreen(mouseX, mouseY);

        SearchBar searchBar = Echo.INSTANCE.getSearchBar();
        searchBar.setAlpha(openingAnimation.getOutput().floatValue() * (1 - sideGUI.getClickAnimation().getOutput().floatValue()));
        searchBar.drawScreen(fakeMouseX, fakeMouseY);

    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        float x = drag.getX(), y = drag.getY();

        if (!Echo.INSTANCE.getSideGui().isFocused()) {
            drag.onClick(mouseX, mouseY, mouseButton, HoveringUtil.isHovering(drag.getX(), drag.getY(), rectWidth, 10, mouseX, mouseY));

            ClickGUIMod clickGUIMod = Echo.INSTANCE.getModuleCollection().getModule(ClickGUIMod.class);
            float catHeight = (rectHeight / 3) / Category.values().length;

            float seperation = 0;
            for (Category category : Category.values()) {
                float catY = y + 32 + seperation;
                boolean hovering = HoveringUtil.isHovering(x, catY + 8, 90, catHeight - 16, mouseX, mouseY);

                if (hovering) {
                    clickGUIMod.setActiveCategory(category);
                }

                seperation += catHeight;
            }

            modulePanel.mouseClicked(mouseX, mouseY, mouseButton);
            Echo.INSTANCE.getSearchBar().mouseClicked(mouseX, mouseY, mouseButton);
        }
        Echo.INSTANCE.getSideGui().mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (!Echo.INSTANCE.getSideGui().isFocused()) {
            drag.onRelease(state);
            modulePanel.mouseReleased(mouseX, mouseY, state);
        }
        Echo.INSTANCE.getSideGui().mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private final List<String> searchTerms = new ArrayList<>();
    private String searchText;

    public List<ModuleRect> getModuleRects(Category category) {
        if (!Echo.INSTANCE.getSearchBar().isFocused()) {
            return moduleRects.get(category);
        }

        String search = Echo.INSTANCE.getSearchBar().getSearchField().getText();

        if (search.equals(searchText)) {
            return searchResults;
        } else {
            searchText = search;
        }

        List<ModuleRect> moduleRects1 = moduleRects.values().stream().flatMap(Collection::stream).collect(Collectors.toList());

        searchResults.clear();
        moduleRects1.forEach(moduleRect -> {
            searchTerms.clear();
            Module module = moduleRect.module;

            searchTerms.add(module.getName());
            searchTerms.add(module.getCategory().name);
            if (!module.getAuthor().isEmpty()) {
                searchTerms.add(module.getAuthor());
            }
            for (Setting setting : module.getSettingsList()) {
                searchTerms.add(setting.name);
            }

            moduleRect.setSearchScore(FuzzySearch.extractOne(search, searchTerms).getScore());
        });

        searchResults.addAll(moduleRects1.stream().filter(moduleRect -> moduleRect.getSearchScore() > 60)
                .sorted(Comparator.comparingInt(ModuleRect::getSearchScore).reversed()).collect(Collectors.toList()));

        return searchResults;
    }
}
