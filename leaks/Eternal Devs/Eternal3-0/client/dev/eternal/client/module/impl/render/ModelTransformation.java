package dev.eternal.client.module.impl.render;

import dev.eternal.client.module.Module;
import dev.eternal.client.module.api.ModuleInfo;
import dev.eternal.client.property.impl.NumberSetting;

@ModuleInfo(name = "ModelTransformation", category = Module.Category.RENDER)
public class ModelTransformation extends Module {
  public NumberSetting x = new NumberSetting(this, "X Transformation", 0, -1, 1, 0.01);
  public NumberSetting y = new NumberSetting(this, "Y Transformation", 0, -1, 1, 0.01);
  public NumberSetting z = new NumberSetting(this, "Z Transformation", 0, -1, 1, 0.01);
}
