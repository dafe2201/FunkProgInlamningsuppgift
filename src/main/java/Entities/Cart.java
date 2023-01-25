package Entities;

public class Cart {

    private int id;
    private int customerOrderID;
    private int stockID;

    public Cart(int id, int customerOrderID, int stockID) {
        this.id = id;
        this.customerOrderID = customerOrderID;
        this.stockID = stockID;
    }

    public Cart(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomerOrderID() {
        return customerOrderID;
    }

    public void setCustomerOrderID(int customerOrderID) {
        this.customerOrderID = customerOrderID;
    }

    public int getStockID() {
        return stockID;
    }

    public void setStockID(int stockID) {
        this.stockID = stockID;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "id=" + id +
                ", customerOrderID=" + customerOrderID +
                ", stockID=" + stockID +
                '}';
    }
}
