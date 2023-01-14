package net.optifine.http;

import java.util.Map;

public class HttpResponse {
   private final int status;
   private final String statusLine;
   private final Map<String, String> headers;
   private final byte[] body;

   public HttpResponse(int status, String statusLine, Map headers, byte[] body) {
      this.status = status;
      this.statusLine = statusLine;
      this.headers = headers;
      this.body = body;
   }

   public int getStatus() {
      return this.status;
   }

   public String getStatusLine() {
      return this.statusLine;
   }

   public Map getHeaders() {
      return this.headers;
   }

   public String getHeader(String key) {
      return this.headers.get(key);
   }

   public byte[] getBody() {
      return this.body;
   }
}
