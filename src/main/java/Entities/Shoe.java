package Entities;

public class Shoe {
// SHOE REPRESENTERAR STOCK PÅ INDIVIDNIVÅ (en sko)
    private int id;
    private Model model;
    private String color;
    private int productSize;
    private int amount;

    public Shoe(){}
    public Shoe(int id, Model model,String color, int productSize, int amount) {
        this.id = id;
        this.color = color;
        this.model = model;
        this.productSize = productSize;
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Model getModel() {
        return model;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public int getProductSize() {
        return productSize;
    }

    public void setProductSize(int productSize) {
        this.productSize = productSize;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Shoe{" +
                "id=" + id +
                ", model=" + model +
                ", color='" + color + '\'' +
                ", productSize=" + productSize +
                ", amount=" + amount +
                '}';
    }
}
