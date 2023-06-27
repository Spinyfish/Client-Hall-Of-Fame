package fr.litarvan.openauth.microsoft;

public class PreAuthData
{
    private final String ppft;
    private final String urlPost;
    
    public PreAuthData(final String ppft, final String urlPost) {
        this.ppft = ppft;
        this.urlPost = urlPost;
    }
    
    public String getPPFT() {
        return this.ppft;
    }
    
    public String getUrlPost() {
        return this.urlPost;
    }
}
