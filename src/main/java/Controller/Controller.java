package Controller;

import Repository.Repository;
import Service.Service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * Använder sig av en Repository Singleton.
 */


public class Controller {

    private static final Scanner scan = new Scanner(System.in);
    private static Service service;


    public static void main(String[] args) throws IOException, SQLException {
        Repository repository = new Repository();
        service = new Service();
        logIn();
    }

    public static void logIn() throws SQLException, IOException {
        boolean loggedIn = false;
        while (!loggedIn) {
            System.out.println("Vänligen uppge användarnamn & lösenord");
            if (service.validateLogIn(scan.nextLine(), scan.nextLine()) == null) {
                failedLogInMessage();
            } else {
                loggedIn = true;
                mainMenu();
            }
        }
    }

    public static void mainMenu() throws SQLException, IOException {
        boolean programOpen = true;
        while (programOpen) {
            systemMessage();
            int userChoice = Integer.parseInt(scan.nextLine());
            switch (userChoice) {
                case 0 -> programOpen = false;
                case 1 -> System.out.println(service.printShoes());
                case 9 -> systemMessage();
            }
        }
    }

    public static void failedLogInMessage() {
        System.out.println("""
                        KUNDE EJ LOGGA IN.
                        ÄR DU EJ MEDLEM? VÄNLIGEN REGISTRERA DIG!
                        ANNARS KONTROLLERA ANVÄNDARNAMN OCH LÖSENORD OCH FÖRSÖK IGEN.
                        """);
    }

    public static void systemMessage() {
        System.out.println("""
                =============================================================         
                |         Tryck 0 för att avsluta programmet                |
                |         Tryck 1 för att hämta alla skor                   |
                |         Tryck 2 för att hämta alla kunder                 |
                |         Tryck 3 för att uppdatera en kunds namn           |
                |         Tryck 4 för att ta bort en kund via id            |  
                |         Tryck 5 för att lägga till en ny kund             |  
                |         Tryck 6 för att använda stored procedure          |  
                |         Tryck 9 för att visa det här meddelandet igen     |
                =============================================================   
                 """);
    }


}





