/* November.lol © 2023 */
package lol.november.utility.render.animation;

/**
 * @author Gavin
 * @since 2.0.0
 */
public class Animation {

  private Easing easing;
  private boolean state;
  private double animationTime;

  private long time;

  /**
   * Creates an animation object with no bound
   *
   * @param easing        the {@link Easing} type
   * @param animationTime the time that it takes to finish the animation
   * @param initialState  the state to start the animation in
   */
  public Animation(Easing easing, double animationTime, boolean initialState) {
    this.easing = easing;
    this.animationTime = animationTime;
    setState(initialState);
  }

  /**
   * Gets the factor of this animation
   *
   * @return the factor based on time and speed
   */
  public double getFactor() {
    double linear = (System.currentTimeMillis() - time) / animationTime;
    if (!state) {
      linear = 1.0 - linear;
    }

    return Math.min(Math.max(easing.ease(linear), 0.0), 1.0);
  }

  public Animation setEasing(Easing easing) {
    this.easing = easing;
    return this;
  }

  public Animation setState(boolean state) {
    this.state = state;
    time = System.currentTimeMillis();
    return this;
  }

  public boolean getState() {
    return state;
  }

  /**
   * Sets the animation time
   *
   * @param animationTime the animation time - lower the number, the faster
   * @return this animation instance
   */
  public Animation setAnimationTime(double animationTime) {
    this.animationTime = animationTime;
    return this;
  }
}
