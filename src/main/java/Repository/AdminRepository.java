package Repository;

import Entities.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class AdminRepository {
    private static AdminRepository instance;
    Properties p = new Properties();

    public AdminRepository() throws IOException {
        p.load(new FileInputStream("src/main/java/resources/Settings.properties"));
    }

    public static AdminRepository getInstance() throws IOException {
        if (instance == null) {
            instance = new AdminRepository();
        }
        return instance;
    }

    public List<Customer> getCustomerAndTransactionalData() {

        List<Customer> customerList = new ArrayList<>();
        List<List<Customer>> finalCustomerList = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        ObjectMapper customerOrderMapper = new ObjectMapper();


        try (Connection con = DriverManager.getConnection(
                p.getProperty("connectionString"),
                p.getProperty("name"),
                p.getProperty("password"));

             //  customer.DOB as CustomerDOB,

             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT customer.id as CustomerID, customer.name as CustomerName, customer.email as CustomerEmail,\n" +
                     "county.id as CountyID, county.name as CountyName, customerOrder.id as CustomerOrderID,\n" +
                     "CustomerOrder.customerid as CustomerOrderCustomerID, cart.id as CartID, cart.customerOrderid as\n" +
                     "CartCustomerOrderID, cart.stockid as CartStockID, model.id as ModelID, model.name as ModelName,\n" +
                     "model.price as ModelPrice, brand.id as BrandID, brand.name as BrandName, stock.id as StockID,\n" +
                     "stock.modelid as StockModelID, stock.productSize as StockProductSize, stock.color as StockColor from customer\n" +
                     "inner join county on customer.countyID = county.id\n" +
                     "inner join customerorder on customer.id = customerorder.customerID\n" +
                     "inner join cart on cart.customerorderid = customerorder.id\n" +
                     "inner join stock on cart.stockid = stock.id\n" +
                     "inner join model on stock.modelID = model.id\n" +
                     "inner join brand on model.brandID = brand.id\n"
             )) {

            while (rs.next()) {

                Model tempModel = new Model();
                Brand tempBrand = new Brand();
                Shoe tempShoeForCart = new Shoe();
                Cart tempCart = new Cart();
                CustomerOrder tempCustomerOrder = new CustomerOrder();

                Customer tempCustomer = new Customer();
                County tempCounty = new County();

                //brand
                tempBrand.setId(rs.getInt("BrandID"));
                tempBrand.setName(rs.getString("BrandName"));

                //model
                tempModel.setBrand(tempBrand);
                tempModel.setId(rs.getInt("ModelID"));
                tempModel.setName(rs.getString("ModelName"));
                tempModel.setPrice(rs.getDouble("ModelPrice"));
                //Shoe
                tempShoeForCart.setModel(tempModel);
                tempShoeForCart.setId(rs.getInt("CartStockID"));
                tempShoeForCart.setColor(rs.getString("StockColor"));
                tempShoeForCart.setProductSize(rs.getInt("StockProductSize"));


                //sätter cart
                tempCart.setId(rs.getInt("CartID"));
                tempCart.setShoe(tempShoeForCart);

                //sätter customerOrder
                tempCustomerOrder.setId(rs.getInt("CustomerOrderId"));
                tempCustomerOrder.setCart(tempCart);

                //sätter county
                tempCounty.setId(rs.getInt("CountyID"));
                tempCounty.setName(rs.getString("CountyName"));

                //sätter customer
                tempCustomer.setId(rs.getInt("CustomerID"));
                tempCustomer.setName(rs.getString("CustomerName"));
                tempCustomer.setEmail(rs.getString("CustomerEmail"));
//                tempCustomer.setDOB(rs.getDate("CustomerDOB").toLocalDate());
                tempCustomer.setCounty(tempCounty);
                //vi populerar EJ isAdmin eller Password. Avsiktligt ignorerat.
                tempCustomer.setCustomerOrder(tempCustomerOrder);

                //adderar till listan
                customerList.add(tempCustomer);
            }



//            //Nya funktionen som ersätter den gamla (om allt går bra) högre ordningens funktion 100%
//            BiFunction<Customer, Integer, List<Customer>> customerModifierFunction = (incomingCustomer, outerIndex) -> {
//
//                //IntStream loopar tar och jämför incomingCustomer med alla andra efterkommande customers och returnerar ut en optional
//                List<Optional<Customer>> listOfCustomers = IntStream.range(outerIndex, customerList.size()).mapToObj(e -> { //tar index i den inre loopen och skickar vidare
//                    Function<Integer, Optional<Customer>> innerLoop = e2 -> { //här kommer index in till den inre loopen, används för att använda .get() på customerList listan och få alla customerobjekt framför inkommande customer
//                        if (customerList.get(e2).getId() == incomingCustomer.getId()) { //om objekten delar id (samma customer)
//                            try {
//                                Customer deepCustomerCopy = mapper
//                                        .readValue(mapper.writeValueAsString(incomingCustomer), Customer.class); //gör en deep copy på den inkommande skon--> https://www.baeldung.com/java-deep-copy
//
//                                CustomerOrder deepCustomerOrder = customerOrderMapper
//                                        .readValue(customerOrderMapper.writeValueAsString(customerList.get(e2).getCustomerOrder()), CustomerOrder.class);
//
//                                deepCustomerCopy.getCustomerOrderSet().add(deepCustomerOrder); //lägg till kategorin på den kopierade skon mot objektet som vi jämförde.
//                                return Optional.of(deepCustomerCopy); //returnera en optional med den kopierade skon
//                            } catch (Exception exception) {
//                                exception.printStackTrace();
//                            }
//                        }
//                        return Optional.empty(); //returnerar om id var falskt, tom optional.
//                    };
//                    return innerLoop.apply(e); // vi returnerar ut en optional från funktionen "innerLoop".
//                }).toList(); //vi måste samla upp alla objekt nu
//                return listOfCustomers.stream().filter(Optional::isPresent).map(Optional::get).distinct().collect(Collectors.toList());
//            };
//
//            finalCustomerList = IntStream.range(0, customerList.size()).mapToObj(e -> customerModifierFunction.apply(customerList.get(e), e)).toList();


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return customerList;
    }

}
