package Controller;

import DTO.ListDTO;
import Entities.Customer;
import Entities.Shoe;
import Repository.Repository;
import Service.Service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Använder sig av en Repository Singleton.
 */


public class Controller {

    private static final Scanner scan = new Scanner(System.in);
    private static Service service;
    //TODO: LISTAN SKALL RENSAS EFTER PROCESSORDER HAR ANROPATS
    static List<Shoe> shoesInCart = new ArrayList<>();
    static Customer currentCustomer = new Customer();

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
            mainMenuMessage();
            int userChoice = Integer.parseInt(scan.nextLine());
            switch (userChoice) {
                case 0 -> System.exit(0);
                case 1 -> productsMainMenu();
                case 2 -> addToCartMenu();
                case 3 -> browseCartMenu();
                case 9 -> mainMenuMessage();
            }
        }
    }

    public static void productsMainMenu() throws SQLException, IOException {
        ListDTO tempListDTO = service.getShoesBrandModelPrice();
        while (true) {
            productsMainMenuMessage();
            int userChoice = Integer.parseInt(scan.nextLine());
            switch (userChoice) {
                case 0 -> goBackToMainMenu();
                case 1 -> printBrand(tempListDTO);
                case 2 -> printModelInformation(tempListDTO);
                case 3 -> findShoeMenu();
                case 9 -> productsMainMenuMessage();

            }
        }
    }


    //TODO nedanför är browseSizesMenu metoder
    public static void browseSizesMessage() {
        System.out.println("""
                          
                -- Skriv in ett modellnamn för att se färger och storlekar --
                =============================================================         
                |         Tryck 0 för att gå tillbaka till föregående meny  |
                |         Tryck 1 för att lägga till vara i kundvagn        |
                =============================================================   
                 """);
    }

    //TODO nedanför är browseShoesMeny metoder
    private static void goBackToMainMenu() throws SQLException, IOException {
        mainMenu();
    }

    private static void printBrand(ListDTO listDTO) {
        listDTO.getListOfBrands().forEach(object -> System.out.println(object.getName()));
    }


    private static void printModelInformation(ListDTO listDTO) {
        listDTO.getListOfModels().forEach(object -> System.out.println("Modell: " + object.getName() + ", Märke: " + object.getBrand().getName() + ", Pris: " + object.getPrice()));
    }

    public static void productsMainMenuMessage() {
        System.out.println("""
                ===============================================================         
                |         Tryck 0 för att återgå till huvudmenyn               |
                |         Tryck 1 för att visa alla märken                     |
                |         Tryck 2 för att visa alla modeller & priser          |
                |         Tryck 3 för att söka efter specifik modell           |
                |         Tryck 4 för att ...                                  |  
                |         Tryck 5 för att ...                                  |  
                |         Tryck 6 för att ...                                  |  
                |         Tryck 9 för att visa det här meddelandet igen        |
                ===============================================================   
                 """);
    }

    //TODO Nedanför är huvudmeny metoder
    public static void failedLogInMessage() {
        System.out.println("""
                KUNDE EJ LOGGA IN.
                ÄR DU EJ MEDLEM? VÄNLIGEN REGISTRERA DIG!
                ANNARS KONTROLLERA ANVÄNDARNAMN OCH LÖSENORD OCH FÖRSÖK IGEN.
                """);
    }

    public static void mainMenuMessage() {
        System.out.println("""
                                           HUVUDMENY
                =============================================================         
                |         Tryck 0 för att avsluta programmet                |
                |         Tryck 1 för att visa alla skor                    |
                |         Tryck 2 för att lägga till i kundvagn             |
                |         Tryck 3 för att se kundvagn och lägga beställning |
                |         Tryck 4 för att ...                               |  
                |         Tryck 5 för att ...                               |  
                |         Tryck 6 för att ...                               |  
                |         Tryck 9 för att visa det här meddelandet igen     |
                =============================================================   
                 """);
    }


}





