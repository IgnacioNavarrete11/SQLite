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

//Esta versión tiene la lógica para las categorías.
public class FoodCursorAdapter extends CursorAdapter {

    private LayoutInflater inflater;
    private NumberFormat currencyFormatter;

    public FoodCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
        this.inflater = LayoutInflater.from(context);

        // 1. Se inicializa el formateador para pesos chilenos (CLP).
        this.currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("es", "CL"));
        // 2. Se le dice explícitamente que NO use decimales, que es lo correcto para CLP.
        this.currencyFormatter.setMaximumFractionDigits(0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return inflater.inflate(R.layout.activity_item_food, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // 1. Se enlazan todos los componentes
        TextView textViewName = view.findViewById(R.id.textViewFoodName);
        TextView textViewDescription = view.findViewById(R.id.textViewFoodDescription);
        TextView textViewCategory = view.findViewById(R.id.textViewFoodCategory);
        TextView textViewPrice = view.findViewById(R.id.textViewFoodPrice);
        TextView textViewCategoryHeader = view.findViewById(R.id.textViewCategoryHeader);

        try {
            // se extraen los datos del cursor.
            String name = cursor.getString(cursor.getColumnIndexOrThrow(FoodEntry.COLUMN_NAME_NOMBRE));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(FoodEntry.COLUMN_NAME_DESCRIPCION));
            String currentCategory = cursor.getString(cursor.getColumnIndexOrThrow(FoodEntry.COLUMN_NAME_CATEGORIA));
            double price = cursor.getDouble(cursor.getColumnIndexOrThrow(FoodEntry.COLUMN_NAME_PRECIO));

            textViewName.setText(name);
            textViewDescription.setText(description);
            textViewCategory.setText(currentCategory);
            // El formateador corregido ahora mostrará el precio correctamente (ej. $7.000)
            textViewPrice.setText(currencyFormatter.format(price));

            // 3. LÓGICA PARA MOSTRAR/OCULTAR EL ENCABEZADO
            int position = cursor.getPosition();
            if (position == 0 || !isSameCategoryAsPrevious(cursor, position)) {
                // Si es el primer plato o la categoría es nueva, mostramos el título.
                textViewCategoryHeader.setText(currentCategory);
                textViewCategoryHeader.setVisibility(View.VISIBLE);
            } else {
                // Si no, lo ocultamos.
                textViewCategoryHeader.setVisibility(View.GONE);
            }

        } catch (IllegalArgumentException e) {
            Log.e("FoodCursorAdapter", "Error al obtener columna del cursor: " + e.getMessage());
        }
    }

    /**
     * MÉTODO AUXILIAR para comprobar si la categoría del elemento actual
     * es la misma que la del elemento anterior.
     */
    private boolean isSameCategoryAsPrevious(Cursor cursor, int position) {
        // Si es el primer elemento (posición 0), no hay anterior, así que devolvemos false.
        if (position == 0) {
            return false;
        }
        
        // Movemos el cursor una posición atrás para "espiar" al elemento anterior.
        cursor.moveToPosition(position - 1);
        String previousCategory = cursor.getString(cursor.getColumnIndexOrThrow(FoodEntry.COLUMN_NAME_CATEGORIA));
        
        // Mueve el cursor de vuelta a su posición original para no alterar el flujo.
        cursor.moveToPosition(position);
        String currentCategory = cursor.getString(cursor.getColumnIndexOrThrow(FoodEntry.COLUMN_NAME_CATEGORIA));
        
        // Compara las categorías y devuelve el resultado.
        return currentCategory.equals(previousCategory);
    }
}