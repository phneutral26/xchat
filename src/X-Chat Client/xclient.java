import java.util.Scanner;

public class xclient extends Client {

    private boolean loggedIn;

    public xclient() {
        super("127.0.0.1", 420);
        Scanner scan = new Scanner(System.in);
        loggedIn = false;

        if (isConnected()) {
            System.out.println("Enter username:");
            String username = scan.nextLine();
            send("LOGIN:" + username);
        }

        while (true) {
            if(!loggedIn) {
                System.out.println("You are not logged in, can't perform operations. Exiting..");
                break;
            }

            System.out.println("Enter your choice:");
            System.out.println("1. Logout");
            System.out.println("2. Broadcast message");
            System.out.println("3. Private message");
            System.out.println("4. List users");
            System.out.println("5. Exit");

            int choice = scan.nextInt();
            scan.nextLine();

            switch (choice) {
                case 1: // Logout
                    System.out.println("Enter username:");
                    String username = scan.nextLine();
                    send("LOGOUT:" + username);
                    break;

                case 2: // Broadcast message
                    System.out.println("Enter your message:");
                    String broadcastMessage = scan.nextLine();
                    send("BROADCAST:" + broadcastMessage);
                    break;

                case 3: // Private message
                    System.out.println("Enter recipient username:");
                    String recipientUsername = scan.nextLine();
                    System.out.println("Enter your message:");
                    String privateMessage = scan.nextLine();
                    send("PRIVATE:" + recipientUsername + ":" + privateMessage);
                    break;

                case 4: // List users
                    send("USERLIST:");
                    break;

                case 5: // Exit
                    if (loggedIn) {
                        send("LOGOUT:" + username);
                    }
                    System.out.println("Exiting chat...");
                    System.exit(0);

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
        } else if (pMessage.equals("LOGOUT_SUCCESS")) {
            loggedIn = false;
            System.out.println("You are now logged out.");
        }
        else if (pMessage.equals("LOGOUT_FAILURE")) {
            System.out.println("Logout failed. Please try again.");
        } else {
            System.out.println(pMessage);
        }
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }
}