package Entities;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Customer {

    protected int id;
    protected String name;
    protected String email;
    protected String password;
    protected County county;
    protected boolean isAdmin;
    protected LocalDate DOB;
    protected CustomerOrder customerOrder;


    public Customer(){}

    public Customer(CustomerOrder customerOrder, int id, String name, String email, String password, County county, boolean isAdmin, LocalDate DOB) {
        this.customerOrder = customerOrder;
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.county = county;
        this.isAdmin = isAdmin;
        this.DOB = DOB;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return id == customer.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public CustomerOrder getCustomerOrder() {
        return customerOrder;
    }

    public void setCustomerOrder(CustomerOrder customerOrder) {
        this.customerOrder = customerOrder;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public County getCounty() {
        return county;
    }

    public void setCounty(County county) {
        this.county = county;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public LocalDate getDOB() {
        return DOB;
    }

    public void setDOB(LocalDate DOB) {
        this.DOB = DOB;
    }

@Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", county=" + county +
                ", isAdmin=" + isAdmin +
                ", DOB=" + DOB +
                ", customerOrder=" + customerOrder +
                '}';
    }
}
