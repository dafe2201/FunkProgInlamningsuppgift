package Controller;

import DTO.ListDTO;
import Entities.Shoe;
import Repository.Repository;
import Service.Service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

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
            mainMenuMessage();
            int userChoice = Integer.parseInt(scan.nextLine());
            switch (userChoice) {
                case 0 -> System.exit(0);
                case 1 -> browseShoesMenu();
                case 9 -> mainMenuMessage();
            }
        }
    }


    public static void browseShoesMenu() throws SQLException, IOException {
        ListDTO tempListDTO = service.getShoes();
        while (true) {
            browseShoesMessage();
            int userChoice = Integer.parseInt(scan.nextLine());
            switch (userChoice) {
                case 0 -> goBackToMainMenu();
                case 1 -> printBrand(tempListDTO);
                case 2 -> printModelInformation(tempListDTO);
                case 3 -> browseSizesMenu();
                case 9 -> browseShoesMessage();

            }
        }
    }

    public static void browseSizesMenu() throws SQLException, IOException {
        while (true) {
            browseSizesMessage();
            String input = scan.nextLine();
            if (input.equals("0")){
                browseShoesMenu();
            }
            //TODO: Kanske regex på namn
            List<Shoe> shoeList = service.getShoeInfo(input);

            if (shoeList.size()==0){
                System.out.println("Modell: " + input + " finns inte. Var vänlig försök igen...");
            } else {
                System.out.println("Tillgängliga skor av " + input + ":\n");
                shoeList.forEach(object -> System.out.println("Färg: " + object.getColor() + ", Storlek: " + object.getProductSize() + ", Saldo: " + object.getAmount()));
            }
        }
    }


    //TODO nedanför är browseSizesMenu metoder
    public static void browseSizesMessage() {
        System.out.println("""
                          
                          -- Skriv in ett modellnamn för att söka --
                =============================================================         
                |         Tryck 0 för att gå tillbaka till föregående meny  |
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

    public static void browseShoesMessage() {
        System.out.println("""
                ===============================================================         
                |         Tryck 0 för att återgå till huvudmenyn               |
                |         Tryck 1 för att visa alla märken                     |
                |         Tryck 2 för att visa alla modeller & priser          |
                |         Tryck 3 för att Sök efter specifik modell            |
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
                =============================================================         
                |         Tryck 0 för att avsluta programmet                |
                |         Tryck 1 för att visa alla skor                    |
                |         Tryck 2 för att ...                               |
                |         Tryck 3 för att ...                               |
                |         Tryck 4 för att ...                               |  
                |         Tryck 5 för att ...                               |  
                |         Tryck 6 för att ...                               |  
                |         Tryck 9 för att visa det här meddelandet igen     |
                =============================================================   
                 """);
    }


}





