package org.newdawn.slick.util;

import org.newdawn.slick.ControlledInputReciever;
import org.newdawn.slick.Input;
import org.newdawn.slick.InputListener;

/**
 * An implement implementation of the InputListener interface
 *
 * @author kevin
 */
public class InputAdapter implements InputListener {
	/** A flag to indicate if we're accepting input here */
	private boolean acceptingInput = true;
	
	/**
	 * @see InputListener#controllerButtonPressed(int, int)
	 */
	public void controllerButtonPressed(int controller, int button) {
	}

	/**
	 * @see InputListener#controllerButtonReleased(int, int)
	 */
	public void controllerButtonReleased(int controller, int button) {
	}

	/**
	 * @see InputListener#controllerDownPressed(int)
	 */
	public void controllerDownPressed(int controller) {
	}

	/**
	 * @see InputListener#controllerDownReleased(int)
	 */
	public void controllerDownReleased(int controller) {
	}

	/**
	 * @see InputListener#controllerLeftPressed(int)
	 */
	public void controllerLeftPressed(int controller) {
	}

	/**
	 * @see InputListener#controllerLeftReleased(int)
	 */
	public void controllerLeftReleased(int controller) {
	}

	/**
	 * @see InputListener#controllerRightPressed(int)
	 */
	public void controllerRightPressed(int controller) {
	}

	/**
	 * @see InputListener#controllerRightReleased(int)
	 */
	public void controllerRightReleased(int controller) {
	}

	/**
	 * @see InputListener#controllerUpPressed(int)
	 */
	public void controllerUpPressed(int controller) {
	}

	/**
	 * @see InputListener#controllerUpReleased(int)
	 */
	public void controllerUpReleased(int controller) {
	}

	/**
	 * @see InputListener#inputEnded()
	 */
	public void inputEnded() {
	}

	/**
	 * @see InputListener#isAcceptingInput()
	 */
	public boolean isAcceptingInput() {
		return acceptingInput;
	}

	/**
	 * Indicate if we should be accepting input of any sort
	 * 
	 * @param acceptingInput True if we should accept input
	 */
	public void setAcceptingInput(boolean acceptingInput) {
		this.acceptingInput = acceptingInput;
	}
	
	/**
	 * @see InputListener#keyPressed(int, char)
	 */
	public void keyPressed(int key, char c) {
	}

	/**
	 * @see InputListener#keyReleased(int, char)
	 */
	public void keyReleased(int key, char c) {
	}

	/**
	 * @see InputListener#mouseMoved(int, int, int, int)
	 */
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
	}

	/**
	 * @see InputListener#mousePressed(int, int, int)
	 */
	public void mousePressed(int button, int x, int y) {
	}

	/**
	 * @see InputListener#mouseReleased(int, int, int)
	 */
	public void mouseReleased(int button, int x, int y) {
	}

	/**
	 * @see InputListener#mouseWheelMoved(int)
	 */
	public void mouseWheelMoved(int change) {
	}

	/**
	 * @see InputListener#setInput(Input)
	 */
	public void setInput(Input input) {
	}

	/**
	 * @see InputListener#mouseClicked(int, int, int, int)
	 */
	public void mouseClicked(int button, int x, int y, int clickCount) {
	}

	public void mouseDragged(int oldx, int oldy, int newx, int newy) {
	}

	/**
	 * @see ControlledInputReciever#inputStarted()
	 */
	public void inputStarted() {
		
	}
}
