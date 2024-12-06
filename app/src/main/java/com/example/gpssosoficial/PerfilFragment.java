package com.example.gpssosoficial;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PerfilFragment extends Fragment {

    private Button aggContacto, btnCerrar,verContac;
    private TextView txtnameUser, txtidentificadorUser, txtEmailUser, txtCelularUser;
    private ImageButton btncopy;
    FirebaseAuth mAuth;
    FirebaseFirestore mFirestore;
    String idsol;
    String apellidosolicitante;
    //Fragments para cambiar de vista
    RelativeLayout Vperfil;

    ContactosFragment contactosFragment = new ContactosFragment();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        //contenedor completo del perfil
        Vperfil = view.findViewById(R.id.Vperfil);


        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        //UserId = mAuth.getCurrentUser().getUid();
        FirebaseUser user = mAuth.getCurrentUser();
        String id = user.getUid();

        verContac = view.findViewById(R.id.ver_contac);

        aggContacto = view.findViewById(R.id.agg_contac); // Asignar el botón por su ID
        btnCerrar = view.findViewById(R.id.btn_cerrar);
        txtnameUser = view.findViewById(R.id.textname);
        txtidentificadorUser = view.findViewById(R.id.textid);
        txtEmailUser = view.findViewById(R.id.txt_email);
        txtCelularUser = view.findViewById(R.id.txt_cel);

        btncopy = view.findViewById(R.id.btncopy);

        txtEmailUser.setText(mAuth.getCurrentUser().getEmail());

        mFirestore.collection("user").document(id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {

                if (documentSnapshot.exists()) {
                    String nombreUser = documentSnapshot.getString("nombre");
                    String identificadorUser = documentSnapshot.getString("identificacion");
                    String emailUser = documentSnapshot.getString("email");
                    String celularUser = documentSnapshot.getString("celular");
                    apellidosolicitante = documentSnapshot.getString("apellido");

                    txtnameUser.setText(nombreUser);
                    txtidentificadorUser.setText(identificadorUser);
                    txtEmailUser.setText(emailUser);
                    txtCelularUser.setText(celularUser);
                }
            }
        });
        //boton para abrir la pantalla para agregar un contacto
        aggContacto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(requireContext());
                LayoutInflater in = getLayoutInflater();
                View vi = in.inflate(R.layout.activity_agregar_contacto, null);
                final EditText txtidentificacion = vi.findViewById(R.id.edit_id);
                final TextView txtnombre = vi.findViewById(R.id.texto_nombre);
                final TextView txtcelular = vi.findViewById(R.id.texto_celular);
                final Button buscar = vi.findViewById(R.id.buscar);
                final Button agregar = vi.findViewById(R.id.agregar);

                //Dar espacio en el numero despues de que lo busque ESTA PENDIENTE
                String txtnumber = txtcelular.getText().toString();

                alert.setView(vi);
                AlertDialog a3 = alert.create();
                a3.show();

                buscar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String myId = txtidentificadorUser.getText().toString();
                        //String Id = txtidentificacion.getText().toString().trim();
                        String searchName = txtidentificacion.getText().toString().trim();

                        //es con el ischeck
                        if (searchName.isEmpty()){

                            Toast.makeText(getActivity(), "Agregar una Id", Toast.LENGTH_SHORT).show();

                        } else if(myId.equals(searchName)){

                            Toast.makeText(getActivity(), "No puedes buscar tu misma Id", Toast.LENGTH_SHORT).show();

                        } else {
                            mFirestore.collection("user").whereEqualTo("identificacion",searchName).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot documentSnapshots) {

                                    if (documentSnapshots.isEmpty()){

                                        Toast.makeText(getActivity(), "Id no encontrado", Toast.LENGTH_SHORT).show();
                                        txtnombre.setText("---");
                                        txtcelular.setText("000 000 0000");

                                    }else {

                                        //lo mas complicado de hacer
                                        for (QueryDocumentSnapshot document : documentSnapshots){
                                            String userName = document.getString("nombre");
                                            String userPhone = document.getString("celular");
                                            idsol = document.getString("id");

                                            //campos que estan dentro del dialog alert
                                            txtnombre.setText(userName);
                                            txtcelular.setText(userPhone);

                                        }
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                }
                            });
                        }
                    }
                });
                agregar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //String pa que jalar el nombre del usuario
                        String idsolicitante = txtnameUser.getText().toString();
                        String numerosolicitante = txtCelularUser.getText().toString();
                        Timestamp fecha = Timestamp.now();
                        // Convertir el Timestamp a una fecha
                        Date date = fecha.toDate();
                        // Formatear la fecha como una cadena
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
                        String idDocumento = sdf.format(date);

                        Map<String, Object> infoDatos = new HashMap<>();
                        infoDatos.put("id", id);
                        infoDatos.put("nombre",idsolicitante);
                        infoDatos.put("categoria",3); //esta categoria es para solicitudes
                        infoDatos.put("apellido",apellidosolicitante);

                        Map<String, Object> map = new HashMap<>();
                        map.put("id", id);
                        map.put("nombre",idsolicitante);
                        map.put("categoria",3); //esta categoria es para solicitudes
                        map.put("apellido",apellidosolicitante);
                        map.put("celular",numerosolicitante);
                        mFirestore.collection("user").document(idsol).collection("idNotificaciones").document(idDocumento).set(infoDatos).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getActivity(), "Ya se mando la solicitud", Toast.LENGTH_SHORT).show();
                                a3.dismiss();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Toast.makeText(getActivity(), "no esta conectado", Toast.LENGTH_SHORT).show();


                            }
                        });
                        mFirestore.collection("user").document(idsol).collection("idSolicitudes").document(mAuth.getCurrentUser().getUid()).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                //Toast.makeText(getActivity(), "Ya se mando la solicitud", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //Toast.makeText(getActivity(), "no esta conectado", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });
        btnCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        btncopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CharSequence textToCopy = txtidentificadorUser.getText();
                copyToClipboard(textToCopy);

            }
        });
        //btn para cambiar la vista del fragment sin quitar el mundo de abajo
        verContac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadFragment(contactosFragment);
                // Opcionalmente oculta el layout solo si el contenedor del nuevo fragmento es independiente
                if (Vperfil != null) {
                    Vperfil.setVisibility(View.GONE);
                }

            }
        });

        return view;
    }
    private void copyToClipboard(CharSequence text) {
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("Texto Copiado", text);
        Toast.makeText(getActivity(), "Id copiado", Toast.LENGTH_SHORT).show();
        clipboard.setPrimaryClip(clip);
    }
    public void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.contenedor_perfil, contactosFragment);
        transaction.commit();
    }
}