

package libraries.javax.jnlp;

public interface ServiceManagerStub {

  public Object lookup(String name) throws UnavailableServiceException;
  public String[] getServiceNames();

}

