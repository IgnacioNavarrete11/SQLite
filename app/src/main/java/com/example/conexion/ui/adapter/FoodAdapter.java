package com.example.conexion.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.conexion.R;
import com.example.conexion.model.FoodItem;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

// =================================================================================================
// CLASE FoodAdapter: El "Puente Inteligente" entre los Datos y la Lista Visual
// =================================================================================================
// COMPARACIÓN (RecyclerView.Adapter vs. CursorAdapter):
// Antes, usábamos un CursorAdapter que estaba atado a la estructura de la base de datos local (un "Cursor").
// Ahora, usamos un RecyclerView.Adapter, que es mucho más flexible. Puede trabajar con CUALQUIER tipo de lista de objetos (en nuestro caso, una lista de `Object`).
public class FoodAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // --- 1. DECLARACIÓN DE CONSTANTES Y VARIABLES ---

    // Constantes para identificar nuestros dos tipos de vistas: una para las cabeceras y otra para los platos.
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    // La lista de "cosas" que vamos a mostrar. Es de tipo `Object` porque puede contener tanto `String` (para los títulos) como `FoodItem` (para los platos).
    private List<Object> items;
    // El "oyente" que avisará a la Activity cuando se haga clic en un plato.
    private OnItemClickListener listener;

    /**
     * Interfaz OnItemClickListener: Es como un "contrato" que la Activity (ej. AdminActivity) debe firmar.
     * Le dice a la Activity: "Oye, si quieres que te avise de los clics, tienes que tener un método llamado onItemClick que reciba un FoodItem".
     */
    public interface OnItemClickListener {
        void onItemClick(FoodItem foodItem);
    }

    /**
     * Constructor: Se llama cuando creamos una nueva instancia del adaptador.
     * @param items La lista de datos a mostrar.
     * @param listener Quién escuchará los clics (normalmente, la Activity).
     */
    public FoodAdapter(List<Object> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    // --- 2. MÉTODOS CLAVE DE RECYCLERVIEW.ADAPTER ---

    /**
     * getItemViewType: Este es el primer método que llama la RecyclerView. Es el "cerebro" del adaptador.
     * Su trabajo es mirar un elemento de la lista y decidir si es una cabecera o un plato.
     * @param position La posición del elemento en la lista.
     * @return El tipo de vista (nuestras constantes TYPE_HEADER o TYPE_ITEM).
     */
    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof String) {
            return TYPE_HEADER; // Si es un texto, es una cabecera.
        } else {
            return TYPE_ITEM;   // Si es cualquier otra cosa (en nuestro caso, un FoodItem), es un plato.
        }
    }

    /**
     * onCreateViewHolder: Se llama cuando la RecyclerView necesita crear una nueva "caja" visual (un ViewHolder).
     * @param viewType El tipo de vista que `getItemViewType` acaba de decidir.
     * @return Un nuevo ViewHolder del tipo correcto (HeaderViewHolder o FoodViewHolder).
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Dependiendo del tipo, "inflamos" (cargamos) el archivo de layout XML correspondiente.
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_item, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food, parent, false);
            return new FoodViewHolder(view);
        }
    }

    /**
     * onBindViewHolder: Se llama cuando la RecyclerView quiere mostrar datos en una "caja" (ViewHolder) específica.
     * Su trabajo es tomar los datos de una posición y pasárselos al ViewHolder para que los "pinte" en pantalla.
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // Dependiendo del tipo de ViewHolder, llamamos a su método `bind` con el objeto correcto.
        if (holder.getItemViewType() == TYPE_HEADER) {
            ((HeaderViewHolder) holder).bind((String) items.get(position));
        } else {
            ((FoodViewHolder) holder).bind((FoodItem) items.get(position), listener);
        }
    }
    
    /**
     * getItemCount: Un método simple que le dice a la RecyclerView cuántos elementos hay en total en la lista.
     */
    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    /**
     * updateList: Un método de ayuda que usamos para actualizar la lista de datos y notificar a la RecyclerView.
     */
    public void updateList(List<Object> newList) {
        this.items = newList;
        notifyDataSetChanged(); // Este método mágico le dice a la RecyclerView: "¡Oye, los datos han cambiado, redibújate!"
    }

    // --- 3. CLASES VIEWHOLDER (Las "Cajas" Visuales) ---

    /**
     * FoodViewHolder: Una "caja" que contiene las referencias a las vistas de un solo plato (un `item_food.xml`).
     * Su única responsabilidad es "sostener" las vistas (TextViews) para no tener que buscarlas cada vez.
     */
    static class FoodViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewName;
        private final TextView textViewDescription;
        private final TextView textViewPrice;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            // Buscamos los componentes del layout una sola vez y los guardamos.
            textViewName = itemView.findViewById(R.id.textViewFoodName);
            textViewDescription = itemView.findViewById(R.id.textViewFoodDescription);
            textViewPrice = itemView.findViewById(R.id.textViewFoodPrice);
        }

        /**
         * bind: Rellena las vistas con los datos de un objeto FoodItem y configura el clic.
         */
        public void bind(final FoodItem foodItem, final OnItemClickListener listener) {
            textViewName.setText(foodItem.getName());
            textViewDescription.setText(foodItem.getDescription());
            
            // Le damos formato al precio para que se vea como moneda chilena.
            NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("es", "CL"));
            format.setMaximumFractionDigits(0);
            String priceString = format.format(foodItem.getPrice());
            textViewPrice.setText(priceString);

            // Configuramos el listener para que cuando se haga clic en toda la fila, se avise a la Activity.
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(foodItem);
                }
            });
        }
    }

    /**
     * HeaderViewHolder: Una "caja" súper simple que solo contiene la referencia a la vista de un título de categoría (`header_item.xml`).
     */
    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final TextView headerTitle;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            headerTitle = itemView.findViewById(R.id.header_title);
        }

        public void bind(String title) {
            headerTitle.setText(title);
        }
    }
}
