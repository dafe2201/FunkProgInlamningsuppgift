package Entities;

public class CustomerOrder {

    protected int id;
    protected Cart cart;

    public CustomerOrder(){}

    public CustomerOrder(int id,Cart cart) {
        this.id = id;
        this.cart = cart;
    }

    public CustomerOrder(int id){
        this.id = id;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "CustomerOrder{" +
                "id=" + id +
                ", cart=" + cart +
                '}';
    }
}
