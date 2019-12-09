package com.rd.dmmr.tutosearchprofesores;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.zelory.compressor.Compressor;

public class RegistrarProf extends AppCompatActivity implements View.OnClickListener {

    //Variables Firebase de User y Referencia de base de datos
    private FirebaseAuth FAutentic;
    private FirebaseAuth.AuthStateListener FInicionIndicdor;
    private DatabaseReference DBReference;

    //FirebaseFirestore
    private FirebaseFirestore fdb;

    //Variables Firebase de Storage
    private StorageReference imgStorage;
    private StorageReference urlStorage;

    private ProgressDialog progressDialog;

    private ImageView ciruclarImageView;

    private Button btnRegistrar, btnCargarFoto;

    private EditText nombres, apellidos, telefono, correo, password, password2;
    private Spinner spnProvincia;

    private TextView fecha_nacimiento;

    //Variables
    private int ano, mes, dia, hora, minutos;
    private static final int GALLERY_INTENT = 1;
    private Uri uri = null;
    byte[] thumb_byte;
    private String keyid;
    String download_url, thumb_downloadUrl, nombreProfCompleto;

    private FloatingActionButton fback_button;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_prof);

        nombres = (EditText) findViewById(R.id.txt_nombres_reg);
        apellidos = (EditText) findViewById(R.id.txt_apellidos_reg);
        fecha_nacimiento = (TextView) findViewById(R.id.txt_fechanacimiento_reg);
        telefono = (EditText) findViewById(R.id.txtTelefonoProf);
        spnProvincia  = (Spinner) findViewById(R.id.spnProvincia);
        correo = (EditText) findViewById(R.id.txtCorreoProf);
        password = (EditText) findViewById(R.id.txtPassword);
        password2 = (EditText) findViewById(R.id.txtPassword2);

        ciruclarImageView = (ImageView) findViewById(R.id.circularprofileProf);

        fback_button = (FloatingActionButton) findViewById(R.id.fBackButton);

        FAutentic = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        DBReference = FirebaseDatabase.getInstance().getReference();
        fdb = FirebaseFirestore.getInstance();

        imgStorage = FirebaseStorage.getInstance().getReference();
        urlStorage = FirebaseStorage.getInstance().getReference();

        btnRegistrar = (Button) findViewById(R.id.btnRegistrar);
        btnCargarFoto = (Button) findViewById(R.id.btnCargarimage);
        FInicionIndicdor = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser usuario = firebaseAuth.getCurrentUser();
                if (usuario != null) {
                    Mtoast("Iniciado como " + firebaseAuth.getCurrentUser().getEmail());
                } else {
                    Mtoast("Cierre de sesión correcto");

                }
            }
        };

        LlenarSpinner();
        btnRegistrar.setOnClickListener(this);
        fback_button.setOnClickListener(this);
        fecha_nacimiento.setOnClickListener(this);
        ciruclarImageView.setOnClickListener(this);
        btnCargarFoto.setOnClickListener(this);


    }

    public void Registrar(String email, String password) {

        progressDialog.setMessage("Creando cuenta...");
        progressDialog.show();
        FAutentic.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Alerta("Ocurrio un error", "Direccion de correo en uso, falla de conexión o correo mal escrito.");

                } else {

                    Alerta("Estado de registro", "Su cuenta ha sido creada correctamente, solo resta ir a su correo y " +
                            "entrar al enlace de verificación para activarla");

                    final FirebaseUser user = FAutentic.getCurrentUser();

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("nombres", nombres.getText().toString());
                    hashMap.put("apellidos", apellidos.getText().toString());
                    hashMap.put("telefonos", telefono.getText().toString());
                    hashMap.put("fecha_nacimiento", fecha_nacimiento.getText().toString());
                    hashMap.put("provincia", spnProvincia.getSelectedItem().toString());
                    hashMap.put("correo", correo.getText().toString());
                    hashMap.put("direccion", "none");
                    hashMap.put("url_pic", "defaultPicProf");
                    hashMap.put("url_thumb_pic", "defaultPicProf");
                    hashMap.put("about_me","null");
                    hashMap.put("estadoOnline","En linea");

                    HashMap<Array, String> hasprueba = new HashMap<>();

                    fdb.collection("Profesores").document(user.getUid())
                            .set(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    FirebaseUser user = FAutentic.getCurrentUser();
                                    user.sendEmailVerification();
                                    if (uri != null) {
                                        SubirImagen(user.getUid());
                                    } else {
                                        FAutentic.signOut();
                                    }


                                    progressDialog.dismiss();
                                    Intent i = new Intent(RegistrarProf.this, LoginProf.class);
                                    RegistrarProf.this.startActivity(i);
                                    finish();

                                    Mtoast("Usuario registrado correctamente");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Mtoast("Ocurrio un error al registrar el usuario");
                                    user.delete();
                                    FAutentic.signOut();
                                    progressDialog.dismiss();

                                }
                            });


                }
                progressDialog.dismiss();
            }
        });


    }

    private void SubirImagen(final String UIDProf) {

        if (uri != null) {


            StorageReference filePath = imgStorage.child("img_profile").child(UIDProf + ".jpg");
            final StorageReference thumb_filePath = imgStorage.child("img_profile").child("thumbs").child(UIDProf + ".jpg");

            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Cargando imagen...");
            progressDialog.setMessage("Cargando imagen espere mientras se carga la imagen.");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            File thumb_filepath = new File(uri.getPath());

            final Bitmap thumb_bitmap = new Compressor(this)
                    .setMaxWidth(200)
                    .setMaxHeight(200)
                    .setQuality(75)
                    .compressToBitmap(thumb_filepath);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            final byte[] thumb_byte = baos.toByteArray();

            try {

                filePath.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {

                            final StorageReference url_filePath = urlStorage.child("img_profile").child(UIDProf + ".jpg");
                            url_filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    download_url = uri.toString();
                                    Map update_hashmMap = new HashMap();
                                    update_hashmMap.put("url_pic", download_url);


                                    fdb.collection("Profesores").document(UIDProf)
                                            .update(update_hashmMap)
                                            .addOnSuccessListener(new OnSuccessListener() {
                                                @Override
                                                public void onSuccess(Object o) {
                                                    progressDialog.dismiss();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });


                                }

                            });

                            UploadTask uploadTask = thumb_filePath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                    final StorageReference url_thumb_filePath = urlStorage.child("img_profile").child("thumbs").child(UIDProf + ".jpg");
                                    url_thumb_filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {

                                            thumb_downloadUrl = uri.toString();
                                            Map update_hashmMap_thumb = new HashMap();
                                            update_hashmMap_thumb.put("url_thumb_pic", thumb_downloadUrl);

                                            fdb.collection("Profesores").document(UIDProf)
                                                    .update(update_hashmMap_thumb)
                                                    .addOnSuccessListener(new OnSuccessListener() {
                                                        @Override
                                                        public void onSuccess(Object o) {
                                                            progressDialog.dismiss();
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                }
                                            });

                                        }

                                    });

                                    if (thumb_task.isSuccessful()) {
                                        FAutentic.signOut();

                                    } else {
                                        Toast.makeText(RegistrarProf.this, "Error al subir la imagen.", Toast.LENGTH_LONG).show();
                                        Log.i("PruebaError", "Excepcion: " + task.getResult() + " Result: " + task.getResult());
                                        FAutentic.signOut();
                                        progressDialog.dismiss();
                                    }
                                }
                            });

                        } else {
                            Toast.makeText(RegistrarProf.this, "Error al subir la imagen.", Toast.LENGTH_LONG).show();
                            FAutentic.signOut();
                            Log.i("PruebaError", "Excepcion: " + task.getResult() + " Result: " + task.getResult());
                            progressDialog.dismiss();
                        }
                    }
                });

            } catch (Exception e) {
                Log.i("PruebaError", "Causa: " + e.getCause() + " Mensaje: " + e.getMessage());
                FAutentic.signOut();
            }

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {

            Uri imageurl = data.getData();
        
            CropImage.activity(imageurl)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            uri = result.getUri();


            File thumb_filePath = new File(uri.getPath());

            final Bitmap thumb_bitmap = new Compressor(this)
                    .setMaxWidth(200)
                    .setMaxHeight(200)
                    .setQuality(75)
                    .compressToBitmap(thumb_filePath);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            thumb_byte = baos.toByteArray();
        }

        ciruclarImageView.setImageURI(uri);

    }

    private void LlenarSpinner() {
        try {

            fdb.collection("Provincias")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            final List<String> provincias = new ArrayList<>();

                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {

                                    provincias.add(document.getId());
                                    spnProvincia = (Spinner) findViewById(R.id.spnProvincia);
                                    ArrayAdapter<String> provinciasAdapter = new ArrayAdapter<String>(RegistrarProf.this, android.R.layout.simple_spinner_item, provincias);
                                    provinciasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    spnProvincia.setAdapter(provinciasAdapter);
                                }
                            } else {
                                Log.i("ErrorSpinnerMateria", ""+task.getException());
                            }
                        }
                    });


        } catch (Exception e) {

        }
    }


    public void Alerta(String Titulo, String Mensaje) {
        AlertDialog alertDialog;
        alertDialog = new AlertDialog.Builder(RegistrarProf.this).setNegativeButton("Ok", null).create();
        alertDialog.setTitle(Titulo);
        alertDialog.setMessage(Mensaje);
        alertDialog.show();

    }

    public void campos_vacios(EditText campo, View view) {
        campo.setError("No puede dejar este campo vacío.");
        view = campo;
    }

    public void Mtoast(String mensaje) {

        Toast toast = Toast.makeText(RegistrarProf.this, mensaje, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onClick(View view) {
        if (view == btnRegistrar) {
            View ViewFocus = null;
            String pass, pass2;
            pass = password.getText().toString();
            pass2 = password2.getText().toString();


            if (nombres.getText().toString().length() == 0) {
                campos_vacios(nombres, ViewFocus);
            } else if (apellidos.getText().toString().length() == 0) {
                campos_vacios(apellidos, ViewFocus);
            } else if (fecha_nacimiento.getText().toString().length() == 0) {
                Alerta("Fecha vacía", "Favor de introducir su fecha de nacimiento.");
            } else if (telefono.getText().toString().length() == 0) {
                campos_vacios(telefono, ViewFocus);
            } else if (correo.getText().toString().length() == 0) {
                campos_vacios(correo, ViewFocus);
            } else if (apellidos.getText().toString().length() == 0) {
                campos_vacios(apellidos, ViewFocus);
            } else if (password.getText().toString().length() == 0) {
                campos_vacios(password, ViewFocus);
            } else if (password2.getText().toString().length() == 0) {
                campos_vacios(password2, ViewFocus);
            } else if (!pass.equals(pass2)) {
                Log.i("Prueba", "" + pass.equals(pass2));
                password2.setError("Las contraseñas no coinciden.");
                ViewFocus = password2;
            } else if (spnProvincia.getSelectedItem()==null) {
                Alerta("Elegir provincia", "Debe elegir la provincia de donde usted.");
            } else {
                Registrar(correo.getText().toString(), password.getText().toString());
            }

        }
        if (view == fback_button) {
            onBackPressed();
        }
        if (view == fecha_nacimiento) {
            final Calendar calendar = Calendar.getInstance();
            ano = calendar.get(Calendar.YEAR);
            mes = calendar.get(Calendar.MONTH);
            dia = calendar.get(Calendar.DAY_OF_MONTH);
            Integer hora, minuto;
            hora = calendar.get(Calendar.HOUR_OF_DAY);
            minuto = calendar.get(Calendar.MINUTE);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int an, int me, int di) {
                    fecha_nacimiento.setText(di + "/" + (me + 1) + "/" + an);
                }
            }
                    , ano, mes, dia);

            Log.i("Fecha", "Fecha: " + dia + "/" + (mes + 1) + "/" + ano + ", Hora:" + hora + ":" + minuto);
            datePickerDialog.show();

        }

        if (view == btnCargarFoto || view == ciruclarImageView) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Seleccione una imagen"), GALLERY_INTENT);
        }
    }
}