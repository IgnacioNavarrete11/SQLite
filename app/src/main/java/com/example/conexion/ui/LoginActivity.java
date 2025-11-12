package com.example.conexion.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

// ✅ IMPORTS AÑADIDOS PARA FIREBASE
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.conexion.R;
import com.example.conexion.data.db.FoodDbHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * `LoginActivity` es la pantalla (Activity) donde los usuarios inician sesión.
 * Es una de las primeras pantallas que ve el usuario después de la pantalla de bienvenida (MainActivity).
 * Su responsabilidad principal es recoger las credenciales del usuario, validarlas contra la base de datos
 * y redirigirlo a la pantalla correcta según su rol (administrador o usuario normal).
 */

/** ¡¡CAMBIOS IMPORTANTES!!: Ahora el codigo se a adaptado a firebase */

public class LoginActivity extends AppCompatActivity {

    // --- Declaración de Componentes de la Interfaz (Vistas) ---
    private EditText editTextUsername;      // Campo de texto para que el usuario ingrese su nombre.
    private EditText editTextPassword;      // Campo de texto para que el usuario ingrese su contraseña.
    private Button buttonLogin;             // Botón que el usuario presiona para intentar iniciar sesión.
    private TextView textViewGoToRegister;  // Texto 'clicable' para navegar a la pantalla de registro.

    // --- Declaración del Ayudante de Base de Datos ---
    // dbHelper es el objeto que nos da acceso a la base de datos para crear, leer, y verificar usuarios.
    private FoodDbHelper dbHelper;

    // Se declara la instancia de Firebase Auth
    private FirebaseAuth mAuth;

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

        // SE INICIALIZA FIREBASE AUTH
        mAuth = FirebaseAuth.getInstance();
        
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
     * SE HA MODIFICADO PARA USAR FIREBASE AUTHENTICATION.
     */
    private void loginUser() {
        // 1. Se obtienen los textos ingresados por el usuario y se limpian de espacios al inicio/final.
        // Para Firebase, el "username" lo trataremos como un email.
        String email = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // 2. Se realizan validaciones básicas para asegurar que los campos no estén vacíos.
        if (TextUtils.isEmpty(email)) {
            editTextUsername.setError("Email requerido");
            editTextUsername.requestFocus(); // Se pone el foco en el campo para que el usuario escriba.
            return; // Se detiene la ejecución del método si hay un error.
        }
        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Contraseña requerida");
            editTextPassword.requestFocus();
            return;
        }

        // 3. Se llama al método de Firebase para iniciar sesión, en lugar del dbHelper.
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Si Firebase confirma que el email y la contraseña son correctos...
                        Toast.makeText(LoginActivity.this, "Inicio de sesión con Firebase exitoso.", Toast.LENGTH_SHORT).show();

                        // --- LÓGICA DE REDIRECCIÓN BASADA EN EL ROL ---
                        // Se mantiene la consulta a la base de datos local SOLO para saber si es admin.
                        if (dbHelper.isAdmin(email)) {
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
                        // Si Firebase devuelve un error (contraseña incorrecta, usuario no existe), se muestra un mensaje.
                        Toast.makeText(LoginActivity.this, "Autenticación fallida: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
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
