package com.example.gpssosoficial;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.gpssosoficial.Adaptadores.AdapterSolicitudes;
import com.example.gpssosoficial.Modelos.solicitud;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class SolicitudesFragment extends Fragment {

    RecyclerView mRecycler;
    AdapterSolicitudes mAdapter;
    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        LayoutInflater in = getLayoutInflater();
        View vi = in.inflate(R.layout.fragment_solicitudes, container, false);

        final Button btnNotificaciones = vi.findViewById(R.id.btnNoti);

        mRecycler = vi.findViewById(R.id.recyclersolicitudes);
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        Query query = mFirestore.collection("user").document(mAuth.getUid()).collection("idSolicitudes");

        FirestoreRecyclerOptions<solicitud>firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<solicitud>().setQuery(query, solicitud.class).build();
        mAdapter = new AdapterSolicitudes(firestoreRecyclerOptions, getActivity());
        mAdapter.notifyDataSetChanged();
        mRecycler.setAdapter(mAdapter);

        btnNotificaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Regresar al fragmento SolicitudesFragment
                //SolicitudesFragment solicitudesFragment = new SolicitudesFragment();

                //getParentFragmentManager().popBackStack();
            }
        });
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
        mAdapter.startListening();
    }
}