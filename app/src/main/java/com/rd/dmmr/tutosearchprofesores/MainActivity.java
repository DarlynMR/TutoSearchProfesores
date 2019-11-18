package com.rd.dmmr.tutosearchprofesores;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {


    FirebaseAuth FAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FAuth= FirebaseAuth.getInstance();
        FirebaseUser user= FAuth.getCurrentUser();


        if(user==null){
            Intent intent = new Intent(MainActivity.this, LoginProf.class);
            MainActivity.this.startActivity(intent);
        }else{
            Intent intent = new Intent(MainActivity.this, Pantalla_Principal.class);
            MainActivity.this.startActivity(intent);
        }


    }


}
