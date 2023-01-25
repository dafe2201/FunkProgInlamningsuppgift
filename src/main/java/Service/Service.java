package Service;

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

    public List<Shoe> printShoes() throws IOException, SQLException {
        return Repository.getInstance().getAllShoes();
    }




}
