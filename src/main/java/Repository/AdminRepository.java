package Repository;

import Entities.County;
import Entities.Customer;

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




    public List<Customer> getAllCustomersFromDB(){

        List<Customer> customerList = new ArrayList<>();

        try (Connection con = DriverManager.getConnection(
                p.getProperty("connectionString"),
                p.getProperty("name"),
                p.getProperty("password"));

        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT customer.id as ID, customer.name as Name, customer.email as Email, customer.DOB as DOB, county.name as County from customer\n" +
                "inner join county on customer.countyID = county.id")) {

            Customer tempCustomer = new Customer();
            County tempCounty = new County();

            while (rs.next()){
                tempCustomer.setId(rs.getInt("ID"));
                tempCustomer.setName("Name");
                tempCustomer.setEmail("Email");
                tempCustomer.setDOB(rs.getDate("DOB").toLocalDate());

                tempCounty.setName(rs.getString("County"));
                tempCustomer.setCounty(tempCounty);

                customerList.add(tempCustomer);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return customerList;
    }

}
