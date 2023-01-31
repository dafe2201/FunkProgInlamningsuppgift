package Repository;

import DTO.ListDTO;
import Entities.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

            }
            listDTO.setListOfBrands(listOfBrands);
            listDTO.setListOfModels(listOfModels);

        }

        return listDTO;
    }

    //TODO skall användas för hålla ett objekt i javaminnet åt addToCart/Process.
    public List<Shoe> getShoeTransactionalData() throws SQLException, JsonProcessingException {
        List<Shoe> allShoes = new ArrayList<>();
        //TODO: KAN TAS BORT NÄR VI HAR ETABLERAT ATT NYA METODEN ERSÄTTER DEN GAMLA:
        //TODO: --
        List<Shoe> shoeListWithoutDuplicates;
        //TODO: --
        List<Shoe> finalShoeList;
        //används för att göra en deepcopy. Se Mavenprojekt för importerade libraries som tillåter oss att göra deepcopies enkelt
        ObjectMapper objectMapper = new ObjectMapper();

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


            //TODO: KAN TAS BORT NÄR VI HAR ETABLERAT ATT NYA METODEN ERSÄTTER DEN GAMLA:
            //TODO: --
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
            //TODO: --


            //TODO: KAN TAS BORT NÄR GENOMGÅNG ÄR GENOMFÖRD:
            //TODO: --
            Shoe deepShoe = objectMapper
                    .readValue(objectMapper.writeValueAsString(allShoes.get(0)), Shoe.class);
            System.out.println("DEEP SHOE: " + deepShoe.getModel().getName());
            System.out.println("OG SHOE (index 0): " + allShoes.get(0).getModel().getName());
            //TODO: ANVÄNDS ENDAST I DEBUGGING-/TESTSYFTE
            //TODO: --

            //Nya funktionen som ersätter den gamla (om allt går bra) högre ordningens funktion 100%
            BiFunction<Shoe, Integer, List<Shoe>> shoeModifierFunction = (incomingShoe, outerIndex) -> {
                //IntStream loopar tar och jämför incomingShoe med alla andra efterkommande skor och returnerar ut en optional
                List<Optional<Shoe>> listOfShoes = IntStream.range(outerIndex, allShoes.size() - 1).mapToObj(e -> { //tar index i den inre loopen och skickar vidare
                    Function<Integer, Optional<Shoe>> innerLoop = e2 -> { //här kommer index in till den inre loopen, används för att använda .get() på allShoes listan och få alla skoobjekt framför inkommande skon
                        if (allShoes.get(e2).getId() == incomingShoe.getId()) { //om objekten delar id (samma sko)
                            try {
                                ObjectMapper mapper = new ObjectMapper();
                                Shoe deepShoeCopy = mapper
                                        .readValue(mapper.writeValueAsString(incomingShoe), Shoe.class); //gör en deep copy på den inkommande skon--> https://www.baeldung.com/java-deep-copy
                                deepShoeCopy.getModel().getCategories().add(allShoes.get(e2).getModel().getCategory()); //lägg till kategorin på den kopierade skon mot objektet som vi jämförde.
                                return Optional.of(deepShoeCopy); //returnera en optional med den kopierade skon
                            } catch (Exception exception) {
                                exception.printStackTrace();
                            }
                        }
                        return Optional.empty(); //returnerar om id var falskt, tom optional.
                    };
                    return innerLoop.apply(e); // vi returnerar ut en optional från funktionen "innerLoop".
                }).toList(); //vi måste samla upp alla objekt nu
              return listOfShoes.stream().filter(Optional::isPresent).map(Optional::get).distinct().collect(Collectors.toList());
            };

            List<List<Shoe>> modifiedShoeList = IntStream.range(0, allShoes.size()).mapToObj(e -> shoeModifierFunction.apply(allShoes.get(e), e)).toList();
            finalShoeList = modifiedShoeList.stream().flatMap(Collection::stream).distinct().toList();

            //TODO: KAN TAS BORT NÄR VI HAR ETABLERAT ATT NYA METODEN ERSÄTTER DEN GAMLA:
            //TODO: --
            //rensar ut duplicerade värden ur ursprungslistan och returnerar ut en städad lista
            shoeListWithoutDuplicates = allShoes.stream().distinct().collect(Collectors.toList());

            //TODO: KAN TAS BORT NÄR VI HAR ETABLERAT ATT NYA METODEN ERSÄTTER DEN GAMLA (DEBUGGING FÖR NYA METODEN VS GAMLA):
            System.out.println("final shoe list: "+finalShoeList.size() + ", get(0)" + finalShoeList.get(0));
            System.out.println("shoe list without duplicate: "+shoeListWithoutDuplicates.size() + ", get(0)" + shoeListWithoutDuplicates.get(0));
            //TODO: --

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return finalShoeList;
    }

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

            //TODO: Lägg till vad som händer om varan man skrivit in inte finns alls.
            //TODO: - Om vi inte vill att man ska kunna lägga en beställning om stock är 0, så behöver vi inte göra något mer.
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

        boolean isSuccessfull = false;

        try (Connection con = DriverManager.getConnection(
                p.getProperty("connectionString"),
                p.getProperty("name"),
                p.getProperty("password"));) {
            CallableStatement stmt = con.prepareCall("CALL addToCart(?, ?, ?)");

            try {
                stmt.setInt(1, currentCustomer.getId());
                stmt.setNull(2, Types.INTEGER);
                stmt.setInt(3, shoesInCart.get(0).getId());
                stmt.execute();

                isSuccessfull = true;

                if (shoesInCart.size() > 1) {
                    int lastid = 0;
                    List<Integer> idNumberList = new ArrayList<>();
                    PreparedStatement lastIdStatement = con.prepareStatement("SELECT customerorder.id AS last_id from customerOrder");
                    ResultSet rs = lastIdStatement.executeQuery();

                    while (rs.next()) {
                        lastid = rs.getInt("last_id");
                        idNumberList.add(lastid);
                    }

                    IntSummaryStatistics summary = idNumberList.stream().mapToInt(num -> num).summaryStatistics();
                    lastid = summary.getMax();

                    for (int i = 1; i < shoesInCart.size(); i++) {
                        stmt.setInt(1, currentCustomer.getId());
                        stmt.setInt(2, lastid);
                        stmt.setInt(3, shoesInCart.get(i).getId());
                        stmt.execute();
                    }
                }
            } catch (IndexOutOfBoundsException ioobe) {
                return isSuccessfull;
            }

        }
        return isSuccessfull;
    }

}

