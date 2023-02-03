package FunctionalInterfaces;

import Entities.Customer;

@FunctionalInterface
public interface IListCustomersByShoeVaraibleNoGenerics {

    boolean searchWithoutGenerics(Customer cust, String searchString) ;

}
