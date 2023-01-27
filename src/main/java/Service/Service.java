package Service;

import DTO.ListDTO;
import Entities.Customer;
import Entities.Shoe;
import Repository.Repository;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class Service {




    public Service() throws IOException {

    }

    public Customer validateLogIn(String userName, String password) throws IOException, SQLException {
        return Repository.getInstance().logInHandler(userName, password);
    }

    public ListDTO getShoes() throws IOException, SQLException {
       return Repository.getInstance().getShoes();
    }

    public List<Shoe> getShoeInfo(String modellNamn) throws IOException, SQLException {
        return Repository.getInstance().getShoeTransactionalData().stream().filter(shoe -> shoe.getModel().getName().equalsIgnoreCase(modellNamn)).toList();

    }

}
