package Entities;

public class CustomerOrder {

private int id;
private int customerID;


public CustomerOrder(){}

    public CustomerOrder(int id, int customerID) {
        this.id = id;
        this.customerID = customerID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    @Override
    public String toString() {
        return "CustomerOrder{" +
                "id=" + id +
                ", customerID=" + customerID +
                '}';
    }

}
