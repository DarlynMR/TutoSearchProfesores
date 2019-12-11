package com.rd.dmmr.tutosearchprofesores;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
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
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.rd.dmmr.tutosearchprofesores.notificaciones.APIService;
import com.rd.dmmr.tutosearchprofesores.notificaciones.Client;
import com.rd.dmmr.tutosearchprofesores.notificaciones.Data;
import com.rd.dmmr.tutosearchprofesores.notificaciones.Response;
import com.rd.dmmr.tutosearchprofesores.notificaciones.Sender;
import com.rd.dmmr.tutosearchprofesores.notificaciones.Token;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;

public class ChatPriv extends AppCompatActivity implements View.OnClickListener {

    Bundle datosAmigo;

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

    APIService apiService;
    boolean notify = false;

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

        FAuth = FirebaseAuth.getInstance();
        FUser = FAuth.getCurrentUser();
        myUID= FUser.getUid();
        fdb = FirebaseFirestore.getInstance();

        mChatList = new ArrayList<>();

        refVisto = fdb.collection("Mensajes");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setStackFromEnd(true);
        //linearLayoutManager.setReverseLayout(true);
        //linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcChat.setHasFixedSize(true);
        rcChat.setLayoutManager(linearLayoutManager);

        apiService = Client.getRetrofit("https://fcm.googleapis.com/").create(APIService.class);


        Intent intent = getIntent();

        datosAmigo = intent.getExtras();

        if (datosAmigo!=null){
            Log.i("Notificaciones", datosAmigo.getString("tipoUser"));
        }


        idAmigo = intent.getStringExtra("idAmigo");
        tipoAmigo = intent.getStringExtra("tipoUser");

        if (datosAmigo!=null){
            Log.i("Notificaciones", "Via bundle"+datosAmigo.getString("tipoUser"));
            Log.i("NotificacionesIntent","Via intent"+ tipoAmigo);
        }

        if (tipoAmigo.equals("Profesor") || tipoAmigo.equals("Profesores")) {
            rutaUser = "Profesores";
        } else if (tipoAmigo.equals("Estudiante") || tipoAmigo.equals("Estudiantes")) {
            rutaUser = "Estudiantes";
        }

        txtMensaje.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length()==0){
                    verifEscrib("ninguno");

                }else {
                    verifEscrib(idAmigo);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        cargarDatosAmigo();
        leerMensajes();
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
                            if (modelChat.getReceptor().equals(myUID) && modelChat.getEmisor().equals(idAmigo)){
                                HashMap<String, Object> hasVisto  = new HashMap<>();
                                hasVisto.put("visto",true);

                            }

                            adapterChat = new AdapterChat(ChatPriv.this, mChatList);
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

        Query ref = fdb.collection("Mensajes").orderBy("timestamp", Query.Direction.ASCENDING);
        ref.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.i("Listen failed.", "" + e);
                    return;
                }

                for (DocumentChange dc : snapshot.getDocumentChanges()) {
                    final DocumentSnapshot docS = dc.getDocument();

                    final ModelChat modelChat = docS.toObject(ModelChat.class);
                    int index = -1;
                    switch (dc.getType()) {
                        case ADDED:
                            //mChatList.clear();

                            Handler handler = new Handler();

                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    if (modelChat.receptor.equals(myUID) && modelChat.emisor.equals(idAmigo) ||
                                            modelChat.receptor.equals(idAmigo) && modelChat.emisor.equals(myUID)){
                                        mChatList.add(new ModelChat(docS.getId(),docS.getString("mensaje"),docS.getString("emisor"),docS.getString("receptor"), docS.getString("timestamp"), docS.getBoolean("visto")));
                                        Log.i("ProbandoPrincipal", "Tamaño: "+ mChatList.size());
                                    }

                                    adapterChat = new AdapterChat(ChatPriv.this, mChatList);
                                    rcChat.setAdapter(adapterChat);
                                    adapterChat.notifyDataSetChanged();


                                }
                            },500);




                            break;
                        case MODIFIED:

                            index = getRCIndex(docS.getId());

                            if (modelChat.receptor.equals(myUID) && modelChat.emisor.equals(idAmigo) ||
                                    modelChat.receptor.equals(idAmigo) && modelChat.emisor.equals(myUID)){
                                mChatList.set(index, new ModelChat(docS.getId(),docS.getString("mensaje"),docS.getString("emisor"),docS.getString("receptor"), docS.getString("timestamp"), docS.getBoolean("visto")));
                                Log.i("ProbandoPrincipal", "Tamaño: "+ mChatList.size());
                            }

                            adapterChat = new AdapterChat(ChatPriv.this, mChatList);
                            rcChat.setAdapter(adapterChat);
                            adapterChat.notifyDataSetChanged();
                            break;

                        case REMOVED:

                            index = getRCIndex(docS.getId());
                            mChatList.remove(index);
                            adapterChat.notifyItemRemoved(index);

                            break;
                    }
                }
            }
        });

    }

    private void enviarMensaje(final String mensaje) {
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


        final DocumentReference docRef = fdb.collection("Profesores").document(myUID);

        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                if (notify){
                    final SharedPreferences pref = getSharedPreferences("ProfPref", Context.MODE_PRIVATE);

                    String myName = pref.getString("nombresProf", "Usuario")+" "+pref.getString("apellidosProf", "");

                    sendNotification(idAmigo, myName.trim(), mensaje);
                }
                notify =false;



            }
        });


        DocumentReference docHis  = fdb.collection("ListChat").document(myUID).collection("lista").document(idAmigo);
        docHis.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot docS = task.getResult();

                if (!docS.exists()){
                    HashMap<String, Object> hash1 = new HashMap<>();
                    hash1.put("timestamp", String.valueOf(System.currentTimeMillis()));
                    hash1.put("tipoUser", rutaUser);

                    docHis.set(hash1);
                }

            }
        });

        DocumentReference docMy  = fdb.collection("ListChat").document(idAmigo).collection("lista").document(myUID);
        docMy.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot docS = task.getResult();

                if (!docS.exists()){
                    HashMap<String, Object> hash2 = new HashMap<>();
                    hash2.put("timestamp", String.valueOf(System.currentTimeMillis()));
                    hash2.put("tipoUser", "Profesores");

                    docMy.set(hash2);
                }

            }
        });
    }

    private void sendNotification(final String idAmigo, String toString, final String mensaje) {
        CollectionReference allTokens = fdb.collection("Tokens");
        Query query  = allTokens.orderBy(FieldPath.documentId()).whereEqualTo(FieldPath.documentId(), idAmigo );

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()){
                    DocumentSnapshot docS = dc.getDocument();
/*
                    DataSnapshot dataSnapshot = (DataSnapshot) docS.getD();

                    Token tokenp = new Token();
                    */
                    Token token = new Token();

                    token.setToken(docS.getString("token"));

                    Log.i("Token", token.getToken());

                    Data data = new Data(myUID, toString+": "+mensaje,  "Nuevo mensaje", idAmigo, R.drawable.imageprofile, "Profesores");

                    Sender sender = new Sender(data, token.getToken());
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<Response>() {
                                @Override
                                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                                    Toast.makeText(ChatPriv.this, ""+response.message(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(Call<Response> call, Throwable t) {

                                }
                            });
                }



            }
        });

    }


    private void cargarDatosAmigo() {

        DocumentReference docRef = fdb.collection(rutaUser).document(idAmigo);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException FirestoreE) {


                String nombreC="", url_thum;
                if (!documentSnapshot.getString("nombres").equals(nombreC)){
                    nombreC = documentSnapshot.getString("nombres") + " " + documentSnapshot.getString("apellidos");
                    txtNombre.setText(nombreC);
                }
                if (!documentSnapshot.getString("url_thumb_pic").equals(suIMG)){
                    suIMG = documentSnapshot.getString("url_thumb_pic");

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
                }

                String estadoOnline = String.valueOf(documentSnapshot.getString("estadoOnline"));
                String estadoEscrib = String.valueOf(documentSnapshot.getString("escribiendoA"));

                if (estadoEscrib.equals(myUID)) {
                    txtEstado.setText("Escribiendo...");
                }else {
                    if (estadoOnline.equals("En linea")) {
                        txtEstado.setText(estadoOnline);
                    }else {
                        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                        cal.setTimeInMillis(Long.parseLong(estadoOnline));
                        String datetime= DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();
                        txtEstado.setText(datetime);

                    }
                }




            }
        });

        /*
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

        */
    }

    private void verifOnline(String estado){
        DocumentReference docRef = fdb.collection("Profesores").document(FUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("estadoOnline", estado);

        docRef.update(hashMap);
    }

    private void verifEscrib(String escrib){
        DocumentReference docRef = fdb.collection("Profesores").document(myUID);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("escribiendoA", escrib);

        docRef.update(hashMap);
    }

    private void verifEstadoUser() {


        if (FUser != null) {

            myUID = FUser.getUid();


        } else {
            startActivity(new Intent(ChatPriv.this, LoginProf.class));
            finish();
        }


    }

    private int getRCIndex(String iMensaje) {

        int index = -1;
        for (int i = 0; i < mChatList.size(); i++) {
            if (mChatList.get(i).idMensaje.equals(iMensaje)) {

                index = i;
                break;
            }
        }

        return index;

    }

    @Override
    protected void onStart() {
        verifEstadoUser();
        verifOnline("En linea");
        super.onStart();
    }

    @Override
    protected void onPause() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        verifOnline(timestamp);
        verifEscrib("ninguno");
        super.onPause();
    }


    @Override
    protected void onResume() {
        verifOnline("En linea");
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnEnviar:
                notify = true;
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


}
