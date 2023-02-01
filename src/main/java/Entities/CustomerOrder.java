package Entities;

public class CustomerOrder {

    protected Cart cart;

    public CustomerOrder(){}
    public CustomerOrder(Cart cart) {
        this.cart = cart;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    @Override
    public String toString() {
        return "CustomerOrder{" +
                "cart=" + cart +
                '}';
    }
}
