import java.util.HashMap;

public class xserver extends Server
{
    List<String> users = new List();
    HashMap<String, String> usersToIP = new HashMap<>();

    public xserver()
    {
        super(420);
    }

    // Funktion zum Login eines Benutzers
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

    // Funktion zum Logout eines Benutzers
    public void logout(String pClientIP, int pClientPort, String pUsername) {
        users.toFirst();
        while(users.hasAccess()) {
            if (users.getContent().equals(pUsername)) {
                String userIP = usersToIP.get(pUsername);
                if (pClientIP.equals(userIP)) {   // Verifizieren Sie, ob die Anfrage legitimerweise vom Benutzer stammt
                    users.remove();
                    usersToIP.remove(pUsername);
                    send(pClientIP, pClientPort, "LOGOUT_SUCCESS");
                } else {
                    send(pClientIP, pClientPort, "LOGOUT_FAILURE");
                }
                return;
            }
            users.next();
        }
        send(pClientIP, pClientPort, "LOGOUT_FAILURE");
    }



    public void broadcastMessage(String message) {
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
            String recipientIP = usersToIP.get(recipientUsername);
            send(recipientIP, 420, "PRIVATE:" + recipientUsername + ":" + message);
        } else {
            System.out.println("User " + recipientUsername + " does not exist or is not online.");
        }
    }
    // Nachrichtenverarbeitung
    public void processMessage(String pClientIP, int pClientPort, String pMessage) {
        if (pMessage.equals("LOGIN:")) { // Uberprufung auf Login Nachrichte
            String pUsername = pMessage.substring(6);
            login(pClientIP, pClientPort, pUsername);
        } else if (pMessage.startsWith("LOGOUT:")) { //Uberprufung auf Logoout
            String pUsername = pMessage.substring(7);
            logout(pClientIP, pClientPort, pUsername);
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
                send(pClientIP, pClientPort, user);
            }
        } else {
            send(pClientIP, pClientPort, "Ungültige Anfrage");
        }
    }
    public void processClosingConnection(String pIP, int pPort)
    {
        
    }
    
    public void processNewConnection(String pIP, int pPort)
    {
    }
}
