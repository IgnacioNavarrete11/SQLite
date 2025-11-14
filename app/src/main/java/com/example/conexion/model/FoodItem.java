package com.example.conexion.model;

import com.google.firebase.firestore.Exclude; // <-- AÑADIDO

public class FoodItem {
    
    @Exclude // <-- AÑADIDO: Para que Firestore ignore este campo al guardar/leer
    private String documentId;

    private String name;
    private String description;
    private double price;
    private String imageUrl;
    private String category;

    // Constructor vacío
    public FoodItem() {}

    // Constructor actualizado (no necesita el id)
    public FoodItem(String name, String description, double price, String imageUrl, String category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.category = category;
    }

    // --- Getters ---
    
    public String getDocumentId() { // <-- AÑADIDO
        return documentId;
    }
    
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
    public String getCategory() { return category; }
    
    // --- Setters ---
    
    public void setDocumentId(String documentId) { // <-- AÑADIDO
        this.documentId = documentId;
    }
}
