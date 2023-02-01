package Service;

import DTO.ListDTO;
import Entities.Customer;

import Entities.Shoe;
import Repository.Repository;
import Repository.AdminRepository;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

import java.util.stream.Collectors;


public class Service {
    public Service() throws IOException, SQLException {

    }

    public static void listCustomersByShoeColor(String input) throws IOException {
        List<Customer> customerList = adminGetAllCustomersFromDB();
        customerList.stream()
                .filter(e -> e.getCustomerOrder().getCart().getShoe().getColor().equalsIgnoreCase(input))
                .distinct()
                .forEach( customer -> {
                    System.out.println(customer.getName());
                    System.out.println(customer.getCounty().getName() +"\n");
                });
    }

    public static void listCustomersByShoeBrand(String input) throws IOException {
        List<Customer> customerList = adminGetAllCustomersFromDB();
        customerList.stream()
                .filter(e -> e.getCustomerOrder().getCart().getShoe().getModel().getBrand().getName().equalsIgnoreCase(input))
                .distinct()
                .forEach( customer -> {
                    System.out.println(customer.getName());
                    System.out.println(customer.getCounty().getName() +"\n");
                });
    }

    public static void listCustomersByShoeSize(String input) throws IOException {
        List<Customer> customerList = adminGetAllCustomersFromDB();
        customerList.stream()
                .filter(e -> e.getCustomerOrder().getCart().getShoe().getProductSize() == Integer.parseInt(input))
                .distinct()
                .forEach( customer -> {
                    System.out.println(customer.getName());
                    System.out.println(customer.getCounty().getName() +"\n");
                });
    }

    public static void listCustomerOrderCount() throws IOException {
        List<Customer> customerList = adminGetAllCustomersFromDB();

        Map<String, Long> numberOfCustomerOrders = customerList.stream().
                collect(Collectors.groupingBy(e -> e.getName(), Collectors.counting()));

        numberOfCustomerOrders.entrySet().stream().forEach( e -> {
            System.out.println(
                    "Namn: " + e.getKey() + ", antal beställningar: " + e.getValue() +"\n"
            );
        });
    }


    public static List<Customer> adminGetAllCustomersFromDB() throws IOException {
        List<Customer> listWithCustomerLists = AdminRepository.getInstance().getCustomerAndTransactionalData();

        return listWithCustomerLists;
    }

    public static void listCustomerSpending() throws IOException {
        List<Customer> customerList = adminGetAllCustomersFromDB();
        //En rapport som listar alla kunder och hur mycket pengar varje kund, sammanlagt, har
        //beställt för. Skriv ut varje kunds namn och summa.


    }


    public Customer validateLogIn(String userName, String password) throws IOException, SQLException {
        return Repository.getInstance().logInHandler(userName, password);
    }

    public ListDTO getShoesBrandModelPrice() throws IOException, SQLException {
        return Repository.getInstance().getShoes();
    }

    public List<Shoe> getAllShoeInfo(String modellNamn) throws IOException, SQLException {
        return Repository.getInstance().getShoeTransactionalData().stream().filter(shoe -> shoe.getModel().getName().equalsIgnoreCase(modellNamn)).toList();
    }

    public List<Shoe> validateStockStatus(List<Shoe> shoeList) throws IOException {

        List<Shoe> shoesThatCannotBeBought = new ArrayList<>();
        for (int i = 0; i < shoeList.size(); i++) {
            if (Repository.getInstance().validateStockStatus(shoeList.get(i).getModel().getName(), shoeList.get(i).getColor(), shoeList.get(i).getProductSize())) {
                shoesThatCannotBeBought.add(shoeList.get(i));
            }
        }
        return shoesThatCannotBeBought;
    }

    public boolean processPayment(List<Shoe> shoesInCart, Customer currentCustomer) throws IOException, SQLException {
        return Repository.getInstance().processPayment(shoesInCart, currentCustomer);
    }
}


//
//    public static void adminFinalGetAllCustomersFromDB() throws IOException, SQLException {
//
//        List<List<Customer>> listWithCustomerLists = adminGetAllCustomersFromDB();
//
//        //        ObjectMapper objectMapper = new ObjectMapper();
////        List<Shoe> listWithAllShoes = adminGetAllShoes();
//
//    Function<Shoe, Optional<Shoe>> shoeMapper = shoe ->  {
//            for (int i=0; i<listWithAllShoes.size();i++) {
//                if (shoe.getId() == listWithAllShoes.get(i).getId()) {
//                    try {
//                        Shoe deepShoeCopy = objectMapper
//                                            .readValue(objectMapper.writeValueAsString(listWithAllShoes.get(i)), Shoe.class);
//                        return Optional.of(deepShoeCopy);
//                    } catch (JsonProcessingException e) {
//                        throw new RuntimeException(e);
//                    }
//
//                }
//            }
//            return Optional.empty();
//        };
//
//
//        for (int i = 0; i < listWithCustomerLists.size(); i++) {
//            List<Customer> mycustomerlist = listWithCustomerLists.get(i);
//            for (int j = 0; j < mycustomerlist.size(); j++) {
//
//                Set<CustomerOrder> customerOrderSet = mycustomerlist.get(j).getCustomerOrderSet();
//
//                customerOrderSet.stream().map(CustomerOrder::getCart).peek(e-> {
//                Optional<Shoe> maybeShoe = shoeMapper.apply(e.getShoe());
//                    maybeShoe.ifPresent(e::setShoe);
//                }).collect(Collectors.toList());
//
//            }
//        }
//
//        listWithCustomerLists.forEach(e -> System.out.println(e));
//        System.out.println("***************************");
//        listWithAllShoes.forEach(e -> System.out.println(e));
//
//    }

//    public static List<Shoe> adminGetAllShoes() throws IOException, SQLException {
//        return Repository.getInstance().getShoeTransactionalData();
//    }