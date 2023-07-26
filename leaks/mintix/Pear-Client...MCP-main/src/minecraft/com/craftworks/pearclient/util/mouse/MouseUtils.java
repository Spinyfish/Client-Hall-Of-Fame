package com.craftworks.pearclient.util.mouse;

import org.lwjgl.input.Mouse;

public class MouseUtils {
	
	public enum Scroll {
		UP, DOWN;
	}

	public static Scroll scroll() {
		int mouse = Mouse.getDWheel();

        if(mouse > 0) {
        	return Scroll.UP;
        }else if(mouse < 0) {
        	return Scroll.DOWN;
        }else {
        	return null;
        }
    }
	
    public static boolean isInside(int mouseX, int mouseY, double x, double y, double width, double height) {
        return (mouseX > x && mouseX < (x + width)) && (mouseY > y && mouseY < (y + height));
    }   
}