package com.rd.dmmr.tutosearchprofesores;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginProf extends AppCompatActivity {

    private FirebaseAuth FAutentic;
    private FirebaseAuth.AuthStateListener FInicionIndicdor;

    private FirebaseDatabase FDatabase;
    private DatabaseReference DBReference;

    private EditText LCorreo;
    private EditText LPassword;




    private ProgressDialog progressDialog;

    private Button JbtnEntrar, JbtnRegistrar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_prof);


        LCorreo = (EditText) findViewById(R.id.txtCorreo);
        LPassword = (EditText) findViewById(R.id.txtPassword);
        JbtnEntrar = (Button)   findViewById(R.id.btnEntrarLogin);
        JbtnRegistrar = (Button) findViewById(R.id.btnRegistroLogin);



        FDatabase= FirebaseDatabase.getInstance();
        DBReference= FDatabase.getReference();



        progressDialog = new ProgressDialog(this);

        FAutentic = FirebaseAuth.getInstance();

        FInicionIndicdor = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser usuario = firebaseAuth.getCurrentUser();


                if (usuario != null) {
                    Mtoast("Iniciado como " + firebaseAuth.getCurrentUser().getEmail());
                }
            }
        };


        JbtnEntrar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Login(LCorreo.getText().toString(),LPassword.getText().toString());
            }
        });

        JbtnRegistrar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent activityChangeIntent = new Intent(LoginProf.this, RegistrarProf.class);
                LoginProf.this.startActivity(activityChangeIntent);
            }
        });




    }



    public void Login(String Usuario,String Password){

       final SharedPreferences pref = getSharedPreferences("ProfPref", Context.MODE_PRIVATE);


        final SharedPreferences.Editor editor = pref.edit();

        if (TextUtils.isEmpty(Usuario)){
            LCorreo.setError("Campo vacío, por favor escriba el correo");
            return;
        }
        if (TextUtils.isEmpty(Password)){
            LPassword.setError("Campo vacío, por favor escriba la contraseña");
            return;
        }

        progressDialog.setMessage("Iniciando sesión...");
        progressDialog.setCancelable(false);
        progressDialog.show();




        FAutentic.signInWithEmailAndPassword(Usuario, Password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        FirebaseUser user = FAutentic.getCurrentUser();
                        assert user != null;
                        DBReference=FirebaseDatabase.getInstance().getReference().child("UCATECI").child("Profesores").child(user.getUid());

                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginProf.this, "Datos incorrectos, por favor digite otra vez sus datos", Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        } else {

                            if (!user.isEmailVerified()) {
                                Toast.makeText(LoginProf.this, "Esta cuenta aun no ha sido verificada o no existe", Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                                return;
                            }
                            if (task.isSuccessful()) {

                                DBReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        /*
                                        if (dataSnapshot==null){
                                            Alerta("Error al iniciar", "La cuenta que esta intentando acceder es de un estudiante, aqui solo se permite iniciar con cuenta de profesor");
                                            progressDialog.dismiss();

                                        }else {
                                        */
                                            editor.putString("nombreProf", dataSnapshot.child("Nombre").getValue().toString());
                                            editor.putString("Correo", dataSnapshot.child("Correo").getValue().toString());
                                            editor.putString("TelefonoProf", dataSnapshot.child("Telefono").getValue().toString());
                                            editor.commit();
                                       // }


                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });



                                Toast.makeText(LoginProf.this, "Inicio de sesión exitoso.",
                                        Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                Intent intent = new Intent(LoginProf.this, Pantalla_Principal.class);
                                LoginProf.this.startActivity(intent);
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(LoginProf.this, "Fallo en el inicio de sesión",
                                        Toast.LENGTH_SHORT).show();

                                progressDialog.dismiss();
                            }

                            // ...
                        }
                    }

                });


    }



    @Override
    protected void onStart() {
        super.onStart();
        FAutentic.addAuthStateListener(FInicionIndicdor);


    }

    @Override
    protected void onStop() {
        super.onStop();
        if (FInicionIndicdor!=null)
        {
            FAutentic.removeAuthStateListener(FInicionIndicdor);


        }
    }

    public void Alerta (String Titulo, String Mensaje){
        AlertDialog alertDialog;
        alertDialog = new AlertDialog.Builder(LoginProf.this).setNegativeButton("Ok",null).create();
        alertDialog.setTitle(Titulo);
        alertDialog.setMessage(Mensaje);
        alertDialog.show();

    }


    public void Mtoast(String mensaje){

        Toast toast= Toast.makeText(LoginProf.this, mensaje,Toast.LENGTH_LONG);
        toast.show();
    }



}
