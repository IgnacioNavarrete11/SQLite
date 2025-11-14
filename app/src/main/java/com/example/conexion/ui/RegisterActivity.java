package com.example.conexion.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.conexion.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

// =================================================================================================
// CLASE RegisterActivity: La Fábrica de Nuevos Usuarios
// =================================================================================================
/**
 * `RegisterActivity` es la pantalla donde los usuarios se registran.
 * Su responsabilidad es recoger los datos del nuevo usuario, crear la cuenta de forma segura en Firebase
 * y guardar su información básica (como el rol) en la base de datos Cloud Firestore.
 */
public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private static final String USERS_COLLECTION = "users";

    // --- 1. DECLARACIÓN DE COMPONENTES DE LA INTERFAZ ---
    private EditText editTextUsername, editTextEmail, editTextPassword, editTextConfirmPassword;
    private Button buttonRegister;
    private TextView textViewGoToLogin;

    // --- 2. DECLARACIÓN DE HERRAMIENTAS DE FIREBASE ---
    private FirebaseAuth mAuth;   // La herramienta de autenticación.
    private FirebaseFirestore db; // La herramienta para la base de datos en la nube.

    // --- 3. ON_CREATE: El Corazón de la Actividad ---
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inicializamos las herramientas de Firebase.
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Enlazamos las variables con los componentes del layout XML.
        editTextUsername = findViewById(R.id.editTextRegisterUsername);
        editTextEmail = findViewById(R.id.editTextRegisterEmail);
        editTextPassword = findViewById(R.id.editTextRegisterPassword);
        editTextConfirmPassword = findViewById(R.id.editTextRegisterConfirmPassword);
        buttonRegister = findViewById(R.id.buttonRegister);
        textViewGoToLogin = findViewById(R.id.textViewGoToLogin);

        // Configuramos los "oyentes" de clics.
        buttonRegister.setOnClickListener(v -> registerUser());
        // Cuando se toca el texto "Ir a Login", simplemente cerramos esta pantalla.
        textViewGoToLogin.setOnClickListener(v -> finish());
    }

    /**
     * registerUser: Contiene la lógica para registrar un nuevo usuario en dos pasos en la nube.
     */
    private void registerUser() {
        // --- PASO 1: RECOGER Y VALIDAR DATOS ---
        String username = editTextUsername.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        // Validaciones de los campos (si algo está mal, muestra un error y se detiene).
        if (TextUtils.isEmpty(username)) {
            editTextUsername.setError("Nombre de usuario requerido");
            editTextUsername.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Introduce un correo válido");
            editTextEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Contraseña requerida");
            editTextPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            editTextPassword.setError("La contraseña debe tener al menos 6 caracteres");
            editTextPassword.requestFocus();
            return;
        }
        if (!password.equals(confirmPassword)) {
            editTextConfirmPassword.setError("Las contraseñas no coinciden");
            editTextConfirmPassword.requestFocus();
            return;
        }

        // --- PASO 2: CREAR LA CUENTA DE AUTENTICACIÓN ---
        // COMPARACIÓN (Firebase vs. SQLite):
        // Antes: Teníamos que encriptar la contraseña nosotros mismos con jbcrypt y luego insertarla en la base de datos local.
        // Ahora: `mAuth.createUserWithEmailAndPassword(...)` hace todo el trabajo pesado. Crea el usuario,
        // encripta la contraseña de forma segura y la guarda en el sistema de autenticación de Firebase. Es mucho más seguro y simple.
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Si la cuenta se creó con éxito...
                        Toast.makeText(RegisterActivity.this, "Cuenta creada.", Toast.LENGTH_SHORT).show();
                        // ...pasamos al siguiente paso: guardar la información del usuario.
                        saveUserInformation(username, email);

                    } else {
                        // Si Firebase no pudo crear la cuenta (ej. el email ya existe), muestra un error claro.
                        Toast.makeText(RegisterActivity.this, "Registro fallido: " + Objects.requireNonNull(task.getException()).getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * saveUserInformation: Guarda los datos adicionales del usuario en Cloud Firestore.
     */
    private void saveUserInformation(String username, String email) {
        // Obtenemos el ID único (UID) que Firebase le acaba de asignar al nuevo usuario.
        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        // Creamos un "mapa" (como un diccionario) con la información que queremos guardar.
        Map<String, Object> user = new HashMap<>();
        user.put("username", username);
        user.put("email", email);
        user.put("role", "user"); // ¡IMPORTANTE! Por defecto, todos los nuevos usuarios son "user".

        // COMPARACIÓN (Firebase vs. SQLite):
        // Antes: Hacíamos `dbHelper.addUser(...)` para insertar los datos en la tabla local.
        // Ahora: Le decimos a Firestore: "En la colección `users`, crea un documento con el ID `userId` y guárdale este mapa de datos".
        // Esto es más flexible, ya que no estamos atados a una estructura de tabla fija.
        db.collection(USERS_COLLECTION).document(userId).set(user)
                .addOnSuccessListener(aVoid -> {
                    // Éxito total: La cuenta está creada y sus datos guardados.
                    Toast.makeText(RegisterActivity.this, "¡Registro completo!", Toast.LENGTH_SHORT).show();
                    finish(); // Cierra esta pantalla y vuelve al Login.
                })
                .addOnFailureListener(e -> {
                    // Este es un caso de error grave: la cuenta existe, pero no pudimos guardar sus datos. Es importante registrarlo.
                    Log.e(TAG, "Error al guardar datos en Firestore", e);
                    Toast.makeText(RegisterActivity.this, "ERROR CRÍTICO: No se pudo guardar la información del usuario.", Toast.LENGTH_LONG).show();
                });
    }

    // COMPARACIÓN (Firebase vs. SQLite):
    // Antes: Necesitábamos `onDestroy` para cerrar la conexión a la base de datos local.
    // Ahora: No es necesario, Firebase gestiona su ciclo de vida automáticamente.
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
