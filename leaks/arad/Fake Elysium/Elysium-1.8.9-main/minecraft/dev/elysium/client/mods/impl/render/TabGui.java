package dev.elysium.client.mods.impl.render;

import dev.elysium.base.events.types.EventTarget;
import dev.elysium.base.mods.Category;
import dev.elysium.base.mods.Mod;
import dev.elysium.client.Elysium;
import dev.elysium.client.events.EventKeyTyped;
import dev.elysium.client.events.EventRenderHUD;
import dev.elysium.client.utils.render.RenderUtils;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;

public class TabGui extends Mod {

    int hoveringOnCategory = 0;
    Category selectedCategory = null;
    Mod selectedModule = null;

    public TabGui() {
        super("TabGui","ClickGUI but in-game", Category.RENDER);
    }

    @EventTarget
    public void onEvent(EventRenderHUD e) {
        RenderUtils.drawARoundedRect(10, 20, 130, 20 + (Category.values().length * 32) + 4, 5, 0xff1A1820);

        RenderUtils.drawGradientRect(10, 25, 15, 20 + (Category.values().length * 32) + 4 - 5, 0xff6089F8, 0xff8764F7);
        RenderUtils.drawACircle(15, 25, 180, 270, 5, 0xff6089F8);
        RenderUtils.drawACircle(15, 25 + (Category.values().length * 32) + 4 - 10, 270, 360, 5, 0xff8764F7);
        int cI = 0;
        for(Category c : Category.values()) {
            float thick = 0.9f;

            RenderUtils.drawGradientRoundedRect(20 - thick, 25 + (cI * 32) - thick, 105 + thick + 20, 25 + (cI * 32) + 25 + thick, 5, hoveringOnCategory == cI ? 0xff7F6DF8 : 0xff34374F, hoveringOnCategory == cI ? 0xff6088F8 : 0xff34374F);
            RenderUtils.drawGradientRoundedRect(20, 25 + (cI * 32), 105 + 20, 25 + (cI * 32) + 25, 5, 0xff181924, 0xff181924);

            Elysium.getInstance().getFontManager().getFont("POPPINS 18").drawString(c.name, 27, 31.5f + (cI * 32), selectedCategory != null && cI == selectedCategory.ordinal() ? -1 : 0xff9599C3);

            for(int circle = 0; circle < 3; circle++) {
                RenderUtils.drawACircleSmall(98 + 20, 33.5 + (cI * 32) + (circle * 4), 0, 360, 1.5, 0xff545997);
            }

            cI++;
        }

        if(selectedCategory != null) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(130, 0, 0);

            RenderUtils.drawARoundedRect(10, 20, 130, 20 + (Elysium.getInstance().getModManager().getModsByCategory(selectedCategory).size() * 32) + 4, 5, 0xff1A1820);

            RenderUtils.drawGradientRect(10, 25, 15, 20 + (Elysium.getInstance().getModManager().getModsByCategory(selectedCategory).size() * 32) + 4 - 5, 0xff6089F8, 0xff8764F7);
            RenderUtils.drawACircle(15, 25, 180, 270, 5, 0xff6089F8);
            RenderUtils.drawACircle(15, 25 + (Elysium.getInstance().getModManager().getModsByCategory(selectedCategory).size() * 32) + 4 - 10, 270, 360, 5, 0xff8764F7);

            int mI = 0;

            for(Mod m : Elysium.getInstance().getModManager().getModsByCategory(selectedCategory)) {
                float thick = 1f;

                RenderUtils.drawGradientRoundedRect(20 - thick, 25 + (mI * 32) - thick, 105 + thick + 20, 25 + (mI * 32) + 25 + thick, 5, selectedCategory.selectIndex == mI ? 0xff7F6DF8 : 0xff34374F, selectedCategory.selectIndex == mI ? 0xff6088F8 : 0xff34374F);
                RenderUtils.drawGradientRoundedRect(20, 25 + (mI * 32), 105 + 20, 25 + (mI * 32) + 25, 5, 0xff181924, 0xff181924);

                for(int circle = 0; circle < 3; circle++) {
                    RenderUtils.drawACircleSmall(98 + 20, 33.5 + (mI * 32) + (circle * 4), 0, 360, 1.5, 0xff545997);
                }

                Elysium.getInstance().getFontManager().getFont("POPPINS 18").drawString(m.name, 27, 31.5f + (mI * 32), m.toggled ? -1 : 0xff9599C3);

                mI++;
            }

            GlStateManager.popMatrix();
        }

        if(selectedModule != null) {

        }
    }

    @EventTarget
    public void onKey(EventKeyTyped e) {
        if(!Keyboard.isKeyDown(e.getKeyCode())) return; //body base moment
        if(e.getKeyCode() == Keyboard.KEY_DOWN) {
            if(selectedCategory == null) {
                hoveringOnCategory++;
                if(hoveringOnCategory > Category.values().length - 1) hoveringOnCategory = 0;
            } else {
                selectedCategory.selectIndex++;
                if(selectedCategory.selectIndex > Elysium.getInstance().getModManager().getModsByCategory(selectedCategory).size() - 1) selectedCategory.selectIndex = 0;
            }
        } else if(e.getKeyCode() == Keyboard.KEY_UP) {
            if(selectedCategory == null) {
                hoveringOnCategory--;
                if(hoveringOnCategory < 0) hoveringOnCategory = Category.values().length - 1;
            } else {
                selectedCategory.selectIndex--;
                if(selectedCategory.selectIndex < 0) selectedCategory.selectIndex = Elysium.getInstance().getModManager().getModsByCategory(selectedCategory).size() - 1;
            }
        } else if(e.getKeyCode() == Keyboard.KEY_RIGHT) {
            if(selectedCategory == null) {
                selectedCategory = Category.values()[hoveringOnCategory];
                //hoveringOnCategory = -1;
            } else {
                Elysium.getInstance().getModManager().getModsByCategory(selectedCategory).get(selectedCategory.selectIndex).toggle();
            }
        } else if(e.getKeyCode() == Keyboard.KEY_LEFT) {
            if(selectedModule != null) {
                selectedModule = null;
            } else {
                selectedCategory = null;
            }
        } else if(e.getKeyCode() == Keyboard.KEY_RETURN) {
            if(selectedCategory != null) {
                selectedModule = Elysium.getInstance().getModManager().getModsByCategory(selectedCategory).get(selectedCategory.selectIndex);
            }
        }
    }
}