package Repository;

import DTO.ListDTO;
import Entities.Brand;
import Entities.Category;
import Entities.Customer;
import Entities.Model;
import Entities.Shoe;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class Repository {

    private static Repository instance;


//TODO: Se över ifall det istället behövs en Repository-klass per Model

// Tänk på: timeStamp.toLocalDateTime().toLocalDate(); - För att göra om timestamps till LocalDateTime

    Properties p = new Properties();

    List<Customer> customerList = new ArrayList<>();

    public Repository() throws IOException {
        p.load(new FileInputStream("src/main/java/resources/Settings.properties"));
    }

    public static Repository getInstance() throws IOException {
        if (instance == null) {
            instance = new Repository();
        }
        return instance;
    }

    public Customer logInHandler(String userName, String password) throws SQLException {

        try (Connection con = DriverManager.getConnection(
                p.getProperty("connectionString"),
                p.getProperty("name"),
                p.getProperty("password"));
             PreparedStatement stmt = con.prepareStatement("SELECT * FROM customer WHERE customer.name = ? and customer.password = ?")) {

            stmt.setString(1, userName);
            stmt.setString(2, password);
            stmt.executeQuery();

            ResultSet rs = stmt.getResultSet();

            Customer currentCustomer = null;

            while (rs.next()) {
                currentCustomer = new Customer();
                currentCustomer.setId(rs.getInt("id"));
                currentCustomer.setName(rs.getString("name"));
                currentCustomer.setEmail(rs.getString("email"));
                currentCustomer.setPassword(rs.getString("password"));
                currentCustomer.setCountyID(rs.getInt("countyID"));
                currentCustomer.setDOB(rs.getDate("DOB").toLocalDate());
            }
            return currentCustomer;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ListDTO getShoes() throws SQLException {
        ListDTO listDTO = new ListDTO();
        List<Brand> listOfBrands = new ArrayList<>();
        List<Model> listOfModels = new ArrayList<>();


        try (Connection con = DriverManager.getConnection(
                p.getProperty("connectionString"),
                p.getProperty("name"),
                p.getProperty("password"));

             PreparedStatement stmt = con.prepareStatement(
                     "SELECT brand.name AS 'brand name',\n" +
                             "model.name AS 'model name', model.price AS 'model price' FROM \n" +
                             "brand\n" +
                             "INNER JOIN model ON brand.id = model.id\n")) {
            stmt.executeQuery();
            ResultSet rs = stmt.getResultSet();

            while (rs.next()) {
                Brand tempBrand = new Brand();
                Model tempModel = new Model();
                tempBrand.setName(rs.getString("brand name"));
                listOfBrands.add(tempBrand);
                tempModel.setName(rs.getString("model name"));
                tempModel.setBrand(tempBrand);
                tempModel.setPrice(rs.getDouble("model price"));
                listOfModels.add(tempModel);


//                System.out.println(rs.getString("brand name"));
//                System.out.println(rs.getString("model name"));
//                System.out.println(rs.getString("model price"));
            }
           listDTO.setListOfBrands(listOfBrands);
           listDTO.setListOfModels(listOfModels);

        }

        return listDTO;
    }

    //TODO skall användas för hålla ett objekt i javaminnet åt addToCart/Process.
    public List<Shoe> getShoeTransactionalData() throws SQLException {
        List<Shoe> allShoes = new ArrayList<>();
        List<Shoe> shoeListWithoutDuplicates = new ArrayList<>();

        try (Connection con = DriverManager.getConnection(
                p.getProperty("connectionString"),
                p.getProperty("name"),
                p.getProperty("password"));

             PreparedStatement stmt = con.prepareStatement(
                     "SELECT brand.id AS 'brand id', brand.name AS 'brand name',\n" +
                             "category.id AS 'category id', category.name AS 'category name',\n" +
                             "model.id AS 'model id', model.name AS 'model name', model.price AS 'model price',\n" +
                             "stock.id AS 'shoe id', stock.productSize AS 'shoe size', stock.color AS 'shoe color', stock.amount AS 'shoe amount'\n" +
                             "FROM brand\n" +
                             "INNER JOIN model ON brand.id = model.id\n" +
                             "INNER JOIN modelCategoryMapping ON model.id = modelCategoryMapping.modelID\n" +
                             "INNER JOIN category ON modelCategoryMapping.categoryID = category.id\n" +
                             "INNER JOIN stock ON stock.modelID = model.id")) {
            stmt.executeQuery();
            ResultSet rs = stmt.getResultSet();


            while (rs.next()) {
                Brand tempBrand = new Brand();
                Category tempCategory = new Category();
                Model tempModel = new Model();
                Shoe tempShoe = new Shoe();

                tempBrand.setId(rs.getInt("brand id"));
                tempBrand.setName(rs.getString("brand name"));

                tempCategory.setId(rs.getInt("category id"));
                tempCategory.setName(rs.getString("category name"));

                tempModel.setId(rs.getInt("model id"));
                tempModel.setName(rs.getString("model name"));
                tempModel.setPrice(rs.getDouble("model price"));

                tempModel.setBrand(tempBrand);
                tempModel.setCategory(tempCategory);

                tempShoe.setId(rs.getInt("shoe id"));
                tempShoe.setProductSize(rs.getInt("shoe size"));
                tempShoe.setColor(rs.getString("shoe color"));
                tempShoe.setAmount(rs.getInt("shoe amount"));

                tempShoe.setModel(tempModel);

                allShoes.add(tempShoe);

            }


            //Itererar över den befintliga listan för att populera kategorilistan med vilka kategorier som en sko förekommer i
            allShoes.stream().peek(e1 -> {
                Consumer<Shoe> updateCategoryListSet = e2 -> {
                    allShoes
                            .forEach(myShoe1 -> {
                                Consumer<Shoe> goingdeeper = myShoe2 -> {
                                    if (myShoe2.getId() == e2.getId()) {
                                        e2.getModel().getCategories().add(myShoe2.getModel().getCategory());
                                    }
                                };
                                goingdeeper.accept(myShoe1);
                            });
                };
                updateCategoryListSet.accept(e1);
            }).toList();

            //rensar ut duplicerade värden ur ursprungslistan och returnerar ut en städad lista
            shoeListWithoutDuplicates = allShoes.stream().distinct().collect(Collectors.toList());


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return shoeListWithoutDuplicates;
    }
}

