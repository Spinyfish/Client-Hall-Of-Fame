package felix.gui.click.util;

/**
 * @author sendQueue <Vinii>
 *
 *         Further info at Vinii.de, file created at 25.10.2020
 * 
 */
public class FramePosition {
	private int posX, posY;
	private boolean isExpanded;

	/**
	 * Initialize new frame position
	 * 
	 * @param posX
	 * @param posY
	 * @param isExpanded
	 */
	public FramePosition(int posX, int posY, boolean isExpanded) {
		this.posX = posX;
		this.posY = posY;
		this.isExpanded = isExpanded;
	}

	/**
	 * @return the posX
	 */
	public int getPosX() {
		return posX;
	}

	/**
	 * @param posX to set
	 */
	public void setPosX(int posX) {
		this.posX = posX;
	}

	/**
	 * @return the posY
	 */
	public int getPosY() {
		return posY;
	}

	/**
	 * @param posY to set
	 */
	public void setPosY(int posY) {
		this.posY = posY;
	}

	/**
	 * @return isExpanded
	 */
	public boolean isExpanded() {
		return isExpanded;
	}

	/**
	 * @param isExpanded to set
	 */
	public void setExpanded(boolean isExpanded) {
		this.isExpanded = isExpanded;
	}
	
	

}
