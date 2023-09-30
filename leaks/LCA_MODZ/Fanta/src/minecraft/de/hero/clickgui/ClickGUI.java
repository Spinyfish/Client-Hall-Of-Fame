package de.hero.clickgui;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import de.fanta.Client;
import de.fanta.clickgui.defaultgui.components.ConfigBox;
import de.fanta.clickgui.defaultgui.components.FontBox;
import de.fanta.module.Module;
import de.fanta.utils.RenderUtil;
import de.hero.clickgui.elements.Element;
import de.hero.clickgui.elements.ModuleButton;
import de.hero.clickgui.elements.menu.ElementSlider;
import de.hero.clickgui.util.ColorUtil;
import de.hero.clickgui.util.FontUtil;

public class ClickGUI extends GuiScreen {
	public static ArrayList<Panel> panels;
	public static ArrayList<Panel> rpanels;
	private ModuleButton mb = null;
	private ConfigBox configBox;
	private FontBox fontBox;
	de.fanta.utils.Translate translate;

	/*
	 * Konstrukor sollte nur einmal aufgerufen werden => in der MainMethode des
	 * eigenen Codes hier Client.startClient()
	 * 
	 * mc.displayGuiScreen(Client.clickgui); this.setToggled(false); das Module wird
	 * sofort wieder beendet damit nchstes mal nicht 2mal der z.B. 'RSHIFT' Knopf
	 * gedrckt werden muss
	 */
	public ClickGUI() {

		FontUtil.setupFontUtils();
		panels = new ArrayList<>();
		double pwidth = 80;
		double pheight = 15;
		double px = 10;
		double py = 10;
		double pyplus = pheight + 10;
		this.configBox = new ConfigBox(100, 100);
	//	this.fontBox = new FontBox(100, 200);
		/*
		 * Zum Sortieren der Panels einfach die Reihenfolge im Enum ndern ;)
		 */
		for (Module.Type c : Module.Type.values()) {
			String title = Character.toUpperCase(c.name().toLowerCase().charAt(0))
					+ c.name().toLowerCase().substring(1);
			ClickGUI.panels.add(new Panel(title, px, py, pwidth, pheight, false, this) {
				@Override
				public void setup() {
					for (Module m : Client.INSTANCE.moduleManager.modules) {
						if (!m.getType().equals(c))
							continue;
						this.Elements.add(new ModuleButton(m, this));
					}
				}
			});
			py += pyplus;
		}

		/*
		 * Wieso nicht einfach rpanels = panels; Collections.reverse(rpanels); Ganz
		 * eifach: durch diese Zuweisung wird rpanels einfach nur eine Weiterleitung zu
		 * panels, was mit 'Collections.reverse(rpanels);' nicht ganz funktionieren
		 * wrde. Und da die Elemente nur 'rberkopiert' werden gibt es keine Probleme ;)
		 */
		rpanels = new ArrayList<Panel>();
		for (Panel p : panels) {
			rpanels.add(p);
		}
		Collections.reverse(rpanels);

	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		ScaledResolution scaledRes = new ScaledResolution(mc);
		//this.renderVignetteScaledResolution(scaledRes);
		this.translate.interpolate((float) GuiContainer.width, (float) GuiContainer.height, 4.0);
		// this.drawDefaultBackground();
		double xmod = GuiContainer.width / 2f - this.translate.getX() / 2.0f;
		double ymod = GuiContainer.height / 2f - this.translate.getY() / 2.0f;
		GlStateManager.translate(xmod, ymod, 0.0);
		GlStateManager.scale(this.translate.getX() / GuiContainer.width, this.translate.getY() / GuiContainer.height,
				1.0f);
		/*
		 * Panels und damit auch Buttons rendern. panels wird NUR hier im Code
		 * verwendet, da das zuletzt gerenderte Panel ganz oben ist Auch wenn es
		 * manchmal egal wre ob panels/rpanels benutzt wird habe ich mich einfach mal
		 * dazu entschieden, einfach weil es einfacher ist nur einmal panels zu benutzen
		 */
		this.configBox.drawConfigBox(mouseX, mouseY);
		//this.fontBox.drawConfigBox(mouseX, mouseY);
		for (Panel p : panels) {
			p.drawScreen(mouseX, mouseY, partialTicks);
		}

		/*															*/ ScaledResolution s = new ScaledResolution(mc);
		/* DO NOT REMOVE */ GL11.glPushMatrix();
		/* copyright HeroCode 2017 */ GL11.glTranslated(s.getScaledWidth(), s.getScaledHeight(), 0);
		GL11.glScaled(0.5, 0.5, 0.5);
		/* https://www.youtube.com/channel/UCJum3PIbnYvIfIEu05GL_yQ */ FontUtil.drawStringWithShadow(
				"b" + "y" + "H" + "e" + "r" + "o" + "C" + "o" + "d" + "e",
				-Minecraft.getMinecraft().fontRendererObj
						.getStringWidth("b" + "y" + "H" + "e" + "r" + "o" + "C" + "o" + "d" + "e"),
				-Minecraft.getMinecraft().fontRendererObj.getFontHeight(), 0xff11F86B);
		/*															*/ GL11.glPopMatrix();

		mb = null;
		/*
		 * berprfen ob ein Button listening == true hat, wenn ja, dann soll nicht mehr
		 * gesucht werden, nicht dass 1+ auf listening steht...
		 */
		listen: for (Panel p : panels) {
			if (p != null && p.visible && p.extended && p.Elements != null && p.Elements.size() > 0) {
				for (ModuleButton e : p.Elements) {
					if (e.listening) {
						mb = e;
						break listen;
					}
				}
			}
		}

		/*
		 * Settings rendern. Da Settings ber alles gerendert werden soll, abgesehen vom
		 * ListeningOverlay werden die Elements von hier aus fast am Schluss gerendert
		 */
		for (Panel panel : panels) {
			if (panel.extended && panel.visible && panel.Elements != null) {
				for (ModuleButton b : panel.Elements) {
					if (b.extended && b.menuelements != null && !b.menuelements.isEmpty()) {
						double off = 0;
						Color temp = ColorUtil.getClickGUIColor().darker();
						int outlineColor = new Color(temp.getRed(), temp.getGreen(), temp.getBlue(), 170).getRGB();
						double height = 0;

						for (Element e : b.menuelements) {
							e.offset = off;
							e.update();
							height += e.height;
							// Gui.drawRect2(e.x, e.y, e.x + e.width + 2, e.y + e.height, outlineColor);

							e.drawScreen(mouseX, mouseY, partialTicks);
							off += e.height;
						}
						
						b.additionalHeight = height;
					}
				}
			}

		}

		/*
		 * Wenn mb != null ist => ein Button listening == true dann wird das Overlay
		 * gerendert mit ein paar Informationen.
		 */
		if (mb != null) {
			drawRect(0, 0, this.width, this.height, 0x88101010);
			GL11.glPushMatrix();
			GL11.glTranslatef(s.getScaledWidth() / 2, s.getScaledHeight() / 2, 0.0F);
			GL11.glScalef(4.0F, 4.0F, 0F);
			FontUtil.drawTotalCenteredStringWithShadow("Listening...", 0, -10, 0xffffffff);
			GL11.glScalef(0.5F, 0.5F, 0F);
			FontUtil.drawTotalCenteredStringWithShadow(
					"Press 'ESCAPE' to unbind " + mb.mod.getName()
							+ (mb.mod.getKeyBind() > -1 ? " (" + Keyboard.getKeyName(mb.mod.getKeyBind()) + ")" : ""),
					0, 0, 0xffffffff);
			GL11.glScalef(0.25F, 0.25F, 0F);
			FontUtil.drawTotalCenteredStringWithShadow("by HeroCode", 0, 20, 0xffffffff);
			GL11.glPopMatrix();
		}
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		/*
		 * Damit man nicht nochmal den Listeningmode aktivieren kann, wenn er schon
		 * aktiviert ist
		 */
		this.configBox.configBoxClicked(mouseX, mouseY, mouseButton);
	//	this.fontBox.configBoxClicked(mouseX, mouseY, mouseButton);
		if (mb != null)
			return;

		/*
		 * Bentigt damit auch mit Elements interagiert werden kann besonders zu beachten
		 * ist dabei, dass zum einen rpanels aufgerufen wird welche eine Eigenstndige
		 * Kopie von panels ist, genauer oben erklrt Also rpanels damit zuerst das panel
		 * 'untersucht' wird, dass als letztes gerendert wurde => Ganz oben ist! sodass
		 * der Nutzer nicht mit dem Unteren interagiern kann, weil er es wohl nicht
		 * will. Und damit nicht einfach mit Panels anstatt Elements interagiert wird
		 * werden hier nur die Settings untersucht. Und wenn wirklich interagiert wurde,
		 * dann endet diese Methode hier. Das ist auch in anderen Loops zu beobachten
		 */
		for (Panel panel : rpanels) {
			if (panel.extended && panel.visible && panel.Elements != null) {
				for (ModuleButton b : panel.Elements) {
					if (b.extended) {
						for (Element e : b.menuelements) {
							if (e.mouseClicked(mouseX, mouseY, mouseButton))
								return;
						}
					}
				}
			}
		}

		/*
		 * Bentigt damit mit ModuleButtons interagiert werden kann und Panels
		 * 'gegriffen' werden knnen
		 */
		for (Panel p : rpanels) {
			if (p.mouseClicked(mouseX, mouseY, mouseButton))
				return;
		}

		/*
		 * Nicht bentigt, aber es ist so einfach sauberer ;)
		 */
		try {
			super.mouseClicked(mouseX, mouseY, mouseButton);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY, int state) {
		/*
		 * Damit man nicht nochmal den Listeningmode aktivieren kann, wenn er schon
		 * aktiviert ist
		 */
		this.configBox.configBoxReleased(mouseX, mouseY, state);
		//this.fontBox.configBoxReleased(mouseX, mouseY, mouseY);
		if (mb != null)
			return;

		/*
		 * Eigentlich nur fr die Slider bentigt, aber durch diesen Call erfhrt jedes
		 * Element, wenn z.B. Rechtsklick losgelassen wurde
		 */
		for (Panel panel : rpanels) {
			if (panel.extended && panel.visible && panel.Elements != null) {
				for (ModuleButton b : panel.Elements) {
					if (b.extended) {
						for (Element e : b.menuelements) {
							e.mouseReleased(mouseX, mouseY, state);
						}
					}
				}
			}
		}

		/*
		 * Bentigt damit Slider auch losgelassen werden knnen und nicht immer an der
		 * Maus 'festkleben' :>
		 */
		for (Panel p : rpanels) {
			p.mouseReleased(mouseX, mouseY, state);
		}

		/*
		 * Nicht bentigt, aber es ist so einfach sauberer ;)
		 */
		super.mouseReleased(mouseX, mouseY, state);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) {
		/*
		 * Bentigt fr die Keybindfunktion
		 */
		for (Panel p : rpanels) {
			if (p != null && p.visible && p.extended && p.Elements != null && p.Elements.size() > 0) {
				for (ModuleButton e : p.Elements) {
					try {
						if (e.keyTyped(typedChar, keyCode))
							return;
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		}

		/*
		 * keyTyped in GuiScreen MUSS aufgerufen werden, damit man mit z.B. ESCAPE aus
		 * dem GUI gehen kann
		 */
		try {
			super.keyTyped(typedChar, keyCode);
		} catch (IOException e2) {
			e2.printStackTrace();
		}
	}

	@Override
	public void initGui() {
		this.translate = new de.fanta.utils.Translate(0.0f, 0.0f);
		
		
		
		
		/*
		 * Start blur
		 */
		
	
		
		
		if (Client.INSTANCE.moduleManager.getModule("GuiBlur").isState()) {
			if (OpenGlHelper.shadersSupported && mc.getRenderViewEntity() instanceof EntityPlayer) {
				if (mc.entityRenderer.theShaderGroup != null) {
					mc.entityRenderer.theShaderGroup.deleteShaderGroup();
				}
				mc.entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));
			}
		}
	}

	public void onGuiClosed() {
		/*
		 * End blur
		 */
		if (mc.entityRenderer.theShaderGroup != null) {
			mc.entityRenderer.theShaderGroup.deleteShaderGroup();
			mc.entityRenderer.theShaderGroup = null;
		}
		/*
		 * Sliderfix
		 */
		for (Panel panel : ClickGUI.rpanels) {
			if (panel.extended && panel.visible && panel.Elements != null) {
				for (ModuleButton b : panel.Elements) {
					if (b.extended) {
						for (Element e : b.menuelements) {
							if (e instanceof ElementSlider) {
								((ElementSlider) e).dragging = false;
							}
						}
					}
				}
			}
		}
	}

	public void closeAllSettings() {
		for (Panel p : rpanels) {
			if (p != null && p.visible && p.extended && p.Elements != null && p.Elements.size() > 0) {
				for (ModuleButton e : p.Elements) {
					e.extended = false;
				}
			}
		}
	}
	public void renderVignetteScaledResolution(ScaledResolution scaledRes)
    {
        Color color = Color.cyan;
        GlStateManager.enableTexture2D();
        GlStateManager.resetColor();
        RenderUtil.color(RenderUtil.injectAlpha(color.getRGB(), MathHelper.clamp_int((int) 60, 0,255)).getRGB());
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(771, 770);
        GlStateManager.tryBlendFuncSeparate(771, 770, 1, 0);

        this.mc.getTextureManager().bindTexture(new ResourceLocation("textures/misc/vignette.png"));
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(0.0D, (double)scaledRes.getScaledHeight(), -90.0D).tex(0.0D, 1.0D).endVertex();
        bufferbuilder.pos((double)scaledRes.getScaledWidth(), (double)scaledRes.getScaledHeight(), -90.0D).tex(1.0D, 1.0D).endVertex();
        bufferbuilder.pos((double)scaledRes.getScaledWidth(), 0.0D, -90.0D).tex(1.0D, 0.0D).endVertex();
        bufferbuilder.pos(0.0D, 0.0D, -90.0D).tex(0.0D, 0.0D).endVertex();

        tessellator.draw();
        GlStateManager.disableBlend();
    }
}
