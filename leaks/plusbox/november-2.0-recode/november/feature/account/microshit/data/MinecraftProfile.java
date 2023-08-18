/* November.lol © 2023 */
package lol.november.feature.account.microshit.data;

/**
 * @param username     the username of the minecraft user
 * @param id           the UUID of the minecraft user
 * @param accessToken  the access token for the microsoft account
 * @param refreshToken the refresh token for the microsoft account
 * @author Gavin
 * @since 2.0.0
 */
public record MinecraftProfile(
  String username,
  String id,
  String accessToken,
  String refreshToken
) {}
