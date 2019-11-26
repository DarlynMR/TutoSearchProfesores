package com.rd.dmmr.tutosearchprofesores;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class ChatPriv extends AppCompatActivity implements View.OnClickListener {


    Toolbar toolbar;
    RecyclerView rcChat;
    ImageView imgUser;
    TextView txtNombre, txtEstado;
    EditText txtMensaje;
    ImageButton btnImgEnviar;

    FirebaseAuth FAuth;
    FirebaseUser FUser;
    FirebaseFirestore fdb;

    String idAmigo, tipoAmigo, myUID, rutaUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_priv);

        Toolbar toolbar = findViewById(R.id.toolbarChatPriv);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");

        rcChat = (RecyclerView) findViewById(R.id.RCChatPriv);
        imgUser = (ImageView) findViewById(R.id.imgCircularChat);
        txtNombre = (TextView) findViewById(R.id.txtNombreUsiarioChat);
        txtEstado = (TextView) findViewById(R.id.estadoUserChat);
        txtMensaje = (EditText) findViewById(R.id.txtMensajeEnviar);
        btnImgEnviar = (ImageButton) findViewById(R.id.btnEnviar);

        FAuth = FirebaseAuth.getInstance();
        FUser = FAuth.getCurrentUser();

        fdb = FirebaseFirestore.getInstance();

        Intent intent = getIntent();

        idAmigo = intent.getStringExtra("idAmigo");
        tipoAmigo = intent.getStringExtra("tipoUser");

        if (tipoAmigo.equals("Profesor")) {
            rutaUser = "Profesores";
        } else if (tipoAmigo.equals("Estudiante")) {
            rutaUser = "Estudiantes";
        }

        cargarDatosAmigo();

        btnImgEnviar.setOnClickListener(this);
    }

    private void cargarDatosAmigo() {

        DocumentReference docRed = fdb.collection(rutaUser).document(idAmigo);
        docRed.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot docS = task.getResult();
                String nombreC, url_thum;
                nombreC = docS.getString("nombres") + " " + docS.getString("apellidos");
                url_thum = docS.getString("url_thumb_pic");



                if (!url_thum.equals("defaultPicUser") && !url_thum.equals("defaultPicProf")){
                    try {
                        Glide.with(ChatPriv.this)
                                .load(url_thum)
                                .fitCenter()
                                .centerCrop()
                                .into(imgUser);

                    } catch (Exception e) {
                        Log.i("ErrorImg", "" + e.getMessage());
                        imgUser.setImageResource(R.drawable.imageprofile);
                    }
                }

                txtNombre.setText(nombreC);


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ChatPriv.this, "Ocurrió un error al cargar los datos del usuario", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void verifEstadoUser() {


        if (FUser != null) {

            myUID = FUser.getUid();


        } else {
            startActivity(new Intent(ChatPriv.this, LoginProf.class));
            finish();
        }


    }

    @Override
    protected void onStart() {
        verifEstadoUser();
        super.onStart();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnEnviar:
                String mensaje = txtMensaje.getText().toString().trim();
                txtMensaje.setText("");

                if (TextUtils.isEmpty(mensaje)){
                    Toast.makeText(this, "No puede enviar un mensaje vacío", Toast.LENGTH_SHORT).show();
                }else {
                    enviarMensaje(mensaje);
                }
                break;
        }
    }

    private void enviarMensaje(String mensaje) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("emisor", myUID);
        hashMap.put("receptor", idAmigo);
        hashMap.put("mensaje", mensaje);

        String keyid = fdb.collection("Mensajes").document().getId();

        fdb.collection("Mensajes").document(keyid)
                .set(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ChatPriv.this, "No se pudo enviar el mensaje", Toast.LENGTH_SHORT).show();
            }
        });




    }
}
