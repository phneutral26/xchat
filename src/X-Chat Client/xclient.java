import java.util.Scanner;
public class xclient extends Client
{
    private boolean loggedIn;

    public xclient() {
        super("127.0.0.1", 420);
        Scanner scan = new Scanner(System.in);
        loggedIn = false;

        if (isConnected()) {
            
            String username = scan.nextLine();
            send(username);
        }
        while (true) {
            System.out.println("1. Login");
            System.out.println("2. Logout");
            System.out.println("3. Broadcast message");
            System.out.println("4. Private message");
            System.out.println("Enter your choice:");

            int choice = scan.nextInt();
            scan.nextLine();  // Consume newline if any

            switch (choice) {
                case 1: // Login
                    System.out.println("Enter username:");
                    String username = scan.nextLine();
                    send("LOGIN:" + username);
                    break;

                case 2: // Logout
                    send("LOGOUT");
                    loggedIn = false;
                    break;

                case 3: // Broadcast message
                    System.out.println("Enter your message:");
                    String broadcastMessage = scan.nextLine();
                    send("BROADCAST:" + broadcastMessage);
                    break;

                case 4: // Private message
                    System.out.println("Enter recipient username:");
                    String recipientUsername = scan.nextLine();
                    System.out.println("Enter your message:");
                    String privateMessage = scan.nextLine();
                    send("PRIVATE:" + recipientUsername + ":" + privateMessage);
                    break;

                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
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
}
