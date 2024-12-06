package com.example.gpssosoficial.Adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gpssosoficial.Modelos.notificacion;
import com.example.gpssosoficial.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class AdapterNotificacion extends FirestoreRecyclerAdapter<notificacion,RecyclerView.ViewHolder> {
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */

    private static final int TYPE_SOLICITUDES = 3; // Para alertas
    private static final int TYPE_CONTACTOS = 2;  // Para contactos
    private static final int TYPE_ALERTA = 1;     // Para solicitudes
    public AdapterNotificacion(@NonNull FirestoreRecyclerOptions<notificacion> options) {
        super(options);
    }

    @Override
    public int getItemViewType(int position) {
        DocumentSnapshot snapshot = getSnapshots().getSnapshot(position);
        return snapshot.getLong("categoria").intValue(); // Recupera el valor de la categoría
    }

    @Override
    protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull notificacion notificacion) {
        int viewType = getItemViewType(position);

        if (viewType == TYPE_SOLICITUDES) {
            SolicitudesViewHolder solicitudesHolder = (SolicitudesViewHolder) holder;
            solicitudesHolder.name.setText(notificacion.getNombre());
            // Lógica específica para solicitudes

        } else if (viewType == TYPE_CONTACTOS) {
            ContactosViewHolder contactosHolder = (ContactosViewHolder) holder;
            contactosHolder.contactName.setText(notificacion.getNombre());
            // Lógica específica para contactos
        }
        else if (viewType == TYPE_ALERTA) {
            AlertaViewHolder alertassHolder = (AlertaViewHolder) holder;
            alertassHolder.AlertaName.setText(notificacion.getNombre());
            // Lógica específica para contactos
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == TYPE_SOLICITUDES) {
            View view = inflater.inflate(R.layout.notificacion_solicitud, parent, false);
            return new SolicitudesViewHolder(view);
        } else if (viewType == TYPE_CONTACTOS) {
            View view = inflater.inflate(R.layout.notificacion_soliaceptada, parent, false);
            return new ContactosViewHolder(view);
        }else if (viewType == TYPE_ALERTA) {
            View view = inflater.inflate(R.layout.notificacion_help, parent, false);
            return new AlertaViewHolder(view);
        }
        throw new IllegalArgumentException("Tipo de vista desconocido: " + viewType);
    }
    public static class SolicitudesViewHolder extends RecyclerView.ViewHolder {
        TextView name;

        public SolicitudesViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.nametxt);
        }
    }
    // ViewHolder para contactos
    public static class ContactosViewHolder extends RecyclerView.ViewHolder {
        TextView contactName;

        public ContactosViewHolder(@NonNull View itemView) {
            super(itemView);
            contactName = itemView.findViewById(R.id.name);
        }
    }
    public static class AlertaViewHolder extends RecyclerView.ViewHolder {
        TextView AlertaName;

        public AlertaViewHolder(@NonNull View itemView) {
            super(itemView);
            AlertaName = itemView.findViewById(R.id.name);
        }
    }
   /* public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.nametxt);
        }
    }*/
}
