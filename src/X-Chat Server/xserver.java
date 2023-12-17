import java.util.HashMap;

public class xserver extends Server {
    List<String> users = new List();
    HashMap<String, String> usersToIP = new HashMap<>();

    public xserver() {
        super(420);
    }

    public void login(String pIP, int pPort, String pUsername) {
        if (usersToIP.containsKey(pUsername)) {
            send(pIP, pPort, "LOGIN_FAILURE: User already exists");
        } else {
            users.append(pUsername);
            usersToIP.put(pUsername, pIP);
            send(pIP, pPort, "LOGIN_SUCCESS");
        }
    }

    public void logout(String pClientIP, int pClientPort, String pUsername) {
        if (!usersToIP.containsKey(pUsername) || !pClientIP.equals(usersToIP.get(pUsername))) {
            send(pClientIP, pClientPort, "LOGOUT_FAILURE");
        } else {
            users.remove(pUsername);
            usersToIP.remove(pUsername);
            send(pClientIP, pClientPort, "LOGOUT_SUCCESS");
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

    public void processClosingConnection(String pIP, int pPort) {
        String username = users.stream()
                .filter(u -> pIP.equals(usersToIP.get(u)))
                .findFirst()
                .orElse(null);
        if (username != null) {
            logout(pIP, pPort, username);
        }
    }

    public void processNewConnection(String pIP, int pPort) {
        // This method intentionally left empty. End classes should handle what should happen when a new connection is established.
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