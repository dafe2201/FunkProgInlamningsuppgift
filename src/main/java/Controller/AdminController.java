package Controller;

import Entities.Customer;
import Service.Service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;


public class AdminController {
    static Scanner scan = new Scanner(System.in);

    public AdminController() {

    }


    public void adminMenu() throws SQLException, IOException {
        while (true) {
            adminMenuMessage();
            int userChoice = Integer.parseInt(scan.nextLine());
            switch (userChoice) {
                // Alla cases överensstämmer med vilket nummer rapporten har i kriterierna. Se "Inlämningsuppgift 2023.pdf"
                case 1 -> searchOrderHistoryByProduct();
                case 2 -> searchOrderHistoryByCustomer();
                case 3 -> listCustomerSpending();
                case 4 -> getRevenueByCounty();
                case 5 -> getTopSellingProducts();

                case 9 -> Controller.customerMainMenu();
                case 0 -> System.exit(0);
            }

        }
    }

    private static void searchOrderHistoryByProduct() throws IOException, SQLException {
//        Service.adminGetAllCustomersFromDB();
        while (true) {
            System.out.println("Vad vill du söka på?");
            System.out.println("1: Färg");
            System.out.println("2: Märke");
            System.out.println("3: Storlek");
            int userChoice = Integer.parseInt(scan.nextLine());
            switch (userChoice) {
                // Alla cases överensstämmer med vilket nummer rapporten har i kriterierna. Se "Inlämningsuppgift 2023.pdf"
                case 1 -> Service.listCustomersByShoeColor(scan.nextLine());
                case 2 -> Service.listCustomersByShoeBrand(scan.nextLine());
                case 3 -> Service.listCustomersByShoeSize(scan.nextLine());

            }
        }
    }

    private static void getTopSellingProducts() {
    }

    private static void getRevenueByCounty() {
    }

    private static void listCustomerSpending() throws IOException {
        Service.listCustomerSpending();
    }

    private static void searchOrderHistoryByCustomer() throws IOException {
        Service.listCustomerOrderCount();
    }



    public static void adminMenuMessage() {
        System.out.println("""
                                       ADMIN - HUVUDMENY
                =============================================================         
                |         Tryck 0 för att avsluta program                   |
                |         Tryck 1 för att searchOrderHistoryByProduct()     |
                |         Tryck 2 för att ...                               |
                |         Tryck 3 för att ...                               |
                |         Tryck 4 för att ...                               |  
                |         Tryck 5 för att ...                               |  
                |         Tryck 6 för att ...                               |  
                |         Tryck 9 för att gå till kundhuvudmeny             |
                =============================================================   
                 """);
    }


}
