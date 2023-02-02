package Service;

import DTO.ListDTO;
import Entities.Customer;

import Entities.Shoe;
import Repository.CustomerRepository;
import Repository.AdminRepository;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

import java.util.stream.Collectors;


public class Service {
    public Service() throws IOException, SQLException {

    }

    //Rapport 1 i VG:
    public  void listCustomersByShoeColor(String input) throws IOException {
        List<Customer> customerList = adminGetAllCustomersFromDB();
        customerList.stream()
                .filter(e -> e.getCustomerOrder().getCart().getShoe().getColor().equalsIgnoreCase(input))
                .distinct()
                .forEach( customer -> {
                    System.out.println(customer.getName());
                    System.out.println(customer.getCounty().getName() +"\n");
                });
    }

    //rapport 1 i VG
    public void listCustomersByShoeBrand(String input) throws IOException {
        List<Customer> customerList = adminGetAllCustomersFromDB();
        customerList.stream()
                .filter(e -> e.getCustomerOrder().getCart().getShoe().getModel().getBrand().getName().equalsIgnoreCase(input))
                .distinct()
                .forEach( customer -> {
                    System.out.println(customer.getName());
                    System.out.println(customer.getCounty().getName() +"\n");
                });
    }

    //rapport 1 i VG
    public void listCustomersByShoeSize(String input) throws IOException {
        List<Customer> customerList = adminGetAllCustomersFromDB();
        customerList.stream()
                .filter(e -> e.getCustomerOrder().getCart().getShoe().getProductSize() == Integer.parseInt(input))
                .distinct()
                .forEach( customer -> {
                    System.out.println(customer.getName());
                    System.out.println(customer.getCounty().getName() +"\n");
                });
    }

    //Rapport 2 i VG
    public void listCustomerOrderCount() throws IOException {
        List<Customer> customerList = adminGetAllCustomersFromDB();

        Map<String, Long> numberOfCustomerOrders = customerList.stream().
                collect(Collectors.groupingBy(e -> e.getName(), Collectors.counting()));

        numberOfCustomerOrders.entrySet().stream().forEach( e -> {
            System.out.println(
                    "Namn: " + e.getKey() + ", antal beställningar: " + e.getValue() +"\n"
            );
        });
    }

    //Rapport 3 i VG
    public void listCustomerSpending() throws IOException {
        List<Customer> customerList = adminGetAllCustomersFromDB();

        Map<String, Double> customerSpending = customerList.stream()
                .collect(Collectors.groupingBy(e -> e.getName(), Collectors.summingDouble(e -> e.getCustomerOrder().
                        getCart().getShoe().getModel().getPrice())));

        customerSpending.entrySet().stream().forEach(e -> {
            System.out.println("Namn: " + e.getKey() + ", Summa av beställningar: " + e.getValue() + "\n");
        });
    }


    //Rapport 4 i VG
    public void getRevenueByCounty() throws IOException {

        List<Customer> customerList = adminGetAllCustomersFromDB();

        Map<String, Double> revenueByCounty = customerList.stream()
                .collect(Collectors.groupingBy(e -> e.getCounty().getName(), Collectors.summingDouble(e -> e.getCustomerOrder()
                        .getCart().getShoe().getModel().getPrice())));

        revenueByCounty.entrySet().stream().forEach(e -> {
            System.out.println("Ort: " + e.getKey() + ", Summa av beställningar: " + e.getValue() + "\n" );
        });

    }

    //Rapport 5 i VG
    public void getTopSellingProducts() throws IOException {
        List <Customer> customerList = adminGetAllCustomersFromDB();

        Map<String, Long> topSellers = customerList.stream()
                .collect(Collectors.groupingBy(e -> e.getCustomerOrder().getCart().getShoe().getModel().getName(), Collectors.counting()));

        topSellers.entrySet().stream().forEach(e -> {
            System.out.println("Produkt: " + e.getKey() + ", Antal beställningar: " + e.getValue() + "\n");
        });

        Map<Long, List<String>> topSellersTwo = topSellers
                .entrySet()
                .stream()
                .collect(Collectors.groupingBy(e -> e.getValue(), Collectors.mapping(Map.Entry::getKey, Collectors.toList())));

        System.out.println("Topp 5 köpta modeller: ");
        topSellersTwo.entrySet().stream().sorted(Collections.reverseOrder(Comparator.comparing(Map.Entry::getKey))).limit(3).forEach(longListEntry -> {
            System.out.println(
                    "Antal beställningar: " + longListEntry.getKey() + ", av modell(er): " + longListEntry.getValue());
        });
    }

    // Metod som kallar på AdminRepository för att hämta relevant data från DB
    public  List<Customer> adminGetAllCustomersFromDB() throws IOException {
        List<Customer> listWithCustomerLists = AdminRepository.getInstance().getCustomerAndTransactionalData();

        return listWithCustomerLists;
    }


    public Customer validateLogIn(String userName, String password) throws IOException, SQLException {
        return CustomerRepository.getInstance().logInHandler(userName, password);
    }

    public ListDTO getShoesBrandModelPrice() throws IOException, SQLException {
        return CustomerRepository.getInstance().getShoes();
    }

    public List<Shoe> getAllShoeInfo(String modellNamn) throws IOException, SQLException {
        return CustomerRepository.getInstance().getShoeTransactionalData().stream().filter(shoe -> shoe.getModel().getName().equalsIgnoreCase(modellNamn)).toList();
    }

    public List<Shoe> validateStockStatus(List<Shoe> shoeList) throws IOException {

        List<Shoe> shoesThatCannotBeBought = new ArrayList<>();
        for (int i = 0; i < shoeList.size(); i++) {
            if (CustomerRepository.getInstance().validateStockStatus(shoeList.get(i).getModel().getName(), shoeList.get(i).getColor(), shoeList.get(i).getProductSize())) {
                shoesThatCannotBeBought.add(shoeList.get(i));
            }
        }
        return shoesThatCannotBeBought;
    }

    public boolean processPayment(List<Shoe> shoesInCart, Customer currentCustomer) throws IOException, SQLException {
        return CustomerRepository.getInstance().processPayment(shoesInCart, currentCustomer);
    }
}