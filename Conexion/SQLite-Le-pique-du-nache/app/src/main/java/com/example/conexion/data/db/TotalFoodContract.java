package com.example.conexion.data.db;

import android.provider.BaseColumns;

//Contract es como un diccionario de la base de datos, su función unica es definir los nombres constantes para que no comenta un error de escritura
public final class TotalFoodContract {

//La clase FInal TotalFoodContract es final ya que ninguna otra clase puede heredar esta además de ser una instancia privada evitando que alguien pueda crear un objeto en la clase
    private TotalFoodContract() {}
//UserEntry funciona como una implementación de forma estatica para organizar todo lo relacionado con las tablas y el _ID
    public static class UserEntry implements BaseColumns {
        public static final String TABLE_NAME = "users";
        public static final String COLUMN_NAME_USERNAME = "username";
        public static final String COLUMN_NAME_PASSWORD_HASH = "password_hash";
        public static final String COLUMN_NAME_EMAIL = "email";
        // --- COLUMNA PARA IDENTIFICAR ADMINISTRADORES ---
        public static final String COLUMN_NAME_IS_ADMIN = "is_admin"; // 1 para admin, 0 para no-admin

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        COLUMN_NAME_USERNAME + " TEXT UNIQUE NOT NULL COLLATE NOCASE," +
                        COLUMN_NAME_PASSWORD_HASH + " TEXT NOT NULL," +
                        COLUMN_NAME_EMAIL + " TEXT UNIQUE COLLATE NOCASE," +
                        // --- SE AÑADE LA COLUMNA A LA CREACIÓN DE LA TABLA ---
                        COLUMN_NAME_IS_ADMIN + " INTEGER DEFAULT 0)";

        public static final String SQL_DELETE_TABLE =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static class FoodEntry implements BaseColumns {
        public static final String TABLE_NAME = "alimentos";
        public static final String COLUMN_NAME_NOMBRE = "nombre";
        public static final String COLUMN_NAME_DESCRIPCION = "descripcion";
        public static final String COLUMN_NAME_PRECIO = "precio";
        public static final String COLUMN_NAME_CATEGORIA = "categoria";
        public static final String COLUMN_NAME_IMAGE_PATH = "image_path";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        COLUMN_NAME_NOMBRE + " TEXT NOT NULL," +
                        COLUMN_NAME_DESCRIPCION + " TEXT," +
                        COLUMN_NAME_PRECIO + " REAL NOT NULL," +
                        COLUMN_NAME_CATEGORIA + " TEXT," +
                        COLUMN_NAME_IMAGE_PATH + " TEXT)";

        public static final String SQL_DELETE_TABLE =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
