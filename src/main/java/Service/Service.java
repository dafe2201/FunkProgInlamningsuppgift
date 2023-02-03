package Service;

import DTO.ListDTO;
import Entities.Customer;

import Entities.Shoe;
import FunctionalInterfaces.IListCustomersByShoeVaraibleNoGenerics;
import FunctionalInterfaces.IListCustomersByShoeVariable;
import Repository.CustomerRepository;
import Repository.AdminRepository;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

import java.util.stream.Collectors;


public class Service {
    public Service() {

    }

    //Rapport 1 i VG: (HÖGRE ORDNINGENS FUNKTION)
    public  void adminListCustomersByShoeColor(String input, IListCustomersByShoeVaraibleNoGenerics function) throws IOException {
        List<Customer> customerList = adminGetAllCustomersFromDB();
        System.out.println("Följande har köpt skor i färgen " + input);
        customerList.stream()
                .filter(customer -> function.searchWithoutGenerics(customer, input))
                .distinct()
                .forEach( customer -> {
                    System.out.println(customer.getName());
                    System.out.println(customer.getCounty().getName() +"\n");
                });
    }

    //rapport 1 i VG: (HÖGRE ORDNINGENS FUNKTION)
    public void adminListCustomersByShoeBrand(String input, IListCustomersByShoeVariable<Customer, String, Boolean> function) throws IOException {
        List<Customer> customerList = adminGetAllCustomersFromDB();
        System.out.println("Följande har köpt skor av märket " + input);
        customerList.stream()
                .filter(customer -> function.search(customer, input)) //Varför vill den konvertera till en boolean? (åtgärdat)
                .distinct()                                           //Svar: Vi var tvungna att extenda returtypen till boolan,
                .forEach( customer -> {                               //Java fattade inte annars att vi garanterade boolean och tvingade oss att casta om resultatet till boolean
                    System.out.println(customer.getName());
                    System.out.println(customer.getCounty().getName() +"\n");
                });
    }

    //rapport 1 i VG: (HÖGRE ORDNINGENS FUNKTION)
    public void adminListCustomersByShoeSize(String input, IListCustomersByShoeVariable<Customer, String, Boolean> function) throws IOException {
        List<Customer> customerList = adminGetAllCustomersFromDB();
        System.out.println("Följande har köpt skor i storleken " + input);
        customerList.stream()
                .filter(customer -> function.search(customer, input))
                .distinct()
                .forEach( customer -> {
                    System.out.println(customer.getName());
                    System.out.println(customer.getCounty().getName() +"\n");
                });
    }

    //Rapport 2 i VG
    public void adminListCustomerOrderCount() throws IOException {
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
    public void adminListCustomerSpending() throws IOException {
        List<Customer> customerList = adminGetAllCustomersFromDB();

        Map<String, Double> customerSpending = customerList.stream()
                .collect(Collectors.groupingBy(e -> e.getName(), Collectors.summingDouble(e -> e.getCustomerOrder().
                        getCart().getShoe().getModel().getPrice())));

        customerSpending.entrySet().stream().forEach(e -> {
            System.out.println("Namn: " + e.getKey() + ", Summa av beställningar: " + e.getValue() + "\n");
        });
    }


    //Rapport 4 i VG
    public void adminGetRevenueByCounty() throws IOException {

        List<Customer> customerList = adminGetAllCustomersFromDB();

        Map<String, Double> revenueByCounty = customerList.stream()
                .collect(Collectors.groupingBy(e -> e.getCounty().getName(), Collectors.summingDouble(e -> e.getCustomerOrder()
                        .getCart().getShoe().getModel().getPrice())));

        revenueByCounty.entrySet().stream().forEach(e -> {
            System.out.println("Ort: " + e.getKey() + ", Summa av beställningar: " + e.getValue() + "\n" );
        });

    }

    //Rapport 5 i VG
    public void adminGetTopSellingProducts() throws IOException {
        List <Customer> customerList = adminGetAllCustomersFromDB();

        Map<String, Long> topSellers = customerList.stream()
                .collect(Collectors.groupingBy(e -> e.getCustomerOrder().getCart().getShoe().getModel().getName(), Collectors.counting()));

        Map<Long, List<String>> topSellersTwo = topSellers
                .entrySet()
                .stream()
                .collect(Collectors.groupingBy(e -> e.getValue(), Collectors.mapping(Map.Entry::getKey, Collectors.toList())));

        System.out.println("Topp 3 mest sålda skor: ");
        topSellersTwo.entrySet().stream().sorted(Collections.reverseOrder(Comparator.comparing(Map.Entry::getKey))).limit(3).forEach(longListEntry -> {
            System.out.println(
                    "Sålda skor: " + longListEntry.getKey() + ", av modell(er): " + longListEntry.getValue());
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