package com.example.gpssosoficial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    InicioFragment inicioFragment = new InicioFragment();
    MapaFragment mapaFragment = new MapaFragment();
    NotificacionesFragment notificacionesFragment = new NotificacionesFragment();
    PerfilFragment perfilFragment = new PerfilFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        loadFragment(inicioFragment);

        navigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int itemId = item.getItemId();

                if (itemId == R.id.inicioFragment) {
                    loadFragment(inicioFragment);
                    return true;
                } else if (itemId == R.id.mapaFragment) {
                    loadFragment(mapaFragment);
                    return true;
                } else if (itemId == R.id.notificacionesFragment) {
                    loadFragment(notificacionesFragment);
                    return true;
                } else if (itemId == R.id.perfilFragment) {
                    loadFragment(perfilFragment);
                    return true;
                }

                return false;
            }
        });
    }
/*
    @Override
    public void onBackPressed() {
        // Obtén el FragmentManager
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Verifica si hay fragmentos en la pila de retroceso
        if (fragmentManager.getBackStackEntryCount() > 0) {
            // Si hay fragmentos en la pila, realiza un pop del último fragmento agregado
            fragmentManager.popBackStack();
        } else {
            super.onBackPressed();
        }
    }
*/
    public void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_conteiner, fragment);
        transaction.commitNow();
    }
}