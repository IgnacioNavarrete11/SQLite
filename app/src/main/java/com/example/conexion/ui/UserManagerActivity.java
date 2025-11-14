package com.example.conexion.ui;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.conexion.R;
import com.example.conexion.model.User;
import com.example.conexion.ui.adapter.UserAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// --- MODIFICADO: Ya no necesitamos los imports de la DB local ---
// import android.database.Cursor;
// import android.view.LayoutInflater;
// import android.view.View;
// import android.widget.EditText;
// import com.example.conexion.data.db.FoodDbHelper;
// import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class UserManagerActivity extends AppCompatActivity implements UserAdapter.OnItemClickListener {

    private static final String TAG = "UserManagerActivity";
    private static final String USERS_COLLECTION = "users";

    // --- MODIFICADO: Componentes de la UI ---
    private RecyclerView usersRecyclerView;
    private UserAdapter userAdapter;
    private final List<User> userList = new ArrayList<>();

    // --- MODIFICADO: Herramientas de Firebase ---
    private FirebaseFirestore db;

    // --- ELIMINADO: Ya no se necesitan las herramientas de la DB local ---
    // private FoodDbHelper dbHelper;
    // private FloatingActionButton fabAddUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_manager);

        // --- INICIALIZACIÓN ---
        db = FirebaseFirestore.getInstance();
        usersRecyclerView = findViewById(R.id.usersRecyclerView); // Nuevo ID

        // --- Configuración del RecyclerView ---
        setupRecyclerView();

        // --- Carga de datos en tiempo real ---
        loadUsersFromFirestore();

        // --- ELIMINADO: Listeners del FAB y del ItemClick de la ListView ---
    }

    private void setupRecyclerView() {
        userAdapter = new UserAdapter(userList, this);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        usersRecyclerView.setAdapter(userAdapter);
    }

    private void loadUsersFromFirestore() {
        db.collection(USERS_COLLECTION)
                .orderBy("username") // Ordenamos por nombre de usuario
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Error al escuchar cambios en usuarios.", e);
                        return;
                    }

                    userList.clear();
                    for (QueryDocumentSnapshot doc : Objects.requireNonNull(snapshots)) {
                        User user = doc.toObject(User.class);
                        user.setDocumentId(doc.getId()); // ¡Importante! Guardamos el ID del documento.
                        userList.add(user);
                    }
                    userAdapter.updateList(userList);
                });
    }

    /**
     * Se activa cuando el administrador hace clic en un usuario de la lista.
     * @param user El objeto User en el que se hizo clic.
     */
    @Override
    public void onItemClick(User user) {
        // Para evitar cambiar accidentalmente el rol del 'super admin', si existe.
        if ("admin@example.com".equals(user.getEmail())) { // O usa un ID fijo si lo tienes
            Toast.makeText(this, "No se puede modificar el rol del administrador principal.", Toast.LENGTH_SHORT).show();
            return;
        }
        
        showRoleEditorDialog(user);
    }

    private void showRoleEditorDialog(final User user) {
        // Creamos un diálogo simple para confirmar el cambio de rol.
        final String newRole = "admin".equals(user.getRole()) ? "user" : "admin";
        
        new AlertDialog.Builder(this)
                .setTitle("Cambiar Rol de Usuario")
                .setMessage("¿Deseas cambiar el rol de '" + user.getUsername() + "' a '" + newRole + "'?")
                .setPositiveButton("Sí, cambiar", (dialog, which) -> {
                    updateUserRole(user, newRole);
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void updateUserRole(User user, String newRole) {
        // Actualizamos solo el campo 'role' en el documento del usuario en Firestore.
        db.collection(USERS_COLLECTION).document(user.getDocumentId())
                .update("role", newRole)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(UserManagerActivity.this, "Rol actualizado correctamente.", Toast.LENGTH_SHORT).show();
                    // No es necesario llamar a loadUsers, el listener lo hará automáticamente.
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(UserManagerActivity.this, "Error al actualizar el rol.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error al actualizar rol", e);
                });
    }

    // --- ELIMINADOS: Todos los métodos que usaban dbHelper (loadUsers, showUserEditorDialog, saveUser, deleteUser) ---

    // El método onDestroy ahora está vacío, ya que no hay cursores ni DBs que cerrar.
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
