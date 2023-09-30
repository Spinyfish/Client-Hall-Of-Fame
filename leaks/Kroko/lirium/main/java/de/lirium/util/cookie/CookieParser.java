package de.lirium.util.cookie;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CookieParser {

    /**
     * Read the contents of a file and return it as a string.
     *
     * @param file The file name.
     * @return The file content as a string.
     */
    private static String readFile(String file) throws IOException {
        StringBuilder sb = new StringBuilder();

        // Read the contents of the file into a BufferedReader
        BufferedReader reader = new BufferedReader(new FileReader(file));

        // Read each line of the file and append it to the StringBuilder
        for (String line; (line = reader.readLine()) != null; sb.append(line).append("\n")) ;

        // Close the reader and return the contents of the StringBuilder as a string
        reader.close();
        return sb.toString();
    }

    /**
     * Parses cookies from a cookie file.
     *
     * @param file The cookie file.
     * @return The cookies in form of an array.
     * @throws CookieParseException If the cookie file is malformed.
     */
    public static Cookie[] parseCookieFile(String file) throws CookieParseException {
        try {
            String content = readFile(file);
            String[] lines = content.split("\n");
            Cookie[] cookies = new Cookie[lines.length];

            for (int i = 0; i < lines.length; i++) {
                String[] cookie = lines[i].split("\t");
                if (cookie.length != 7) {
                    throw new CookieParseException("Cookie line has invalid argument count: Expected 7, but got " + cookie.length);
                }

                cookies[i] = new Cookie(cookie[0], cookie[1], cookie[2], cookie[3], cookie[4], cookie[5], cookie[6]);
            }

            return cookies;
        } catch (IOException e) {
            throw new CookieParseException("Could not parse cookie file: " + file);
        }
    }
}
