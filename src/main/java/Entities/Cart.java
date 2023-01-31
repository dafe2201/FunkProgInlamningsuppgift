package Entities;

import java.util.List;

public class Cart {
    protected List<Shoe> shoesInCart;

    public Cart(){}
    public Cart(List<Shoe> shoesInCart) {
        this.shoesInCart = shoesInCart;
    }

    public List<Shoe> getShoesInCart() {
        return shoesInCart;
    }

    public void setShoesInCart(List<Shoe> shoesInCart) {
        this.shoesInCart = shoesInCart;
    }

}
