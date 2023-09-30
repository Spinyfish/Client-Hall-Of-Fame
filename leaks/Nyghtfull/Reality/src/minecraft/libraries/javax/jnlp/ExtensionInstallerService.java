

package libraries.javax.jnlp;

public interface ExtensionInstallerService {

  public String getInstallPath();
  public String getExtensionVersion();
  public java.net.URL getExtensionLocation();
  public void hideProgressBar();
  public void hideStatusWindow();
  public void setHeading(String heading);
  public void setStatus(String status);
  public void updateProgress(int value);
  public void installSucceeded(boolean needsReboot);
  public void installFailed();
  public void setJREInfo(String platformVersion, String jrePath);
  public void setNativeLibraryInfo(String path);
  public String getInstalledJRE(java.net.URL url, String version);

}

