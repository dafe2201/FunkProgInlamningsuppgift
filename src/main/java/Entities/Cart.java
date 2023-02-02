package Entities;

public class Cart {

    protected int id;
    protected Shoe shoe;


    public Cart(){}

    public Cart(int id, Shoe shoe) {
        this.id = id;
        this.shoe = shoe;
    }

    public Shoe getShoe() {
        return shoe;
    }

    public void setShoe(Shoe shoe) {
        this.shoe = shoe;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "id=" + id +
                ", shoe=" + shoe +
                '}';
    }
}
