package com.example.gpssosoficial.Adaptadores;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gpssosoficial.Modelos.contacto;
import com.example.gpssosoficial.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdapterContactos extends FirestoreRecyclerAdapter<contacto, AdapterContactos.ViewHolder> {
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */

    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    String idContacto;
    Activity activity;
    public AdapterContactos(@NonNull FirestoreRecyclerOptions<contacto> options, Activity activity) {
        super(options);
        this.activity = activity;
    }

    @Override
    protected void onBindViewHolder(@NonNull AdapterContactos.ViewHolder holder, int position, @NonNull contacto contacto) {
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(holder.getPosition());
        idContacto = documentSnapshot.getId();

        holder.name.setText(contacto.getNombre());
        holder.surname.setText(contacto.getApellido());
        holder.celular.setText(contacto.getCelular());

        holder.btndelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteContacto();
            }
        });
    }

    private void DeleteContacto() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String id = user.getUid();
        mFirestore.collection("user").document(id).collection("idContacto").document(idContacto).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(activity, "Se elimino el contacto", Toast.LENGTH_SHORT).show();
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
    public AdapterContactos.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_contactos,parent,false);

        return new AdapterContactos.ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name,surname,celular;
        Button btndelete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.nameContac);
            surname = itemView.findViewById(R.id.lastnameContac);
            celular = itemView.findViewById(R.id.celularContac);
            btndelete = itemView.findViewById(R.id.deleteContac);
        }
    }
}
