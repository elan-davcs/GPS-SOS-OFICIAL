package com.example.gpssosoficial;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class InicioFragment extends Fragment {


    private int MY_PERMISSIONS_REQUEST_READ_CONTACTS;

    //Datos que se utilizan y privados
    private FusedLocationProviderClient fusedLocationClient;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private Double latitud, longitud;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private boolean actualizacionesIniciadas = false;
    String idContacto,nombreUser,apellidolUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inicio, container, false);
        //getlocalizacion();

        //Declarar la base de datos para guardar los datos
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String id = user.getUid();
        mFirestore.collection("user").document(id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {

                if (documentSnapshot.exists()) {
                    nombreUser = documentSnapshot.getString("nombre");
                    apellidolUser = documentSnapshot.getString("apellido");
                    //celularUser = documentSnapshot.getString("celular");
                }
            }
        });
        // configurar el boton y los permisos
        final Button btnCirculo = view.findViewById(R.id.btnCirculo);
        verificarPermisos();

        configurarSolicitudesDeUbicacion();

        btnCirculo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                infoDatos.put("categoria",1);

                //cambiar interpretacion es usuario, contactos usuarios , id notificaciones de los contactos
                mFirestore.collection("user").document(id).collection("idContacto").get().addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot1 : queryDocumentSnapshots) {
                        String idNotiContacto  = documentSnapshot1.getString("id");
                        mFirestore.collection("user").document(idNotiContacto).collection("idNotificaciones").document(idDocumento).set(infoDatos).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getActivity(), "Enviada exitosamente", Toast.LENGTH_SHORT).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("FirestoreError", "Error al enviar la notificacion", e);
                            }
                        });
                    }
                });
                subirUbicacion(new Runnable() {
                    @Override
                    public void run() {
                        if (!actualizacionesIniciadas) {
                            iniciarActualizacionesDeUbicacion();
                        }
                    }
                });
            }
        });

        return view;
    }

    private void iniciarActualizacionesDeUbicacion() {
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        actualizacionesIniciadas = true;
        Toast.makeText(getActivity(), "Actualizaciones de ubicación iniciadas", Toast.LENGTH_SHORT).show();
    }
    private void detenerActualizacionesDeUbicacion() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
        actualizacionesIniciadas = false;
        //Toast.makeText(getActivity(), "Actualizaciones de ubicación detenidas", Toast.LENGTH_SHORT).show();
    }
    private void actualizarUbicacion(Double latitud, Double longitud) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Log.e("InicioFragment", "Usuario no autenticado");
            return;
        }

        String idUsuario = user.getUid();
        Map<String, Object> ubicacion = new HashMap<>();
        ubicacion.put("latitud", latitud);
        ubicacion.put("longitud", longitud);

        // Actualizar latitud y longitud en el documento
        mFirestore.collection("user").document(idUsuario)
                .collection("idContacto")
                .document(idUsuario)
                .update(ubicacion)  // Usamos update para actualizar los datos
                .addOnSuccessListener(aVoid -> Log.d("InicioFragment", "Ubicación actualizada"))
                .addOnFailureListener(e -> Log.e("FirestoreError", "Error al actualizar ubicación", e));
    }

    @Override
    public void onStart() {
        super.onStart();
        if (actualizacionesIniciadas) {
            iniciarActualizacionesDeUbicacion();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        detenerActualizacionesDeUbicacion();
    }

    private void configurarSolicitudesDeUbicacion() {
        locationRequest = LocationRequest.create()
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setInterval(10000) // Cada 10 segundos
                .setFastestInterval(5000); // Cada 5 segundos

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        Double latitud = location.getLatitude();
                        Double longitud = location.getLongitude();
                        actualizarUbicacion(latitud, longitud);
                    }
                }
            }
        };
    }

    private void subirUbicacion(Runnable onSuccess) {
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                //Ultima ubicación
                if (location != null) {
                    Double latitud = location.getLatitude();
                    Double longitud = location.getLongitude();

                    GuardarDatos(latitud, longitud, onSuccess);
                } else {
                    Toast.makeText(getActivity(), "No se pudo obtener la ubicación actual", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void GuardarDatos(Double latitud, Double longitud, Runnable onSuccess) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(getActivity(), "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        String idUsuario = user.getUid();
        mFirestore.collection("user").document(idUsuario).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                nombreUser = documentSnapshot.getString("nombre");

                if (latitud == null || longitud == null) {
                    Toast.makeText(getActivity(), "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show();
                    return;
                }

                Map<String, Object> ubicacion = new HashMap<>();
                ubicacion.put("id", idUsuario);
                ubicacion.put("nombre", nombreUser);
                ubicacion.put("latitud", latitud);
                ubicacion.put("longitud", longitud);

                mFirestore.collection("user").document(idUsuario).collection("idContacto").get().addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot1 : queryDocumentSnapshots) {
                        String idContacto  = documentSnapshot1.getString("id");
                        mFirestore.collection("user").document(idContacto).collection("idUbicaciones").document(idUsuario).set(ubicacion).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getActivity(), "Ubicación enviada exitosamente", Toast.LENGTH_SHORT).show();
                                if (onSuccess != null) {
                                    onSuccess.run();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("FirestoreError", "Error al enviar ubicación", e);
                            }
                        });
                    }
                });
            }
        });
    }
    private void verificarPermisos() {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }
    private void getlocalizacion() {
        int permiso = ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permiso == PackageManager.PERMISSION_DENIED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION)){

            }else {
                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
        }
    }
}