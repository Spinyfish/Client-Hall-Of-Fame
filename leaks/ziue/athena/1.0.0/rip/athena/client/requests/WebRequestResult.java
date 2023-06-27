package rip.athena.client.requests;

import java.util.*;

public class WebRequestResult
{
    private WebRequest request;
    private Map<String, List<String>> headers;
    private String data;
    private int response;
    
    public WebRequestResult(final WebRequest request, final String data, final Map<String, List<String>> map, final int response) {
        this.request = request;
        this.data = data;
        this.headers = map;
        this.response = response;
    }
    
    public WebRequest getRequest() {
        return this.request;
    }
    
    public String getData() {
        return this.data;
    }
    
    public Map<String, List<String>> getHeaders() {
        return this.headers;
    }
    
    public int getResponse() {
        return this.response;
    }
}
