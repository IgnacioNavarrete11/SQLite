package com.example.conexion.data.db;

import android.provider.BaseColumns;

public final class TotalFoodContract {

    private TotalFoodContract() {}

    public static class UserEntry implements BaseColumns {
        public static final String TABLE_NAME = "users";
        public static final String COLUMN_NAME_USERNAME = "username";
        public static final String COLUMN_NAME_PASSWORD_HASH = "password_hash";
        public static final String COLUMN_NAME_EMAIL = "email";
        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        COLUMN_NAME_USERNAME + " TEXT UNIQUE NOT NULL," +
                        COLUMN_NAME_PASSWORD_HASH + " TEXT NOT NULL," +
                        COLUMN_NAME_EMAIL + " TEXT UNIQUE)";
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
                "CREATE TABLE " + TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_NAME_NOMBRE + " TEXT NOT NULL," + COLUMN_NAME_DESCRIPCION + " TEXT," + COLUMN_NAME_PRECIO + " REAL NOT NULL," + COLUMN_NAME_CATEGORIA + " TEXT," + COLUMN_NAME_IMAGE_PATH + " TEXT)";

        public static final String SQL_DELETE_TABLE =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
