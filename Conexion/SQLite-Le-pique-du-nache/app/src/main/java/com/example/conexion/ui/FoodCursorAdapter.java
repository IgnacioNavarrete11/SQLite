// Ubicación: com.example.conexion.ui/FoodCursorAdapter.java
package com.example.conexion.ui;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import com.example.conexion.R;
import com.example.conexion.data.db.TotalFoodContract.FoodEntry;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * La clase FoodCursorAdapter actúa como un intermediario entre los datos
 * provenientes de un Cursor (resultado de una consulta a la base de datos)
 * y una ListView que necesita mostrar esos datos de forma visual.
 * Su función es crear y poblar cada una de las filas de la lista.
 */
public class FoodCursorAdapter extends CursorAdapter {

    // --- Variables de la Clase ---

    // Un objeto LayoutInflater es una herramienta estándar de Android para
    // instanciar un archivo de layout XML en sus correspondientes objetos View.
    private LayoutInflater inflater;

    // Un objeto NumberFormat para dar un formato de moneda a los valores numéricos
    // del precio, adaptándose a la configuración regional del dispositivo.
    private NumberFormat currencyFormatter;


    // --- Constructor ---
    // Se invoca al crear una instancia de este adaptador desde una Activity.
    public FoodCursorAdapter(Context context, Cursor c) {
        // Se llama al constructor de la superclase (CursorAdapter).
        // El parámetro '0' indica que no se usarán flags especiales. La gestión
        // del ciclo de vida del Cursor se realizará mediante el método changeCursor().
        super(context, c, 0);

        // Se inicializa el LayoutInflater a partir del contexto proporcionado.
        this.inflater = LayoutInflater.from(context);

        // Se inicializa el formateador de moneda. Locale.getDefault() asegura que
        // el formato (ej. puntos vs. comas, símbolo de moneda) sea el correcto
        // para el usuario final, según la configuración de su dispositivo.
        this.currencyFormatter = NumberFormat.getCurrencyInstance(Locale.getDefault());

        Locale chileLocale = new Locale("es", "CL");
        this.currencyFormatter = NumberFormat.getCurrencyInstance(chileLocale);
    }


    // --- newView: Creación de la Estructura de una Fila ---
    // La ListView invoca este método cuando necesita crear una nueva vista de fila.
    // Esto ocurre para las primeras filas visibles hasta que la pantalla se llena.
    // Posteriormente, el sistema recicla estas vistas para optimizar el rendimiento.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Se utiliza el LayoutInflater para crear una nueva jerarquía de vistas a partir
        // del layout 'activity_item_food.xml'. El resultado es una vista de fila
        // estructuralmente completa, pero todavía sin datos.
        return inflater.inflate(R.layout.activity_item_food, parent, false);
    }


    // --- bindView: Vinculación de Datos a una Fila ---
    // La ListView invoca este método para poblar una vista de fila (ya sea nueva o reciclada)
    // con los datos correspondientes a una posición específica del Cursor.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // PASO 1: Obtener referencias a los componentes de la interfaz de la fila.
        // Se utiliza 'view.findViewById()' para buscar los TextViews dentro de la
        // vista de la fila proporcionada.
        TextView textViewName = view.findViewById(R.id.textViewFoodName);
        TextView textViewDescription = view.findViewById(R.id.textViewFoodDescription);
        TextView textViewCategory = view.findViewById(R.id.textViewFoodCategory);
        TextView textViewPrice = view.findViewById(R.id.textViewFoodPrice);

        try {
            // PASO 2: Extraer datos del Cursor.
            // El objeto Cursor ya está posicionado en la fila correcta. Se extraen los
            // valores de cada columna usando las constantes definidas en TotalFoodContract
            // para garantizar la seguridad y evitar errores de tipeo.
            String name = cursor.getString(cursor.getColumnIndexOrThrow(FoodEntry.COLUMN_NAME_NOMBRE));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(FoodEntry.COLUMN_NAME_DESCRIPCION));
            String category = cursor.getString(cursor.getColumnIndexOrThrow(FoodEntry.COLUMN_NAME_CATEGORIA));
            double price = cursor.getDouble(cursor.getColumnIndexOrThrow(FoodEntry.COLUMN_NAME_PRECIO));

            // PASO 3: Asignar los datos a las vistas.
            // Los datos extraídos se establecen como el contenido de los TextViews.
            textViewName.setText(name);
            textViewDescription.setText(description);
            textViewCategory.setText(category);

            // El precio se formatea como moneda antes de ser mostrado.
            textViewPrice.setText(currencyFormatter.format(price));

        } catch (IllegalArgumentException e) {
            // Este bloque catch maneja una excepción que ocurriría si una columna
            // solicitada no existiera en el Cursor, indicando un error en la consulta
            // a la base de datos. Se registra el error para facilitar la depuración.
            Log.e("FoodCursorAdapter", "Error al obtener columna del cursor: " + e.getMessage());
        }
    }
}
