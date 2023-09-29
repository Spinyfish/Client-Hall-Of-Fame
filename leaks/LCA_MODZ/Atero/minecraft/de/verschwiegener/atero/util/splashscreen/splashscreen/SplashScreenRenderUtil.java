package de.verschwiegener.atero.util.splashscreen.splashscreen;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

import org.lwjgl.opengl.Display;

import de.verschwiegener.atero.util.render.RenderUtil;
import de.verschwiegener.atero.util.splashscreen.CurveRenderer;
import de.verschwiegener.atero.util.splashscreen.LineRenderer;

public class SplashScreenRenderUtil {
	
	static ArrayList<RenderCommand> completet = new ArrayList<>();
	ArrayList<RenderCommand> rendercommands = new ArrayList<>();
	int count;
	static LineRenderer lr;
	CurveRenderer cr;
	RenderCommand current;
	public boolean finish;
	
	public SplashScreenRenderUtil() {
		lr = new LineRenderer();
		cr = new CurveRenderer();
		int x = 10;
		int y = 200;
		//Render A
		/*add(new RenderCommand(x - 20, y, x + 40, y - 200, 10));
		add(new RenderCommand(x + 40, y - 200, x + 60, y - 200, 2));
		add(new RenderCommand(x + 60, y - 200, x + 120, y, 10));
		add(new RenderCommand(x + 120, y, x + 100, y, 2));
		add(new RenderCommand(x + 100, y, x + 80, y - 60, 5));
		add(new RenderCommand(x + 80, y - 60, x + 22, y - 60, 4));
		add(new RenderCommand(x + 22, y - 60, x, y, 2));
		add(new RenderCommand(x, y, x - 20, y, 10));*/
		
		//Test render A
		add(new RenderCommand(-2, 0, 4, -20, 20));
		add(new RenderCommand(4, -20, 6, -20, 4));
		add(new RenderCommand(6, -20, 12, 0, 20));
		add(new RenderCommand(12, 0, 10, 0, 4));
		add(new RenderCommand(10, 0, 8, -6, 10));
		add(new RenderCommand(8, -6, 2.2F, -6, 6));
		add(new RenderCommand(2.2F, -6, 0, 0, 10));
		add(new RenderCommand(0, 0, -2, 0, 4));
		
		
		//add(new RenderCommand(x + 25, y - 80, x + 75, y - 80, 5));
		//add(new RenderCommand(x + 75, y - 80, x + 50, y - 180, 6));
		//add(new RenderCommand(x + 50, y - 180, x + 25, y - 80, 6));
		
		add(new RenderCommand(2.5F, -8, 7.5F, -8, 10));
		add(new RenderCommand(7.5F, -8, 5, -18, 12));
		add(new RenderCommand(5,-18, 2.5F, -8, 12));
		
		//Render T
		/*add(new RenderCommand(20, 0, 20, -18, 9));
		add(new RenderCommand(20, -18, 14, -18, 5));
		add(new RenderCommand(14, -18, 14, -20, 2));
		add(new RenderCommand(14, -20, 28, -20, 10));
		add(new RenderCommand(28, -20, 28, -18, 2));
		add(new RenderCommand(28, -18, 22, -18, 5));
		add(new RenderCommand(22, -18, 22, 0, 10));
		add(new RenderCommand(22, 0, 20, 0, 2));
		
		//Render E
		add(new RenderCommand(31, 0, 31, -20, 10));
		add(new RenderCommand(31, -20, 45, -20, 10));
		add(new RenderCommand(45, -20, 45, -18, 2));
		add(new RenderCommand(45, -18, 33, -18, 8));
		add(new RenderCommand(33, -18, 33, -11, 5));
		add(new RenderCommand(33, -11, 45, -11, 8));
		add(new RenderCommand(45, -11, 45, -9, 2));
		add(new RenderCommand(45, -9, 33, -9, 8));
		add(new RenderCommand(33, -9, 33, -2, 5));
		add(new RenderCommand(33, -2, 45, -2, 8));
		add(new RenderCommand(45, -2, 45, 0, 2));
		add(new RenderCommand(45, 0, 31, 0, 10));
		
		//Render R
		add(new RenderCommand(48, 0, 48, -20, 10));
		add(new RenderCommand(48, -20, 56, -20, 5));
		add(new RenderCommand(56, -15, 50));
		add(new RenderCommand(56, -10, 50, -10, 5));
		add(new RenderCommand(50, -10, 50, 0, 5));
		add(new RenderCommand(50, 0, 48, 0, 2));
		add(new RenderCommand(56, -10, 61, 0, 5));
		add(new RenderCommand(61, 0, 59, 0, 0));
		add(new RenderCommand(59, 0, 54, -10, 0));
		add(new RenderCommand(50, -12, 50, -18, 0));
		add(new RenderCommand(50, -18, 55, -18, 0));
		add(new RenderCommand(55, -15F, 30F));
		add(new RenderCommand(55, -12, 50, -12, 0));
		//Draw O
		add(new RenderCommand(70, -10, 7, 10));
		add(new RenderCommand(70, -10, 5, 8));*/

		current = rendercommands.get(0);
		rendercommands.remove(0);
		finish = false;
		count = 0;
	}
	
	private void add(RenderCommand rc) {
		rendercommands.add(rc);
	}
	
	public void draw() {
		for (RenderCommand rc : completet) {
			switch (rc.getType()) {
			case Line:
				lr.setValues(rc);
				lr.drawLine(Color.white, 5f);
				break;

			case Curve:
				// cr.setValues(current);
				// cr.drawFinish();
				// cr.setValues(current);
				// cr.draw();
				// bcr.setValues(rc);
				// bcr.drawBezierCurve(Color.white, 5F);

				break;
			case CustomCurve:
				cr.setValues(current);
				cr.drawCustom();
				break;
			}
		}
		if (current != null) {
			switch (current.getType()) {
			case Line:
				lr.setValues(current);
				lr.drawLineAnimated(Color.white, 5F);
				
				completet.add(current);
				rendercommands.remove(current);
				if (rendercommands.size() > 0) {
					current = rendercommands.get(0);
				} else {
					current = null;
					finish = true;
				}
				break;

			case Curve:
				cr.setValues(current);
				cr.draw();
				
				completet.add(current);
				rendercommands.remove(current);
				if (rendercommands.size() > 0) {
					current = rendercommands.get(0);
				} else {
					current = null;
					finish = true;
				}
				break;
			case CustomCurve:
				cr.setValues(current);
				cr.drawCustom();
				
				completet.add(current);
				rendercommands.remove(current);
				if (rendercommands.size() > 0) {
					current = rendercommands.get(0);
				} else {
					current = null;
					finish = true;
				}
				break;
			}
		}
	}
	
	public boolean isFinish() {
		return finish;
	}

}
