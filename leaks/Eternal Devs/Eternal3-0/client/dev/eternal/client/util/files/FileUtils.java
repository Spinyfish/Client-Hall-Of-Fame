package dev.eternal.client.util.files;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

/**
 * A utility class the provides useful functionality for working with the {@link File} class.
 *
 * @author Eternal & Dort
 */
@UtilityClass
public class FileUtils {

  private static final Minecraft mc = Minecraft.getMinecraft();

  /**
   * @return The base folder for where client info will be saved
   * @author Eternal
   */
  public static File getEternalFolder() {
    File eternalFolder = new File(mc.mcDataDir, "Eternal");
    if (!eternalFolder.exists()) eternalFolder.mkdirs();
    return eternalFolder;
  }

  public static File getEternalSaveState() {
    File saveStateFile = new File(mc.mcDataDir, "Eternal/SaveState");
    if (!saveStateFile.exists()) saveStateFile.mkdirs();
    return saveStateFile;
  }

  /**
   * @param folder The folder where the file is located.
   * @param file   The {@link File} to be checked.
   * @return Whether the folder or File exist.
   * @auther Eternal
   */
  public static boolean fileExists(String folder, String file) {
    File pathFile = new File(getEternalFolder(), folder);
    if (!pathFile.exists()) return false;
    File fileToReturn = new File(pathFile, file);
    return fileToReturn.exists();
  }

  /**
   * If the folder or File does not exist this function will create them. If you are trying to
   * verify the existence of a File, use {@link FileUtils#getFileLocationFromFolder(String,
   * String)}.
   *
   * @param folder The folder where the file is located.
   * @param file   The {@link File} to be returned.
   * @return The File in the folder parsed in.
   * @auther Eternal
   */
  public static File getFileFromFolder(String folder, String file) {
    File pathFile = new File(getEternalFolder(), folder);
    if (!pathFile.exists()) pathFile.mkdirs();
    File fileToReturn = new File(pathFile, file);
    if (!fileToReturn.exists()) {
      try {
        fileToReturn.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return fileToReturn;
  }

  /**
   * @param path The path to the resource to be read. Eg: assets/minecraft/eternal/document.txt
   * @return A {@link List} containing the contents of the File.
   * @author Eternal
   */
  @SneakyThrows
  public static List<String> readFromWithin(String path) {
    return new BufferedReader(
        new InputStreamReader(
            Objects.requireNonNull(FileUtils.class.getResourceAsStream(path))))
        .lines()
        .toList();
  }

  /**
   * This function does not create the folder nor verify it exists, so it is possible the File it
   * returns does not exist.
   *
   * @param folder Self-explanatory
   * @param file   Self-explanatory
   * @return The File from the Folder.
   * @author Eternal
   */
  public static File getFileLocationFromFolder(String folder, String file) {
    File pathFile = new File(getEternalFolder(), folder);
    if (!pathFile.exists()) pathFile.mkdirs();
    return new File(pathFile, file);
  }

  /**
   * @param file
   * @param toWrite
   */
  public static void writeToFile(File file, List<String> toWrite) {
    try {
      FileWriter fileWriter = new FileWriter(file);

      for (String s : toWrite) fileWriter.append(s).append("\n");

      fileWriter.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @SneakyThrows
  public static void addToFile(File file, List<String> toWrite) {
    final List<String> contents = readFromFile(file);
    contents.addAll(toWrite);
    new BufferedWriter(new FileWriter(file)).write(String.join("\n", contents));
  }

  public static void addToCompressed(File file, List<String> toWrite) {
    try {
      List<String> arg = readCompressed(file);
      arg.addAll(toWrite);
      writeCompressed(file, arg);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void writeCompressed(File file, List<String> toWrite) {
    try {
      File tempFile = File.createTempFile("Eternal", ".etrnl");
      FileUtils.writeToFile(tempFile, toWrite);
      FileInputStream fileInputStream = new FileInputStream(tempFile);
      FileOutputStream fileOutputStream = new FileOutputStream(file);
      DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(fileOutputStream);

      int i;
      while ((i = fileInputStream.read()) != -1) deflaterOutputStream.write(i);

      deflaterOutputStream.close();
      fileOutputStream.close();
      tempFile.delete();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static List<String> readCompressed(File file) {
    List<String> toReturn = new ArrayList<>();
    try {
      File tempFile = File.createTempFile("Eternal", ".etrnl");
      FileInputStream fileInputStream = new FileInputStream(file);
      FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
      InflaterInputStream deflaterInputStream = new InflaterInputStream(fileInputStream);

      if (fileInputStream.available() == 0) return new ArrayList<>();

      int i;
      while ((i = deflaterInputStream.read()) != -1) fileOutputStream.write(i);

      toReturn.addAll(readFromFile(tempFile));
      fileInputStream.close();
      deflaterInputStream.close();
      tempFile.delete();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return toReturn;
  }

  @SneakyThrows
  public static String dumpFile(File file) {
    return new BufferedReader(new FileReader(file)).lines().collect(Collectors.joining("\n"));
  }

  @SneakyThrows
  public static List<String> readFromFile(File file) {
    return new BufferedReader(new FileReader(file)).lines().toList();
  }

  public static List<File> getFilesFromFolder(Path path, String fileExtension) {
    List<File> files = new ArrayList<>();
    if(!path.toFile().exists()) return Collections.emptyList();
    try {
      files =
          Files.walk(path)
              .filter(Files::isRegularFile)
              .map(Path::toFile)
              .filter(f -> f.getName().endsWith(fileExtension))
              .collect(Collectors.toList());
    } catch (IOException e) {
      e.printStackTrace();
    }
    return files;
  }

  public String readAsset(String resourcePath) {
    return readInputStream(FileUtils.class.getResourceAsStream(resourcePath));
  }

  public String readInputStream(InputStream inputStream) {
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
    return bufferedReader.lines().collect(Collectors.joining("\n"));
  }
}
