package Service;

import DTO.ListDTO;
import Entities.Customer;
import Entities.Shoe;
import Repository.Repository;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class Service {
    public Service() throws IOException, SQLException  {

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
        for (int i=0; i<shoeList.size(); i++) {
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
