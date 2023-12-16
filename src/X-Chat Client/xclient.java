import java.util.Scanner;
public class xclient extends Client
{
    private boolean loggedIn;

    public xclient() {
        super("127.0.0.1", 420);
        loggedIn = false;

        if (isConnected()) {
            Scanner scan = new Scanner(System.in);
            String username = scan.nextLine();
            send(username);
        }
    }


    public void start(String pServerIP, int pServerPort) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("1. Login");
            System.out.println("2. Logout");
            System.out.println("3. Broadcast message");
            System.out.println("4. Private message");
            System.out.println("Enter your choice:");

            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline if any

            switch (choice) {
                case 1: // Login
                    System.out.println("Enter username:");
                    String username = scanner.nextLine();
                    server.login(username);
                    break;

                case 2: // Logout
                    send("LOGOUT");
                    break;

                case 3: // Broadcast message
                    System.out.println("Enter your message:");
                    String broadcastMessage = scanner.nextLine();
                    send("BROADCAST:" + broadcastMessage);
                    break;

                case 4: // Private message
                    System.out.println("Enter recipient username:");
                    String recipientUsername = scanner.nextLine();
                    System.out.println("Enter your message:");
                    String privateMessage = scanner.nextLine();
                    send("PRIVATE:" + recipientUsername + ":" + privateMessage);
                    break;

                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    public void processMessage(String pMessage) {
        if (pMessage.equals("LOGIN_SUCCESS")) {
            loggedIn = true;
            System.out.println("You are now logged in.");
        } else if (pMessage.equals("LOGIN_FAILURE")) {
            loggedIn = false;
            System.out.println("Login failed. Please try again.");
        } else {
            System.out.println(pMessage);
        }
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void writeMessage(String pMessage) {
        send("CHATROOM_MESSAGE:" + pMessage);
    }

    public void writePrivateMessage(String pServerIP, int pServerPort, String pMessage) {
        ("PRIVATE_MESSAGE:" + empfaengerIP + ":" + pMessage);
    }
}
