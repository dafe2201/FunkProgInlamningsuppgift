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
            currentCustomer = service.validateLogIn(scan.nextLine(), scan.nextLine());
            if (currentCustomer == null) {
                failedLogInMessage();
            }
            else if(currentCustomer.getIsAdmin() > 0){
                loggedIn = true;
                adminMenu();
            }

            else {
            //TODO: else if currentCustomer isAdmin - adminMenu(); ?
                loggedIn = true;
                mainMenu();
            }
        }
    }

    private static void adminMenu() throws SQLException, IOException {
        boolean programOpen = true;
        while(programOpen) {
            adminMenuMessage();

            int userChoice = Integer.parseInt(scan.nextLine());
            switch (userChoice) {
                // Alla cases överensstämmer med vilket nummer rapporten har i kriterierna. Se "Inlämningsuppgift 2023.pdf"
                case 1 -> searchOrderHistoryByProduct();
                case 2 -> searchOrderHistoryByCustomer();
                case 3 -> getTotalRevenue();
                case 4 -> getRevenueByCounty();
                case 5 -> getTopSellingProducts();

                case 9 -> mainMenu();
                case 0 -> System.exit(0);

            }

        }
    }

    private static void getTopSellingProducts() {
    }

    private static void getRevenueByCounty() {
    }

    private static void getTotalRevenue() {
    }

    private static void searchOrderHistoryByCustomer() {
    }

    private static void searchOrderHistoryByProduct() {
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
    public static void findShoeMenu() throws SQLException, IOException {
        while (true) {
            browseSizesMessage();
            String input = scan.nextLine();
            if (input.equals("0")) {
                productsMainMenu();
            }
            if (input.equals("1")) {
                addToCartMenu();
            }
            List<Shoe> shoeList = service.getAllShoeInfo(input);

            if (shoeList.size() == 0) {
                System.out.println("Modell: " + input + " finns inte. Var vänlig försök igen...");
            } else {
                System.out.println("Tillgängliga skor av " + input + ":\n");
                shoeList.forEach(object -> System.out.println("Färg: " + object.getColor() + ", Storlek: " + object.getProductSize() + ", Saldo: " + object.getAmount()));
            }
        }
    }

    public static void browseSizesMessage() {
        System.out.println("""
                          
                -- Skriv in ett modellnamn för att se färger och storlekar --
                =============================================================         
                |         Tryck 0 för att gå tillbaka till föregående meny  |
                |         Tryck 1 för att lägga till vara i kundvagn        |
                =============================================================   
                 """);
    }

    //TODO nedanför är productsMainMenu metoder
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

    // TODO: Man måste gå in i kundvagnen för att kunna lägga beställning. Genom denna ska man även kunna ta bort produkter från kundvagnen.
    private static void browseCartMenu() throws SQLException, IOException {
        browseCartMessage();
        AtomicInteger counter = new AtomicInteger(1);
        Double sum = shoesInCart.stream().map(e -> e.getModel().getPrice()).reduce(0.0, (subTotal, element) -> subTotal + element);

        System.out.println("KUNDVAGN \n");
        shoesInCart.forEach(shoe -> {
            System.out.println(counter + ": " + shoe.getModel().getName() + ", " + shoe.getColor() + ", " + shoe.getProductSize() + ", " + shoe.getModel().getPrice());
            counter.getAndIncrement();
        });
        System.out.println("____________________________________________");
        System.out.println("Totalt beställningsbelopp: " + sum);

        String input = scan.nextLine();
        if (input.equals("0")) { // Gå bak i meny
            findShoeMenu();
        }
        if (input.equals("1")) { // Lägg beställning
            processPayment(shoesInCart, currentCustomer);
        }
        if (input.equals("2")) {
            removeFromCart();
        }

    }

    private static void removeFromCart() {
        System.out.println("Ange skonamn, färg och storlek för att välja produkt att radera ur kundvagnen");
        String input = scan.nextLine();

        String[] inputArray = input.split(", ");
        String modelName = inputArray[0];
        String color = inputArray[1];
        String size = inputArray[2];

        int index = 0;
        for (int i = 0; i < shoesInCart.size(); i++) {
            if (shoesInCart.get(i).getModel().getName().equalsIgnoreCase(modelName) && shoesInCart.get(i).getColor().equalsIgnoreCase(color) && shoesInCart.get(i).getProductSize() == Integer.parseInt(size)) {
                index = i;
            }
        }
        shoesInCart.remove(shoesInCart.get(index));
        System.out.println("Tog bort skojävel");
    }

    private static void processPayment(List<Shoe> shoesInCart, Customer currentCustomer) throws IOException, SQLException {
        List<Shoe> deniedShoes = service.validateStockStatus(shoesInCart);

        if (deniedShoes.size() == 0) {
            if (service.processPayment(shoesInCart, currentCustomer)) {
                shoesInCart.clear();
            }
        } else {
            deniedShoes.forEach(shoe -> {
                System.out.println("Finns ej tillräckligt med skor i lager för att kunna köpa följande produkter: " + shoe.getModel().getName() + ", storlek: " + shoe.getProductSize() + ", saldo: " + shoe.getAmount());
            });

        }
    }


    private static void addToCartMenu() throws SQLException, IOException {
        while (true) {

            addToCartMessage();
            String input = scan.nextLine();

            if (input.equals("0")) {
                findShoeMenu();
            }

            String[] inputArray = input.split(", ");
            String modelName = inputArray[0];
            String color = inputArray[1];
            String size = inputArray[2];

            Shoe currentShoe = validateIfShoeExists(modelName, color, size);
            if (currentShoe != null) {
                System.out.println("Skon tillagd i kundvagn");
                shoesInCart.add(currentShoe);
            }


        }
    }

    private static Shoe validateIfShoeExists(String modelName, String color, String size) throws SQLException, IOException {
        List<Shoe> shoeList = service.getAllShoeInfo(modelName);

        try {
            List<Shoe> tempList = shoeList.stream().filter(shoe -> shoe.getColor().equals(color) && shoe.getProductSize() == Integer.parseInt(size)).collect(Collectors.toList());
            return tempList.get(0);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Skon finns inte. Försök igen.");
        }
        return null;
    }

    private static void browseCartMessage() {
        //TODO: Implementera metod för att ta bort vara ur kundavagn.
        System.out.println("""
                          
                =============================================================         
                |         Tryck 0 för att gå tillbaka till föregående meny  |
                |         Tryck 1 för att lägga beställning                 |
                |         Tryck 2 för att ta bort vara ur kundvagn          |
                =============================================================   
                 """);
    }

    public static void addToCartMessage() {
        System.out.println("""
                             
                -- Skriv in modellnamn, färg och storlek för den sko du vill ha --
                             --          Ex: Supernova, white, 36         --
                   =============================================================         
                   |         Tryck 0 för att gå tillbaka till föregående meny  |
                   =============================================================   
                    """);
    }


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

    public static void adminMenuMessage() {
        System.out.println("""
                                       ADMIN - HUVUDMENY
                =============================================================         
                |         Tryck 0 för att avsluta program                   |
                |         Tryck 1 för att gå till kundhuvudmeny             |
                |         Tryck 2 för att ...                               |
                |         Tryck 3 för att ...                               |
                |         Tryck 4 för att ...                               |  
                |         Tryck 5 för att ...                               |  
                |         Tryck 6 för att ...                               |  
                |         Tryck 9 för att ...                               |
                =============================================================   
                 """);
    }

}





