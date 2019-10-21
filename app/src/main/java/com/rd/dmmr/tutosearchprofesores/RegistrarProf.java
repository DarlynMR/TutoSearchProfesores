package com.rd.dmmr.tutosearchprofesores;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrarProf extends AppCompatActivity implements View.OnClickListener {


    private FirebaseAuth FAutentic;
    private FirebaseAuth.AuthStateListener FInicionIndicdor;
    private DatabaseReference DBReference;

    private ProgressDialog progressDialog;

    private Button JbtnRegistrar;

    private EditText NombreCompleto;
    private EditText Telefono;
    private EditText Correo;
    private EditText Password;
    private EditText Password2;

    private FloatingActionButton fback_button;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_prof);

        NombreCompleto= (EditText) findViewById(R.id.txtNombreRegistrar);
        Telefono= (EditText) findViewById(R.id.txtTelefono);
        Correo= (EditText) findViewById(R.id.txtCorreo);
        Password= (EditText) findViewById(R.id.txtPassword);
        Password2= (EditText) findViewById(R.id.txtPassword2);

        fback_button= (FloatingActionButton) findViewById(R.id.fBackButton);

        FAutentic = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        DBReference = FirebaseDatabase.getInstance().getReference();

        JbtnRegistrar = (Button) findViewById(R.id.btnRegistrar);
        FInicionIndicdor = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser usuario = firebaseAuth.getCurrentUser();
                if (usuario != null) {
                    Mtoast("Iniciado como "+firebaseAuth.getCurrentUser().getEmail());
                } else {
                    Mtoast("Cierre de sesión correcto");

                }
            }
        };



        JbtnRegistrar.setOnClickListener(this);
       fback_button.setOnClickListener(this);



    }

    public void Registrar(String email, String password){

        progressDialog.setMessage("Creando cuenta...");
        progressDialog.show();
        FAutentic.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                String Nom = NombreCompleto.getText().toString();
                String Tel = Telefono.getText().toString();
                String Mail= Correo.getText().toString();
                if(!task.isSuccessful()){
                    Alerta("Ocurrio un error", "Direccion de correo en uso, falla de conexión o correo mal escrito.");

                }else {

                    Alerta("Estado de registro","Su cuenta ha sido creada correctamente, solo resta ir a su correo y " +
                            "entrar al enlace de verificación para activarla");

                    FirebaseUser user= FAutentic.getCurrentUser();
                    DBReference.child("UCATECI").child("Profesores").child(user.getUid()).child("Nombre").setValue(Nom);
                    DBReference.child("UCATECI").child("Profesores").child(user.getUid()).child("Telefono").setValue(Tel);
                    DBReference.child("UCATECI").child("Profesores").child(user.getUid()).child("Correo").setValue(Mail);

                    user.sendEmailVerification();









                    Intent i = new Intent(RegistrarProf.this, LoginProf.class);
                    RegistrarProf.this.startActivity(i);

                }
                progressDialog.dismiss();
            }
        });


    }



    public void Alerta (String Titulo, String Mensaje){
        AlertDialog alertDialog;
        alertDialog = new AlertDialog.Builder(RegistrarProf.this).setNegativeButton("Ok",null).create();
        alertDialog.setTitle(Titulo);
        alertDialog.setMessage(Mensaje);
        alertDialog.show();

    }

    public void Mtoast(String mensaje){

        Toast toast= Toast.makeText(RegistrarProf.this, mensaje,Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onClick(View view) {
        if (view==JbtnRegistrar){
            Registrar(Correo.getText().toString(),Password.getText().toString());
        }
        if (view==fback_button){
           onBackPressed();
        }
    }
}
