package com.example.conexion.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.conexion.R;
import com.example.conexion.model.User;
import java.util.List;

/**
 * UserAdapter es el adaptador para la RecyclerView que muestra la lista de usuarios.
 * Conecta la lista de objetos User con la interfaz gráfica.
 */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;
    private OnItemClickListener listener;

    /**
     * Interfaz para manejar los eventos de clic en un elemento de la lista.
     */
    public interface OnItemClickListener {
        void onItemClick(User user);
    }

    public UserAdapter(List<User> userList, OnItemClickListener listener) {
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Se infla el layout de la fila (item_user.xml)
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        // Se obtiene el usuario de la posición actual y se vincula con el ViewHolder.
        User user = userList.get(position);
        holder.bind(user, listener);
    }

    @Override
    public int getItemCount() {
        return userList == null ? 0 : userList.size();
    }

    /**
     * Método para actualizar la lista de usuarios y notificar al RecyclerView que debe redibujarse.
     */
    public void updateList(List<User> newList) {
        this.userList = newList;
        notifyDataSetChanged();
    }

    /**
     * UserViewHolder: Mantiene las referencias a las vistas de una sola fila (un item_user.xml).
     */
    static class UserViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewName;
        private final TextView textViewEmail;
        private final TextView textViewRole;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewUserName);
            textViewEmail = itemView.findViewById(R.id.textViewUserEmail);
            textViewRole = itemView.findViewById(R.id.textViewUserRole);
        }

        /**
         * Vincula un objeto User con las vistas del layout y configura el listener de clic.
         */
        public void bind(final User user, final OnItemClickListener listener) {
            textViewName.setText(user.getUsername());
            textViewEmail.setText(user.getEmail());
            // Mostramos el rol del usuario de forma clara.
            textViewRole.setText("Rol: " + user.getRole());

            // Se configura el listener de clic en toda la vista de la fila.
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(user);
                }
            });
        }
    }
}
