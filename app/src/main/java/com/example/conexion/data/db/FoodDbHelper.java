package com.example.conexion.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.conexion.data.db.TotalFoodContract.UserEntry;
import com.example.conexion.data.db.TotalFoodContract.FoodEntry;

public class FoodDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "MenuApp.db";

    private static final String TAG = "FoodDbHelper";

    public FoodDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
            db.execSQL(UserEntry.SQL_CREATE_TABLE);
            db.execSQL(FoodEntry.SQL_CREATE_TABLE);
            insertSampleFoodData(db);

        }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(UserEntry.SQL_DELETE_TABLE);
        db.execSQL(FoodEntry.SQL_DELETE_TABLE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public boolean addUser(String username, String password, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserEntry.COLUMN_NAME_USERNAME, username);

        values.put(UserEntry.COLUMN_NAME_PASSWORD_HASH, password);
        if (email != null && !email.isEmpty()) {
            values.put(UserEntry.COLUMN_NAME_EMAIL, email);
        }

        long newRowId = db.insert(UserEntry.TABLE_NAME, null, values);
        return (newRowId == -1);
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {UserEntry._ID};
        String selection = UserEntry.COLUMN_NAME_USERNAME + " = ?" + " and " + UserEntry.COLUMN_NAME_PASSWORD_HASH + " = ?";
        String[] selectionArgs = {username, password};
        Cursor cursor = null;
        try { cursor = db.query(UserEntry.TABLE_NAME, columns, selection, selectionArgs, null, null, null);
            int count = cursor.getCount();
            Log.d(TAG, "checkUser: Usuario '" + username + "' encontrado: " + (count > 0));
            return count > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error en checkUser: " + e.getMessage());
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public long addFoodItem(String nombre, String descripcion, double precio, String categoria, String imagePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FoodEntry.COLUMN_NAME_NOMBRE, nombre);
        values.put(FoodEntry.COLUMN_NAME_DESCRIPCION, descripcion);
        values.put(FoodEntry.COLUMN_NAME_PRECIO, precio);
        values.put(FoodEntry.COLUMN_NAME_CATEGORIA, categoria);
        if (imagePath != null && !imagePath.isEmpty()) {
            values.put(FoodEntry.COLUMN_NAME_IMAGE_PATH, imagePath);
        }

        return db.insert(FoodEntry.TABLE_NAME, null, values);
    }


    public Cursor getAllFoodItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {
                FoodEntry._ID,
                FoodEntry.COLUMN_NAME_NOMBRE,
                FoodEntry.COLUMN_NAME_DESCRIPCION,
                FoodEntry.COLUMN_NAME_PRECIO,
                FoodEntry.COLUMN_NAME_CATEGORIA,
                FoodEntry.COLUMN_NAME_IMAGE_PATH
        };
        String sortOrder = FoodEntry.COLUMN_NAME_CATEGORIA + " ASC, " + FoodEntry.COLUMN_NAME_NOMBRE + " ASC";
        return db.query(FoodEntry.TABLE_NAME, projection, null, null, null, null, sortOrder);

    }

    public int updateFoodItem(long id, String nombre, String descripcion, double precio, String categoria, String imagePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FoodEntry.COLUMN_NAME_NOMBRE, nombre);
        values.put(FoodEntry.COLUMN_NAME_DESCRIPCION, descripcion);
        values.put(FoodEntry.COLUMN_NAME_PRECIO, precio);
        values.put(FoodEntry.COLUMN_NAME_CATEGORIA, categoria);
        if (imagePath != null ){
            values.put(FoodEntry.COLUMN_NAME_IMAGE_PATH, imagePath);
        }
        String selection = FoodEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        return db.update(FoodEntry.TABLE_NAME, values, selection, selectionArgs);
    }
    public int deleteFoodItem(long id) {
SQLiteDatabase db = this.getWritableDatabase();
String selection = FoodEntry._ID + "= ?";
String [] selectionArgs = {String.valueOf(id)};
return db.delete(FoodEntry.SQL_CREATE_TABLE, selection, selectionArgs);
    }

    private void insertSampleFoodData(SQLiteDatabase db) {
        addFoodItemToDb(db, "Pizza Margarita", "Clásica pizza italiana con tomate fresco, mozzarella y albahaca.", 9.99, "Platos Fuertes", null);
        addFoodItemToDb(db, "Hamburguesa Clásica", "Carne de res a la parrilla, lechuga, tomate, cebolla y pepinillos en pan artesanal.", 8.50, "Platos Fuertes", null);
        addFoodItemToDb(db, "Ensalada César", "Lechuga romana fresca, crutones, queso parmesano y aderezo César casero.", 7.25, "Entradas", null);
        addFoodItemToDb(db, "Sopa de Tomate", "Sopa cremosa de tomates maduros, servida con un toque de albahaca.", 5.50, "Entradas", null);
        addFoodItemToDb(db, "Tiramisú", "Postre italiano tradicional con bizcochos de soletilla, café, mascarpone y cacao.", 6.00, "Postres", null);
        addFoodItemToDb(db, "Coca Cola", "Bebida carbonatada refrescante.", 2.00, "Bebidas", null);
    }

    private void addFoodItemToDb(SQLiteDatabase db, String nombre, String descripcion, double precio, String categoria, String imagePath) {
        ContentValues values = new ContentValues();
        values.put(FoodEntry.COLUMN_NAME_NOMBRE, nombre);
        values.put(FoodEntry.COLUMN_NAME_DESCRIPCION, descripcion);
        values.put(FoodEntry.COLUMN_NAME_PRECIO, precio);
        values.put(FoodEntry.COLUMN_NAME_CATEGORIA, categoria);
        if (imagePath != null && !imagePath.isEmpty()) {
            values.put(FoodEntry.COLUMN_NAME_IMAGE_PATH, imagePath);
        }
        db.insert(FoodEntry.TABLE_NAME, null, values);
    }


}
