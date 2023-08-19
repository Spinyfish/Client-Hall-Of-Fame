package cc.swift.module.impl.render.hud.impl.data;

import cc.swift.module.impl.render.hud.HudComponent;
import cc.swift.module.impl.render.hud.HudFont;
import cc.swift.module.impl.render.hud.impl.data.components.PotionDataComponent;
import cc.swift.module.impl.render.hud.impl.data.components.TimeComponent;
import cc.swift.module.impl.render.hud.impl.data.components.BPSDataComponent;
import cc.swift.util.ChatUtil;


import java.util.ArrayList;


public class DataComponentWrapper extends HudComponent {

    private final ArrayList<DataComponent> dataComponents = new ArrayList<>();
    public DataComponentWrapper(float posX, float posY) {
        super(posX, posY);
        dataComponents.add(new TimeComponent());
        dataComponents.add(new BPSDataComponent());
        dataComponents.add(new PotionDataComponent());
    }


    @Override
    public void render(HudFont font) {
        float yStack = 0;
        for (DataComponent dataComponent : dataComponents) {
            if (dataComponent.enabled) {
                dataComponent.render(font, posX, posY + yStack);
                yStack += dataComponent.lines * font.getFontHeight() + 4;
            }
        }
    }
}
