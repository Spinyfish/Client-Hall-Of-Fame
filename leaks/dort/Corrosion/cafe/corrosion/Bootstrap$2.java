package cafe.corrosion;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

final class Bootstrap$2 extends Authenticator {
    // $FF: synthetic field
    final String val$s1;
    // $FF: synthetic field
    final String val$s2;

    Bootstrap$2(String var1, String var2) {
        this.val$s1 = var1;
        this.val$s2 = var2;
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(this.val$s1, this.val$s2.toCharArray());
    }
}
