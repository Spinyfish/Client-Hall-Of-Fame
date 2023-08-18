/* November.lol © 2023 */
package lol.november.feature.account.cookie;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Gavin
 * @since 2.0.0
 */
public class CookieParser {

  /**
   * Parses a NETSCAPE cookie file
   *
   * @param content the content of the file
   * @return a {@link HashMap} of the cookie name with the {@link Cookie} object
   */
  public static Map<String, Cookie> parse(String content) {
    // netscape format
    //  *  0             1               2           3               4              5           6
    // * '{DOMAIN_RAW}\t{ISDOMAIN_RAW}\t{PATH_RAW}\t{ISSECURE_RAW}\t{EXPIRES_RAW}\t{NAME_RAW}\t{CONTENT_RAW}'

    Map<String, Cookie> cookieMap = new HashMap<>();
    for (String line : content.split("\n")) {
      String[] parts = line.trim().replaceAll("\n", "").split("\t");

      String domain = parts[0];
      boolean isDomain = Boolean.parseBoolean(parts[1]);
      String path = parts[2];
      boolean secure = Boolean.parseBoolean(parts[3]);
      long expires = Long.parseLong(parts[4]);
      String name = parts[5];
      String value = parts[6];

      cookieMap.put(
        name,
        new Cookie(domain, isDomain, path, secure, expires, name, value)
      );
    }

    return cookieMap;
  }
}
