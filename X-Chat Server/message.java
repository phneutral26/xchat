import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class message {
    String username;
    String message;
    int time;

    public message(String username, String message)
    {
        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:m");

        this.message = message + " (" + formatter.format(time) + ")";
        this.username = username;
    }

    public String getMessage()
    {
        return message;
    }

    public String getUsername()
    {
        return username;
    }
}
