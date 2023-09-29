package juul.ui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import juul.util.Utils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.TextureImpl;

import juul.Juul;
import juul.font.CustomFontRenderer;
import juul.font.FontUtil;
import juul.module.Module;
import juul.module.Module.Category;
import juul.module.settings.BooleanSetting;
import juul.module.settings.NumberSetting;
import juul.module.settings.RangeSetting;
import juul.module.settings.Setting;
import juul.util.image.Animation;
import juul.util.image.Draw;
import juul.util.image.Resources;
import juul.util.image.TextureImage;
import net.minecraft.client.gui.GuiScreen;

public class GUI extends GuiScreen implements Utils {

    int categoryIndex, mouseX, mouseY;
    public Animation categoryTrans = new Animation(), introTrans = new Animation();
    public boolean closing;
    public Module keyBinding;
    public Setting dragging;
    public Setting hovering;
    public boolean end;
    public String tooltip = "";

    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        introTrans.set(0);
        closing = false;
        dragging = null;
        hovering = null;
        end = false;
        keyBinding = null;
    }

    public void onTick() {
        if (introTrans.getPosition() < 0.1 && closing) {
            Utils.mc.displayGuiScreen(null);
            return;
        }

        float left = width / 2f - 441 / 2f + 18,
                top = height / 2f - 336 / 2f + 15;

        categoryTrans.tick(categoryIndex, 1.5f);

        introTrans.tick(closing ? 0 : 1, 1.5f);


        int count = 0;
        for (Category category : Module.Category.values()) {
            float categoryLeft = left + 5 + 15,
                    categoryTop = top + 48 + (count * 45);

            category.fade.tick(isHovering(categoryLeft, categoryTop, 30, 30, mouseX, mouseY) ? 0.3f : 0, 2);

            count++;
        }

        List<Module> categoryModules = Juul.INSTANCE.manager.getModules(Module.Category.values()[categoryIndex]);
        for (Module module : categoryModules) {

            module.animation.tick(module.isEnabled() ? 1 : 0, 2);

            for (Setting setting : module.settings) {
                if (setting instanceof BooleanSetting) {
                    BooleanSetting booleanSetting = (BooleanSetting) setting;

                    booleanSetting.animation.tick(booleanSetting.isEnabled() ? 1 : 0, 2);
                }
                if (setting instanceof NumberSetting) {
                    NumberSetting numberSetting = (NumberSetting) setting;

                    numberSetting.animation.tick((float) numberSetting.getValue(), 1.5f);
                }
                if (setting instanceof RangeSetting) {
                    RangeSetting rangeSetting = (RangeSetting) setting;

                    rangeSetting.startAnimation.tick((float) rangeSetting.getStart(), 1.5f);
                    rangeSetting.endAnimation.tick((float) rangeSetting.getEnd(), 1.5f);
                }
            }
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;

        float left = width / 2f - 441 / 2f + 18,
                top = height / 2f - 336 / 2f + 15,

                moduleLeft = left + 75,
                moduleTop = top + 22.5f;

        int white = new Color(1f, 1f, 1f, introTrans.getPosition()).getRGB(),
                gray = new Color(0.725f, 0.733f, 0.745f, introTrans.getPosition()).getRGB(),
                darkGray = new Color(79 / 255f, 84 / 255f, 92 / 255f, introTrans.getPosition()).getRGB();

        GL11.glPushMatrix();
		/*GL11.glTranslated(width/2f, height/2f, 0);
		GL11.glScalef(introTrans.getPosition()/2f + 0.5f, introTrans.getPosition()/2f + 0.5f, 0);
		GL11.glTranslated(-(width/2f), -(height/2f), 0);*/

        GL11.glTranslatef(0, introTrans.getPosition() * height / 3f - height / 3f, 0);

        drawBackground(left, top);

        drawCategories(left, top);

        GL11.glEnable(GL11.GL_BLEND);

        List<Module> categoryModules = Juul.INSTANCE.manager.getModules(Module.Category.values()[categoryIndex]);
        float yPos = 0;
        float xPos = 0;
        List<Module> lastModules = new ArrayList<Module>();
        int settingsIndex = 0;
        float threshold = 210;
        float paragraphThreshold = 72.5f;
        for (Module module : categoryModules) {
			
			/*for(String s : FontUtil.book_14.wrapWords(module.description, paragraphThreshold)) {
				yPos += 6.5f;
			}*/

            yPos += 7.5f + 10.5f;

            settingsIndex = 0;
            for (Setting setting : module.settings) {
                if (setting instanceof RangeSetting) {
                    RangeSetting rangeSetting = (RangeSetting) setting;

                    //yPos += 10;
                    yPos += 6.5f;
					
					/*for(String s : FontUtil.book_14.wrapWords(setting.getDescription(), 67.5f)) {
						yPos += 6.5f;
					}*/

                    yPos += 12.5f;
                    yPos += 4.5f + 10.5f;
                }
                if (setting instanceof NumberSetting) {
                    NumberSetting numberSetting = (NumberSetting) setting;

                    //if(!setting.getName().equals("Range"))
                    //yPos += 8;
                    yPos += 6.5f;
					
					/*for(String s : FontUtil.book_14.wrapWords(setting.getDescription(), 67.5f)) {
						yPos += 6.5f;
					}*/

                    yPos += 6.5f + 16 - 10.5f;
                    yPos += 15.5f;
                }
                if (setting instanceof BooleanSetting) {
                    BooleanSetting booleanSetting = (BooleanSetting) setting;
                    boolean toggled = booleanSetting.isEnabled();

                    //yPos += 18;
					
					/*for(String s : FontUtil.book_14.wrapWords(setting.getDescription(), paragraphThreshold)) {
						yPos += 6.5f;
					}*/

                    //yPos -= 2.5f + 7.5f;
                    yPos += 14.5f + ((module.settings.size() > settingsIndex + 1 && !(module.settings.get(settingsIndex + 1) instanceof BooleanSetting)) || settingsIndex + 1 == module.settings.size() ? 4 : 0);
                }

                settingsIndex++;

                if (settingsIndex == module.settings.size())
                    yPos += 4;

                if (yPos > threshold) {
                    xPos += 125 - 6;
                    yPos = 0;
                }
            }

            if (settingsIndex == 0)
                yPos += 4;

            yPos += 5;

            if (yPos > threshold) {
                xPos += 125 - 6;
                yPos = 0;
            }
        }
        yPos = 0;
        xPos = 0;
        if (!categoryModules.isEmpty()) {
            lastModules.add(categoryModules.get(categoryModules.size() - 1));
        }
        for (Module module : categoryModules) {
            GL11.glColor4f(1, 1, 1, introTrans.getPosition());
            FontUtil.semibold_18.drawNoBSString(module.name, moduleLeft + xPos, moduleTop + yPos, white);
            drawToggleSwitch(moduleLeft + 77.5f + xPos + 2 - 14.5f, moduleTop + yPos - 1.5f - 2.5f, yPos, xPos, module);

            if (isHoveringString(FontUtil.semibold_18, module.name, moduleLeft + xPos, moduleTop + yPos, mouseX, mouseY))
                tooltip = module.description;

            GL11.glEnable(GL11.GL_BLEND);

            //Keybind
            GL11.glColor4f(1, 1, 1, introTrans.getPosition());
            drawSprite(moduleLeft + 79f + 25 + xPos - 14.5f, moduleTop + yPos + 1 - 2.5f, 92.5f, isHovering(moduleLeft + 79f + 25 + xPos - 14.5f, moduleTop + yPos + 1 - 2.5f, 10, 10, mouseX, mouseY) || keyBinding == module ? 10 : 0, 102, 44.5f, 10, 10, "components.png");
            GL11.glEnable(GL11.GL_BLEND);

            FontUtil.semibold_14.drawNoBSString(module.getKeyCharacter(), moduleLeft + 79f + xPos + 25 + 4 - FontUtil.semibold_14.getStringWidth(module.getKeyCharacter()) / 2f + 1.5f - 14.5f, moduleTop + yPos + 1 + 3f - 2.5f, white);
			
			/*for(String s : FontUtil.book_14.wrapWords(module.description, paragraphThreshold)) {
				GlStateManager.color(1, 1, 1, introTrans.getPosition());
				FontUtil.book_14.drawNoBSString(s.replace("\uFFFF", "").replace("\u00A7", ""), moduleLeft + xPos, moduleTop + yPos + 10.5f, gray);
				yPos += 6.5f;
			}*/

            GL11.glColor4f(1, 1, 1, introTrans.getPosition());

            yPos += 7.5f + 10.5f;

            settingsIndex = 0;
            for (Setting setting : module.settings) {
                if (setting instanceof RangeSetting) {
                    RangeSetting rangeSetting = (RangeSetting) setting;

                    //yPos += 10;
                    GL11.glColor4f(1, 1, 1, introTrans.getPosition());
                    FontUtil.semibold_16.drawNoBSString(setting.getName(), moduleLeft + xPos, moduleTop + yPos, white);

                    if (isHoveringString(FontUtil.semibold_16, setting.getName(), moduleLeft + xPos, moduleTop + yPos, mouseX, mouseY))
                        tooltip = setting.getDescription();


                    yPos += 6.5f;


                    yPos += 12.5f;

                    float x = moduleLeft + xPos,
                            y = moduleTop + yPos - 3.5f;

                    float startPercent = (float) ((rangeSetting.start - rangeSetting.minimum) / (rangeSetting.maximum - rangeSetting.minimum));
                    float endPercent = (float) ((rangeSetting.end - rangeSetting.minimum) / (rangeSetting.maximum - rangeSetting.minimum));
                    y += 4.5f;


                    //Click Start Grabber
                    if (dragging == null && this.isHovering(x - 4.5f + (100 * startPercent), y - 4.5f, 7, 14, mouseX, mouseY)) {
                        hovering = rangeSetting;
                        end = false;
                    }

                    //Click End Grabber
                    if (dragging == null && !(startPercent == 1 && endPercent == 1) && this.isHovering(x - 4.5f + (100 * endPercent), y - 4.5f, 7, 14, mouseX, mouseY)) {
                        hovering = rangeSetting;
                        end = true;
                    }

                    if (dragging != null && dragging == setting) {
                        double offset = (mouseX - (moduleLeft + xPos)) / 100d;
                        double percent = Math.max(0, Math.min(1, offset));
                        double precision = 1 / rangeSetting.increment;
                        double value = Math.round((rangeSetting.minimum + (percent * (rangeSetting.maximum - rangeSetting.minimum))) * precision) / precision;

                        if (end) {
                            if (value >= rangeSetting.start)
                                rangeSetting.end = value;

                        } else {
                            if (value <= rangeSetting.end)
                                rangeSetting.start = value;
                        }
                    }

                    drawRangeSlider(moduleLeft + xPos, moduleTop + yPos - 3.5f, white, yPos, xPos, rangeSetting);

                    yPos += 4.5f + 10.5f;
                }
                if (setting instanceof NumberSetting) {
                    NumberSetting numberSetting = (NumberSetting) setting;

                    GL11.glColor4f(1, 1, 1, introTrans.getPosition());
                    FontUtil.semibold_16.drawNoBSString(setting.getName(), moduleLeft + xPos, moduleTop + yPos, white);

                    if (isHoveringString(FontUtil.semibold_16, setting.getName(), moduleLeft + xPos, moduleTop + yPos, mouseX, mouseY))
                        tooltip = setting.getDescription();

                    yPos += 6.5f;

                    GL11.glColor4f(1, 1, 1, introTrans.getPosition());

                    yPos += 6.5f + 16 - 10.5f;

                    float x = moduleLeft + xPos,
                            y = moduleTop + yPos - 3.5f;

                    y += 4.5f;

                    float startPercent = (float) ((numberSetting.value - numberSetting.minimum) / (numberSetting.maximum - numberSetting.minimum));

                    if (dragging == null && this.isHovering(x - 3 + (100 * startPercent), y - 4.5f, 7, 14, mouseX, mouseY)) {
                        hovering = numberSetting;
                        end = false;
                    }

                    if (dragging != null && dragging == setting) {
                        double offset = (mouseX - (moduleLeft + xPos)) / 100d;
                        double percent = Math.max(0, Math.min(1, offset));
                        double precision = 1 / numberSetting.increment;

                        numberSetting.value = Math.round((numberSetting.minimum + (percent * (numberSetting.maximum - numberSetting.minimum))) * precision) / precision;

                    }

                    drawSlider(moduleLeft + xPos, moduleTop + yPos - 3.5f, white, yPos, xPos, numberSetting);

                    yPos += 15.5f;
                }
                if (setting instanceof BooleanSetting) {
                    BooleanSetting booleanSetting = (BooleanSetting) setting;

                    drawToggleSwitch(moduleLeft + xPos + 1, moduleTop + yPos - 1.5f, yPos, xPos, booleanSetting);

                    GL11.glEnable(GL11.GL_BLEND);

                    FontUtil.semibold_16.drawNoBSString(setting.getName(), moduleLeft + xPos + 26, moduleTop + yPos + 1.5f, white);

                    if (isHoveringString(FontUtil.semibold_16, setting.getName(), moduleLeft + xPos + 26, moduleTop + yPos + 1.5f, mouseX, mouseY))
                        tooltip = setting.getDescription();


                    GL11.glColor4f(1, 1, 1, introTrans.getPosition());

                    yPos += 14.5f + ((module.settings.size() > settingsIndex + 1 && !(module.settings.get(settingsIndex + 1) instanceof BooleanSetting)) || settingsIndex + 1 == module.settings.size() ? 4 : 0);
                }

                settingsIndex++;

                if (settingsIndex == module.settings.size())
                    yPos += 4;

                if (yPos > threshold) {
                    xPos += 125 - 6;
                    yPos = 0;
                }
            }

            if (settingsIndex == 0)
                yPos += 4;

            if (!lastModules.contains(module)) {
                drawRect(moduleLeft + xPos, moduleTop + yPos - 3.5f, moduleLeft + 115 + xPos - 14.5f, moduleTop + yPos - 3, darkGray);
                yPos += 5;
            }

            if (yPos > threshold) {
                xPos += 125 - 6;
                yPos = 0;
            }
        }

        FontUtil.book_14.drawNoBSString(tooltip, left + 75, top + 274, gray);

        tooltip = "";
        GL11.glPopMatrix();
    }

    public void drawTooltip(int mouseX, int mouseY, int gray, float paragraphThreshold) {
        float descriptionPos = 0;
        float longestStringLength = 0;
        GL11.glEnable(GL11.GL_BLEND);
        List<String> lines = FontUtil.book_14.wrapWords(tooltip, paragraphThreshold);
        for (String s : lines) {
            double length = FontUtil.book_14.getStringWidth(s);
            if (length > longestStringLength) {
                longestStringLength = (float) length;
            }
            descriptionPos += 6.5f;
        }
        drawRect(mouseX + 6, mouseY - 2, mouseX + 6 + longestStringLength, mouseY - 2 + descriptionPos, 0xff40444b);
        descriptionPos = 0;
        for (String s : lines) {
            glStateManager.color(1, 1, 1, introTrans.getPosition());
            FontUtil.book_14.drawNoBSString(s.replace("\uFFFF", "").replace("\u00A7", ""), mouseX + 8, mouseY + descriptionPos, gray);
            descriptionPos += 6.5f;
        }
    }

    public void drawCategories(float left, float top) {
        int count = 0;
        for (Category category : Module.Category.values()) {
            float categoryLeft = left + 5 + 15,
                    categoryTop = top + 48 + (count * 45);

            GL11.glColor4f(1, 1, 1, (1 - category.fade.getPosition()) * introTrans.getPosition());
            drawSprite(categoryLeft, categoryTop, count * 30, 0, 180, 30, 30, 30, "categorySheet.png");
            count++;
        }
    }

    public void drawBackground(float left, float top) {
        drawImage(left - 18, top - 15, 441, 336, "background.png", introTrans.getPosition());
        drawImage(left + 65, top + 12.5f, 330, 275, "innerBackground.png", introTrans.getPosition());
        drawImage(left + 19, top + 12.5f, 32.5f, 15, "guiLogo.png", introTrans.getPosition());

        drawImage(left + 5, top + 38 + (categoryTrans.getPosition() * 45), 60, 50, "card.png", introTrans.getPosition());
    }

    public void drawToggleSwitch(float x, float y, float yPos, float xPos, Module toggled) {
        //Color color = Color.getHSBColor((218 + 9 * toggled.animation.getPosition()) / 360f, (9 + 39 * toggled.animation.getPosition()) / 100f, (49 + 36 * toggled.animation.getPosition()) / 100f);
        Color color = toggled.toggled ? Juul.INSTANCE.THEME.color() : Color.gray;
        GL11.glColor4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, introTrans.getPosition());


        drawSprite(x - 2, y, 0, 0, 102, 44.5f, 25, 16, "components.png");
        GL11.glColor4f(1, 1, 1, introTrans.getPosition());
        drawSprite(x - 2 + toggled.animation.getPosition() * 10, y, 26, 0, 102, 44.5f, 14, 14, "components.png");
    }

    public void drawToggleSwitch(float x, float y, float yPos, float xPos, BooleanSetting toggled) {
        //Color color = Color.getHSBColor((218 + 9 * toggled.animation.getPosition()) / 360f, (9 + 39 * toggled.animation.getPosition()) / 100f, (49 + 36 * toggled.animation.getPosition()) / 100f);
        Color color = toggled.enabled ? Juul.INSTANCE.THEME.color() : Color.gray;
        GL11.glColor4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, introTrans.getPosition());

        drawSprite(x - 2, y, 0, 0, 102, 44.5f, 25, 16, "components.png");
        GL11.glColor4f(1, 1, 1, introTrans.getPosition());
        drawSprite(x - 2 + toggled.animation.getPosition() * 10, y, 26, 0, 102, 44.5f, 14, 14, "components.png");
    }

    public void drawSlider(float x, float y, int white, float yPos, float xPos, NumberSetting numberSetting) {
        float percent = (float) ((numberSetting.animation.getPosition() - numberSetting.minimum) / (numberSetting.maximum - numberSetting.minimum));
        y += 4.5f;

        GL11.glColor4f(79 / 255f, 84 / 255f, 92 / 255f, introTrans.getPosition());
        drawSprite(x, y, 2, 41f, 102, 44.5f, 100, 4, "components.png");
        //a
        Color c = Juul.INSTANCE.THEME.color();
        GL11.glColor4f(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, introTrans.getPosition());
        //
        drawSprite(x, y, 2, 41f, 102, 44.5f, 100 * percent, 4, "components.png");
        GL11.glColor4f(1, 1, 1, introTrans.getPosition());
        drawSprite(x - 4.5f + (100 * percent), y - 4.5f, 93, 21f, 102, 44.5f, 7, 14, "components.png");
        GL11.glEnable(GL11.GL_BLEND);
        String value = numberSetting.increment == 1 ? String.valueOf((int) numberSetting.value) : String.valueOf(numberSetting.value);
        FontUtil.semibold_10.drawNoBSString(value, x + (100 * percent) - FontUtil.semibold_10.getStringWidth(value) / 2f, y - 9, white);
    }

    public void drawRangeSlider(float x, float y, int white, float yPos, float xPos, RangeSetting rangeSetting) {
        float startPercent = (float) ((rangeSetting.startAnimation.getPosition() - rangeSetting.minimum) / (rangeSetting.maximum - rangeSetting.minimum));
        float endPercent = (float) ((rangeSetting.endAnimation.getPosition() - rangeSetting.minimum) / (rangeSetting.maximum - rangeSetting.minimum));
        y += 4.5f;


        GL11.glColor4f(79 / 255f, 84 / 255f, 92 / 255f, introTrans.getPosition());
        drawSprite(x, y, 2, 41f, 102, 44.5f, 100, 4, "components.png");

        // the part inbetween bounds
        Color c = Juul.INSTANCE.THEME.color();
        GL11.glColor4f(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, introTrans.getPosition());
        drawSprite(x + (100 * startPercent), y, 2, 41f, 102, 44.5f, (100 * endPercent) - (100 * startPercent), 4, "components.png");
        /////////////

        GL11.glColor4f(1, 1, 1, introTrans.getPosition());
        drawSprite(x - 4.5f + (100 * startPercent), y - 4.5f, 93, 21f, 102, 44.5f, 7, 14, "components.png");
        GL11.glEnable(GL11.GL_BLEND);

        String value = rangeSetting.increment == 1 ? String.valueOf((int) rangeSetting.start) : String.valueOf(rangeSetting.start);
        FontUtil.semibold_10.drawNoBSString(value, x + (100 * startPercent) - FontUtil.semibold_10.getStringWidth(value) / 2f, y - 9, white);

        GL11.glColor4f(1, 1, 1, introTrans.getPosition());
        drawSprite(x - 4.5f + (100 * endPercent), y - 4.5f, 93, 21f, 102, 44.5f, 7, 14, "components.png");
        GL11.glEnable(GL11.GL_BLEND);
        value = rangeSetting.increment == 1 ? String.valueOf((int) rangeSetting.end) : String.valueOf(rangeSetting.end);
        FontUtil.semibold_10.drawNoBSString(value, x + (100 * endPercent) - FontUtil.semibold_10.getStringWidth(value) / 2f, y - 9, white);
    }

    public void drawImage(float x, float y, float width, float height, String url, float opacity) {
        TextureImage logo = Resources.downloadTexture("https://intent.store/juul/" + url);

        if (logo.pixels != null) {
            TextureImpl.bindNone();
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            Draw.rectTexture(x, y, width, height, logo.getTexture(), new Color(1f, 1f, 1f, opacity).getRGB());
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
        }
    }

    public void drawSprite(float x, float y, float offsetX, float offsetY, float textureWidth, float textureHeight, float width, float height, String url) {
        TextureImage logo = Resources.downloadTexture("https://intent.store/juul/" + url);

        if (logo.pixels != null) {
            TextureImpl.bindNone();
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            Draw.rectTexture(x, y, offsetX, offsetY, textureWidth, textureHeight, width, height, logo.getTexture(), 0xffffffff);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (keyBinding != null)
            keyBinding = null;

        float left = width / 2f - 441 / 2f + 18,
                top = height / 2f - 336 / 2f + 15,

                moduleLeft = left + 75,
                moduleTop = top + 22.5f;

        int count = 0;
        for (Category category : Module.Category.values()) {
            float categoryLeft = left + 5 + 15,
                    categoryTop = top + 48 + (count * 45);

            if (isHovering(categoryLeft, categoryTop, 30, 30, mouseX, mouseY)) {
                categoryIndex = count;
                break;
            }
            count++;
        }

        List<Module> categoryModules = Juul.INSTANCE.manager.getModules(Module.Category.values()[categoryIndex]);
        float yPos = 0;
        float xPos = 0;
        List<Module> lastModules = new ArrayList<>();
        int settingsIndex;
        float threshold = 210;
        for (Module module : categoryModules) {

            yPos += 7.5f + 10.5f;

            settingsIndex = 0;
            for (Setting setting : module.settings) {
                if (setting instanceof RangeSetting) {

                    yPos += 6.5f;

                    yPos += 12.5f;
                    yPos += 4.5f + 10.5f;
                }
                if (setting instanceof NumberSetting) {
                    yPos += 6.5f;

                    yPos += 6.5f + 16 - 10.5f;
                    yPos += 15.5f;
                }
                if (setting instanceof BooleanSetting) {

                    yPos += 14.5f + ((module.settings.size() > settingsIndex + 1 && !(module.settings.get(settingsIndex + 1) instanceof BooleanSetting)) || settingsIndex + 1 == module.settings.size() ? 4 : 0);
                }

                settingsIndex++;

                if (settingsIndex == module.settings.size())
                    yPos += 4;

                if (yPos > threshold) {
                    xPos += 125 - 6;
                    yPos = 0;
                }
            }

            if (settingsIndex == 0)
                yPos += 4;

            yPos += 5;

            if (yPos > threshold) {
                xPos += 125 - 6;
                yPos = 0;
            }
        }
        yPos = 0;
        xPos = 0;
        if (!categoryModules.isEmpty()) {
            lastModules.add(categoryModules.get(categoryModules.size() - 1));
        }
        for (Module module : categoryModules) {
            if (this.isHovering(moduleLeft + 77.5f + xPos + 2 - 14.5f, moduleTop + yPos - 2.5f, 21, 12, mouseX, mouseY))
                module.toggle();

            if (isHovering(moduleLeft + 79f + 25 + xPos - 14.5f, moduleTop + yPos + 1 - 2.5f, 10, 10, mouseX, mouseY))
                keyBinding = module;

            yPos += 7.5f + 10.5f;

            settingsIndex = 0;
            for (Setting setting : module.settings) {
                if (setting instanceof RangeSetting) {
                    RangeSetting rangeSetting = (RangeSetting) setting;

                    yPos += 6.5f;


                    yPos += 12.5f;

                    float x = moduleLeft + xPos,
                            y = moduleTop + yPos - 3.5f;

                    float startPercent = (float) ((rangeSetting.start - rangeSetting.minimum) / (rangeSetting.maximum - rangeSetting.minimum));
                    float endPercent = (float) ((rangeSetting.end - rangeSetting.minimum) / (rangeSetting.maximum - rangeSetting.minimum));
                    y += 4.5f;

                    //Click Start Grabber
                    if (this.isHovering(x - 4.5f + (100 * startPercent), y - 4.5f, 7, 14, mouseX, mouseY)) {
                        dragging = rangeSetting;
                        end = false;
                    }

                    //Click End Grabber
                    if (!(startPercent == 1 && endPercent == 1) && this.isHovering(x - 4.5f + (100 * endPercent), y - 4.5f, 7, 14, mouseX, mouseY)) {
                        dragging = rangeSetting;
                        end = true;
                    }

                    yPos += 4.5f + 10.5f;
                }
                if (setting instanceof NumberSetting) {
                    NumberSetting numberSetting = (NumberSetting) setting;

                    yPos += 6.5f;
                    yPos += 6.5f + 16 - 10.5f;

                    float x = moduleLeft + xPos,
                            y = moduleTop + yPos - 3.5f;

                    y += 4.5f;

                    float startPercent = (float) ((numberSetting.value - numberSetting.minimum) / (numberSetting.maximum - numberSetting.minimum));

                    if (this.isHovering(x - 3 + (100 * startPercent), y - 4.5f, 7, 14, mouseX, mouseY)) {
                        dragging = numberSetting;
                        end = false;
                    }

                    //Handle Slider
                    //drawSlider(moduleLeft + xPos, moduleTop + yPos - 3.5f, white, yPos, xPos, numberSetting);

                    yPos += 15.5f;
                }
                if (setting instanceof BooleanSetting) {
                    BooleanSetting booleanSetting = (BooleanSetting) setting;

                    //Handle Toggle
                    if (this.isHovering(moduleLeft + xPos + 1, moduleTop + yPos, 21, 12, mouseX, mouseY))
                        booleanSetting.toggle();

                    yPos += 14.5f + ((module.settings.size() > settingsIndex + 1 && !(module.settings.get(settingsIndex + 1) instanceof BooleanSetting)) || settingsIndex + 1 == module.settings.size() ? 4 : 0);
                }

                settingsIndex++;

                if (settingsIndex == module.settings.size())
                    yPos += 4;

                if (yPos > threshold) {
                    xPos += 125 - 6;
                    yPos = 0;
                }
            }

            if (settingsIndex == 0)
                yPos += 4;

            if (!lastModules.contains(module)) {
                yPos += 5;
            }

            if (yPos > threshold) {
                xPos += 125 - 6;
                yPos = 0;
            }
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        dragging = null;
        end = false;
    }

    public void keyTyped(char character, int keyCode) {
        if (keyCode == 1 && keyBinding == null)
            closing = true;
        else if (keyCode == 1)
            keyBinding = null;

        if (keyBinding != null) {
            if (character == ' ') {
                keyBinding.keyBind = 0;
                keyBinding = null;
                return;
            }

            keyBinding.keyBind = keyCode;
            keyBinding = null;
        }


        if (hovering != null && (keyCode == 203 || keyCode == 208 || keyCode == 200 || keyCode == 205)) {
            boolean negative = keyCode == 203 || keyCode == 208;
            if (hovering instanceof RangeSetting) {
                RangeSetting rangeSetting = (RangeSetting) hovering;
                rangeSetting.increment(!end, !negative);
            }
            if (hovering instanceof NumberSetting) {
                NumberSetting numberSetting = (NumberSetting) hovering;
                numberSetting.increment(!negative);
            }
        }
    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        Juul.INSTANCE.manager.getModule(juul.module.render.GUI.class).disable();
    }

    public boolean isHoveringString(CustomFontRenderer font, String text, float x, float y, int mouseX, int mouseY) {
        return mouseX >= x && mouseY >= y && x + font.getStringWidth(text) > mouseX && y + font.getHeight() > mouseY;
    }

    public boolean isHovering(float x, float y, float width, float height, int mouseX, int mouseY) {
        return mouseX >= x && mouseY >= y && x + width > mouseX && y + height > mouseY;
    }

    public void drawRect(float left, float top, float right, float bottom, int color) {
        float other;

        if (left < right) {
            other = left;
            left = right;
            right = other;
        }

        if (top < bottom) {
            other = top;
            top = bottom;
            bottom = other;
        }

        float alpha = (color >> 24 & 255) / 255.0F;
        float red = (color >> 16 & 255) / 255.0F;
        float greeen = (color >> 8 & 255) / 255.0F;
        float blue = (color & 255) / 255.0F;

        glStateManager.enableBlend();
        glStateManager.disableTexture2D();
        glStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        glStateManager.color(red, greeen, blue, alpha);

        worldRenderer.startDrawingQuads();
        worldRenderer.addVertex(left, bottom, 0.0D);
        worldRenderer.addVertex(right, bottom, 0.0D);
        worldRenderer.addVertex(right, top, 0.0D);
        worldRenderer.addVertex(left, top, 0.0D);
        tessalator.draw();

        glStateManager.enableTexture2D();
        glStateManager.disableBlend();
    }

}
