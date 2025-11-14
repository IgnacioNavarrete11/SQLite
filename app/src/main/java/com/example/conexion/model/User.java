package com.example.conexion.model;

import com.google.firebase.firestore.Exclude;

public class User {
    // Es crucial que el ID del documento esté disponible pero no se guarde como un campo
    // dentro del propio documento en Firestore, por eso usamos @Exclude.
    @Exclude
    private String documentId;

    private String username;
    private String email;
    private String role;

    // Constructor vacío, ¡obligatorio para que Firestore pueda deserializar los datos!
    public User() {}

    public User(String username, String email, String role) {
        this.username = username;
        this.email = email;
        this.role = role;
    }

    // --- Getters ---
    public String getDocumentId() {
        return documentId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    // --- Setters ---
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}
