package com.alan.clients.module.impl.render;

import com.alan.clients.api.Rise;
import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.module.impl.render.esp.ChamsESP;
import com.alan.clients.module.impl.render.esp.GlowESP;
import com.alan.clients.value.impl.BooleanValue;

@Rise
@ModuleInfo(name = "module.render.esp.name", description = "module.render.esp.description", category = Category.RENDER)
public final class ESP extends Module {

    private BooleanValue glowESP = new BooleanValue("Glow", this, false, new GlowESP("", this));
    private BooleanValue chamsESP = new BooleanValue("Chams", this, false, new ChamsESP("", this));

}
