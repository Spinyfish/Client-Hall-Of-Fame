/* November.lol © 2023 */
package lol.november.feature.account.cookie;

/**
 * @param doamin      the domain this cookie belongs to
 * @param isDomainRaw if the domain is raw
 * @param path        the path for the cookie off the domain
 * @param secure      if this cookie is secured
 * @param expires     when this cookie expires
 * @param name        the name of this cookie
 * @param value       the value of this cookie
 * @author Gavin
 * @since 2.0.0
 */
public record Cookie(
  String doamin,
  boolean isDomainRaw,
  String path,
  boolean secure,
  long expires,
  String name,
  String value
) {}
