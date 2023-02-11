

package libraries.javax.jnlp;

public interface FileOpenService {

  public FileContents openFileDialog(String pathHint, String[] extensions) throws java.io.IOException;
  public FileContents[] openMultiFileDialog(String pathHint, String[] extensions) throws java.io.IOException;

}

