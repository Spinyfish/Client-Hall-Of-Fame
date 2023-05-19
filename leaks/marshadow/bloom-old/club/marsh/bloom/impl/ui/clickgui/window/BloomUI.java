package club.marsh.bloom.impl.ui.clickgui.window;

import java.awt.Color;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import club.marsh.bloom.impl.utils.other.Handle;
import org.lwjgl.opengl.GL11;

import club.marsh.bloom.Bloom;
import club.marsh.bloom.api.module.Category;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.impl.utils.render.RenderUtil;
import club.marsh.bloom.impl.utils.render.FontRenderer;
import club.marsh.bloom.impl.utils.other.MathUtils;
import club.marsh.bloom.api.value.BooleanValue;
import club.marsh.bloom.api.value.ModeValue;
import club.marsh.bloom.api.value.NumberValue;
import club.marsh.bloom.api.value.Value;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;

public class BloomUI extends GuiScreen {
	FontRenderer fr = Bloom.INSTANCE.fontManager.defaultFont;
	public BloomUI() {
    	int index = 5;
        for (Category category : Category.values()) {

            selectedCategory = category;
            categoryy = y+(index*5);
            index += 3;
        }
	}
	int x = 20;
    int y = 20;
    int y2 = 370;
    private double sliderWidth;
    int x2 = 350;
    boolean isHolding = false;
    int holdingButton = -1;
    private NumberValue sliding;
    Category selectedCategory = Category.COMBAT;
    int categoryy = y+60;
    public void handle(int mouseX, int mouseY, int button, Handle type) {
        int expandcount = 0;
        for (Module module : Bloom.INSTANCE.moduleManager.getModulesByCategory(selectedCategory)) {
        	if(module.isExpanded()) {
        		if (++expandcount > 0) {
        			for (Module mod : Bloom.INSTANCE.moduleManager.getModulesByCategory(selectedCategory)) {
        				mod.setExpanded(false);
        			}
        			module.setExpanded(true);
        			expandcount = 0;
        		}
        	}
        }
        switch (type) {
            case DRAW: {
                int index = 5;
                RenderUtil.prepareScissorBox(x,y,y2,x2);
                drawGradientRect(x,y,x2,y2, new Color(70,70,70,125).getRGB(),new Color(50,50,50,100).getRGB());
                //BlurUtil.blur(x,y,x2,y2,5);
                //RenderUtil.drawLeftRounded(x,y,x+70,y2,20,Color.darkGray);
                drawHorizontalLine(x,x2-1,y+15, Color.white.getRGB());
                RenderUtil.drawRoundedRect(x,categoryy-2,x+70,categoryy+fontRendererObj.FONT_HEIGHT+1,10,new Color(140, 68, 240, 255));
                drawVerticalLine(x+70,y+14,y2,Color.white.getRGB());
                if (isHolding) {
                    if (isHovered(x,y,x2,y+15,mouseX,mouseY)) {
                        x = mouseX+((x-x2)/2);
                        y = mouseY-10;
                    	for (Category category : Category.values()) {
                            if (category == selectedCategory) {
                                categoryy = y+(index*5);
                            }
                            index += 3;
                        }
                        x2 = x + 350;
                        y2 = y + 350;
                    }



                    //select category
                    for (Category category : Category.values()) {

                        if (isHovered(x,y+(index*5),x+70,(y+20)+(index*10),mouseX,mouseY)) {
                            selectedCategory = category;
                            categoryy = y+(index*5);
                        }
                        index += 3;
                    }

                }
                drawCategory();
                int y3 = 0;
                for (Module module : Bloom.INSTANCE.moduleManager.getModulesByCategory(selectedCategory)) {
                    fr.drawStringWithShadow(module.getName(),x+75,y+y3+20,module.toggled ? new Color(0,255,255) : new Color(255,255,255));
                    y3 += 10;
                }
                fr.drawString("bloom",x,y+7,-1);
                break;
            }
            case CLICK: {
                isHolding = true;
                holdingButton = button;
                int y3 = 0;
                for (Module module : Bloom.INSTANCE.moduleManager.getModulesByCategory(selectedCategory)) {
                        if (isHovered((float) (x+75), (float) (y+y3+20), (float) ((x+75)+fr.getWidth(module.getName())),y+y3+30,mouseX,mouseY)){
                            if (button == 1) {
                                module.setExpanded(!module.isExpanded());
                            }else{
                                module.setToggled(!module.isToggled());
                            }
                        }

                    y3 += 10;
                }
                break;
            }
            case RELEASE: {
                holdingButton = -1;
                isHolding = false;
                break;
            }
        }
        int left = (int) (x-25);
        for (Module module : Bloom.INSTANCE.moduleManager.getModulesByCategory(selectedCategory)) {
            if (module.isExpanded()) {
                int count = 0;
                int index = 0;
                for (Value value : Bloom.INSTANCE.valueManager.getAllValuesFrom(module.getName())) {
                    if (!value.isVisible())
                        continue;
                    int top = (int) (y2-15);
                    //System.out.println(top - (count-24) + "");
                    top = top - ((++index)*20);
                    count += 12;
                    if (value instanceof NumberValue) {
                        NumberValue set = (NumberValue) value;
                        Number min = set.getMin();
                        Number max = set.getMax();
                        Number val = ((Number) set.getObject());
                        double renderWidth = (88) * (val.doubleValue() - min.doubleValue()) / (max.doubleValue() - min.doubleValue());
                        float translationFactor = (float) (14.4 / Minecraft.getDebugFPS());
                        final double offset = fr.getWidth(set.getName()) + 105;
                        if(Math.abs(renderWidth - set.getTranslate().x) >= 1) {
                            set.getTranslate().interpolate(renderWidth, 0, translationFactor);
                        }


                        boolean hovered = isHovered((float) (left + offset), top + count + 11, left + 90 + 300, top + count + 16, mouseX, mouseY);
                        RenderUtil.drawRoundedRect((int) (left + offset), top + count + 12, (int) (left + offset + 88), (int) (top + count + 15), 0D, new Color(68, 96, 140, 255));
                        //Gui.drawRect(left + 308, top + index + 20, right + 220 + 88, top + index + 25, hovered ?  new Color(30, 30, 45).getRGB(): new Color(30, 30, 45).brighter().getRGB());

                        GL11.glColor4f(66, 123, 184, 1);
                        final double theLeft = (left + set.getTranslate().x);


                        //Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("Bloom/gui/SliderDot.png"));
                        //Gui.drawModalRectWithCustomSizedTexture((int) (MathUtils.roundToPlace(theLeft + offset, 4)), top + count + 11, 5, 5, 5, 5, 5, 5);
                        Gui.drawRect((int) (MathUtils.roundToPlace(theLeft + offset, 4)), top + count + 11, (int) (MathUtils.roundToPlace(theLeft + offset, 4)) + 5, top + count + 16, new Color(255,255,255).getRGB());
                        fr.drawString(set.getName(), left + 100, (float) round(top + count + 10, 4), -1);
                        fr.drawString(set.getObject() + "", (float) (left + offset + 95), (float) round(top + count + 10, 4), -1);


                        if (hovered && type == Handle.CLICK && button == 0) {
                            sliding = set;
                            this.sliderWidth = offset;
                        }
                        if (type == Handle.RELEASE && button == 0) {
                            sliding = null;
                        }

                    }else if (value instanceof BooleanValue) {
                        BooleanValue set = (BooleanValue) value;
                        boolean hovered = isHovered(left + 100, top + count + 10, (left + 98) + 300, top + count + 20, mouseX, mouseY);

                        fr.drawString(value.getName(), left + 110, (float) round(top + count + 10, 4), -1);

                        //if (set.getObject()) {
                        //    Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("Bloom/gui/SliderDot.png"));
                        //    Gui.drawModalRectWithCustomSizedTexture((int) (fr.getWidth(value.getName()) + left + 103.5), (int) (top + count + 10.5), 10, 5, 5, 5, 5, 5);
//
                        //}
                        Gui.drawRect(left + 100, top + count + 10, left + 110, top + count + 18, !set.getObject() ? new Color(0,0,0,255).getRGB() : Color.white.getRGB());
                        //RenderUtil.drawCircle(fr.getWidth(value.getName()) + left + 106, round(top + count + 13, 8), 3, new Color(0,0,0));
                        //Gui.drawRect(left + 300, top + index + 30, right + 400, top + index + 50, color.darker().getRGB());
                        ///Gui.drawRect(left + 400, top + index, right + 300, top + 1, new Color(255, 255, 255).getRGB());
                        if (hovered && type == Handle.CLICK && button == 0) {
                            set.flip();
                        }
                    }else if (value instanceof ModeValue) {
                    	ModeValue set = (ModeValue) value;
                        boolean hovered = isHovered(left + 100, top + count + 10, (left + 88) + 300, top + count + 20, mouseX, mouseY);

                        fr.drawString(value.hitboxname, left + 100, (float) round(top + count + 10, 4), -1);
                        //Gui.drawRect(left + 300, top + index + 30, right + 400, top + index + 50, color.darker().getRGB());
                        ///Gui.drawRect(left + 400, top + index, right + 300, top + 1, new Color(255, 255, 255).getRGB());
                        if (hovered && type == Handle.CLICK) {
                        	if (button == 0)
                        		set.cycle();
                        	else if (button == 1)
                        		set.cycleReverse();
                        }
                    }
                }
            }
        }
        double diff = Math.min(88, Math.max(0, mouseX - (left + sliderWidth)));
        if (sliding != null) {

            Number min = sliding.getMin();
            Number max = sliding.getMax();
            Number val = ((Number) sliding.getObject());
            if (diff == 0) {
                sliding.setObject(sliding.getMin());
            } else {
                double renderWidth = (66) * (val.doubleValue() - min.doubleValue()) / (max.doubleValue() - min.doubleValue());
                if (sliding.getObject() instanceof Integer) {

                    int newValue = (int) MathUtils.roundToPlace(((diff / 88) * (max.intValue() - min.intValue()) + min.intValue()), 2);
                    sliding.setObject(newValue);
                } else if (sliding.getObject() instanceof Float) {
                    double newValue = MathUtils.roundToPlace(((diff / 88) * (max.floatValue() - min.floatValue()) + min.floatValue()), 2);
                    sliding.setObject((float) newValue);
                } else if (sliding.getObject() instanceof Long) {
                    double newValue = MathUtils.roundToPlace(((diff / 88) * (max.longValue() - min.longValue()) + min.longValue()), 2);
                    sliding.setObject((long) newValue);
                } else if (sliding.getObject() instanceof Double) {
                    double newValue = MathUtils.roundToPlace(((diff / 88) * (max.doubleValue() - min.doubleValue()) + min.doubleValue()), 2);
                    sliding.setObject(newValue);
                }


            }
        }
    }



    @Override
	public boolean doesGuiPauseGame() {
		// TODO Auto-generated method stub
		return false;
	}


    void drawCategory() {
        int index = 5;
        for (Category category : Category.values()) {
            float width = 0;
            int count = 1+(index-5);
            fr.drawString(category.name(),x+2,y + (index * 5),-1);
            index += 3;
        }
    }


















    //don't want to look at these

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        handle(mouseX, mouseY, -1, Handle.DRAW);
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        handle(mouseX, mouseY, mouseButton, Handle.CLICK);
    }

    protected void mouseReleased(int mouseX, int mouseY, int state) {
        handle(mouseX, mouseY, state, Handle.RELEASE);

    }

    public boolean isHovered(float left, float top, float right, float bottom, int mouseX, int mouseY) {
        return mouseX >= left && mouseY >= top && mouseX < right && mouseY < bottom;
    }

    public static double round(final double value, final int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
    }
    @Override
    public void initGui() {

    }
    class Point {
        public final float x;
        public final float y;

        public Point(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }
    }

}
