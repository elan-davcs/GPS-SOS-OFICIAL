package com.example.gpssosoficial;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;


public class MapaFragment extends Fragment implements OnMapReadyCallback {


    GoogleMap map;
    private LocationManager locationManager;
    private Location mylocation;
    private ArrayList<Marker> temporalTimeMarkers = new ArrayList<>();
    private ArrayList<Marker> realTimeMarkers = new ArrayList<>();

    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    String idUsuario;
    private Marker marcadorUbicacionActual; // Marcador de mi posision personalizado
    private float currentZoomLevel = 15.0f; // Nivel de zoom inicial

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_mapa, container, false);
        // Configurar el fragmento del mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Inicializar Firebase
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            idUsuario = user.getUid();
        } else {
            Toast.makeText(getActivity(), "No se pudo obtener el usuario actual", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

        // Establecer ubicación inicial
        LatLng manzanillo = new LatLng(19.11028, -104.2162417);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(manzanillo, currentZoomLevel));

        // Verificar permisos
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getActivity(), "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
            return;
        }

        // Habilitar la ubicación actual
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(false);

        // Registrar un listener para cambios en la ubicación
        map.setOnMyLocationChangeListener(location -> {
            LatLng miUbicacion = new LatLng(location.getLatitude(), location.getLongitude());

            // Agregar o mover el marcador de ubicación actual
            if (marcadorUbicacionActual == null) {
                marcadorUbicacionActual = map.addMarker(new MarkerOptions()
                        .position(miUbicacion)
                        .title("Tu")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            } else {
                marcadorUbicacionActual.setPosition(miUbicacion);
            }
            // Mover la cámara si es necesario
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(miUbicacion)
                    .zoom(currentZoomLevel)
                    .build();
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        });

        // Capturar cambios en el zoom del mapa
        map.setOnCameraIdleListener(() -> {
            currentZoomLevel = map.getCameraPosition().zoom;
        });

        // Cargar ubicaciones desde Firestore
        cargarUbicacionesDesdeFirestore();
    }
    private void cargarUbicacionesDesdeFirestore() {

        if (idUsuario == null) {
            Log.e("MapaFragment", "ID de usuario es nulo. No se pueden cargar ubicaciones.");
            return;
        }

        mFirestore.collection("user").document(idUsuario).collection("idUbicaciones")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Limpiar marcadores previos
                    for (Marker marker : realTimeMarkers) {
                        marker.remove();
                    }

                    // Verificar si hay ubicaciones
                    if (queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(getActivity(), "No se encontraron ubicaciones", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Agregar nuevos marcadores
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Double latitud = documentSnapshot.getDouble("latitud");
                        Double longitud = documentSnapshot.getDouble("longitud");
                        String nombreContacto = documentSnapshot.getString("nombre");

                        if (latitud != null && longitud != null) {
                            MarkerOptions markerOptions = new MarkerOptions()
                                    .position(new LatLng(latitud, longitud))
                                    .title(nombreContacto != null ? nombreContacto : "Sin nombre");
                            temporalTimeMarkers.add(map.addMarker(markerOptions));
                        } else {
                            Log.e("MapaFragment", "Latitud o longitud nula para el documento: " + documentSnapshot.getId());
                        }
                    }

                    // Actualizar la lista de marcadores
                    realTimeMarkers.clear();
                    realTimeMarkers.addAll(temporalTimeMarkers);
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Error al obtener ubicaciones: " + e.getMessage(), e);
                });
    }
}