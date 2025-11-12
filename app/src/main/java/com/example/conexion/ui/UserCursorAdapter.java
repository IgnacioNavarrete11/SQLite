package com.example.conexion.ui;

import android.content.Context;import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import com.example.conexion.R;
import com.example.conexion.data.db.TotalFoodContract.UserEntry;

public class UserCursorAdapter extends CursorAdapter {

    public UserCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView textViewName = view.findViewById(R.id.textViewUserName);
        TextView textViewEmail = view.findViewById(R.id.textViewUserEmail);
        TextView textViewRole = view.findViewById(R.id.textViewUserRole);

        String name = cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_NAME_USERNAME));
        String email = cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_NAME_EMAIL));
        int isAdmin = cursor.getInt(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_NAME_IS_ADMIN));

        textViewName.setText(name);
        textViewEmail.setText(email);

        if (isAdmin == 1) {
            textViewRole.setText("ADMIN");
            textViewRole.setVisibility(View.VISIBLE);
        } else {
            // Oculta la etiqueta si no es un administrador.
            textViewRole.setVisibility(View.GONE);
        }
    }
}
