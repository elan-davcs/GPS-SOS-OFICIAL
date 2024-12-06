package com.example.gpssosoficial.Adaptadores;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gpssosoficial.Modelos.solicitud;
import com.example.gpssosoficial.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AdapterSolicitudes extends FirestoreRecyclerAdapter<solicitud,AdapterSolicitudes.ViewHolder> {
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    String  idSolicitud, nombreContacto,apellidoContacto,celularContacto;
    TextView txtnomsoli;
    String nombreUser,apellidolUser,celularUser; //datos que se enviaran para crear el contacto
    Activity activity;
    public AdapterSolicitudes(@NonNull FirestoreRecyclerOptions<solicitud> options, Activity activity) {
        super(options);
        this.activity = activity;
    }

    @Override
    protected void onBindViewHolder(@NonNull AdapterSolicitudes.ViewHolder holder, int position, @NonNull solicitud solicitud) {
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
        idSolicitud = documentSnapshot.getString("id");
        nombreContacto = documentSnapshot.getString("nombre");
        apellidoContacto = documentSnapshot.getString("apellido");
        celularContacto = documentSnapshot.getString("celular");

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String id = user.getUid();
        mFirestore.collection("user").document(id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {

                if (documentSnapshot.exists()) {
                    nombreUser = documentSnapshot.getString("nombre");
                    apellidolUser = documentSnapshot.getString("apellido");
                    celularUser = documentSnapshot.getString("celular");
                }
            }
        });
        holder.name.setText(solicitud.getNombre());
        holder.surname.setText(solicitud.getApellido());
        holder.celular.setText(solicitud.getCelular());

        //datos para el AlertDialog
        holder.btnVerCorfirmacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent i = new Intent(activity, ConfirmacionActivity.class);
                i.putExtra("idSolicitud",idSolicitud);
                activity.startActivity(i);*/
                MostrarAlert(v.getContext());

            }
        });
    }

    private void MostrarAlert(Context context) {

        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        LayoutInflater in = LayoutInflater.from(context);
        View view = in.inflate(R.layout.activity_confirmacion, null);
        final Button Aceptar = view.findViewById(R.id.aceptar);
        final Button Rechazar = view.findViewById(R.id.rechazar);
        txtnomsoli = view.findViewById(R.id.textname_corfirmacion);
        txtnomsoli.setText(nombreContacto);

        alert.setView(view);
        AlertDialog a3 = alert.create();
        a3.show();

        Aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AgregarContacto();
                DeleteSolicitud();
                Toast.makeText(activity, "Solicitud Aceptada", Toast.LENGTH_SHORT).show();
                a3.dismiss();
            }
        });
        Rechazar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteSolicitud();
                Toast.makeText(activity, "Solicitud eliminada", Toast.LENGTH_SHORT).show();
                a3.dismiss();

            }
        });
    }

    private void DeleteSolicitud() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String id = user.getUid();

        mFirestore.collection("user").document(id).collection("idSolicitudes").document(idSolicitud).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(activity, "no esta conectado", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void AgregarContacto() {
        //String pa que jalar el nombre del usuario
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String id = user.getUid();
        Timestamp fecha = Timestamp.now();
        // Convertir el Timestamp a una fecha
        Date date = fecha.toDate();
        // Formatear la fecha como una cadena
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String idDocumento = sdf.format(date);
        Map<String, Object> infoDatos = new HashMap<>();
        infoDatos.put("id", id);
        infoDatos.put("nombre",nombreUser);
        infoDatos.put("apellido",apellidolUser);
        infoDatos.put("categoria",2);

        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("nombre",nombreUser);
        map.put("apellido",apellidolUser);
        map.put("celular",celularUser);
        mFirestore.collection("user").document(idSolicitud).collection("idNotificaciones").document(idDocumento).set(infoDatos).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Toast.makeText(activity, "no esta conectado", Toast.LENGTH_SHORT).show();
            }
        });
        mFirestore.collection("user").document(idSolicitud).collection("idContacto").document(id).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(activity, "no esta conectado", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @NonNull
    @Override
    public AdapterSolicitudes.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_solicitudes,parent,false);
        return new AdapterSolicitudes.ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name,surname,celular;
        Button btnVerCorfirmacion;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.nametxt);
            surname = itemView.findViewById(R.id.lastnametxt);
            celular = itemView.findViewById(R.id.celular);
            btnVerCorfirmacion = itemView.findViewById(R.id.btnVerSolis);
        }
    }
}
