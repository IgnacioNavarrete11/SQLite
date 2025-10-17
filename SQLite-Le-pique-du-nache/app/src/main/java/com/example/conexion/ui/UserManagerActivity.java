package com.example.conexion.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.conexion.R;
import com.example.conexion.data.db.FoodDbHelper;
import com.example.conexion.data.db.TotalFoodContract.UserEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class UserManagerActivity extends AppCompatActivity {

    private FoodDbHelper dbHelper;
    private UserCursorAdapter userAdapter;
    private ListView listViewUsers;
    private FloatingActionButton fabAddUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_manager);

        dbHelper = new FoodDbHelper(this);
        listViewUsers = findViewById(R.id.listViewUsers);
        fabAddUser = findViewById(R.id.fabAddUser);

        userAdapter = new UserCursorAdapter(this, null);
        listViewUsers.setAdapter(userAdapter);

        fabAddUser.setOnClickListener(v -> showUserEditorDialog(null));

        listViewUsers.setOnItemClickListener((parent, view, position, id) -> {
            showUserEditorDialog(id);
        });

        loadUsers();
    }

    private void loadUsers() {
        Cursor cursor = dbHelper.getAllUsers();
        userAdapter.changeCursor(cursor);
    }

    private void showUserEditorDialog(final Long userId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_user_editor, null);
        builder.setView(dialogView);

        final EditText editTextName = dialogView.findViewById(R.id.editTextUserName);
        final EditText editTextEmail = dialogView.findViewById(R.id.editTextUserEmail);
        final EditText editTextPassword = dialogView.findViewById(R.id.editTextUserPassword);
        final SwitchMaterial switchIsAdmin = dialogView.findViewById(R.id.switchIsAdmin);

        if (userId == null) {
            builder.setTitle("Añadir Nuevo Usuario");
            editTextPassword.setHint("Contraseña (obligatoria)");
        } else {
            builder.setTitle("Editar Usuario");
            loadUserDataForEdit(userId, editTextName, editTextEmail, switchIsAdmin);
        }

        builder.setPositiveButton(userId == null ? "Añadir" : "Actualizar", (dialog, which) -> {
            saveUser(userId, editTextName, editTextEmail, editTextPassword, switchIsAdmin);
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        if (userId != null) {
            builder.setNeutralButton("Eliminar", (dialog, which) -> {
                // Prevenir eliminar al usuario 'admin'
                String currentUsername = editTextName.getText().toString();
                if ("admin".equalsIgnoreCase(currentUsername)) {
                    Toast.makeText(UserManagerActivity.this, "No se puede eliminar al administrador principal", Toast.LENGTH_SHORT).show();
                    return;
                }

                new AlertDialog.Builder(UserManagerActivity.this)
                        .setTitle("Confirmar Eliminación")
                        .setMessage("¿Estás seguro de que quieres eliminar a este usuario?")
                        .setPositiveButton("Sí, Eliminar", (confirmDialog, confirmWhich) -> deleteUser(userId))
                        .setNegativeButton("No", null)
                        .show();
            });
        }

        builder.create().show();
    }

    private void loadUserDataForEdit(long userId, EditText name, EditText email, SwitchMaterial isAdminSwitch) {
        Cursor cursor = dbHelper.getReadableDatabase().query(UserEntry.TABLE_NAME,
                null, UserEntry._ID + " = ?", new String[]{String.valueOf(userId)}, null, null, null);

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    name.setText(cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_NAME_USERNAME)));
                    email.setText(cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_NAME_EMAIL)));
                    int isAdmin = cursor.getInt(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_NAME_IS_ADMIN));
                    isAdminSwitch.setChecked(isAdmin == 1);
                }
            } finally {
                cursor.close();
            }
        }
    }

    private void saveUser(Long userId, EditText name, EditText email, EditText password, SwitchMaterial isAdminSwitch) {
        String username = name.getText().toString().trim();
        String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();
        boolean isAdmin = isAdminSwitch.isChecked();

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "El nombre de usuario es obligatorio", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userId == null) { // Crear nuevo
            if (TextUtils.isEmpty(userPassword)) {
                Toast.makeText(this, "La contraseña es obligatoria para nuevos usuarios", Toast.LENGTH_SHORT).show();
                return;
            }
            if (dbHelper.addUser(username, userPassword, userEmail)) { // Reutilizamos tu método addUser
                // Si queremos que sea admin, lo actualizamos inmediatamente porque addUser no lo permite
                if (isAdmin) {
                    Cursor c = dbHelper.getReadableDatabase().query(UserEntry.TABLE_NAME, new String[]{UserEntry._ID}, UserEntry.COLUMN_NAME_USERNAME + "=?", new String[]{username}, null, null, null);
                    if (c != null && c.moveToFirst()) {
                        long newId = c.getLong(0);
                        dbHelper.updateUser(newId, username, userEmail, null, true); // Se actualiza sin cambiar la pass
                        c.close();
                    }
                }
                Toast.makeText(this, "Usuario añadido", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error al añadir. El usuario podría ya existir.", Toast.LENGTH_SHORT).show();
            }
        } else { // Actualizar existente
            int rowsAffected = dbHelper.updateUser(userId, username, userEmail, userPassword, isAdmin);
            if (rowsAffected > 0) {
                Toast.makeText(this, "Usuario actualizado", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
            }
        }
        loadUsers(); // Recargar la lista
    }

    private void deleteUser(long userId) {
        int rowsDeleted = dbHelper.deleteUser(userId);
        if (rowsDeleted > 0) {
            Toast.makeText(this, "Usuario eliminado", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show();
        }
        loadUsers(); // Recargar la lista
    }

    @Override
    protected void onDestroy() {
        if (userAdapter != null && userAdapter.getCursor() != null && !userAdapter.getCursor().isClosed()) {
            userAdapter.getCursor().close();
        }
        if (dbHelper != null) {
            dbHelper.close();
        }
        super.onDestroy();
    }
}
