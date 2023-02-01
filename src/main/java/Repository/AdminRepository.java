package Repository;

import Entities.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class AdminRepository {
    private static AdminRepository instance;
    Properties p = new Properties();
    List<Customer> customerList = new ArrayList<>();

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

                customerList.add(tempCustomer);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return customerList;
    }

}
