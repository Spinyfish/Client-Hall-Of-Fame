/* November.lol © 2023 */
package lol.november.feature.account.microshit.exception;

/**
 * @author Gavin
 * @since 2.0.0
 */
public class MicroshitException extends Exception {

  /**
   * Creates a {@link MicroshitException}
   *
   * @param data the data of why something failed
   */
  public MicroshitException(String data) {
    super(data);
  }
}
