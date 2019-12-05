package com.rd.dmmr.tutosearchprofesores;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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
import java.util.Calendar;
import java.util.List;

public class DetallesTutorias extends AppCompatActivity implements View.OnClickListener {

    Bundle datosTuto;

    private FirebaseAuth FAuth;
    DatabaseReference DBRefence;

    private FirebaseFirestore fdb;

    private CardView btnAsistir;

    private ImageView imgDetalleTuto;

    private TextView fecha, hora, lugar, titulo, descripcion, txtMateria, textListado;

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

        imgDetalleTuto = (ImageView) findViewById(R.id.imgDetalleTuto);

        fecha = (TextView) findViewById(R.id.textFecha);
        hora = (TextView) findViewById(R.id.textHora);
        lugar = (TextView) findViewById(R.id.textLugar);
        titulo = (TextView) findViewById(R.id.textTitulo);
        txtMateria = (TextView) findViewById(R.id.txtMateria);
        descripcion = (TextView) findViewById(R.id.textDescripcion);

        textListado = (TextView) findViewById(R.id.textLista);

        btnAsistir = (CardView) findViewById(R.id.btnAsistir);

        RCListadoEstudiantes = (RecyclerView) findViewById(R.id.rcListaAsistir);

        mListListado = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(DetallesTutorias.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        RCListadoEstudiantes.setLayoutManager(layoutManager);

        adapterListado = new AdapterListado(mListListado);

        RCListadoEstudiantes.setAdapter(adapterListado);

        adapterListado.notifyDataSetChanged();

        Intent intent = getIntent();

        datosTuto = intent.getExtras();
        if (datosTuto != null) {
            String urlImg = datosTuto.getString("imgTuto");
            idTuto = datosTuto.getString("idTuto");

            Calendar calInicial = Calendar.getInstance();
            Calendar calFinal = Calendar.getInstance();



            calInicial.setTimeInMillis(Long.parseLong(datosTuto.getString("timestampI")));
            calFinal.setTimeInMillis(Long.parseLong(datosTuto.getString("timestampF")));

            String fechaCal =  calInicial.get(Calendar.DAY_OF_MONTH)+"/"+(calInicial.get(Calendar.MONTH)+1)+"/"+calInicial.get(Calendar.YEAR);
            String horaCal = "";

            titulo.setText(datosTuto.getString("Titulo"));
            txtMateria.setText(datosTuto.getString("Materia"));
            descripcion.setText(datosTuto.getString("Descripcion"));
            fecha.setText("Fecha: "+fechaCal);
            lugar.setText("Lugar: "+datosTuto.getString("Lugar"));


            if (urlImg.equals("defaultPresencial")){
                calFinal.setTimeInMillis(Long.parseLong(datosTuto.getString("timestampF")));
                horaCal = calInicial.get(Calendar.HOUR)+":"+calInicial.get(Calendar.MINUTE)+" - " + calFinal.get(Calendar.HOUR)+":"+ calFinal.get(Calendar.MINUTE);
                hora.setText("Hora: "+horaCal);

                imgDetalleTuto.setImageResource(R.mipmap.presencial_background);
            } else if(urlImg.equals("defaultLive")){
                horaCal = calInicial.get(Calendar.HOUR)+":"+calInicial.get(Calendar.MINUTE);
                hora.setText("Hora: "+horaCal);
                imgDetalleTuto.setImageResource(R.drawable.video_camera_live);

            } else {
                try {
                    Glide.with(this)
                            .load(urlImg)
                            .fitCenter()
                            .centerCrop()
                            .into(imgDetalleTuto);
                } catch (Exception e) {
                    Toast.makeText(this, "Ha ocurrido un error al cargar la imagen", Toast.LENGTH_SHORT).show();

                }
            }


            btnAsistir.setOnClickListener(this);
            CargarEstudiantes();

        }
    }

    private void CargarEstudiantes() {

        Log.i("ProbandoListado", idTuto);
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

                            Log.i("Probando", "" +  modelListado.getTimestamp());



                            mListListado.add(new ModelListado(docS.getId(), modelListado.getTimestamp()));

                            adapterListado.notifyDataSetChanged();
                            textListado.setText("Lista de estudiantes: "+mListListado.size());

                            break;
                        case MODIFIED:


                            idEstduante = docS.getId();

                            Log.i("Probando", "" + docS);

                            index = getRCIndex(idEstduante);

                                mListListado.set(index, new ModelListado(modelListado.getIdEstudiante(), modelListado.getTimestamp()));


                            adapterListado.notifyItemChanged(index);
                            textListado.setText("Lista de estudiantes: "+mListListado.size());
                            break;
                        case REMOVED:


                            index = getRCIndex(docS.getId());

                            mListListado.remove(index);
                            adapterListado.notifyItemRemoved(index);
                            textListado.setText("Lista de estudiantes: "+mListListado.size());
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
