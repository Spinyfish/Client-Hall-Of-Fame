package Realioty.server.utile;

public class ServiceUtils {
    public static String getRank(String name) {
        if (name.equals("SuperSkidder")) {
            return "\2476" + "[Developer]\247r";
        }
        switch (name) {
            case "SuperSkidder":
                return "\2474" + "[Admin]\247r";
            case "vlouboos":
                return "\2476" + "[Developer]\247r";
            case "QianXia":
                return "\2476" + "[Developer]\247r";
        }

        return "\2477[User]\247f";
    }
}
