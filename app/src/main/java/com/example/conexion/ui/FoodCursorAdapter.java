package com.example.conexion.ui;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import com.example.conexion.R;
import com.example.conexion.data.db.TotalFoodContract.FoodEntry;
import java.text.NumberFormat;
import java.util.Locale;

public class FoodCursorAdapter extends CursorAdapter {

    private LayoutInflater inflater;
    private NumberFormat currencyFormatter;

    public FoodCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
        inflater = LayoutInflater.from(context);
        currencyFormatter = NumberFormat.getCurrencyInstance(Locale.getDefault());
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return inflater.inflate(R.layout.activity_item_food, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView textViewName = view.findViewById(R.id.textViewFoodName);
        TextView textViewDescription = view.findViewById(R.id.textViewFoodDescription);
        TextView textViewCategory = view.findViewById(R.id.textViewFoodCategory);
        TextView textViewPrice = view.findViewById(R.id.textViewFoodPrice);

        try {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(FoodEntry.COLUMN_NAME_NOMBRE));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(FoodEntry.COLUMN_NAME_DESCRIPCION));
            String category = cursor.getString(cursor.getColumnIndexOrThrow(FoodEntry.COLUMN_NAME_CATEGORIA));
            double price = cursor.getDouble(cursor.getColumnIndexOrThrow(FoodEntry.COLUMN_NAME_PRECIO));

            textViewName.setText(name);
            textViewDescription.setText(description);
            textViewCategory.setText(category);
            textViewPrice.setText(currencyFormatter.format(price));
        } catch (IllegalArgumentException e) {
            android.util.Log.e("FoodCursorAdapter", "Error al obtener columna: " + e.getMessage());
        }
    }
}
