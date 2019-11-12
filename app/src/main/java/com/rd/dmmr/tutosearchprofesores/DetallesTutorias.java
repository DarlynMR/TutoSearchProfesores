package com.rd.dmmr.tutosearchprofesores;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class DetallesTutorias extends AppCompatActivity implements View.OnClickListener {

    Bundle datosTuto;

    FirebaseAuth FAuth;
    DatabaseReference DBRefence;

    private FirebaseFirestore fdb;

    CardView btnAsistir;

    TextView fecha,hora, lugar,titulo, descripcion;

    String idTuto, tutoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_tutorias);



        fdb = FirebaseFirestore.getInstance();
        FAuth= FirebaseAuth.getInstance();

        fecha=(TextView) findViewById(R.id.textFecha);
        hora=(TextView) findViewById(R.id.textHora);
        lugar=(TextView) findViewById(R.id.textLugar);
        titulo=(TextView) findViewById(R.id.textTitulo);
        descripcion=(TextView) findViewById(R.id.textDescripcion);

        btnAsistir= (CardView) findViewById(R.id.btnAsistir);

        Intent intent= getIntent();

        datosTuto=intent.getExtras();
        if (datosTuto!=null) {
            idTuto = datosTuto.getString("idTuto");
            Log.i("Prueba", idTuto);

            fecha.setText(datosTuto.getString("Fecha"));
            hora.setText(datosTuto.getString("Hora"));
            lugar.setText(datosTuto.getString("Lugar"));
            titulo.setText(datosTuto.getString("Titulo"));
            descripcion.setText(datosTuto.getString("Descripcion"));
            Log.i("ProbandoAsistir",""+datosTuto.getString("TipoEs"));
        }


        btnAsistir.setOnClickListener(this);




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



    public void Alerta (String Titulo, String Mensaje){
        AlertDialog alertDialog;
        alertDialog = new AlertDialog.Builder(DetallesTutorias.this).setNegativeButton("Ok",null).create();
        alertDialog.setTitle(Titulo);
        alertDialog.setMessage(Mensaje);
        alertDialog.show();

    }
    public void Mtoast(String mensaje){

        Toast toast= Toast.makeText(DetallesTutorias.this, mensaje,Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    public void onClick(View view) {

        /*
        if (view==btnAsistir){

        }*/
    }
}
