package com.example.gpssosoficial;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gpssosoficial.Adaptadores.AdapterContactos;
import com.example.gpssosoficial.Modelos.contacto;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ContactosFragment extends Fragment {


    RecyclerView mRecycler;
    AdapterContactos mAdapter;
    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        LayoutInflater in = getLayoutInflater();
        View vi = in.inflate(R.layout.fragment_contactos, container, false);

        mRecycler = vi.findViewById(R.id.recyclercontactos);
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        Query query =  mFirestore.collection("user").document(mAuth.getUid()).collection("idContacto");

        FirestoreRecyclerOptions<contacto> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<contacto>().setQuery(query, contacto.class).build();

        mAdapter = new AdapterContactos(firestoreRecyclerOptions,getActivity());
        mAdapter.notifyDataSetChanged();
        mRecycler.setAdapter(mAdapter);
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
}