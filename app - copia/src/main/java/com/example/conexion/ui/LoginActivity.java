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

/**
 * `LoginActivity` es la pantalla (Activity) donde los usuarios inician sesión.
 * Es una de las primeras pantallas que ve el usuario después de la pantalla de bienvenida (MainActivity).
 * Su responsabilidad principal es recoger las credenciales del usuario, validarlas contra la base de datos
 * y redirigirlo a la pantalla correcta según su rol (administrador o usuario normal).
 */
public class LoginActivity extends AppCompatActivity {

    // --- Declaración de Componentes de la Interfaz (Vistas) ---
    private EditText editTextUsername;      // Campo de texto para que el usuario ingrese su nombre.
    private EditText editTextPassword;      // Campo de texto para que el usuario ingrese su contraseña.
    private Button buttonLogin;             // Botón que el usuario presiona para intentar iniciar sesión.
    private TextView textViewGoToRegister;  // Texto 'clicable' para navegar a la pantalla de registro.

    // --- Declaración del Ayudante de Base de Datos ---
    // dbHelper es el objeto que nos da acceso a la base de datos para crear, leer, y verificar usuarios.
    private FoodDbHelper dbHelper;

    /**
     * `onCreate` es el primer método que se llama cuando se crea esta pantalla.
     * Aquí es donde se inicializa todo: se enlaza la vista (el XML del layout),
     * se buscan los componentes de la interfaz y se configuran los listeners para los botones.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 1. Se establece el layout que esta Activity va a usar (activity_login.xml).
        setContentView(R.layout.activity_login);
        
        // 2. Se crea una instancia del FoodDbHelper, dándole el "contexto" actual (esta Activity).
        dbHelper = new FoodDbHelper(this);

        // 3. Se enlazan las variables declaradas arriba con los componentes definidos en el XML.
        editTextUsername = findViewById(R.id.editTextLoginUsername);
        editTextPassword = findViewById(R.id.editTextLoginPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewGoToRegister = findViewById(R.id.textViewGoToRegister);

        // 4. Se configuran los "oyentes" de clics (eventos).
        // Cuando el usuario presione el botón de login, se llamará al método loginUser().
        buttonLogin.setOnClickListener(v -> loginUser());
        
        // Cuando el usuario presione el texto para registrarse, se crea un Intent para abrir RegisterActivity.
        textViewGoToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    /**
     * `loginUser` contiene toda la lógica para el proceso de inicio de sesión.
     */
    private void loginUser() {
        // 1. Se obtienen los textos ingresados por el usuario y se limpian de espacios al inicio/final.
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // 2. Se realizan validaciones básicas para asegurar que los campos no estén vacíos.
        if (TextUtils.isEmpty(username)) {
            editTextUsername.setError("Nombre de usuario requerido");
            editTextUsername.requestFocus(); // Se pone el foco en el campo para que el usuario escriba.
            return; // Se detiene la ejecución del método si hay un error.
        }
        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Contraseña requerida");
            editTextPassword.requestFocus();
            return;
        }

        // 3. Se llama al método checkUser del dbHelper, que ahora usa encriptación.
        // Este método devuelve `true` si la contraseña coincide con el hash guardado.
        if (dbHelper.checkUser(username, password)) {
            Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();

            // --- LÓGICA DE REDIRECCIÓN BASADA EN EL ROL ---
            // Se pregunta a la base de datos si el usuario que acaba de iniciar sesión es un administrador.
            if (dbHelper.isAdmin(username)) {
                // Si es admin, se crea un Intent para ir a la pantalla de administración.
                Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                startActivity(intent);
            } else {
                // Si es un usuario normal, se crea un Intent para ir al menú principal.
                Intent intent = new Intent(LoginActivity.this, MainMenuActivity.class);
                startActivity(intent);
            }
            // Se llama a finish() para cerrar LoginActivity. Esto evita que el usuario pueda
            // volver a esta pantalla presionando el botón "Atrás" después de iniciar sesión.
            finish();

        } else {
            // Si checkUser devuelve `false`, se muestra un mensaje de error.
            Toast.makeText(this, "Nombre de usuario o contraseña incorrectos", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * `onDestroy` se llama cuando la pantalla está a punto de ser destruida.
     * Es una buena práctica cerrar la conexión a la base de datos aquí para liberar recursos.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
