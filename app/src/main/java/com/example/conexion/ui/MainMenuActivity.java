package com.example.conexion.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.conexion.R;
import com.example.conexion.data.db.FoodDbHelper;

public class MainMenuActivity extends AppCompatActivity {

    private ListView listViewFoodMenu;
    private Button buttonLogout;
    private FoodDbHelper dbHelper;
    private FoodCursorAdapter foodAdapter;
    private Cursor currentFoodCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        dbHelper = new FoodDbHelper(this);
        listViewFoodMenu = findViewById(R.id.listViewFoodMenu);
        buttonLogout = findViewById(R.id.buttonLogout);

        foodAdapter = new FoodCursorAdapter(this, null);
        listViewFoodMenu.setAdapter(foodAdapter);

        loadFoodMenu();

        buttonLogout.setOnClickListener(v -> logoutUser());
    }

    private void loadFoodMenu() {
        if (currentFoodCursor != null && !currentFoodCursor.isClosed()) {
            currentFoodCursor.close();
        }
        currentFoodCursor = dbHelper.getAllFoodItems();
        if (currentFoodCursor != null && currentFoodCursor.getCount() > 0) {
            foodAdapter.changeCursor(currentFoodCursor);
        } else {
            foodAdapter.changeCursor(null);
            Toast.makeText(this, "No hay platos en el menú.", Toast.LENGTH_LONG).show();
        }
    }

    private void logoutUser() {
        Toast.makeText(this, "Cerrando sesión...", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainMenuActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (foodAdapter != null && foodAdapter.getCursor() != null && !foodAdapter.getCursor().isClosed()) {
            foodAdapter.getCursor().close();
        }
        if (currentFoodCursor != null && !currentFoodCursor.isClosed()) { // esto asegura que el cursor directo también se cierre
            currentFoodCursor.close();
        }
        if (dbHelper != null) {
            dbHelper.close();
        }
        super.onDestroy();
    }
}
