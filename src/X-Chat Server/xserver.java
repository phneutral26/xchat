import java.util.HashMap;

public class xserver extends Server
{
    List<String> users = new List();
    HashMap<String, String> usersToIP = new HashMap<>();

    public xserver()
    {
        super(420);
    }

    public void login(String pIP, int pPort, String pUsername)
    {
        boolean userVorhanden = false;
        users.toFirst();
        while(users.hasAccess())
        {
            if (users.getContent().equals(pUsername))
                userVorhanden = true;
            users.next();
        }
        if (!userVorhanden)
        {
            users.append(pUsername);
            usersToIP.put(pUsername, pIP);
            send(pIP, pPort, "LOGIN_SUCESS");
        } else {
            send(pIP, pPort, "LOGIN_FAILURE");
        }
    }

    public void logout(String pUsername, String pIP) {
        users.toFirst();
        while(users.hasAccess()) {
            if (users.getContent().equals(pUsername)) {
                String userIP = usersToIP.get(pUsername);
                if (pIP.equals(userIP)) {    // Verify if request is legitimately from the user
                    users.remove();
                    usersToIP.remove(pUsername);
                    System.out.println("User " + pUsername + " has been logged out.");
                } else {
                    System.out.println("Logout request from a different IP detected for user " + pUsername);
                }
                return;
            }
            users.next();
        }
        System.out.println("User " + pUsername + " is not logged in.");
    }



    public void broadcastMessage(String message) {
        // Iterate through all connected users
        users.toFirst();
        while(users.hasAccess()) {
            String recipientUsername = users.getContent();
            String recipientIP = usersToIP.get(recipientUsername);
            send(recipientIP, 420, "MESSAGE:" + message);
            users.next();
        }
    }

    public void privateMessage(String recipientUsername, String message) {
        if (usersToIP.containsKey(recipientUsername)) {
            // If the recipient user is online, send the private message
            String recipientIP = usersToIP.get(recipientUsername);
            send(recipientIP, 420, "PRIVATE:" + recipientUsername + ":" + message);
        } else {
            System.out.println("User " + recipientUsername + " does not exist or is not online.");
        }
    }
    public void processMessage(String pClientIP, int pClientPort, String pMessage) {
        if (pMessage.equals("LOGIN:")) {
            String pUsername = pMessage.substring(6);
            login(pClientIP, pClientPort, pUsername);
        } else if (pMessage.startsWith("LOGOUT:")) {
            String pUsername = pMessage.substring(7);
            logout(pUsername, pClientIP);
            send(pClientIP, pClientPort, "You have been logged out.");
        } else if (pMessage.startsWith("BROADCAST:")) { // handle general message
            String message = pMessage.substring(10);
            broadcastMessage(message);
        } else if (pMessage.startsWith("PRIVATE:")) { // handle private message
            String[] parts = pMessage.substring(8).split(":");
            String recipient = parts[0];
            String message = parts[1];
            privateMessage(recipient, message);
        } else if (pMessage.startsWith("USERLIST:")) {
            String[] users = pMessage.substring(9).split(",");
            System.out.println("Connected users:");
            for (String user : users) {
                System.out.println(user);
            }
        } else {
            System.out.println(pMessage);
        }
    }
    public void processClosingConnection(String pIP, int pPort)
    {
        
    }
    
    public void processNewConnection(String pIP, int pPort)
    {
        this.send(pIP, pPort, "Bitte gib deinen Benutzernamen an:");

         
    }
}
