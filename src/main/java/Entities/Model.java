package Entities;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Model {

    private int id;
    private String name;
    private double price;

    private Brand brand;

    private Category category;

    private Set<Category> categoriesSet = new HashSet<>();


    public Model() {};

    public Model(int id, String name, double price, Brand brand, Category category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.brand = brand;
        this.category = category;
    }

    public Set<Category> getCategories() {
        return categoriesSet;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Model{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", brand=" + brand +
                ", category=" + category +
                ", categoriesSet=" + categoriesSet +
                '}';
    }
}





