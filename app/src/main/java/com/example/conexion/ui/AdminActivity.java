package com.example.conexion.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.conexion.R;
import com.example.conexion.data.db.FoodDbHelper;
import com.example.conexion.data.db.TotalFoodContract.FoodEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

//Lógica de Categorías
public class AdminActivity extends AppCompatActivity {

    private FoodDbHelper dbHelper;
    private FoodCursorAdapter foodAdapter;
    private ListView listViewAdminFood;
    private FloatingActionButton fabAddFood;
    private Button buttonManageUsers;
    private Button buttonLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        dbHelper = new FoodDbHelper(this);
        listViewAdminFood = findViewById(R.id.listViewAdminFood);
        fabAddFood = findViewById(R.id.fabAddFood);
        buttonManageUsers = findViewById(R.id.buttonManageUsers);
        buttonLogout = findViewById(R.id.button_logout); //SE ENLAZA EL BOTÓN DE LOGOUT

        foodAdapter = new FoodCursorAdapter(this, null);
        listViewAdminFood.setAdapter(foodAdapter);

        fabAddFood.setOnClickListener(v -> showFoodEditorDialog(null));

        listViewAdminFood.setOnItemClickListener((parent, view, position, id) -> {
            showFoodEditorDialog(id);
        });

        buttonManageUsers.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, UserManagerActivity.class);
            startActivity(intent);
        });
        
        // LÓGICA DEL BOTÓN DE LOGOUT
        buttonLogout.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        loadFoodItems();
    }

    private void loadFoodItems() {
        Cursor cursor = dbHelper.getAllFoodItems();
        foodAdapter.changeCursor(cursor);
    }

    private void showFoodEditorDialog(final Long foodId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_food_editor, null);
        builder.setView(dialogView);

        final EditText editTextName = dialogView.findViewById(R.id.editTextFoodName);
        final EditText editTextDescription = dialogView.findViewById(R.id.editTextFoodDescription);
        final EditText editTextPrice = dialogView.findViewById(R.id.editTextFoodPrice);
        final EditText editTextCategory = dialogView.findViewById(R.id.editTextFoodCategory);

        if (foodId == null) {
            builder.setTitle("Añadir Plato Nuevo");
        } else {
            builder.setTitle("Editar Plato");
            loadFoodDataForEdit(foodId, editTextName, editTextDescription, editTextPrice, editTextCategory);
        }

        builder.setPositiveButton(foodId == null ? "Añadir" : "Actualizar", (dialog, which) -> {
            saveFoodItem(foodId, editTextName, editTextDescription, editTextPrice, editTextCategory);
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        if (foodId != null) {
            builder.setNeutralButton("Eliminar", (dialog, which) -> {
                new AlertDialog.Builder(AdminActivity.this)
                        .setTitle("Confirmar Eliminación")
                        .setMessage("¿Estás seguro de que quieres eliminar este plato?")
                        .setPositiveButton("Sí, Eliminar", (confirmDialog, confirmWhich) -> deleteFoodItem(foodId))
                        .setNegativeButton("No", null)
                        .show();
            });
        }

        builder.create().show();
    }

    private void loadFoodDataForEdit(long foodId, EditText name, EditText desc, EditText price, EditText cat) {
        Cursor cursor = dbHelper.getReadableDatabase().query(FoodEntry.TABLE_NAME,
                null, FoodEntry._ID + " = ?", new String[]{String.valueOf(foodId)}, null, null, null);

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    name.setText(cursor.getString(cursor.getColumnIndexOrThrow(FoodEntry.COLUMN_NAME_NOMBRE)));
                    desc.setText(cursor.getString(cursor.getColumnIndexOrThrow(FoodEntry.COLUMN_NAME_DESCRIPCION)));
                    price.setText(String.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow(FoodEntry.COLUMN_NAME_PRECIO))));
                    cat.setText(cursor.getString(cursor.getColumnIndexOrThrow(FoodEntry.COLUMN_NAME_CATEGORIA)));
                }
            } finally {
                cursor.close();
            }
        }
    }

    private void saveFoodItem(Long foodId, EditText name, EditText desc, EditText price, EditText cat) {
        String foodName = name.getText().toString().trim();
        String foodDesc = desc.getText().toString().trim();
        String priceStr = price.getText().toString().trim();
        String foodCat = cat.getText().toString().trim();

        if (TextUtils.isEmpty(foodName) || TextUtils.isEmpty(priceStr)) {
            Toast.makeText(this, "El nombre y el precio son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        double foodPrice;
        try {
            foodPrice = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "El precio no es un número válido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (foodId == null) { 
            long newId = dbHelper.addFoodItem(foodName, foodDesc, foodPrice, foodCat, null); // Se pasa null para la imagen
            if (newId != -1) {
                Toast.makeText(this, "Plato añadido", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error al añadir el plato", Toast.LENGTH_SHORT).show();
            }
        } else { 
            int rowsAffected = dbHelper.updateFoodItem(foodId, foodName, foodDesc, foodPrice, foodCat, null); // Se pasa null para la imagen
            if (rowsAffected > 0) {
                Toast.makeText(this, "Plato actualizado", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
            }
        }
        loadFoodItems();
    }

    private void deleteFoodItem(long foodId) {
        int rowsDeleted = dbHelper.deleteFoodItem(foodId);
        if (rowsDeleted > 0) {
            Toast.makeText(this, "Plato eliminado", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show();
        }
        loadFoodItems();
    }

    @Override
    protected void onDestroy() {
        if (foodAdapter != null && foodAdapter.getCursor() != null && !foodAdapter.getCursor().isClosed()) {
            foodAdapter.getCursor().close();
        }
        if (dbHelper != null) {
            dbHelper.close();
        }
        super.onDestroy();
    }
}