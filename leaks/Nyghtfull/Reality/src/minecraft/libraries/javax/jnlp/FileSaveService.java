

package libraries.javax.jnlp;

public interface FileSaveService {

  public FileContents saveFileDialog(String pathHint, String[] extensions, java.io.InputStream stream, String name) throws java.io.IOException;
  public FileContents saveAsFileDialog(String pathHint, String[] extensions, FileContents contents) throws java.io.IOException;

}

