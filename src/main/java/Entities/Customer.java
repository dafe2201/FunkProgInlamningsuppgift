package Entities;

import java.time.LocalDate;

public class Customer {

    protected int id;
    protected String name;
    protected String email;
    protected String password;
    protected int countyID;
    protected int isAdmin;
    protected LocalDate DOB;


    public Customer(){}

    public Customer(int id, String name, String email, String password, int countyID, int isAdmin, LocalDate DOB) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.countyID = countyID;
        this.isAdmin = isAdmin;
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

    public int getCountyID() {
        return countyID;
    }

    public void setCountyID(int countyID) {
        this.countyID = countyID;
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

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", countyID=" + countyID +
                ", isAdmin=" + isAdmin +
                ", DOB=" + DOB +
                '}';
    }
}
