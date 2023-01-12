package dev.eternal.client.module.impl.render;

import dev.eternal.client.Client;
import dev.eternal.client.font.FontManager;
import dev.eternal.client.font.FontType;
import dev.eternal.client.font.renderer.TrueTypeFontRenderer;
import dev.eternal.client.module.Module;
import dev.eternal.client.module.api.ModuleInfo;
import dev.eternal.client.module.impl.render.hud.*;
import dev.eternal.client.property.impl.ColorSetting;
import dev.eternal.client.property.impl.ModeSetting;
import lombok.Getter;

import java.awt.*;

@ModuleInfo(name = "HUD", description = "In game GUI", category = Module.Category.RENDER)
@Getter
public class HUD extends Module {

  private final TrueTypeFontRenderer normal = FontManager.getFontRenderer(FontType.ROBOTO_REGULAR, 18);
  private final TrueTypeFontRenderer large = FontManager.getFontRenderer(FontType.ROBOTO_REGULAR, 24);
  public final ColorSetting colorSetting = new ColorSetting(this, "Color", new Color(255, 50, 50));

  private final ModeSetting mode = new ModeSetting(this, "Mode",
      new EternalMode(this, "Eternal"),
      new OGDortwareMode(this, "OG Dortware"),
      new OldDortwareMode(this, "Old Dortware"),
      new VaziakMode(this, "Vaziak"),
      new DortwareMode(this, "Dortware"));

  public HUD() {
    toggle();
  }

}
