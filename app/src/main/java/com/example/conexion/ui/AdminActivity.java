package com.example.conexion.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.conexion.R;
import com.example.conexion.model.FoodItem;
import com.example.conexion.ui.adapter.FoodAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

//Lógica de Categorías
public class AdminActivity extends AppCompatActivity implements FoodAdapter.OnItemClickListener {

    private static final String TAG = "AdminActivity";
    private static final String DISHES_COLLECTION = "dishes";

    // --- 1. DECLARACIÓN DE COMPONENTES DE LA INTERFAZ ---
    private SwipeRefreshLayout swipeRefreshLayoutAdmin;
    private RecyclerView adminRecyclerView;
    private ProgressBar progressBarAdmin;
    private TextView textViewEmptyAdmin;
    private FloatingActionButton fabAddFood;
    private Button buttonManageUsers;
    private Button buttonLogout;

    // --- 2. DECLARACIÓN DE HERRAMIENTAS DE DATOS ---

    // COMPARACIÓN (Firebase vs. SQLite):
    // Antes: private FoodDbHelper dbHelper;
    // Ahora: Tenemos una conexión directa a la base de datos en la nube.
    private FirebaseFirestore db;

    // COMPARACIÓN (Firebase vs. SQLite):
    // Antes: Usábamos un CursorAdapter que era más rígido.
    // Ahora: Usamos nuestro propio FoodAdapter, que es más flexible y potente.
    private FoodAdapter foodAdapter;
    // Esta lista contendrá los títulos de las categorías (String) y los platos (FoodItem).
    private final List<Object> displayList = new ArrayList<>();

    // --- 3. ON_CREATE: El Corazón de la Actividad ---
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Inicializamos la conexión a Firestore.
        db = FirebaseFirestore.getInstance();

        // --- ENLACE DE VISTAS ---
        swipeRefreshLayoutAdmin = findViewById(R.id.swipeRefreshLayoutAdmin);
        adminRecyclerView = findViewById(R.id.listViewAdminFood);
        progressBarAdmin = findViewById(R.id.progressBarAdmin);
        textViewEmptyAdmin = findViewById(R.id.textViewEmptyAdmin);
        fabAddFood = findViewById(R.id.fabAddFood);
        buttonManageUsers = findViewById(R.id.buttonManageUsers);
        buttonLogout = findViewById(R.id.button_logout); //SE ENLAZA EL BOTÓN DE LOGOUT

        setupRecyclerView();

        // --- CONFIGURACIÓN DE LISTENERS ---
        fabAddFood.setOnClickListener(v -> showFoodEditorDialog(null));
        buttonManageUsers.setOnClickListener(v -> startActivity(new Intent(AdminActivity.this, UserManagerActivity.class)));
        
        // LÓGICA DEL BOTÓN DE LOGOUT
        buttonLogout.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // LÓGICA DEL SWIPE TO REFRESH
        swipeRefreshLayoutAdmin.setOnRefreshListener(() -> {
            // La lógica es simple: si el usuario desliza, volvemos a cargar los datos.
            // No necesitamos hacer nada más, el listener de Firestore hará el resto.
            loadFoodItemsFromFirestore();
        });

        // Carga inicial de datos al abrir la pantalla.
        loadFoodItemsFromFirestore();
    }

    /**
     * setupRecyclerView: Prepara nuestra lista visual.
     */
    private void setupRecyclerView() {
        // COMPARACIÓN (Firebase vs. SQLite):
        // Antes: listView.setAdapter(unCursorAdapter);
        // Ahora: La RecyclerView necesita un "LayoutManager" que le diga CÓMO mostrar los elementos (en este caso, en una lista vertical).
        foodAdapter = new FoodAdapter(displayList, this);
        adminRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adminRecyclerView.setAdapter(foodAdapter);
    }

    /**
     * onItemClick: El FoodAdapter lo llama cuando el usuario hace clic en un plato.
     */
    @Override
    public void onItemClick(FoodItem foodItem) {
        // COMPARACIÓN (Firebase vs. SQLite):
        // Antes: El click nos daba un `id` numérico de la base de datos local.
        // Ahora: Recibimos el objeto `FoodItem` completo, que ya conoce su propio ID de documento de Firestore. Es más directo.
        if (foodItem != null) {
            showFoodEditorDialog(foodItem);
        }
    }

    /**
     * loadFoodItemsFromFirestore: El motor de la pantalla. Pide los datos a la nube.
     */
    private void loadFoodItemsFromFirestore() {
        if (!swipeRefreshLayoutAdmin.isRefreshing()) {
            progressBarAdmin.setVisibility(View.VISIBLE);
        }
        adminRecyclerView.setVisibility(View.GONE);
        textViewEmptyAdmin.setVisibility(View.GONE);

        // COMPARACIÓN (Firebase vs. SQLite):
        // Antes: Hacíamos `Cursor cursor = dbHelper.getAllFoodItems();` y luego `adapter.changeCursor(cursor);`
        // Ahora: Nos suscribimos a los cambios en tiempo real con `addSnapshotListener`.
        db.collection(DISHES_COLLECTION)
                .orderBy("category", Query.Direction.ASCENDING)
                .orderBy("name", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    progressBarAdmin.setVisibility(View.GONE);
                    swipeRefreshLayoutAdmin.setRefreshing(false);

                    if (e != null) {
                        Log.w(TAG, "Error al escuchar cambios.", e);
                        textViewEmptyAdmin.setText("Error al cargar el menú.\nDesliza para reintentar.");
                        textViewEmptyAdmin.setVisibility(View.VISIBLE);
                        return;
                    }

                    // COMPARACIÓN (Firebase vs. SQLite):
                    // Antes: Recorríamos un `Cursor` con `cursor.moveToNext()` y leíamos cada columna con `cursor.getString(...)`.
                    // Ahora: Firestore nos da objetos `FoodItem` directamente. Es más limpio, seguro y menos propenso a errores.
                    Map<String, List<FoodItem>> itemsByCategory = new LinkedHashMap<>();
                    for (QueryDocumentSnapshot doc : Objects.requireNonNull(snapshots)) {
                        FoodItem item = doc.toObject(FoodItem.class);
                        item.setDocumentId(doc.getId());
                        String category = item.getCategory();
                        if (category == null || category.isEmpty()) category = "Otros";
                        if (!itemsByCategory.containsKey(category)) {
                            itemsByCategory.put(category, new ArrayList<>());
                        }
                        Objects.requireNonNull(itemsByCategory.get(category)).add(item);
                    }

                    displayList.clear();
                    if (itemsByCategory.isEmpty()) {
                        textViewEmptyAdmin.setText("No hay platos en el menú.\nToca el botón '+' para añadir el primero.");
                        textViewEmptyAdmin.setVisibility(View.VISIBLE);
                        adminRecyclerView.setVisibility(View.GONE);
                    } else {
                        textViewEmptyAdmin.setVisibility(View.GONE);
                        adminRecyclerView.setVisibility(View.VISIBLE);
                        for (String category : itemsByCategory.keySet()) {
                            displayList.add(category.toUpperCase());
                            displayList.addAll(Objects.requireNonNull(itemsByCategory.get(category)));
                        }
                    }
                    foodAdapter.updateList(displayList);
                });
    }

    /**
     * showFoodEditorDialog: Muestra la ventana emergente para añadir o editar un plato.
     */
    private void showFoodEditorDialog(final FoodItem foodItem) {
        // ... (La lógica interna de este diálogo es muy similar, pero ahora recibe un objeto FoodItem) ...
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_food_editor, null);
        builder.setView(dialogView);

        final EditText editTextName = dialogView.findViewById(R.id.editTextFoodName);
        final EditText editTextDescription = dialogView.findViewById(R.id.editTextFoodDescription);
        final EditText editTextPrice = dialogView.findViewById(R.id.editTextFoodPrice);
        final EditText editTextCategory = dialogView.findViewById(R.id.editTextFoodCategory);
        
        editTextCategory.setVisibility(View.VISIBLE);

        if (foodItem != null) {
            builder.setTitle("Editar Plato");
            editTextName.setText(foodItem.getName());
            editTextDescription.setText(foodItem.getDescription());
            editTextPrice.setText(String.valueOf(foodItem.getPrice()));
            editTextCategory.setText(foodItem.getCategory());
        } else {
            builder.setTitle("Añadir Plato Nuevo");
        }

        builder.setPositiveButton(foodItem == null ? "Añadir" : "Actualizar", (dialog, which) -> {
            String documentId = (foodItem != null) ? foodItem.getDocumentId() : null;
            saveFoodItem(documentId, editTextName, editTextDescription, editTextPrice, editTextCategory);
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        if (foodItem != null) {
            builder.setNeutralButton("Eliminar", (dialog, which) -> {
                new AlertDialog.Builder(AdminActivity.this)
                        .setTitle("Confirmar Eliminación")
                        .setMessage("¿Estás seguro de que quieres eliminar '" + foodItem.getName() + "'? Esta acción no se puede deshacer.")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Sí, Eliminar", (confirmDialog, confirmWhich) -> deleteFoodItem(foodItem.getDocumentId()))
                        .setNegativeButton("No", null)
                        .show();
            });
        }
        builder.create().show();
    }

    /**
     * saveFoodItem: Guarda un plato nuevo o actualiza uno existente en Firestore.
     */
    private void saveFoodItem(String documentId, EditText name, EditText desc, EditText price, EditText category) {
        // ... (Validaciones de campos) ...
        String foodName = name.getText().toString().trim();
        String foodDesc = desc.getText().toString().trim();
        String priceStr = price.getText().toString().trim();
        String foodCategory = category.getText().toString().trim();

        if (TextUtils.isEmpty(foodName) || TextUtils.isEmpty(priceStr) || TextUtils.isEmpty(foodCategory)) {
            Toast.makeText(this, "Nombre, precio y categoría son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        double foodPrice;
        try {
            foodPrice = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "El precio no es un número válido", Toast.LENGTH_SHORT).show();
            return;
        }

        FoodItem newFoodItem = new FoodItem(foodName, foodDesc, foodPrice, "", foodCategory);
        
        // COMPARACIÓN (Firebase vs. SQLite):
        // Antes: Llamábamos a `dbHelper.addFoodItem(...)` o `dbHelper.updateFoodItem(...)` y luego teníamos que llamar
        // manualmente a `loadUsers()` para refrescar la lista. Era un proceso en dos pasos.
        // Ahora: Simplemente le enviamos el objeto a Firestore. El `addSnapshotListener` se encargará de actualizar la UI por nosotros.
        if (documentId == null) {
            db.collection(DISHES_COLLECTION).add(newFoodItem)
                .addOnSuccessListener(docRef -> Toast.makeText(this, "Plato añadido", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Error al añadir", Toast.LENGTH_SHORT).show());
        } else {
            db.collection(DISHES_COLLECTION).document(documentId).set(newFoodItem)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Plato actualizado", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show());
        }
    }

    /**
     * deleteFoodItem: Elimina un plato de Firestore.
     */
    private void deleteFoodItem(String documentId) {
        if (documentId == null) return;
        // COMPARACIÓN (Firebase vs. SQLite):
        // Antes: `dbHelper.deleteFoodItem(id);` y luego `loadUsers();`
        // Ahora: Solo `db.collection(...).delete(...)`. El listener hace el resto. Más simple.
        db.collection(DISHES_COLLECTION).document(documentId).delete()
            .addOnSuccessListener(aVoid -> Toast.makeText(this, "Plato eliminado", Toast.LENGTH_SHORT).show())
            .addOnFailureListener(e -> Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show());
    }

    // COMPARACIÓN (Firebase vs. SQLite):
    // Antes: Necesitábamos un método `onDestroy` para cerrar la conexión a la base de datos (`dbHelper.close()`).
    // Ahora: Firebase gestiona su propio ciclo de vida y conexiones, por lo que no es necesario.
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
