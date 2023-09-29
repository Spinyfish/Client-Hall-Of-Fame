package community;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message implements Serializable {
    public String username, message, time;

    public Message(String username, String message) {
        this.username = username;
        this.message = message;

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        Date date = new Date();

        this.time = formatter.format(date);
    }
}
