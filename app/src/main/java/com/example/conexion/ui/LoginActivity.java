package com.example.conexion.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.conexion.R;
import com.example.conexion.data.db.FoodDbHelper;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonLogin;
    private TextView textViewGoToRegister;
    private FoodDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        dbHelper = new FoodDbHelper(this);

        editTextUsername = findViewById(R.id.editTextLoginUsername);
        editTextPassword = findViewById(R.id.editTextLoginPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewGoToRegister = findViewById(R.id.textViewGoToRegister);

        buttonLogin.setOnClickListener(v -> loginUser());
        textViewGoToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser() {
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            editTextUsername.setError("Nombre de usuario requerido");
            editTextUsername.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Contraseña requerida");
            editTextPassword.requestFocus();
            return;
        }

        if (dbHelper.checkUser(username, password)) {
            Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();

            // --- ¡NUEVA LÓGICA DE REDIRECCIÓN! ---
            if (dbHelper.isAdmin(username)) {
                // Es un administrador, ir al panel de admin
                Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                startActivity(intent);
            } else {
                // Es un usuario normal, ir al menú normal
                Intent intent = new Intent(LoginActivity.this, MainMenuActivity.class);
                startActivity(intent);
            }
            finish(); // Finaliza LoginActivity en ambos casos

        } else {
            Toast.makeText(this, "Nombre de usuario o contraseña incorrectos", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        if (dbHelper != null) {
            dbHelper.close();
        }
        super.onDestroy();
    }
}
