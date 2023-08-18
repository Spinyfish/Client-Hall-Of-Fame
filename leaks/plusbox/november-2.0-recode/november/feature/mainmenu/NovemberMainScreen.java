/* November.lol © 2023 */
package lol.november.feature.mainmenu;

import static org.lwjgl.opengl.GL11.*;

import java.io.IOException;
import lol.november.utility.render.shader.Shader;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

/**
 * @author Gavin
 * @since 2.0.0
 */
public class NovemberMainScreen extends GuiScreen {

  /**
   * The shader to render in the background
   */
  private static final Shader backgroundShader = new Shader(
    "/assets/november/shader/vertex.vsh",
    "/assets/november/shader/mainmenu.fsh",
    shader -> {
      shader.createUniform("time");
      shader.createUniform("resolution");
    }
  );

  private long initTime = -1L;

  @Override
  public void initGui() {
    super.initGui();
    initTime = System.currentTimeMillis();
  }

  @Override
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    ScaledResolution res = new ScaledResolution(mc);

    backgroundShader.use(pid -> {
      backgroundShader.set(
        "time",
        (System.currentTimeMillis() - initTime) / 1000.0f
      );
      backgroundShader.set(
        "resolution",
        (float) res.getScaledWidth(),
        (float) res.getScaledHeight()
      );
    });

    double x = 0.0, y = 0.0;
    double width = res.getScaledWidth_double();
    double height = res.getScaledHeight_double();

    glBegin(GL_QUADS);
    {
      glTexCoord2d(0, 0);
      glVertex2d(x, y);
      glTexCoord2d(0, 1);
      glVertex2d(x, y + height);
      glTexCoord2d(1, 1);
      glVertex2d(x + width, y + height);
      glTexCoord2d(1, 0);
      glVertex2d(x + width, y);
    }
    glEnd();

    backgroundShader.stop();

    mc.fontRendererObj.drawStringWithShadow(
      "November 2.0.0 - Cracked by aesthetical (real)",
      1.0f,
      (float) (res.getScaledHeight_double() - mc.fontRendererObj.FONT_HEIGHT),
      -1
    );
  }

  @Override
  protected void mouseClicked(int mouseX, int mouseY, int mouseButton)
    throws IOException {
    super.mouseClicked(mouseX, mouseY, mouseButton);

    if (mouseButton == 0) mc.displayGuiScreen(new GuiMainMenu());
  }
}
