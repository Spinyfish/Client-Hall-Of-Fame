/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.module.visual;

import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;

public class CharacterRenderer
extends Module {
    private final Setting<Image> image = new Setting<Image>("Style", Image.ASTOLFO).describedBy("The image to appear in GUIs.");
    private final Setting<Boolean> showImagesInMinecraftGuis = new Setting<Boolean>("Show in Minecraft GUIs", true).describedBy("Whether to show the images in Minecraft GUIs.");

    public CharacterRenderer() {
        super("Character", "Render a character in GUIs.", Category.VISUAL);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    public boolean renderInMinecraftGuis() {
        return this.showImagesInMinecraftGuis.getValue();
    }

    public Setting<Image> getImage() {
        return this.image;
    }

    public static enum Image {
        ASTOLFO("Astolfo"),
        FELIX("Felix"),
        BARRY("Barry"),
        SIMON("Simon"),
        BLAHAJ("BL\u00c5HAJ"),
        TEE_GRIZZLY("Tee Grizzly (absolute retarded nn)"),
        KOBLEY("Kobley"),
        CONFIG_ISSUE("Config Issue"),
        SKEPPY("Skeppy"),
        MR_WOOD("Mr. Wood"),
        HAIKU("haiku :3");

        String character;

        public String toString() {
            return this.character;
        }

        private Image(String character) {
            this.character = character;
        }
    }
}

