package FunctionalInterfaces;

import Entities.Customer;

/**
 * @param <T> Takes in the Type to search for.
 * @param <R> Returns the specified type we have assigned to the functional method.
 */


@FunctionalInterface
public interface IListCustomersByShoeVariable<T extends Customer, U extends String, R extends Boolean> {

    R search(T t, U u);

}
