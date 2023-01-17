package com.alan.clients.util.shader;

import com.alan.clients.util.shader.base.RiseShader;
import com.alan.clients.util.shader.impl.*;

public interface RiseShaders {
    AlphaShader ALPHA_SHADER = new AlphaShader();
    RiseShader POST_BLOOM_SHADER = new BloomShader();
    RiseShader UI_BLOOM_SHADER = new BloomShader();
    RiseShader GAUSSIAN_BLUR_SHADER = new GaussianBlurShader();
    RiseShader OUTLINE_SHADER = new OutlineShader();
    RQShader RQ_SHADER = new RQShader();
    RGQShader RGQ_SHADER = new RGQShader();
    ROQShader ROQ_SHADER = new ROQShader();
    ROGQShader ROGQ_SHADER = new ROGQShader();
    RiseShader MAIN_MENU_SHADER = new MainMenuBackgroundShader();
}
