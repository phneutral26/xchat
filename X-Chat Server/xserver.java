import java.util.HashMap;

public class xserver extends Server {
    /**
     * List of users.
     * <p>
     * This variable represents a list of users in the xserver class. It is used to keep track of the users who are logged in to the server.
     */
    List<String> users = new List<>();
    /**
     * Contains a list of chatrooms.
     * <p>
     * Each chatroom has a list of room users, a name, and a privacy status.
     */
    List<chatroom> chatrooms = new List<>();
    /**
     * A HashMap that stores the mapping between usernames and IP addresses for logged-in users.
     * <p>
     * The keys of the HashMap are the usernames of the logged-in users, and the corresponding values are their IP addresses.
     * This data structure is used to keep track of users who are currently logged in to the server.
     */
    HashMap<String, String> usersToIP = new HashMap<>();
    /**
     * HashMap variable that maps each user to their corresponding port number.
     */
    HashMap<String, Integer> usersToPort = new HashMap<>();
    /**
     * Variable representing a mapping of users to the list of users they have blocked.
     * <p>
     * This HashMap is used to keep track of which users have blocked other users. Each key in the HashMap is a username of a user who has blocked other users.
     * The corresponding value is a list of usernames representing the users that have been blocked by the key user.
     * <p>
     * Example Usage:
     * <p>
     * HashMap<String, List<String>> usersToBlock = new HashMap<>();
     * usersToBlock.put("user1", List("blockedUser1", "blockedUser2", "blockedUser3"));
     * usersToBlock.put("user2", List("blockedUser4", "blockedUser5"));
     * <p>
     * This example shows that "user1" has blocked "blockedUser1", "blockedUser2", and "blockedUser3", while "user2" has blocked "blockedUser4" and "blockedUser5".
     * <p>
     * Note: It is recommended to use methods like `blockUser()` and `isBlocked()` to manipulate and query this HashMap.
     *
     * @see xserver#blockUser(String, String)
     * @see xserver#isBlocked(String, String)
     */
    HashMap<String, List<String>> usersToBlock = new HashMap<>();
    chatroom general;

    //chatroom general;

    /**
     * Initializes a new instance of the xserver class.
     * <p>
     * This constructor initializes the xserver class by calling the constructor of its superclass with the provided port number.
     * It also initializes a chatroom named "general" with the given user list and sets it as the general chatroom.
     *
     */
    public xserver() {
        super(420);
        System.out.println("Server-Log(X-Chat V1)");
        general = new chatroom(users, "general", false);
        chatrooms.append(general);
    }


    /**
     * Logs in a user with the provided IP address, port number, and username.
     *
     * @param pIP       The IP address of the user.
     * @param pPort     The port number of the user.
     * @param pUsername The username of the user.
     */
    public void login(String pIP, int pPort, String pUsername) {
        if (pIP == null || pUsername == null) {
            System.out.println("IP or Username cannot be null");
            return;
        }
        if (usersToIP.containsKey(pUsername)) {
            send(pIP, pPort, "LOGIN_FAILURE");
            System.out.println("[" + pIP + ":" + pPort + "]" + " - LOGIN_FAILURE");
        } else {
            users.append(pUsername);
            usersToIP.put(pUsername, pIP);
            usersToPort.put(pUsername, pPort);
            List<String> newList = new List<>();
            usersToBlock.put(pUsername, newList);
            general.addUser(pUsername);
            send(pIP, pPort, "LOGIN_SUCCESS");
            System.out.println("[" + pIP + ":" + pPort + "]" + " - LOGIN_SUCCESS");
        }
    }

    /**
     * Logout method for the xserver class.
     * <p>
     * This method is used to log out a user from the server. It removes the user from the list of logged-in users and sends a success or failure message back to the client.
     *
     * @param pClientIP The IP address of the client.
     * @param pClientPort The port number of the client.
     * @param pUsername The username of the user who wants to log out.
     */
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
            System.out.println("[" + pClientIP + ":" + pClientPort + "]" + " - LOGOUT_SUCCESS");
        } else {
            send(pClientIP, pClientPort, "LOGOUT_FAILURE");
            System.out.println("[" + pClientIP + ":" + pClientPort + "]" + " - LOGOUT_FAILURE");
        }
    }

    /**
     * Broadcasts a message to all users in the chatrooms who are not blocked by the sender.
     *
     * @param pMessage The message to be broadcasted.
     */
    public void broadcastMessage(message pMessage) {
        chatrooms.toFirst();
        chatrooms.getContent().getUsers().toFirst();
        while (chatrooms.getContent().getUsers().hasAccess())
        {
            String hUsername = chatrooms.getContent().getUsers().getContent();
            if(!isBlocked(hUsername, pMessage.getUsername())){
                send(usersToIP.get(hUsername), usersToPort.get(hUsername), pMessage.getUsername() + ": " + pMessage.getMessage());
            }
            chatrooms.getContent().getUsers().next();

        }
    }


    /**
     * Sends a private message to a specific user.
     * <p>
     * This method checks if the sender or the recipient is blocked by the other user and sends an appropriate failure message.
     * If a chat room already exists between the sender and recipient, the message is sent to the chat room.
     * Otherwise, a new chat room is created with the sender and recipient, and the message is sent.
     *
     * @param pAdressat The recipient of the private message*/
    public void privateMessage(String pAdressat, message pMessage) {
        if (isBlocked(pMessage.getUsername(), pAdressat))
            {
                send(usersToIP.get(pMessage.getUsername()), usersToPort.get(pMessage.getUsername()), "MSG_FAILURE_BLOCKED");
                System.out.println("[" + usersToIP.get(pMessage.getUsername()) + ":" + usersToPort.get(pMessage.getUsername()) + "]" + " - MSG_FAILURE_BLOCKED");
                return;
            } else if (isBlocked(pAdressat, pMessage.getUsername())) {
            send(usersToIP.get(pMessage.getUsername()), usersToPort.get(pMessage.getUsername()), "MSG_FAILURE_BEENBLOCKED");
            System.out.println("[" + usersToIP.get(pMessage.getUsername()) + ":" + usersToPort.get(pMessage.getUsername()) + "]" + " - MSG_FAILURE_BEENBLOCKED");
            return;
        }

        List<String> tempusers = new List<>();
        tempusers.append(pAdressat); tempusers.append(pMessage.getUsername());
        tempusers.toFirst();
        chatrooms.toFirst();

        boolean areBothEqual = false;
        boolean isThereAListAlready = false;
        String existentChatroom;

        while(chatrooms.hasAccess())
        {
            chatrooms.getContent().getUsers().toFirst();
            while(tempusers.hasAccess() && chatrooms.getContent().getUsers().hasAccess())
            {
                areBothEqual = !tempusers.getContent().equals(chatrooms.getContent().getUsers().getContent());
                tempusers.next(); chatrooms.getContent().getUsers().next();
            }
            tempusers.append(tempusers.getContent());
            tempusers.remove();
            chatrooms.getContent().getUsers().toFirst();
            while(tempusers.hasAccess() && chatrooms.getContent().getUsers().hasAccess())
            {
                areBothEqual = !tempusers.getContent().equals(chatrooms.getContent().getUsers().getContent());
                tempusers.next(); chatrooms.getContent().getUsers().next();
            }
            if (areBothEqual && chatrooms.getContent().getPrivate())
            {
                isThereAListAlready = true;
                existentChatroom = chatrooms.getContent().getName();
                System.out.println(existentChatroom);
                send(usersToIP.get(pMessage.getUsername()), usersToPort.get(pMessage.getUsername()), "Es existiert bereits ein Chatroom mit diesem Benutzer - " + existentChatroom);
            }

            chatrooms.next();
        }

        if (!isThereAListAlready)
        {
            chatroom newChatroom = new chatroom(tempusers, pAdressat + pMessage.getUsername(), true);
            chatrooms.append(newChatroom);
        }
        send(usersToIP.get(pMessage.getUsername()), usersToPort.get(pMessage.getUsername()), "MSG_SUCCESS");
        send(usersToIP.get(pAdressat), usersToPort.get(pAdressat), /*pMessage.getUsername() + "-> " + */pMessage.getMessage());
        System.out.println("[" + usersToIP.get(pMessage.getUsername()) + ":" + usersToPort.get(pMessage.getUsername()) + "]" + " - MSG_SUCCESS");

    }

    /**
     * Sends an userlist to a specific user.
     *
     * @param pUsername The username of the user to whom the userlist will be sent.
     */
    public void userlist(String pUsername)
    {
        users.toFirst();
        int i = 1;
        while(users.hasAccess())
        {
            if(users.getContent().equals(pUsername))
            {
                send(usersToIP.get(pUsername), usersToPort.get(pUsername), i + " - " + users.getContent() + " (Du)");
            }
            else if(isBlocked(pUsername, users.getContent()))
            {
                send(usersToIP.get(pUsername), usersToPort.get(pUsername), i + " - " + users.getContent() + " (Blockiert)");
            }
            else
            {
                send(usersToIP.get(pUsername), usersToPort.get(pUsername), i + " - " + users.getContent());
            }
            i = i+1;
            users.next();
        }
        send(usersToIP.get(pUsername), usersToPort.get(pUsername), "USERLIST_SUCCESS");
        System.out.println("[" + usersToIP.get(pUsername) + ":" + usersToPort.get(pUsername) + "]" + " - USERLIST_SUCCESS");

    }


    /**
     * Blocks a user from sending messages to another user.
     * <p>
     * This method checks if the user to be blocked is the same as the requesting user. If they are the same,
     * a BLOCK_FAILURE_SELFBLOCK message is sent and printed. If they are different, the user to be blocked is added
     * to the blocked list of the requesting user, and a BLOCK_SUCCESS message is sent and printed. If the user to be
     * blocked is already in the blocked list of the requesting user, a BLOCK_FAILURE message is sent and printed.
     *
     * @param pUsername The username of the requesting user.
     * @param toBlock   The username of the user to be blocked.
     */
    public void blockUser(String pUsername, String toBlock)
    {
        if (pUsername.equals(toBlock))
        {
            send(usersToIP.get(pUsername), usersToPort.get(pUsername), "BLOCK_FAILURE_SELFBLOCK");
            System.out.println("[" + usersToIP.get(pUsername) + ":" + usersToPort.get(pUsername) + "]" + " - BLOCK_FAILURE_SELFBLOCK");
            return;
        }

        List<String> temp = usersToBlock.get(pUsername);
        boolean isAlreadyBlocked = false;
        temp.toFirst();
        while(temp.hasAccess())
        {
            if(toBlock.equals(temp.getContent()))
            {
                isAlreadyBlocked = true;


            }
            temp.next();
        }
        if (!isAlreadyBlocked )
        {
            temp.append(toBlock);
            send(usersToIP.get(pUsername), usersToPort.get(pUsername), "BLOCK_SUCCESS");
            System.out.println("[" + usersToIP.get(pUsername) + ":" + usersToPort.get(pUsername) + "]" + " - BLOCK_SUCCESS");
        }
        else
        {
            send(usersToIP.get(pUsername), usersToPort.get(pUsername), "BLOCK_FAILURE");
            System.out.println("[" + usersToIP.get(pUsername) + ":" + usersToPort.get(pUsername) + "]" + " - BLOCK_FAILURE");
        }
    }

    /**
     * Checks if a user is blocked by another user.
     *
     * @param pUsername    The username of the user who wants to check if they are blocked.
     * @param pToBeChecked The username of the user to be checked if they are blocked.
     * @return true if the user is blocked, false otherwise.
     */
    public boolean isBlocked(String pUsername, String pToBeChecked)
    {
        // ITERIEREN DURCH HASHMAP -> LISTE DIE pUsername gehoert

        if (!usersToBlock.get(pUsername).hasAccess())
        {
            return false;
        }
        usersToBlock.get(pUsername).toFirst();
        while (usersToBlock.get(pUsername).hasAccess())
        {
            if (usersToBlock.get(pUsername).getContent().equals(pToBeChecked))
            {
                return true;
            }
            usersToBlock.get(pUsername).next();
        }
        return false;
    }

    /**
     * Removes the user with the given IP address and port number from the list of logged-in users, as well as their associated IP address in the usersToIP map.
     * If the IP address and port number match with a user in the list, that user will be removed.
     *
     * @param pIP   The IP address of the user to be removed.
     * @param pPort The port number of the user to be removed.
     */
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

    /**
     * Processes a message received from the client.
     * <p>
     * This method processes the message received from the client and performs the corresponding action.
     * If the message starts with "LOGIN:", it extracts the username and calls the login() method.
     * If the message starts with "LOGOUT:", it extracts the username and calls the logout() method.
     * If the message starts with "BROADCAST:", it splits the message into three parts and creates a new message object,
     * then calls the broadcastMessage() method.
     * If the message starts with "PRIVATE:", it splits the message into four parts, creates a new message object,
     * and calls the privateMessage() method.
     * If the message starts with "USERLIST:", it extracts the username and calls the userlist() method.
     * If the message starts with "BLOCK:", it splits the message into three parts and calls the blockUser() method.
     * If the message doesn't match any of the above formats, it sends an "Invalid request" message back to the client.
     *
     * @param pClientIP   The IP address of the client.
     * @param pClientPort The port number of the client.
     * @param pMessage    The message received from the client.
     */
    public void processMessage(String pClientIP, int pClientPort, String pMessage)
    {
        System.out.println("[" + pClientIP + ":" + pClientPort + "]" + " - " + pMessage);

        String prefix = pMessage.split(":", 2)[0];
        String content = pMessage.split(":", 2).length > 1 ? pMessage.split(":", 2)[1] : "";

        switch (prefix) {
            case "LOGIN":
                login(pClientIP, pClientPort, content);
                break;
            case "LOGOUT":
                logout(pClientIP, pClientPort, content);
                break;
            case "BROADCAST":
            {
                String[] parts = content.split(":", 2);
                if (parts.length < 2) {
                    send(pClientIP, pClientPort, "Invalid broadcast message format.");
                    return;
                }
                message msg = new message(parts[0], parts[1]);
                broadcastMessage(msg);
                break;
            }
            case "PRIVATE":
            {
                String[] parts = content.split(":", 2);
                if (parts.length < 2) {
                    send(pClientIP, pClientPort, "Invalid private message format.");
                    return;
                }
                message msg = new message(parts[0], parts[1]);
                privateMessage(parts[0], msg);
                break;
            }
            case "USERLIST":
                userlist(content);
                break;
            case "BLOCK":
            {
                String[] parts = content.split(":", 2);
                if (parts.length < 2) {
                    send(pClientIP, pClientPort, "Invalid block message format.");
                    return;
                }
                blockUser(parts[0], parts[1]);
                break;
            }
            default:
                send(pClientIP, pClientPort, "Invalid request");
                break;
        }
    }
}