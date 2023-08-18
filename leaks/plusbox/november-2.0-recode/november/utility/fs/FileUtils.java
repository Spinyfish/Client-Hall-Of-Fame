/* November.lol © 2023 */
package lol.november.utility.fs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

/**
 * @author Gavin, TerrificTable
 * @since 2.0.0
 */
@Log4j2
public class FileUtils {

  /**
   * The {@link File} object pointing to the november directory
   */
  public static File directory;

  /**
   * The {@link JsonParser} for parsing JSON
   */
  public static final JsonParser jsonParser = new JsonParser();

  /**
   * The {@link Gson} object for creating JSON objects
   */
  public static final Gson gson = new GsonBuilder()
    .setPrettyPrinting()
    .create();

  static {
    try {
      String home = System.getProperty("user.home");
      directory = new File(home, "november");

      if (!directory.exists()) {
        boolean result = directory.mkdir();
        log.info(
          "Created {} {}",
          directory,
          result ? "successfully" : "unsuccessfully"
        );
      }
    } catch (Exception e) {
      throw new RuntimeException("Failed to create november directory");
    }
  }

  /**
   * Reads the contents from a file
   *
   * @param file the {@link File} object
   * @return the contents of the file or null
   */
  @SneakyThrows
  public static String readFile(File file) {
    if (!file.exists() || !file.isFile()) return null;

    InputStream is = Files.newInputStream(file.toPath());

    StringBuilder builder = new StringBuilder();
    int b;
    while ((b = is.read()) != -1) {
      builder.append((char) b);
    }

    is.close();
    return builder.toString();
  }

  /**
   * Writes to a file
   *
   * @param file    the {@link File} object
   * @param content the contents to write to the file
   */
  @SneakyThrows
  public static void writeFile(File file, String content) {
    if (!file.exists()) file.createNewFile();

    OutputStream os = Files.newOutputStream(file.toPath());

    byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
    os.write(bytes, 0, bytes.length);

    os.close();
  }

  /**
   * Lists directories, list excludes subdirectories if `subdirs` is true
   * Warning: if also listing `subdirs` it is recursive and blocks current thread until all subdirs and subdirectories of subdirectories and so on are listed
   *
   * @param dir       initial directory to list
   * @param subdirs   if true, also listing files in subdirectories (and excludes subdirectories from resulting list)
   * @param filter    filter for files
   * @return returns  list of files (if subdirs is false, including subdirectories of `dir` otherwise doesnt)
   */
  public static List<File> listDirectory(
    File dir,
    boolean subdirs,
    Predicate<File> filter
  ) {
    // wow this looks like shit, but it works and it should be farely efficient (right?)
    List<File> list = Arrays.asList(Objects.requireNonNull(dir.listFiles()));
    List<File> result = new ArrayList<>(
      list.stream().filter(File::isFile).filter(filter).toList()
    );

    if (subdirs) {
      for (File subdir : list
        .stream()
        .filter(File::isDirectory)
        .filter(filter)
        .toList()) {
        result.addAll(listDirectory(subdir, subdirs, filter));
      }
    } else {
      result.addAll(
        list.stream().filter(File::isDirectory).filter(filter).toList()
      ); // add directories
    }

    return result;
  }

  /**
   * Lists directories, list excludes subdirectories if `subdirs` is true
   * Warning: if also listing `subdirs` it is recursive and blocks current thread until all subdirs and subdirectories of subdirectories and so on are listed
   *
   * @param dir       initial directory to list
   * @param subdirs   if true, also listing files in subdirectories (and excludes subdirectories from resulting list)
   * @return returns  list of files (if subdirs is false, including subdirectories of `dir` otherwise doesnt)
   */
  public static List<File> listDirectory(File dir, boolean subdirs) {
    return listDirectory(dir, subdirs, f -> true);
  }
}
