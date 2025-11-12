package com.example.conexion.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.conexion.R;
import com.example.conexion.data.db.FoodDbHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * RegisterActivity es la pantalla donde los usuarios se registran.
 * Su responsabilidad es recoger los datos del nuevo usuario, validarlos,
 * crear el usuario en Firebase Authentication y luego guardarlo en la base de datos local.
 */
public class RegisterActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextEmail, editTextPassword, editTextConfirmPassword;
    private Button buttonRegister;
    private TextView textViewGoToLogin;
    private FoodDbHelper dbHelper;

    // --- Declaración de la herramienta de Autenticación de Firebase ---
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        // --- Inicialización de Herramientas ---
        // 1. Se inicializa la herramienta de base de datos local.
        dbHelper = new FoodDbHelper(this);
        // 2. Se inicializa la herramienta de autenticación de Firebase.
        mAuth = FirebaseAuth.getInstance();

        // --- Enlace de Componentes de la Interfaz ---
        editTextUsername = findViewById(R.id.editTextRegisterUsername);
        editTextEmail = findViewById(R.id.editTextRegisterEmail);
        editTextPassword = findViewById(R.id.editTextRegisterPassword);
        editTextConfirmPassword = findViewById(R.id.editTextRegisterConfirmPassword);
        buttonRegister = findViewById(R.id.buttonRegister);
        textViewGoToLogin = findViewById(R.id.textViewGoToLogin);

        // --- Configuración de Listeners ---
        buttonRegister.setOnClickListener(v -> registerUser());
        textViewGoToLogin.setOnClickListener(v -> finish());
    }

    /**
     * registerUser contiene la lógica para el registro de un nuevo usuario.
     * Se ha modificado para usar un flujo en dos pasos: Firebase primero, luego la DB local.
     */
    private void registerUser() {
        // 1. Se obtienen los datos de los campos de texto.
        String username = editTextUsername.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        // 2. Se realizan las validaciones de los campos.
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

        // 3. Se intenta crear el usuario en Firebase Authentication.
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // 4. SI FIREBASE TUVO ÉXITO, se guarda el usuario en la base de datos local.
                        Toast.makeText(RegisterActivity.this, "Usuario creado en Firebase.", Toast.LENGTH_SHORT).show();
                        
                        if (dbHelper.addUser(username, password, email)) {
                            Toast.makeText(RegisterActivity.this, "Usuario registrado exitosamente en DB local.", Toast.LENGTH_SHORT).show();
                            finish(); // Éxito total, volvemos al login.
                        } else {
                            // Este es un caso de error grave que debemos manejar.
                            Toast.makeText(RegisterActivity.this, "ERROR CRÍTICO: No se pudo guardar el usuario en la base de datos local.", Toast.LENGTH_LONG).show();
                        }

                    } else {
                        // 5. SI FIREBASE FALLÓ, se muestra el error específico.
                        Toast.makeText(RegisterActivity.this, "Registro en Firebase fallido: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
    }

    @Override
    protected void onDestroy() {
        if (dbHelper != null) {
            dbHelper.close();
        }
        super.onDestroy();
    }
}
