package com.rd.dmmr.tutosearchprofesores;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginProf extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth FAutentic;
    private FirebaseAuth.AuthStateListener FInicionIndicdor;

    private FirebaseDatabase FDatabase;
    private DatabaseReference DBReference;

    private FirebaseFirestore fdb;

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

        fdb = FirebaseFirestore.getInstance();


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

        JbtnEntrar.setOnClickListener(this);
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
                        try {
                            FirebaseUser user = FAutentic.getCurrentUser();
                            assert user != null;
                            DBReference=FirebaseDatabase.getInstance().getReference().child("usuarios").child("profesores").child(user.getUid());

                            if (!task.isSuccessful()) {
                                Toast.makeText(LoginProf.this, "Datos incorrectos, por favor digite otra vez sus datos", Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                                FAutentic.signOut();
                            } else {

                                if (!user.isEmailVerified()) {
                                    Toast.makeText(LoginProf.this, "Esta cuenta aun no ha sido verificada o no existe", Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                    FAutentic.signOut();
                                    return;
                                }
                                if (task.isSuccessful()) {

                                    /*
                                    DBReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                        if (dataSnapshot==null){
                                            Alerta("Error al iniciar", "La cuenta que esta intentando acceder es de un estudiante, aqui solo se permite iniciar con cuenta de profesor");
                                            progressDialog.dismiss();

                                        }else {

                                            Log.i("Prueba", dataSnapshot.child("nombres").getValue(String.class));
                                            editor.putString("nombresProf", dataSnapshot.child("nombres").getValue(String.class));
                                            editor.putString("apellidosProf", dataSnapshot.child("apellidos").getValue(String.class));
                                            editor.putString("TelefonoProf", dataSnapshot.child("telefono").getValue(String.class));
                                            editor.putString("FechaNacimiento", dataSnapshot.child("fecha_nacimiento").getValue(String.class));
                                            editor.putString("Correo", dataSnapshot.child("correo").getValue(String.class));
                                            editor.apply();
                                            // }


                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                    */

                                    DocumentReference profesorRef = fdb.collection("Profesores").document(user.getUid());
                                    profesorRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {


                                                    DocumentSnapshot dc = task.getResult();

                                                    Log.i("Prueba",  dc.getString("nombres"));
                                                    editor.putString("nombresProf", dc.getString("nombres"));
                                                    editor.putString("apellidosProf", dc.getString("apellidos"));
                                                    editor.putString("TelefonoProf",  dc.getString("telefono"));
                                                    editor.putString("FechaNacimiento", dc.getString("fecha_nacimiento"));
                                                    editor.putString("Correo",  dc.getString("correo"));
                                                    editor.apply();
                                                    // }

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
                        } catch (Exception e){
                            Alerta("Error al tratar de iniciar", e.getMessage());
                            progressDialog.dismiss();
                        }

                    }

                });


        }


    public static boolean Validad_email(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
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


    @Override
    public void onClick(View view) {
        View ViewFocus = null;
        if (view==JbtnEntrar){
            if (LCorreo.getText().toString().length() == 0) {
                campos_vacios(LCorreo,ViewFocus);
            } else if (LPassword.getText().toString().length() == 0){
                campos_vacios(LPassword,ViewFocus);
            } else if (!Validad_email(LCorreo.getText().toString())){
                LCorreo.setError("Correo inválido");
            }else{
                Login(LCorreo.getText().toString(),LPassword.getText().toString());
            }
        }
    }

    public void campos_vacios(EditText campo, View view){
        campo.setError("No puede dejar este campo vacío.");
        view= campo;
    }
}
