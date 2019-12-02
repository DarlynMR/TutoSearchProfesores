package com.rd.dmmr.tutosearchprofesores;

import android.content.Intent;
import android.graphics.ColorSpace;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DetallesTutorias extends AppCompatActivity implements View.OnClickListener {

    Bundle datosTuto;

    private FirebaseAuth FAuth;
    DatabaseReference DBRefence;

    private FirebaseFirestore fdb;

    private CardView btnAsistir;

    private TextView fecha, hora, lugar, titulo, descripcion;

    private String idTuto, idEstduante, tutoes;

    //Lo necesario para recibir el listado de estudiantes
    private RecyclerView RCListadoEstudiantes;
    private AdapterListado adapterListado;
    private List<ModelListado> mListListado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_tutorias);


        fdb = FirebaseFirestore.getInstance();
        FAuth = FirebaseAuth.getInstance();

        fecha = (TextView) findViewById(R.id.textFecha);
        hora = (TextView) findViewById(R.id.textHora);
        lugar = (TextView) findViewById(R.id.textLugar);
        titulo = (TextView) findViewById(R.id.textTitulo);
        descripcion = (TextView) findViewById(R.id.textDescripcion);

        btnAsistir = (CardView) findViewById(R.id.btnAsistir);

        RCListadoEstudiantes = (RecyclerView) findViewById(R.id.rcListaAsistir);

        mListListado = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(DetallesTutorias.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        RCListadoEstudiantes.setLayoutManager(layoutManager);

        adapterListado = new AdapterListado(mListListado);

        RCListadoEstudiantes.setAdapter(adapterListado);
        CargarEstudiantes();
        adapterListado.notifyDataSetChanged();

        Intent intent = getIntent();

        datosTuto = intent.getExtras();
        if (datosTuto != null) {
            idTuto = datosTuto.getString("idTuto");
            Log.i("Prueba", idTuto);

            fecha.setText(datosTuto.getString("Fecha"));
            hora.setText(datosTuto.getString("Hora"));
            lugar.setText(datosTuto.getString("Lugar"));
            titulo.setText(datosTuto.getString("Titulo"));
            descripcion.setText(datosTuto.getString("Descripcion"));
            Log.i("ProbandoAsistir", "" + datosTuto.getString("TipoEs"));
        }


        btnAsistir.setOnClickListener(this);
        CargarEstudiantes();


    }

    private void CargarEstudiantes() {

        CollectionReference ref = fdb.collection("Tutorias_institucionales").document(idTuto).collection("Lista_asistir");
        ref.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.i("Listen failed.", "" + e);
                    return;
                }

                for (DocumentChange dc : snapshot.getDocumentChanges()) {
                    DocumentSnapshot docS = dc.getDocument();
                    ModelListado modelListado = docS.toObject(ModelListado.class);

                    int index = -1;
                    switch (dc.getType()) {
                        case ADDED:


                            idEstduante = docS.getId();

                            Log.i("Probando", "" + docS);


                            mListListado.add(new ModelListado(modelListado.getIdEstudiante(), modelListado.getTimestamp()));

                            adapterListado.notifyDataSetChanged();

                            break;
                        case MODIFIED:


                            idEstduante = docS.getId();

                            Log.i("Probando", "" + docS);

                            index = getRCIndex(idEstduante);

                                mListListado.set(index, new ModelListado(modelListado.getIdEstudiante(), modelListado.getTimestamp()));


                            adapterListado.notifyItemChanged(index);
                            break;
                        case REMOVED:


                            index = getRCIndex(docS.getId());

                            mListListado.remove(index);
                            adapterListado.notifyItemRemoved(index);

                            break;
                    }
                }

            }
        });
    }
/*
    public void Asistir(){
        //try {

        Intent intent= getIntent();

        datosTuto=intent.getExtras();
        Log.i("ProbandoAsistir",datosTuto.getString("TipoEs"));

        FirebaseUser user= FAuth.getCurrentUser();
        DBRefence= FirebaseDatabase.getInstance().getReference().child("tutorias_asistir").child(datosTuto.getString("TipoEs")).child(idTuto);

        final SharedPreferences pref = getSharedPreferences("EstudiantePref", Context.MODE_PRIVATE);

        HashMap<String,String> hashMap= new HashMap<>();

        hashMap.put(user.getUid(),user.getUid());



        DBRefence.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Mtoast("Ha marcado que asistirá a esta tutoría");
                }else {
                    Mtoast("Ocurrió un error, por favor intente de nuevo");
                }
            }
        });
        /*
        } catch (Exception e){
            Mtoast("Ha ocurrido un error al realizar la operación");
            Alerta("Error", ""+e.toString());

    }
    */

    private int getRCIndex(String iEstudiante) {

        int index = -1;
        for (int i = 0; i < mListListado.size(); i++) {
            if (mListListado.get(i).idEstudiante.equals(iEstudiante)) {

                index = i;
                break;
            }
        }

        return index;

    }

    public void Alerta(String Titulo, String Mensaje) {
        AlertDialog alertDialog;
        alertDialog = new AlertDialog.Builder(DetallesTutorias.this).setNegativeButton("Ok", null).create();
        alertDialog.setTitle(Titulo);
        alertDialog.setMessage(Mensaje);
        alertDialog.show();

    }

    public void Mtoast(String mensaje) {

        Toast toast = Toast.makeText(DetallesTutorias.this, mensaje, Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    public void onClick(View view) {

        /*
        if (view==btnAsistir){

        }*/
    }
}
