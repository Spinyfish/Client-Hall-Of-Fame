package com.alan.clients.module.impl.other;

import com.alan.clients.api.Rise;
import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;

@Rise
@ModuleInfo(name = "module.other.securityfeatures.name", description = "module.other.securityfeatures.description", category = Category.OTHER, autoEnabled = true)
public final class SecurityFeatures extends Module {
}
