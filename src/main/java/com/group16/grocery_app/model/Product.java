package com.group16.grocery_app.model;

import javafx.scene.image.Image;

public class Product {
    private int id;
    private String name;
    private ProductType type;
    private double price;
    private double stock;
    private double threshold;
    private Image image;

    public Product(int id, String name, ProductType type, double price, double stock, double threshold, Image image) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.price = price;
        this.stock = stock;
        this.threshold = threshold;
        this.image = image;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public ProductType getType() { return type; }
    public double getPrice() { return price; }
    public double getStock() { return stock; }
    public Image getImage() { return image; }
}