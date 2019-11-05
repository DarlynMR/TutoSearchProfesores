package com.rd.dmmr.tutosearchprofesores;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ElegirTipoTutoria extends AppCompatActivity implements View.OnClickListener {

    CardView crdPresencial, crdTransmisionLive;

    FloatingActionButton fabBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elegir_tipo_tutoria);


        crdPresencial = (CardView) findViewById(R.id.crdTutoriaPresencial);
        crdTransmisionLive = (CardView) findViewById(R.id.crdTutoriaLive);
        fabBack = (FloatingActionButton) findViewById(R.id.fabBackButton);


        crdPresencial.setOnClickListener(this);
        crdTransmisionLive.setOnClickListener(this);
        fabBack.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if(view==crdPresencial){
            Intent intent = new Intent(ElegirTipoTutoria.this, AgregarTutoria.class);
            ElegirTipoTutoria.this.startActivity(intent);
        }
        if (view==crdTransmisionLive){
            Intent intent = new Intent(ElegirTipoTutoria.this, AgregarTutoriaLive.class);
            ElegirTipoTutoria.this.startActivity(intent);
        }
        if (view==fabBack){
            onBackPressed();
        }

    }
}
