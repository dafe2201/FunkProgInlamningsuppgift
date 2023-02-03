package Controller;

import Entities.Customer;
import FunctionalInterfaces.IListCustomersByShoeVaraibleNoGenerics;
import FunctionalInterfaces.IListCustomersByShoeVariable;
import Service.Service;

import java.io.IOException;
import java.util.Scanner;


public class AdminController {
    static Scanner scan = new Scanner(System.in);
    Service service = new Service();

    //variabel med funktionsdefinionen här
    IListCustomersByShoeVaraibleNoGenerics shoeVariableColorSearchNoGenerics = (cust, input) -> cust.getCustomerOrder().getCart().getShoe().getColor().equalsIgnoreCase(input);
    IListCustomersByShoeVariable<Customer, String, Boolean> shoeVariableBrandSearch = (cust, input) -> cust.getCustomerOrder().getCart().getShoe().getModel().getBrand().getName().equalsIgnoreCase(input);
    IListCustomersByShoeVariable<Customer, String, Boolean> shoeVariableSizeSearch = (cust, input) -> cust.getCustomerOrder().getCart().getShoe().getProductSize() == Integer.parseInt(input);

    public AdminController() {

    }

    public void adminMenu()  {
        while (true) {
            adminMenuMessage();
            try {
                int userChoice = Integer.parseInt(scan.nextLine());
                switch (userChoice) {
                    case 0 -> System.exit(0);
                    case 1 -> searchOrderHistoryByProduct();
                    case 2 -> searchOrderHistoryByCustomer();
                    case 3 -> listCustomerSpending();
                    case 4 -> getRevenueByCounty();
                    case 5 -> getTopSellingProducts();
                    case 9 -> CustomerController.customerMainMenu();

                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Felaktig input, försök igen");
                adminMenuMessage();
            }

        }
    }

    private void searchOrderHistoryByProduct() {
        while (true) {
            searchOrderHistoryByProductMessage();
            try {
                int userChoice = Integer.parseInt(scan.nextLine());
                switch (userChoice) {
                    case 0 -> adminMenu();

                    case 1 ->  {
                        System.out.println("Ange en färg (engelska)");
                        service.adminListCustomersByShoeColor(scan.nextLine(), shoeVariableColorSearchNoGenerics);
                    }
                    case 2 -> {
                        System.out.println("Ange ett märke");
                        service.adminListCustomersByShoeBrand(scan.nextLine(), shoeVariableBrandSearch);
                    }
                    case 3 -> {
                        System.out.println("Ange storlek");
                        service.adminListCustomersByShoeSize(scan.nextLine(), shoeVariableSizeSearch);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Felaktig input, försök igen");
                searchOrderHistoryByProduct();
            }
        }
    }


    private void getTopSellingProducts() throws IOException {
        service.adminGetTopSellingProducts();
    }

    private void getRevenueByCounty() throws IOException {
        service.adminGetRevenueByCounty();
    }

    private void listCustomerSpending() throws IOException {
        service.adminListCustomerSpending();
    }

    private void searchOrderHistoryByCustomer() throws IOException {
        service.adminListCustomerOrderCount();
    }



    public void adminMenuMessage() {
        System.out.println("""
                                       ADMIN - HUVUDMENY
                =============================================================
                |         Tryck 0 för att avsluta program                   |
                |         Tryck 1 för Rap.1: Säljhistorik per variabel      |
                |         Tryck 2 för Rap.2: Antal ordrar per kund          |
                |         Tryck 3 för Rap.3: Ordervärde per kund            |
                |         Tryck 4 för Rap.4: Ordervärde per ort             |
                |         Tryck 5 för Rap.5: Topplista mest sålda produkter |
                |         Tryck 9 för att gå till kundhuvudmeny             |
                =============================================================   
                 """);
    }

    private void searchOrderHistoryByProductMessage() {
        System.out.println("""
                                   ADMIN - Undermeny, Rap.1
                =============================================================
                |         Tryck 0 för att gå tillbaka till admin-huvudmeny  |
                |         Tryck 1 för att söka efter färg                   |
                |         Tryck 2 för att söka efter märke                  |
                |         Tryck 3 för att söka efter storlek                |
                =============================================================   
                 """);
    }


}
