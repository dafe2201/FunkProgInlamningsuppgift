package Repository;

import  DTO.ListDTO;
import Entities.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class CustomerRepository {
    private static CustomerRepository instance;

    Properties p = new Properties();

    public CustomerRepository() throws IOException {
        p.load(new FileInputStream("src/main/java/resources/Settings.properties"));
    }

    public static CustomerRepository getInstance() throws IOException {
        if (instance == null) {
            instance = new CustomerRepository();
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
                currentCustomer.setAdmin(rs.getBoolean("isAdmin"));
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

            }
            listDTO.setListOfBrands(listOfBrands);
            listDTO.setListOfModels(listOfModels);
        }
        return listDTO;
    }

    public List<Shoe> getShoeTransactionalData() throws SQLException, JsonProcessingException {
        List<Shoe> allShoes = new ArrayList<>();
        List<Shoe> finalShoeList;
        //Anv??nds f??r att g??ra en deep copy. Se Maven projekt f??r importerade libraries som till??ter oss att g??ra deep copies enkelt
        ObjectMapper mapper = new ObjectMapper();

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

            //Egensnickrad funktion
            BiFunction<Shoe, Integer, List<Shoe>> shoeModifierFunction = (incomingShoe, outerIndex) -> {
                //IntStream loopar tar och j??mf??r incomingShoe med alla andra efterkommande skor och returnerar ut en optional
                List<Optional<Shoe>> listOfShoes = IntStream.range(outerIndex, allShoes.size()).mapToObj(e -> { //tar index i den inre loopen och skickar vidare
                    Function<Integer, Optional<Shoe>> innerLoop = e2 -> { //h??r kommer index in till den inre loopen, anv??nds f??r att anv??nda .get() p?? allShoes listan och f?? alla skoobjekt framf??r inkommande skon
                        if (allShoes.get(e2).getId() == incomingShoe.getId()) { //om objekten delar id (samma sko)
                            try {
                                Shoe deepShoeCopy = mapper
                                        .readValue(mapper.writeValueAsString(incomingShoe), Shoe.class); //g??r en deep copy p?? den inkommande skon--> https://www.baeldung.com/java-deep-copy
                                deepShoeCopy.getModel().getCategories().add(allShoes.get(e2).getModel().getCategory()); //l??gg till kategorin p?? den kopierade skon mot objektet som vi j??mf??rde.
                                return Optional.of(deepShoeCopy); //returnera en optional med den kopierade skon
                            } catch (Exception exception) {
                                exception.printStackTrace();
                            }
                        }
                        return Optional.empty(); //returnerar om id var falskt, tom optional.
                    };
                    return innerLoop.apply(e); // vi returnerar ut en optional fr??n funktionen "innerLoop".
                }).toList(); //vi m??ste samla upp alla objekt nu
              return listOfShoes.stream().filter(Optional::isPresent).map(Optional::get).distinct().collect(Collectors.toList());
            };

            //H??gre ordningens funktion (VG)
            List<List<Shoe>> modifiedShoeList = IntStream.range(0, allShoes.size()).mapToObj(e -> shoeModifierFunction.apply(allShoes.get(e), e)).toList();
            finalShoeList = modifiedShoeList.stream().flatMap(Collection::stream).distinct().toList();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return finalShoeList;
    }

    //Lagerstatus skall alltid vara f??rsk information fr??n DB. D??rav att vi anropar DB direkt f??r att validera lagerstatus
    //Vi anser att det vidare ??r of??rsvarsbart att dra in hela databasen igen f??r att kontrollera lagerstatus av en specifik sko.
    public boolean validateStockStatus(String modelName, String color, int size) {
        boolean productCantBeBought = true;
        try (Connection con = DriverManager.getConnection(
                p.getProperty("connectionString"),
                p.getProperty("name"),
                p.getProperty("password"));

             PreparedStatement stmt = con.prepareStatement(
                     "SELECT stock.amount FROM stock\n" +
                             "INNER JOIN model ON stock.modelID = model.id\n" +
                             "WHERE model.name = ? AND stock.color = ? AND stock.productSize = ?")) {
            stmt.setString(1, modelName);
            stmt.setString(2, color);
            stmt.setDouble(3, size);
            stmt.executeQuery();
            ResultSet rs = stmt.getResultSet();

            while (rs.next()) {
                if (rs.getInt("amount") <= 0) {
                    return productCantBeBought;
                } else {
                    productCantBeBought = false;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return productCantBeBought;
    }


    public boolean processPayment(List<Shoe> shoesInCart, Customer currentCustomer) throws SQLException {

        boolean isSuccessful = false;

        try (Connection con = DriverManager.getConnection(
                p.getProperty("connectionString"),
                p.getProperty("name"),
                p.getProperty("password"))) {
            CallableStatement stmt = con.prepareCall("CALL addToCart(?, ?, ?)");

            try {
                stmt.setInt(1, currentCustomer.getId());
                stmt.setNull(2, Types.INTEGER);
                stmt.setInt(3, shoesInCart.get(0).getId());
                stmt.execute();

                isSuccessful = true;

                if (shoesInCart.size() > 1) {
                    int lastId;
                    List<Integer> idNumberList = new ArrayList<>();
                    PreparedStatement lastIdStatement = con.prepareStatement("SELECT customerorder.id AS last_id from customerOrder");
                    ResultSet rs = lastIdStatement.executeQuery();

                    while (rs.next()) {
                        lastId = rs.getInt("last_id");
                        idNumberList.add(lastId);
                    }

                    IntSummaryStatistics summary = idNumberList.stream().mapToInt(num -> num).summaryStatistics();
                    lastId = summary.getMax();

                    for (int i = 1; i < shoesInCart.size(); i++) {
                        stmt.setInt(1, currentCustomer.getId());
                        stmt.setInt(2, lastId);
                        stmt.setInt(3, shoesInCart.get(i).getId());
                        stmt.execute();
                    }
                }
            } catch (IndexOutOfBoundsException ioobe) {
                return isSuccessful;
            }
        }
        return isSuccessful;
    }

}

