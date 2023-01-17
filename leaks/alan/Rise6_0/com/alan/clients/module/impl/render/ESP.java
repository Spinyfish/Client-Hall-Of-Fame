package com.alan.clients.module.impl.render;

import com.alan.clients.api.Rise;
import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.value.impl.BooleanValue;
import com.alan.clients.module.impl.render.esp.GlowESP;

@Rise
@ModuleInfo(name = "ESP", description = "module.render.esp.description", category = Category.RENDER)
public final class ESP extends Module {

    private BooleanValue glowESP = new BooleanValue("Glow", this, false, new GlowESP("", this));

}
