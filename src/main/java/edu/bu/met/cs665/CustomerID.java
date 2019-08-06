package edu.bu.met.cs665;

public class CustomerID {
    private String name;
    private int id;
    private String address;

    public CustomerID(String n, int id, String address) {
        this.name = n;
        this.id = id;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
