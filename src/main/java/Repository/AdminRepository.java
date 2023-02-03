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

        try (Connection con = DriverManager.getConnection(
                p.getProperty("connectionString"),
                p.getProperty("name"),
                p.getProperty("password"));

             //  customer.DOB as CustomerDOB,

             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT customer.id AS 'CustomerID', customer.name AS 'CustomerName', customer.email AS 'CustomerEmail',\n" +
                             "county.id AS 'CountyID', county.name AS 'CountyName', customerOrder.id AS 'CustomerOrderID',\n" +
                             "customerOrder.customerID AS 'CustomerOrderCustomerID', cart.id AS 'CartID', cart.customerOrderID AS\n" +
                             "'CartCustomerOrderID', cart.stockID AS 'CartStockID', model.id AS 'ModelID', model.name  AS 'ModelName',\n" +
                             "model.price AS 'ModelPrice', brand.id AS 'BrandID', brand.name AS 'BrandName', stock.id AS 'StockID',\n" +
                             "stock.modelID AS 'StockModelID', stock.productSize AS 'StockProductSize', stock.color AS 'StockColor' \n" +
                             "FROM customer\n" +
                             "INNER JOIN county ON customer.countyID = county.id\n" +
                             "INNER JOIN customerOrder ON customer.id = customerOrder.customerID\n" +
                             "INNER JOIN cart ON cart.customerOrderID = customerOrder.id\n" +
                             "INNER JOIN stock ON cart.stockID = stock.id\n" +
                             "INNER JOIN model ON stock.modelID = model.id \n" +
                             "INNER JOIN brand on model.brandID = brand.id"
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
                tempCustomer.setCounty(tempCounty);
                //vi populerar EJ isAdmin eller Password. Avsiktligt ignorerat.
                tempCustomer.setCustomerOrder(tempCustomerOrder);

                //adderar till listan
                customerList.add(tempCustomer);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return customerList;
    }

}
