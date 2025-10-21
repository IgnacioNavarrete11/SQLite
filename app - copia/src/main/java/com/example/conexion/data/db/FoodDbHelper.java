package com.example.conexion.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

// --- PASO 1: IMPORTAR LA LIBRERÍA DE ENCRIPTACIÓN ---
// Se importa la clase BCrypt para poder hashear y verificar contraseñas.
import org.mindrot.jbcrypt.BCrypt;

import com.example.conexion.data.db.TotalFoodContract.UserEntry;
import com.example.conexion.data.db.TotalFoodContract.FoodEntry;

public class FoodDbHelper extends SQLiteOpenHelper {

    // --- SE SUBE LA VERSIÓN DE LA BASE DE DATOS ---
    // Cada vez que cambiamos la estructura de la BD (como ahora, que cambiaremos la lógica de contraseñas),
    // es una buena práctica incrementar la versión. Esto fuerza la llamada a onUpgrade()
    // y asegura que la base de datos se recree con la nueva estructura y datos seguros.
    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "MenuApp.db";
    private static final String TAG = "FoodDbHelper";

    public FoodDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            Log.d(TAG, "Creando tablas...");
            db.execSQL(UserEntry.SQL_CREATE_TABLE);
            db.execSQL(FoodEntry.SQL_CREATE_TABLE);
            Log.d(TAG, "Tablas creadas.");

            insertSampleFoodData(db);
            Log.d(TAG, "Datos de ejemplo para alimentos insertados.");

            // Se crea el usuario administrador con contraseña segura.
            createDefaultAdmin(db);

        } catch (SQLException e) {
            Log.e(TAG, "Error al crear la base de datos: " + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Actualizando base de datos de la versión " + oldVersion + " a " + newVersion + ". Se borrarán los datos antiguos.");
        db.execSQL(UserEntry.SQL_DELETE_TABLE);
        db.execSQL(FoodEntry.SQL_DELETE_TABLE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    // --- PASO 2: HASHEAR LA CONTRASEÑA DEL ADMINISTRADOR ---
    private void createDefaultAdmin(SQLiteDatabase db) {
        ContentValues adminValues = new ContentValues();
        adminValues.put(UserEntry.COLUMN_NAME_USERNAME, "admin");

        // Se genera un "hash" de la contraseña. BCrypt.gensalt() añade una "sal" aleatoria
        // para que dos contraseñas iguales no generen el mismo hash, aumentando la seguridad.
        String hashedPassword = BCrypt.hashpw("admin123", BCrypt.gensalt());
        adminValues.put(UserEntry.COLUMN_NAME_PASSWORD_HASH, hashedPassword); // Se guarda el hash, no el texto plano.

        adminValues.put(UserEntry.COLUMN_NAME_IS_ADMIN, 1);
        db.insert(UserEntry.TABLE_NAME, null, adminValues);
        Log.d(TAG, "Usuario administrador por defecto creado con contraseña segura.");
    }

    // --- PASO 3: HASHEAR LA CONTRASEÑA AL AÑADIR UN USUARIO ---
    public boolean addUser(String username, String password, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserEntry.COLUMN_NAME_USERNAME, username);

        // Se hashea la contraseña del nuevo usuario antes de guardarla.
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        values.put(UserEntry.COLUMN_NAME_PASSWORD_HASH, hashedPassword);

        if (email != null && !email.isEmpty()) {
            values.put(UserEntry.COLUMN_NAME_EMAIL, email);
        }

        long newRowId = db.insert(UserEntry.TABLE_NAME, null, values);
        return newRowId != -1;
    }

    // --- PASO 4: REHACER LA LÓGICA DE COMPROBACIÓN DEL USUARIO ---
    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            // 1. Buscamos al usuario solo por su nombre de usuario.
            String[] columns = {UserEntry.COLUMN_NAME_PASSWORD_HASH};
            String selection = UserEntry.COLUMN_NAME_USERNAME + " = ?";
            String[] selectionArgs = {username};

            cursor = db.query(UserEntry.TABLE_NAME, columns, selection, selectionArgs, null, null, null);

            // 2. Si el usuario existe, obtenemos su hash guardado.
            if (cursor != null && cursor.moveToFirst()) {
                String storedHash = cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_NAME_PASSWORD_HASH));

                // 3. Usamos BCrypt.checkpw para comparar la contraseña introducida (sin hashear)
                // con el hash que estaba guardado en la base de datos.
                if (BCrypt.checkpw(password, storedHash)) {
                    Log.d(TAG, "checkUser: Contraseña correcta para el usuario '" + username + "'.");
                    return true; // La contraseña es correcta.
                }
            }
            Log.d(TAG, "checkUser: Usuario o contraseña incorrecta para '" + username + "'.");
            return false; // El usuario no existe o la contraseña no coincide.
        } catch (Exception e) {
            Log.e(TAG, "Error en checkUser: " + e.getMessage());
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
    
    // --- PASO 5: HASHEAR LA CONTRASEÑA AL ACTUALIZAR UN USUARIO ---
    public int updateUser(long id, String username, String email, String password, boolean isAdmin) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserEntry.COLUMN_NAME_USERNAME, username);
        values.put(UserEntry.COLUMN_NAME_EMAIL, email);
        values.put(UserEntry.COLUMN_NAME_IS_ADMIN, isAdmin ? 1 : 0);

        // Solo se actualiza y hashea la contraseña si se proporciona una nueva.
        if (password != null && !password.isEmpty()) {
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            values.put(UserEntry.COLUMN_NAME_PASSWORD_HASH, hashedPassword);
        }

        String selection = UserEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };

        return db.update(UserEntry.TABLE_NAME, values, selection, selectionArgs);
    }
    
    // --- MÉTODOS QUE NO REQUIEREN CAMBIOS ---
    
    public boolean isAdmin(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            String query = "SELECT " + UserEntry.COLUMN_NAME_IS_ADMIN + " FROM " +
                    UserEntry.TABLE_NAME + " WHERE " + UserEntry.COLUMN_NAME_USERNAME + " = ?";
            cursor = db.rawQuery(query, new String[]{username});
            if (cursor != null && cursor.moveToFirst()) {
                int isAdmin = cursor.getInt(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_NAME_IS_ADMIN));
                return isAdmin == 1;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error en isAdmin: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return false;
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
        return db.delete(FoodEntry.TABLE_NAME, selection, selectionArgs);
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
    
    public Cursor getAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {
                UserEntry._ID,
                UserEntry.COLUMN_NAME_USERNAME,
                UserEntry.COLUMN_NAME_EMAIL,
                UserEntry.COLUMN_NAME_IS_ADMIN
        };
        return db.query(UserEntry.TABLE_NAME, projection, null, null, null, null, UserEntry.COLUMN_NAME_USERNAME + " ASC");
    }

    public int deleteUser(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = UserEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };
        return db.delete(UserEntry.TABLE_NAME, selection, selectionArgs);
    }
}
