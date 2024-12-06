package com.example.gpssosoficial;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.gpssosoficial.Adaptadores.AdapterNotificacion;
import com.example.gpssosoficial.Modelos.notificacion;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class NotificacionesFragment extends Fragment {

    RecyclerView mRecycler;
    AdapterNotificacion mAdapter;
    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;
    SolicitudesFragment vSolicitudes = new SolicitudesFragment();
    RelativeLayout vNoti;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        LayoutInflater in = getLayoutInflater();
        View vi = in.inflate(R.layout.fragment_notificaciones, container, false);
        vNoti = vi.findViewById(R.id.Contenedor_notificaciones);

        final Button btnsolicitudes = vi.findViewById(R.id.btnSolis);

       mRecycler = vi.findViewById(R.id.recyclernotificaciones);
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        Query query = mFirestore.collection("user").document(mAuth.getUid()).collection("idNotificaciones");

        FirestoreRecyclerOptions<notificacion> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<notificacion>().setQuery(query, notificacion.class).build();

        mAdapter = new AdapterNotificacion(firestoreRecyclerOptions);
        mAdapter.notifyDataSetChanged();
        mRecycler.setAdapter(mAdapter);

        btnsolicitudes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadFragment(vSolicitudes);
                vNoti.setVisibility(View.GONE);
            }
        });
        // Inflate the layout for this fragment
        //inflater.inflate(R.layout.fragment_notificaciones, container, false);
        return vi;

    }
    @Override
    public void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }
    private void loadFragment(SolicitudesFragment vSolicitudes) {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.vistaNotificaciones,vSolicitudes);
        transaction.commitNow();
    }
}