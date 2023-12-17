import java.util.HashMap;

public class xserver extends Server {
    List<String> users = new List();
    HashMap<String, String> usersToIP = new HashMap<>();

    public xserver() {
        super(420);
    }

    public void login(String pIP, int pPort, String pUsername) {
        if (usersToIP.containsKey(pUsername)) {
            send(pIP, pPort, "LOGIN_FAILURE");
        } else {
            users.append(pUsername);
            usersToIP.put(pUsername, pIP);
            send(pIP, pPort, "LOGIN_SUCCESS");
        }
    }

    public void logout(String pClientIP, int pClientPort, String pUsername) {
        if(usersToIP.containsKey(pUsername) && pClientIP.equals(usersToIP.get(pUsername))) {
            users.toFirst();
            while (users.hasAccess()) {
                if (users.getContent().equals(pUsername)) {
                    users.remove();
                    break;
                }
                users.next();
            }
            usersToIP.remove(pUsername);
            send(pClientIP, pClientPort, "LOGOUT_SUCCESS");
        } else {
            send(pClientIP, pClientPort, "LOGOUT_FAILURE");
        }
    }

    public void broadcastMessage(String message) {
        for(String recipientUsername : usersToIP.keySet()) {
            send(usersToIP.get(recipientUsername), 420, "MESSAGE:" + message);
        }
    }

    public void privateMessage(String recipientUsername, String message) {
        if (!usersToIP.containsKey(recipientUsername)) {
            System.out.println("User " + recipientUsername + " does not exist or is not online.");
        } else {
            send(usersToIP.get(recipientUsername), 420, "PRIVATE:" + recipientUsername + ":" + message);
        }
    }

    public void processClosingConnection(String pIP, int pPort){
        users.toFirst(); // Go to beginning of user list
        while(users.hasAccess()){ // Iterate through the list
            String currentUser = users.getContent(); // Assume getContentObject will give you current user's name
            if(pIP.equals(usersToIP.get(currentUser))){
                users.remove();
                usersToIP.remove(currentUser);
            } else {
                users.next();
            }
        }
    }

    public void processNewConnection(String pIP, int pPort) {
    }

    public void processMessage(String pClientIP, int pClientPort, String pMessage) {
        if (pMessage.startsWith("LOGIN:")) {
            String pUsername = pMessage.substring(6);
            login(pClientIP, pClientPort, pUsername);
        } else if (pMessage.startsWith("LOGOUT:")) {
            String pUsername = pMessage.substring(7);
            logout(pClientIP, pClientPort, pUsername);
        } else if (pMessage.startsWith("BROADCAST:")) {
            String message = pMessage.substring(10);
            broadcastMessage(message);
        } else if (pMessage.startsWith("PRIVATE:")) {
            String[] parts = pMessage.split(":", 3);
            if (parts.length < 3) {
                send(pClientIP, pClientPort, "Invalid private message format.");
                return;
            }
            privateMessage(parts[1], parts[2]);
        } else if (pMessage.startsWith("USERLIST:")) {
            send(pClientIP, pClientPort, String.join(",", usersToIP.keySet()));
        } else {
            send(pClientIP, pClientPort, "Invalid request");
        }
    }
}