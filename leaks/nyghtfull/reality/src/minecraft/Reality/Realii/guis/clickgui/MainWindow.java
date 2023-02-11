package Reality.Realii.guis.clickgui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import Reality.Realii.Client;
import Reality.Realii.guis.font.FontLoaders;
import Reality.Realii.guis.font.UnicodeFontRenderer;
import Reality.Realii.mods.Module;
import Reality.Realii.mods.ModuleType;
import Reality.Realii.utils.cheats.world.TimerUtil;
import Reality.Realii.utils.render.RenderUtil;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class MainWindow extends GuiScreen {
    public static float x = 100;
    public static float y = 100;
    public static float width = 700;
    public static float height = 200;

    public static Color mainColor = new Color(55, 171, 255);

    public boolean drag;
    public float dragX = 0, dragY = 0;

    public static ModuleType currentCategory = ModuleType.Combat;

    public TimerUtil timer = new TimerUtil();

    public float lastPercent;
    public float percent;
    public float percent2;
    public float lastPercent2;
    public float outro;
    public float lastOutro;
//    private boolean close = false;
//    private boolean closed;

    public static ArrayList<ModuleWindow> mods = new ArrayList<ModuleWindow>();
    private float rollX = 0;
    private float totalWidth;
    private float rollX2;

    public float smoothTrans(double current, double last) {
        return (float) (current + (last - current) / (mc.debugFPS / 10));
    }

    @Override
    public void initGui() {
        super.initGui();

//        if (mods.size() == 0) {
        mods.clear();
        for (Module module : Client.instance.getModuleManager().modules) {
            mods.add(new ModuleWindow(module, x, y + 50, mainColor));
        }
//        }


        percent = 1.33f;
        lastPercent = 0.98f;

        percent2 = 0.98f;
        lastPercent2 = 1f;

        outro = 1;
        lastOutro = 1;
        mainColor = new Color(106, 145, 235);

    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
//        if ((!closed && keyCode == Keyboard.KEY_ESCAPE)) {
//            percent = percent2;
//            close = true;
//            return;
//        }


        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        ScaledResolution sr = new ScaledResolution(mc);
//        float outro = smoothTrans(this.outro, lastOutro);
//        if (mc.currentScreen == null) {
//            GlStateManager.translate(sr.getScaledWidth() / 2, sr.getScaledHeight() / 2, 0);
//            GlStateManager.scale(outro, outro, 0);
//            GlStateManager.translate(-sr.getScaledWidth() / 2, -sr.getScaledHeight() / 2, 0);
//        }


        //animation
        //è°ƒè‰²æ�¿
//        for (int i = 0; i < 100; i++) {
//            int color1 = Color.HSBtoRGB(0.5f, i / 100f, i / 100f);
//            int color2 = Color.HSBtoRGB(0.5f, 0, i / 100f);
//
//            RenderUtil.drawGradientSideways(0, 100 - y, 100, 100 - i - 1, color2, color1);
//        }

//        percent = smoothTrans(this.percent, lastPercent);
//
//        if (!close) {
//            if (percent > 0.981) {
//                GlStateManager.translate(sr.getScaledWidth() / 2, sr.getScaledHeight() / 2, 0);
//                GlStateManager.scale(percent, percent, 0);
//                GlStateManager.translate(-sr.getScaledWidth() / 2, -sr.getScaledHeight() / 2, 0);
//            } else {
//                percent2 = smoothTrans(this.percent2, lastPercent2);
//                GlStateManager.translate(sr.getScaledWidth() / 2, sr.getScaledHeight() / 2, 0);
//                GlStateManager.scale(percent2, percent2, 0);
//                GlStateManager.translate(-sr.getScaledWidth() / 2, -sr.getScaledHeight() / 2, 0);
//            }
//        } else {
//            GlStateManager.translate(sr.getScaledWidth() / 2, sr.getScaledHeight() / 2, 0);
//            GlStateManager.scale(percent, percent, 0);
//            GlStateManager.translate(-sr.getScaledWidth() / 2, -sr.getScaledHeight() / 2, 0);
//        }
//
//
//        if (percent <= 1.5 && close) {
//            percent = smoothTrans(this.percent, 2);
////            percent2 = smoothTrans(this.percent2, 2);
//        }
//
//        if (percent >= 1.4 && close) {
//            mc.currentScreen = null;
//            mc.mouseHelper.grabMouseCursor();
//            mc.inGameHasFocus = true;
//        }
        //animation 2

//        if(x != x1){
//            x += (x1 - x) / 30;
//        }
//        if(y != y1){
//            y += (y1 - y) / 30;
//        }

        width = 500;
        height = 300;
        if (dragX != 0 && drag) {
            x = mouseX - dragX;
        }

        if (dragY != 0 && drag) {
            y = mouseY - dragY;
        }

        float wheel = Mouse.getDWheel();
        if (isHovered(x, y + 35, x + width, y + height, mouseX, mouseY)) {//æ»šåŠ¨
            if (wheel > 0 && rollX < 0) {
                rollX += 16;
            } else if (wheel < 0 && rollX + x + totalWidth > x + width + 20) {
                rollX -= 16;
            }
        }

        if (isHovered(x, y, x + 70, y + 35, mouseX, mouseY) && Mouse.isButtonDown(0)) {//æ‹–åŠ¨ä¸»çª—å�£
            drag = true;
            if (dragX == 0) {
                dragX = mouseX - x;
            }
            if (dragY == 0) {
                dragY = mouseY - y;
            }

        } else if (dragX != 0 || dragY != 0) {
            dragX = 0;
            dragY = 0;
            drag = false;
        }
        //Background
        RenderUtil.drawRoundedRect(x, y, x + width, y + height, 2, new Color(237, 240, 244).getRGB());
        RenderUtil.drawRoundedRect(x, y, x + width, y + 35, 2, new Color(255, 255, 255).getRGB());

        FontLoaders.arial24.drawString(Client.CLIENT_NAME, x + 18, y + 15, mainColor.getRGB());

        UnicodeFontRenderer cateFont = Client.fontLoaders.msFont18;

        float cateX = x + 90;
        for (ModuleType e : ModuleType.values()) {
            cateFont.drawString(e.name(), cateX, y + 15, e == currentCategory ? mainColor.getRGB() : new Color(170, 170, 170).getRGB());

            if (isHovered(cateX, y, cateX + cateFont.getStringWidth(e.name()), y + 35, mouseX, mouseY) && Mouse.isButtonDown(0) && timer.delay(200) && currentCategory != e) {
                mods.clear();
                for (Module module : Client.instance.getModuleManager().modules) {
                    mods.add(new ModuleWindow(module, 0, 0, mainColor));
                }
                totalWidth = 0;
                rollX = 0;
//                rollX2 = 0;
                currentCategory = e;
                timer.reset();
            }

            cateX += cateFont.getStringWidth(e.name()) + 15;
        }

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        RenderUtil.doGlScissor(x, y + 35, width, height);
        //Slider
        if (width / totalWidth <= 1) {
            RenderUtil.drawRect(x + ((-rollX2) / totalWidth) * width + 20, y + height - 3, x + ((-rollX2) / totalWidth) * width + width / totalWidth * width - 20, y + height - 1.8f, new Color(106, 145, 235, 200));
        }

        if (rollX2 != rollX) {
            rollX2 += (rollX - rollX2) / 10f;
        }

        //åŠŸèƒ½åˆ—è¡¨
        float modsX = x + 10, modsY = y + 45;
        for (ModuleWindow mw : mods) {
            if (mw.mod.type == currentCategory) {

                if (modsY + mw.height2 + 25 + 10 > y + height) {
                    modsX += 110;
                    modsY = y + 45;
                }
                if (totalWidth < modsX) {
                    totalWidth = modsX + 110;
                }


                mw.x = modsX + (int) (rollX2 * 10) / 10f;

                if (mw.y == 0) {
                    mw.y = modsY + 15;
                }
                if (Math.abs(mw.y - modsY) < 0.1) {
                    mw.y = modsY;
                }
                if (!drag) {
                    if (mw.y != modsY) {
                        mw.y += (modsY - mw.y) / 30;
                    }
                } else {
                    mw.y = modsY;
                }


//                mw.x = modsX;
//                mw.y = modsY;
                mw.drawModule(mouseX, mouseY);
                if (modsY + mw.height2 <= y + height) {
                    modsY += mw.height2 + (mw.mod.values.size() == 0 ? 25 : 20) + 10;
                }
            }
        }


        GL11.glDisable(GL11.GL_SCISSOR_TEST);

    }

    public static boolean isHovered(float x, float y, float x1, float y1, float mouseX, float mouseY) {
        if (mouseX > x && mouseY > y && mouseX < x1 && mouseY < y1) {
            return true;
        }
        return false;
    }

}
