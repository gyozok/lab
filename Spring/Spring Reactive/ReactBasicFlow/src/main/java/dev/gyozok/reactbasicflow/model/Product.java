package dev.gyozok.reactbasicflow.model;

import org.springframework.data.annotation.Id;

// we don't need @Entity annotation, because this is not a JPA application
public class Product {

    @Id
    private int id;
    private String name;

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
}
