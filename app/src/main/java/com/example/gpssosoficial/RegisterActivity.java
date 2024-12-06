package com.example.gpssosoficial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class RegisterActivity extends AppCompatActivity {

    private Button regresar, registrar;
    private EditText nombre, apellido, celular, email, pass, passConfirm;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        nombre = findViewById(R.id.etxtnombre);
        apellido = findViewById(R.id.etxtapellido);
        celular = findViewById(R.id.etxtcelular);
        email = findViewById(R.id.etxtemail);
        pass = findViewById(R.id.etxtpassword);
        passConfirm = findViewById(R.id.etxtpasswordconfirm);

        regresar = findViewById(R.id.btnRegresar);
        registrar = findViewById(R.id.btnRegistrar);

        //Ir a iniciar sesion
        regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regresar();
            }
        });
        
        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String passUser = pass.getText().toString().trim();
                String confirmPass = passConfirm.getText().toString().trim();
                String emailUser = email.getText().toString().trim();
                String nombreUser = nombre.getText().toString().trim();
                String apellidoUser = apellido.getText().toString().trim();
                String celUser = celular.getText().toString().trim();

                if (emailUser.equals("") || nombreUser.equals("") || apellidoUser.equals("") || celUser.equals("")) {
                    Toast.makeText(RegisterActivity.this, "Verifica que los datos esten completos", Toast.LENGTH_SHORT).show();
                } else if (passUser.equals("") && confirmPass.equals("") || !passUser.equals(confirmPass)) {
                    Toast.makeText(RegisterActivity.this, "Las contraseñas no coinciden o están vacías", Toast.LENGTH_SHORT).show();
                } else if (celUser.length() != 10) {
                    Toast.makeText(RegisterActivity.this, "El numero de telefono debe de ser 10 digitos", Toast.LENGTH_SHORT).show();
                } else {
                    RegistrarUser(nombreUser, apellidoUser, celUser, emailUser, passUser);
                }
            }

        });
    }

    private void RegistrarUser(String nombreUser, String apellidoUser,
                               String celUser, String emailUser, String passUser) {

        mAuth.createUserWithEmailAndPassword(emailUser,passUser).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                FirebaseUser user = mAuth.getCurrentUser();
                String userId = user.getUid();

                // Generar un número aleatorio de 9 dígitos
                Random random = new Random();
                String numeroAleatorio = String.valueOf(random.nextInt(900000000) + 100000000);
                Timestamp fecha = Timestamp.now();



                Map<String, Object> map = new HashMap<>();
                map.put("id", userId);
                map.put("identificacion", numeroAleatorio);
                map.put("fechaRegistro", fecha);
                map.put("nombre", nombreUser);
                map.put("apellido", apellidoUser);
                map.put("celular", celUser);
                map.put("email", emailUser);
                map.put("password", passUser);

                mFirestore.collection("user").document(userId).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        finish();
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        Toast.makeText(RegisterActivity.this, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(RegisterActivity.this, "No se guardaron los datos", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this, "ya esta registrado", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void regresar() {
        finish();
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}