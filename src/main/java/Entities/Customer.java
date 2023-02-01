package Entities;

import java.time.LocalDate;
import java.util.List;

public class Customer {

   protected List<CustomerOrder> customerOrderList;
    protected int id;
    protected String name;
    protected String email;
    protected String password;
    protected County county;
    protected int isAdmin;
    protected LocalDate DOB;


    public Customer(){}

    public Customer(int id, String name, String email, String password, int isAdmin, County county, LocalDate DOB) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.isAdmin = isAdmin;
        this.county = county;
        this.DOB = DOB;
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

    public LocalDate getDOB() {
        return DOB;
    }

    public void setDOB(LocalDate DOB) {
        this.DOB = DOB;
    }

    public int getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(int isAdmin) {
        this.isAdmin = isAdmin;
    }

    public County getCounty() {
        return county;
    }

    public void setCounty(County county) {
        this.county = county;
    }

    public List<CustomerOrder> getCustomerOrderList() {
        return customerOrderList;
    }

    public void setCustomerOrderList(List<CustomerOrder> customerOrderList) {
        this.customerOrderList = customerOrderList;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerOrderList=" + customerOrderList +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", county=" + county +
                ", isAdmin=" + isAdmin +
                ", DOB=" + DOB +
                '}';
    }
}
