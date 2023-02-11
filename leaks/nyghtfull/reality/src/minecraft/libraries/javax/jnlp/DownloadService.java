

package libraries.javax.jnlp;

public interface DownloadService {

  public boolean isResourceCached(java.net.URL ref, String version);
  public boolean isPartCached(String part);
  public boolean isPartCached(String[] parts);
  public boolean isExtensionPartCached(java.net.URL ref, String version, String part);
  public boolean isExtensionPartCached(java.net.URL ref, String version, String[] parts);
  public void loadResource(java.net.URL ref, String version, DownloadServiceListener progress) throws java.io.IOException;
  public void loadPart(String part, DownloadServiceListener progress) throws java.io.IOException;
  public void loadPart(String[] parts, DownloadServiceListener progress) throws java.io.IOException;
  public void loadExtensionPart(java.net.URL ref, String version, String part, DownloadServiceListener progress) throws java.io.IOException;
  public void loadExtensionPart(java.net.URL ref, String version, String[] parts, DownloadServiceListener progress) throws java.io.IOException;
  public void removeResource(java.net.URL ref, String version) throws java.io.IOException;
  public void removePart(String part) throws java.io.IOException;
  public void removePart(String[] parts) throws java.io.IOException;
  public void removeExtensionPart(java.net.URL ref, String version, String part) throws java.io.IOException;
  public void removeExtensionPart(java.net.URL ref, String version, String[] parts) throws java.io.IOException;
  public DownloadServiceListener getDefaultProgressWindow();

}

