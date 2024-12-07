import java.util.InputMismatchException;
import java.util.Scanner;
public class xclient extends Client {

    private boolean loggedIn;

    /**
     * xclient class represents a client for X-Chat V1.
     * It allows a user to connect to the server and perform various operations such as logging in, sending broadcast messages, sending private messages, listing users, and blocking
     * users.
     * To use this class, create an instance of the xclient class.
     * The user will be prompted to enter a username.
     * The client will attempt to establish a connection with the server using the IP address "127.0.0.1" and port number 420.
     * If the connection is successful, the user will be prompted for further options.
     * The user can choose to logout, send a broadcast message, send a private message, list users, or block a user.
     * When the user selects an option, the client will send the appropriate message to the server.
     * The client will continue to prompt the user for options until the user chooses to logout.
     * If the user is not logged in, no operations can be performed and the program will exit.
     */
    public xclient() {
        super("127.0.0.1", 420);
        System.out.println("Client Log(X-Chat V1)");
        String username;
        Scanner scan = new Scanner(System.in);
        loggedIn = false;

        if (isConnected()) {
            System.out.println("Gib deinen Benutzernamen an:");
            username = scan.nextLine();
            send("LOGIN:" + username);
            System.out.print('\u000C');
        }
        else
        {
            System.out.println("Server nicht erreichbar");
            return;
        }
        try {
          Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        while (true) {

            if(!loggedIn) {
                System.out.println("You are not logged in, can't perform operations. Exiting..");
                break;
            }

            System.out.println("Wählen sie eine Option!");
            System.out.println("1. Ausloggen");
            System.out.println("2. Broadcast-Nachricht");
            System.out.println("3. Private-Nachricht");
            System.out.println("4. Nutzer auflisten");
            System.out.println("5. Nutzer blockieren");
            System.out.println("----------------------------");

            int choice = 0;
            try {
                choice = scan.nextInt();
                scan.nextLine();
                System.out.print('\u000C');
                // Fügen Sie hier den Code hinzu, der basierend auf der Auswahl des Benutzers ausgeführt werden soll.
            } catch (InputMismatchException e) {
                System.out.println("Ungültige Eingabe. Bitte geben Sie eine Zahl ein.");
                scan.nextLine();  // Diese Zeile ist erforderlich, um die ungültige Eingabe aus dem Scanner-Input zu entfernen.
            }

            switch (choice) {
                case 1: // Logout
                    send("LOGOUT:" + username);
                    System.out.println("Du wurdest erfolgreich ausgeloggt!");
                    break;

                case 2: // Broadcast message
                    System.out.println("Gib deine Nachricht ein:");
                    String broadcastMessage = scan.nextLine();
                    System.out.print('\u000C');
                    send("BROADCAST:" + username + ":" + broadcastMessage);

                    break;

                case 3: // Private message
                    System.out.println("Gib den Empfänger ein:");
                    String recipientUsername = scan.nextLine();
                    System.out.print('\u000C');
                    System.out.println("Gib deine Nachricht ein:");
                    String privateMessage = scan.nextLine();
                    System.out.print('\u000C');
                    send("PRIVATE:" + recipientUsername + ":" + username + ":" + privateMessage);
                    break;

                case 4: // List users
                    send("USERLIST:" + username);
                    break;

                case 5: // Block User
                    System.out.println("Gib den Nutzer den du blockieren möchtest ein:");
                    String toBlock = scan.nextLine();
                    System.out.print('\u000C');
                    send("BLOCK:" + username + ":" + toBlock);
                    break;

                default:
                    System.out.println("Ungültige Eingabe!");
                    break;
            }
        }
    }

    /**
     * Processes the given message and performs the appropriate actions based on its value.
     *
     * @param pMessage the message to be processed
     */
    public void processMessage(String pMessage) {
        switch (pMessage) {
            case "LOGIN_SUCCESS" -> {
                loggedIn = true;
                System.out.println("Du bist nun eingeloggt!");
                System.out.print('\u000C');
            }
            case "LOGIN_FAILURE" -> {
                loggedIn = false;
                System.out.println("Login fehlgeschlagen!");
            }
            case "LOGOUT_SUCCESS" -> {
                loggedIn = false;
                System.out.println("Du bist nun ausgeloggt!");
            }
            case "LOGOUT_FAILURE" -> System.out.println("Auslaggen fehlgeschlagen!");
            case "BROADCAST_SUCCESS" -> System.out.println("Broadcast erfolgreich ausgeführt!");
            case "MSG_SUCCESS" -> System.out.println("Nachricht erfolgreich gesendet!");
            case "MSG_FAILURE" -> System.out.println("Nachricht wurde nicht gesendet!");
            case "USERLIST_SUCCESS" -> System.out.println("Userlist erfolgreich ausgegeben!");
            case "BLOCK_SUCCESS" -> System.out.println("Der Nutzer wurde erfolgreich geblockt!");
            case "BLOCK_FAILURE" -> System.out.println("Der Nutzer konnte nicht geblockt werden!");
            case "BLOCK_FAILURE_SELFBLOCK" -> System.out.println("Du kannst dich nicht selbst blockieren!");
            case "MSG_FAILURE_BLOCKED" ->
                    System.out.println("Du kannst einer von dir blockierten Person keine Nachrichten schreiben!");
            case "MSG_FAILURE_BEENBLOCKED" -> System.out.println("Du wurdest blockiert!");
            default -> System.out.println(pMessage);
        }
    }

}