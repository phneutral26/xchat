public class user {
    private String username;
    private boolean isBlocked;
    private String IP;
    private int Port;

    public user(String username, String IP, int Port, boolean isBlocked)
    {
        this.username = username;
        this.IP = IP;
        this.Port = Port;
        this.isBlocked = isBlocked;

    }

    public String getUsername()
    {
        return username;
    }

    public String getIP()
    {
        return IP;
    }

    public int getPort()
    {
        return Port;
    }

    public boolean getBlocked()
    {
        return isBlocked;
    }
}
