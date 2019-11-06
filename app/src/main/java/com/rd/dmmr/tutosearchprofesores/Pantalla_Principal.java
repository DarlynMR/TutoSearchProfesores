package com.rd.dmmr.tutosearchprofesores;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.rd.dmmr.tutosearchprofesores.R.id.nav_txtCorreoProfMenu;
import static com.rd.dmmr.tutosearchprofesores.R.id.nav_txtNombreProfMenu;


public class Pantalla_Principal extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    CardView crdAgregarTutoria, crdTutoriasAceptadas,crdMensajes;

    TextView txtNombreProf, txtCorreoProf;

    FirebaseAuth FAuth;
    FirebaseUser FUser;
    DatabaseReference UserReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla__principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();



        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        FAuth= FirebaseAuth.getInstance();
        FUser= FAuth.getCurrentUser();
        UserReference= FirebaseDatabase.getInstance().getReference().child("usuarios").child("profesores").child(FUser.getUid());


        crdAgregarTutoria= (CardView)findViewById(R.id.crdAgregarTutoria);
        crdTutoriasAceptadas= (CardView)findViewById(R.id.card_tutorias_reservadas);
        crdMensajes= (CardView)findViewById(R.id.card_mensajes);


        crdAgregarTutoria.setOnClickListener(this);
        crdTutoriasAceptadas.setOnClickListener(this);
        crdMensajes.setOnClickListener(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.pantalla__principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onStart() {
        try {
            FUser = FAuth.getCurrentUser();
            super.onStart();
            if (FUser==null){
                VolverInicio();
            }else{
                UserReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        String nombresProf, apellidosProf, correoProf;

                        nombresProf = dataSnapshot.child("nombres").getValue(String.class);
                        apellidosProf = dataSnapshot.child("apellidos").getValue(String.class);
                        correoProf = dataSnapshot.child("correo").getValue(String.class);

                        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                        View headerview = navigationView.getHeaderView(0);

                        TextView txt_nombreprofNav =(TextView) headerview.findViewById(nav_txtNombreProfMenu);
                        TextView txt_correoNav =(TextView) headerview.findViewById(nav_txtCorreoProfMenu);

                        txt_nombreprofNav.setText(nombresProf + " " + apellidosProf);
                        txt_correoNav.setText(correoProf);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(Pantalla_Principal.this,"Ha ocurrido un problema para obtener la información del usuario", Toast.LENGTH_SHORT).show();
                    }
                });

            }





        }catch (Exception e){
            Toast.makeText(Pantalla_Principal.this,"Ha ocurrido un error al tratar de abrir la aplicación", Toast.LENGTH_SHORT).show();
        }
    }

    private void VolverInicio() {
        try {
            Intent startintent = new Intent(Pantalla_Principal.this, LoginProf.class);
            startActivity(startintent);
            finish();
        }catch (Exception e){
            Toast.makeText(Pantalla_Principal.this,"Ha ocurrido un error al tratar de abrir la aplicación", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.compartir) {

        } else if (id == R.id.logout) {

            FAuth.signOut();
            Intent intent = new Intent(Pantalla_Principal.this, LoginProf.class);
            Pantalla_Principal.this.startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View view) {
        if (view==crdAgregarTutoria){
                    Intent intent = new Intent(Pantalla_Principal.this, ElegirTipoTutoria.class);
                    Pantalla_Principal.this.startActivity(intent);
        } else if (view==crdTutoriasAceptadas){
            Intent intent = new Intent(Pantalla_Principal.this, TransmisionActivity.class);
            Pantalla_Principal.this.startActivity(intent);
        }else if (view==crdMensajes){
            Intent intent = new Intent(Pantalla_Principal.this, TransmisionActivity2.class);
            Pantalla_Principal.this.startActivity(intent);
        }
    }
}
