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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatPriv extends AppCompatActivity implements View.OnClickListener {


    Toolbar toolbar;
    RecyclerView rcChat;
    ImageView imgUser;
    TextView txtNombre, txtEstado;
    EditText txtMensaje;
    ImageButton btnImgEnviar;

    CollectionReference refVisto;

    ValueEventListener valueEventListener;
    List<ModelChat> mChatList;
    AdapterChat adapterChat;

    FirebaseAuth FAuth;
    FirebaseUser FUser;
    FirebaseFirestore fdb;

    String idAmigo, tipoAmigo, myUID, rutaUser, suIMG;

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
        fdb = FirebaseFirestore.getInstance();

        mChatList = new ArrayList<>();

        refVisto = fdb.collection("Mensajes");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        //rcChat.setHasFixedSize(true);
        rcChat.setLayoutManager(linearLayoutManager);


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


        leerMensajes();
        cargarDatosAmigo();
        vistoMensajes();

        btnImgEnviar.setOnClickListener(this);
    }

    private void vistoMensajes() {

        refVisto.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.i("Listen failed.", "" + e);
                    return;
                }
                for (DocumentChange dc : snapshot.getDocumentChanges()) {
                    DocumentSnapshot docS = dc.getDocument();

                    ModelChat modelChat = docS.toObject(ModelChat.class);
                    int index = -1;
                    switch (dc.getType()) {
                        case ADDED:
                            mChatList.clear();
                            if (modelChat.getReceptor().equals(myUID) && modelChat.getEmisor().equals(idAmigo)){
                                HashMap<String, Object> hasVisto  = new HashMap<>();
                                hasVisto.put("visto",true);

                            }

                            adapterChat = new AdapterChat(ChatPriv.this, mChatList,suIMG);
                            adapterChat.notifyDataSetChanged();
                            rcChat.setAdapter(adapterChat);

                            break;
                        case MODIFIED:

                            break;
                        case REMOVED:


                            break;
                    }
                }

            }
        });

    }

    private void leerMensajes() {
        mChatList = new ArrayList<>();

        CollectionReference ref = fdb.collection("Mensajes");
        ref.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.i("Listen failed.", "" + e);
                    return;
                }

                for (DocumentChange dc : snapshot.getDocumentChanges()) {
                    DocumentSnapshot docS = dc.getDocument();

                    ModelChat modelChat = docS.toObject(ModelChat.class);
                    int index = -1;
                    switch (dc.getType()) {
                        case ADDED:
                            //mChatList.clear();
                            if (modelChat.receptor.equals(myUID) && modelChat.emisor.equals(idAmigo) ||
                                    modelChat.receptor.equals(idAmigo) && modelChat.emisor.equals(myUID)){
                                mChatList.add(new ModelChat(docS.getId(),docS.getString("mensaje"),docS.getString("emisor"),docS.getString("receptor"), docS.getString("timestamp"), docS.getBoolean("visto")));

                            }

                            adapterChat = new AdapterChat(ChatPriv.this, mChatList,suIMG);
                            rcChat.setAdapter(adapterChat);
                            adapterChat.notifyDataSetChanged();


                            break;
                        case MODIFIED:

                            break;
                        case REMOVED:


                            break;
                    }
                }
            }
        });

    }

    private void cargarDatosAmigo() {

        DocumentReference docRed = fdb.collection(rutaUser).document(idAmigo);
        docRed.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot docS = task.getResult();
                String nombreC, url_thum;
                nombreC = docS.getString("nombres") + " " + docS.getString("apellidos");
                suIMG = docS.getString("url_thumb_pic");


                if (!suIMG.equals("defaultPicUser") && !suIMG.equals("defaultPicProf")) {
                    try {
                        Glide.with(ChatPriv.this)
                                .load(suIMG)
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
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnEnviar:
                String mensaje = txtMensaje.getText().toString().trim();
                txtMensaje.setText("");

                if (TextUtils.isEmpty(mensaje)) {
                    Toast.makeText(this, "No puede enviar un mensaje vacío", Toast.LENGTH_SHORT).show();
                } else {
                    enviarMensaje(mensaje);
                }
                break;
        }
    }

    private void enviarMensaje(String mensaje) {
        String timestamp = String.valueOf(System.currentTimeMillis());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("emisor", myUID);
        hashMap.put("receptor", idAmigo);
        hashMap.put("mensaje", mensaje);
        hashMap.put("timestamp", timestamp);
        hashMap.put("visto", false);

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
