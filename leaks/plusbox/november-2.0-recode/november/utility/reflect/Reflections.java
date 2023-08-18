/* November.lol © 2023 */
package lol.november.utility.reflect;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author TerrificTable
 * @since 1.0.0
 */
public class Reflections {

  public static List<Class<?>> getAllClassesInPackage(String packageName)
    throws IOException, ClassNotFoundException {
    List<Class<?>> classes = new ArrayList<>();
    String path = packageName.replace('.', '/');

    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    for (URL resource : Collections.list(classLoader.getResources(path))) {
      String fullPath = resource.getFile().replace("%20", " ");
      if (fullPath.endsWith(".jar")) {
        classes.addAll(getClassesFromJar(fullPath, packageName));
      } else classes.addAll(
        getClassesFromDirectory(new File(fullPath), packageName)
      );
    }
    return classes;
  }

  private static List<Class<?>> getClassesFromJar(
    String jarPath,
    String packageName
  ) throws IOException, ClassNotFoundException {
    List<Class<?>> classes = new ArrayList<>();
    JarFile jarFile = new JarFile(jarPath);
    Enumeration<JarEntry> entries = jarFile.entries();
    while (entries.hasMoreElements()) {
      JarEntry entry = entries.nextElement();
      String entryName = entry.getName();
      if (
        entryName.endsWith(".class") &&
        entryName.startsWith(packageName.replace('.', '/'))
      ) {
        String className = entryName
          .replace('/', '.')
          .substring(0, entryName.length() - 6);
        classes.add(Class.forName(className));
      }
    }
    jarFile.close();
    return classes;
  }

  private static List<Class<?>> getClassesFromDirectory(
    File directory,
    String packageName
  ) throws ClassNotFoundException {
    List<Class<?>> classes = new ArrayList<>();
    if (!directory.exists()) {
      return classes;
    }

    File[] files = directory.listFiles();
    for (File file : files) {
      if (file.isDirectory()) {
        classes.addAll(
          getClassesFromDirectory(file, packageName + "." + file.getName())
        );
      } else if (file.getName().endsWith(".class")) {
        String className =
          packageName +
          '.' +
          file.getName().substring(0, file.getName().length() - 6);
        classes.add(Class.forName(className));
      }
    }
    return classes;
  }

  public static Class<?> getClass(String className, String packageName) {
    try {
      return Class.forName(
        packageName + className.substring(0, className.lastIndexOf('.'))
      );
    } catch (ClassNotFoundException ignored) {}
    return null;
  }
}
