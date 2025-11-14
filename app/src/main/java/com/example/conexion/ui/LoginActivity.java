package com.example.conexion.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.conexion.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

// =================================================================================================
// CLASE LoginActivity: La Puerta de Entrada a la App
// =================================================================================================
/**
 * `LoginActivity` es la pantalla donde los usuarios inician sesión.
 * Su responsabilidad es recoger las credenciales del usuario, validarlas contra los servicios de Firebase
 * y redirigirlo a la pantalla correcta según su rol (administrador o usuario normal).
 */

/** ¡¡CAMBIOS IMPORTANTES!!: Ahora el codigo se a adaptado a firebase */

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    // --- Declaración de Componentes de la Interfaz (Vistas) ---
    private EditText editTextUsername;      // Campo de texto para que el usuario ingrese su email.
    private EditText editTextPassword;      // Campo de texto para que el usuario ingrese su contraseña.
    private Button buttonLogin;             // Botón que el usuario presiona para intentar iniciar sesión.
    private TextView textViewGoToRegister;  // Texto 'clicable' para navegar a la pantalla de registro.

    // --- Declaración de las Herramientas de Firebase ---
    // La herramienta de Firebase que gestiona la autenticación (login, registro, etc.).
    private FirebaseAuth mAuth;
    // La herramienta que nos da acceso a la base de datos en la nube (para leer los roles).
    private FirebaseFirestore db;

    /**
     * `onCreate` es el primer método que se llama cuando se crea esta pantalla.
     * Aquí es donde se inicializa todo.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // SE INICIALIZA FIREBASE AUTH Y FIRESTORE
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Se enlazan las variables de Java con los componentes del XML.
        editTextUsername = findViewById(R.id.editTextLoginUsername);
        editTextPassword = findViewById(R.id.editTextLoginPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewGoToRegister = findViewById(R.id.textViewGoToRegister);

        // Se configuran los "oyentes" de clics.
        buttonLogin.setOnClickListener(v -> loginUser());
        textViewGoToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    /**
     * `loginUser` contiene la lógica para el inicio de sesión, ahora 100% en la nube.
     */
    private void loginUser() {
        String email = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Validaciones básicas de los campos.
        if (TextUtils.isEmpty(email)) {
            editTextUsername.setError("Email requerido");
            editTextUsername.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Contraseña requerida");
            editTextPassword.requestFocus();
            return;
        }

        // COMPARACIÓN (Firebase vs. SQLite):
        // Antes: `dbHelper.checkUser(email, password)`. Teníamos que manejar la encriptación y la consulta SQL nosotros mismos.
        // Ahora: `mAuth.signInWithEmailAndPassword(...)`. Le pasamos el email y la contraseña a Firebase, y él se encarga de todo
        // el proceso seguro de verificación. Es más fácil, rápido y muchísimo más seguro.
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Si Firebase dice "OK, las credenciales son correctas"...
                        Toast.makeText(LoginActivity.this, "Inicio de sesión exitoso.", Toast.LENGTH_SHORT).show();
                        // ...ahora necesitamos saber si es un admin o un usuario normal.
                        checkUserRole(Objects.requireNonNull(task.getResult().getUser()));
                    } else {
                        // Si Firebase dice "Error", le mostramos el mensaje al usuario.
                        Toast.makeText(LoginActivity.this, "Autenticación fallida: " + Objects.requireNonNull(task.getException()).getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
    
    /**
     * checkUserRole: Consulta Cloud Firestore para obtener el rol del usuario.
     */
    private void checkUserRole(FirebaseUser user) {
        // COMPARACIÓN (Firebase vs. SQLite):
        // Antes: `dbHelper.isAdmin(email)`. Hacíamos una consulta a la base de datos local.
        // Ahora: Usamos el ID único (UID) del usuario para pedir su documento específico en la colección "users" de Firestore.
        db.collection("users").document(user.getUid()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Si encontramos el documento, leemos el campo "role".
                            String role = document.getString("role");
                            // Comparamos si el rol es "admin".
                            if ("admin".equals(role)) {
                                goToActivity(AdminActivity.class); // Si es admin, a la pantalla de admin.
                            } else {
                                goToActivity(MainMenuActivity.class); // Si no, a la pantalla de menú normal.
                            }
                        } else {
                            // Esto es un caso raro: el usuario existe en el sistema de autenticación, pero no tiene datos en la base de datos.
                            // Por seguridad, lo tratamos como un usuario normal.
                            Log.d(TAG, "El usuario no tiene un documento de rol en Firestore.");
                            goToActivity(MainMenuActivity.class);
                        }
                    } else {
                        // Si hay un error al leer de Firestore, también lo tratamos como un usuario normal por seguridad.
                        Toast.makeText(LoginActivity.this, "Error al verificar el rol del usuario.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error al obtener documento de rol: ", task.getException());
                        goToActivity(MainMenuActivity.class);
                    }
                });
    }

    /**
     * goToActivity: Método de ayuda para navegar a otra pantalla y limpiar el historial.
     */
    private void goToActivity(Class<?> activityClass) {
        Intent intent = new Intent(LoginActivity.this, activityClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // Cierra LoginActivity para que el usuario no pueda volver atrás.
    }

    // COMPARACIÓN (Firebase vs. SQLite):
    // Antes: Necesitábamos `onDestroy` para cerrar la conexión a la base de datos local con `dbHelper.close()`.
    // Ahora: Firebase gestiona su propio ciclo de vida, por lo que este método puede quedar vacío.
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
