package rip.athena.client.requests;

public enum ContentType
{
    NONE(""), 
    JSON("application/json"), 
    FORM("application/x-www-form-urlencoded"), 
    MULTIPART_FORM("multipart/form-data");
    
    private String header;
    
    private ContentType(final String header) {
        this.header = header;
    }
    
    @Override
    public String toString() {
        return this.header;
    }
}
