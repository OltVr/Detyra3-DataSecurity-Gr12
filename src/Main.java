import java.util.Scanner;


public static void main() {
    Scanner scanner = new Scanner(System.in);
    System.out.println("Are you a client or server (c/s)");
    System.out.println("[TESTING] for testing reasons we can run both with (b) ");
    System.out.print("Your choice: ");
    String choice = scanner.nextLine();
    switch (choice) {
        case "c":
            Client.ClientStart();
            break;
        case "s":
            Server.ServerStart();
            break;
        case "b":
            Thread serverThread = new Thread(Server::ServerStart);
            Thread clientThread = new Thread(Client::ClientStart);
            serverThread.start();
            clientThread.start();
            break;
    }
}