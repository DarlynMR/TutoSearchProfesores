package com.rd.dmmr.tutosearchprofesores;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.rd.dmmr.tutosearchprofesores.R.id.nav_imagePorf;
import static com.rd.dmmr.tutosearchprofesores.R.id.nav_txtCorreoProfMenu;
import static com.rd.dmmr.tutosearchprofesores.R.id.nav_txtNombreProfMenu;


public class Pantalla_Principal extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    CardView crdAgregarTutoria, crdTutoriasAceptadas,crdMensajes, crdTutoriasAgregadas;

    TextView txtNombreProf, txtCorreoProf;

    FirebaseAuth FAuth;
    FirebaseUser FUser;
    DatabaseReference UserReference;

    FirebaseFirestore fdb;

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

        fdb = FirebaseFirestore.getInstance();


        crdAgregarTutoria= (CardView)findViewById(R.id.crdAgregarTutoria);
        crdTutoriasAceptadas= (CardView)findViewById(R.id.card_tutorias_reservadas);
        crdMensajes= (CardView)findViewById(R.id.card_mensajes);
        crdTutoriasAgregadas = (CardView)findViewById(R.id.card_tutorias_inst);


        crdAgregarTutoria.setOnClickListener(this);
        crdTutoriasAceptadas.setOnClickListener(this);
        crdMensajes.setOnClickListener(this);
        crdTutoriasAgregadas.setOnClickListener(this);

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

                /*
                UserReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        String nombresProf, apellidosProf, correoProf, url_pic;

                        nombresProf = dataSnapshot.child("nombres").getValue(String.class);
                        apellidosProf = dataSnapshot.child("apellidos").getValue(String.class);
                        correoProf = dataSnapshot.child("correo").getValue(String.class);
                        url_pic= dataSnapshot.child("url_pic").getValue(String.class);

                        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                        View headerview = navigationView.getHeaderView(0);

                        TextView txt_nombreprofNav =(TextView) headerview.findViewById(nav_txtNombreProfMenu);
                        TextView txt_correoNav =(TextView) headerview.findViewById(nav_txtCorreoProfMenu);
                        ImageView img_navProf = (ImageView) headerview.findViewById(nav_imagePorf);

                        txt_nombreprofNav.setText(nombresProf + " " + apellidosProf);
                        txt_correoNav.setText(correoProf);

                        if (url_pic.equals("defaultPicProf")){
                            img_navProf.setImageResource(R.mipmap.default_profile_prof);
                        } else {
                            try {
                                Glide.with(Pantalla_Principal.this)
                                        .load(url_pic)
                                        .fitCenter()
                                        .centerCrop()
                                        .into(img_navProf);

                            }catch (Exception e){
                                Log.i("Error", ""+e.getMessage());
                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(Pantalla_Principal.this,"Ha ocurrido un problema para obtener la información del usuario", Toast.LENGTH_SHORT).show();
                    }
                });
                */

                DocumentReference dc = fdb.collection("Profesores").document(FUser.getUid());
                dc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        String nombresProf, apellidosProf, correoProf, url_pic;

                        DocumentSnapshot docS = task.getResult();

                        nombresProf = docS.getString("nombres");
                        apellidosProf = docS.getString("apellidos");
                        correoProf = docS.getString("correo");
                        url_pic= docS.getString("url_pic");

                        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                        View headerview = navigationView.getHeaderView(0);

                        TextView txt_nombreprofNav =(TextView) headerview.findViewById(nav_txtNombreProfMenu);
                        TextView txt_correoNav =(TextView) headerview.findViewById(nav_txtCorreoProfMenu);
                        ImageView img_navProf = (ImageView) headerview.findViewById(nav_imagePorf);

                        txt_nombreprofNav.setText(nombresProf + " " + apellidosProf);
                        txt_correoNav.setText(correoProf);

                        if (url_pic.equals("defaultPicProf")){
                            img_navProf.setImageResource(R.mipmap.default_profile_prof);
                        } else {
                            try {
                                Glide.with(Pantalla_Principal.this)
                                        .load(url_pic)
                                        .fitCenter()
                                        .centerCrop()
                                        .into(img_navProf);

                            }catch (Exception e){
                                Log.i("Error", ""+e.getMessage());
                            }

                        }

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
        }
        if (view==crdTutoriasAceptadas){

        }
        if (view==crdMensajes){

        }

        if (view==crdTutoriasAgregadas){
            Intent intent = new Intent(Pantalla_Principal.this, TutoriasAgregadas.class);
            Pantalla_Principal.this.startActivity(intent);
        }


    }
}
