package com.example.conexion.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.conexion.R;
import com.example.conexion.model.FoodItem;
import com.example.conexion.ui.adapter.FoodAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

// =================================================================================================
// CLASE MainMenuActivity: La Pantalla que Ven los Clientes
// =================================================================================================
// Esta clase es mucho más simple que la del Admin, porque su única responsabilidad es MOSTRAR el menú.
public class MainMenuActivity extends AppCompatActivity {

    // Una etiqueta para identificar los mensajes de esta pantalla en el Logcat.
    private static final String TAG = "MainMenuActivity";
    // El nombre exacto de nuestra colección de platos en Firestore.
    private static final String DISHES_COLLECTION = "dishes";

    // --- 1. DECLARACIÓN DE COMPONENTES DE LA INTERFAZ ---
    private SwipeRefreshLayout swipeRefreshLayoutMenu; // El componente para "deslizar para refrescar".
    private RecyclerView recyclerViewFoodMenu;       // La lista visual donde se mostrará el menú.
    private ProgressBar progressBarMenu;             // El círculo de carga.
    private TextView textViewEmptyMenu;              // El texto que aparece si el menú está vacío.
    private Button buttonLogout;                     // El botón para cerrar sesión.

    // --- 2. DECLARACIÓN DE HERRAMIENTAS DE DATOS ---
    private FirebaseFirestore db;                    // La conexión a la base de datos en la nube.
    private FoodAdapter foodAdapter;                 // El puente entre los datos y la lista visual.
    private final List<Object> displayList = new ArrayList<>(); // La lista de cabeceras y platos para el adaptador.

    // --- 3. ON_CREATE: El Corazón de la Actividad ---
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        // Inicializamos la conexión a Firestore.
        db = FirebaseFirestore.getInstance();

        // --- ENLACE DE VISTAS ---
        // (Asumiendo que has actualizado tu `activity_main_menu.xml` con estos IDs)
        swipeRefreshLayoutMenu = findViewById(R.id.swipeRefreshLayoutMenu);
        recyclerViewFoodMenu = findViewById(R.id.recyclerViewFoodMenu); // Usar el nuevo ID
        progressBarMenu = findViewById(R.id.progressBarMenu);
        textViewEmptyMenu = findViewById(R.id.textViewEmptyMenu);
        buttonLogout = findViewById(R.id.buttonLogout);

        setupRecyclerView();

        // --- CONFIGURACIÓN DE LISTENERS ---
        buttonLogout.setOnClickListener(v -> logoutUser());

        swipeRefreshLayoutMenu.setOnRefreshListener(this::loadFoodMenuFromFirestore);

        // Carga inicial de datos.
        loadFoodMenuFromFirestore();
    }

    // --- 4. MÉTODOS DE CONFIGURACIÓN Y LÓGICA ---

    /**
     * setupRecyclerView: Prepara nuestra lista visual.
     */
    private void setupRecyclerView() {
        // Creamos el adaptador. Le pasamos la lista y `null` para el listener, porque en esta pantalla, los clics en los platos no hacen nada.
        foodAdapter = new FoodAdapter(displayList, null);
        recyclerViewFoodMenu.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewFoodMenu.setAdapter(foodAdapter);
    }

    /**
     * loadFoodMenuFromFirestore: Pide el menú a la nube y lo muestra.
     */
    private void loadFoodMenuFromFirestore() {
        // Mostramos el indicador de carga apropiado.
        if (!swipeRefreshLayoutMenu.isRefreshing()) {
            progressBarMenu.setVisibility(View.VISIBLE);
        }
        recyclerViewFoodMenu.setVisibility(View.GONE);
        textViewEmptyMenu.setVisibility(View.GONE);

        // COMPARACIÓN (Firebase vs. SQLite):
        // La lógica es idéntica a la de AdminActivity, lo que demuestra la reutilización de nuestro código.
        // Nos suscribimos en tiempo real para que si el admin cambia el menú, el cliente lo vea al instante.
        db.collection(DISHES_COLLECTION)
                .orderBy("category", Query.Direction.ASCENDING)
                .orderBy("name", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    // Cuando llegan los datos, ocultamos los indicadores de carga.
                    progressBarMenu.setVisibility(View.GONE);
                    swipeRefreshLayoutMenu.setRefreshing(false);

                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        textViewEmptyMenu.setText("Error al cargar el menú.\nDesliza para reintentar.");
                        textViewEmptyMenu.setVisibility(View.VISIBLE);
                        return;
                    }

                    // Agrupamos los platos por categoría.
                    Map<String, List<FoodItem>> itemsByCategory = new LinkedHashMap<>();
                    for (QueryDocumentSnapshot doc : Objects.requireNonNull(snapshots)) {
                        FoodItem item = doc.toObject(FoodItem.class);
                        String category = item.getCategory();
                        if (category == null || category.isEmpty()) category = "Otros";
                        if (!itemsByCategory.containsKey(category)) {
                            itemsByCategory.put(category, new ArrayList<>());
                        }
                        Objects.requireNonNull(itemsByCategory.get(category)).add(item);
                    }

                    // Construimos la lista final para mostrar en pantalla.
                    displayList.clear();
                    if (itemsByCategory.isEmpty()) {
                        // Si no hay platos, mostramos el mensaje de menú vacío.
                        textViewEmptyMenu.setText("El menú está vacío por ahora. ¡Vuelve pronto!");
                        textViewEmptyMenu.setVisibility(View.VISIBLE);
                        recyclerViewFoodMenu.setVisibility(View.GONE);
                    } else {
                        textViewEmptyMenu.setVisibility(View.GONE);
                        recyclerViewFoodMenu.setVisibility(View.VISIBLE);
                        for (String category : itemsByCategory.keySet()) {
                            displayList.add(category.toUpperCase());
                            displayList.addAll(Objects.requireNonNull(itemsByCategory.get(category)));
                        }
                    }
                    // Le avisamos al adaptador que redibuje la pantalla con los nuevos datos.
                    foodAdapter.updateList(displayList);
                });
    }

    /**
     * logoutUser: Cierra la sesión del usuario y lo devuelve a la pantalla de Login.
     */
    private void logoutUser() {
        Toast.makeText(this, "Cerrando sesión...", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainMenuActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
