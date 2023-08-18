/* November.lol © 2023 */
package lol.november.feature.account.microshit.data;

/**
 * @param sfttTag the SFTT tag gotten from the original microsoft request
 * @param postUrl the new URL to post to after making the original microsoft request
 * @param cookie  the "Set-Cookie" parameter <b>required</b> for the next authentication step
 * @author Gavin
 * @since 2.0.0
 */
public record OAuthData(String sfttTag, String postUrl, String cookie) {}
