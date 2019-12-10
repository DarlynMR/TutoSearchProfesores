package com.rd.dmmr.tutosearchprofesores;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.zelory.compressor.Compressor;

public class AgregarTutoriaLive extends AppCompatActivity implements View.OnClickListener {


    //Variables
    private static final int GALLERY_INTENT = 1;
    byte[] thumb_byte;
    String download_url, thumb_downloadUrl,nombreProfCompleto;
    private int pub_ano,pub_mes,pub_dia,pub_hora,pub_minutos;

    //Objetos
    private ImageView imgTutoLive;
    private FloatingActionButton fabImgChange;
    private Spinner spnMateria;
    private EditText txtTitulo, txtDescripcion, txtFecha, txtHoraInicio;
    private Button btnFecha, btnHora, btnPublicar;
    private ProgressDialog progressDialog;
    private String keyid;
    private Uri uri = null;

    private int ano, mes, dia, hora, minutos;

    //Firebase user
    private FirebaseAuth FAutentic;
    private FirebaseUser FUser;

    //Firebase Database
    private DatabaseReference DBReference;
    private DatabaseReference DBRefGetid;
    private DatabaseReference DBRefGetMateria;
    private DatabaseReference UserReference;

    private FirebaseFirestore fdb;

    //Firebase Storage
    private StorageReference imgStorage;
    private StorageReference urlStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_tutoria_live);


        imgStorage = FirebaseStorage.getInstance().getReference();
        urlStorage = FirebaseStorage.getInstance().getReference();

        fdb = FirebaseFirestore.getInstance();

        FAutentic = FirebaseAuth.getInstance();
        FUser = FirebaseAuth.getInstance().getCurrentUser();

        Log.i("Fecha", ""+FUser.getMetadata().getCreationTimestamp()+" Otro "+FUser.getProviderData());
        UserReference= FirebaseDatabase.getInstance().getReference().child("usuarios").child("profesores").child(FUser.getUid());


        try {
            DocumentReference dc =  fdb.collection("Profesores").document(FUser.getUid());

            dc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    DocumentSnapshot docS = task.getResult();
                    if (task.isSuccessful()){

                        nombreProfCompleto= docS.getString("nombres")+ " " + docS.getString("apellidos");
                    }
                    if (task.isCanceled()){
                        Toast.makeText(AgregarTutoriaLive.this,"No se pudo obtener los datos del usuario",Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }catch (Exception e){
            Toast.makeText(AgregarTutoriaLive.this, "Error al obtener los datos del usuario.", Toast.LENGTH_SHORT).show();
        }


        DBReference = FirebaseDatabase.getInstance().getReference();
        DBRefGetid = FirebaseDatabase.getInstance().getReference();
        DBRefGetMateria = FirebaseDatabase.getInstance().getReference().child("materias");

        imgTutoLive = (ImageView) findViewById(R.id.imgTutoLive);

        fabImgChange = (FloatingActionButton) findViewById(R.id.fabImgChange);

        spnMateria = (Spinner) findViewById(R.id.spnMateriaLive);

        txtTitulo = (EditText) findViewById(R.id.txtTitulo);
        txtDescripcion = (EditText) findViewById(R.id.txtDescripcion);
        txtFecha = (EditText) findViewById(R.id.txtFecha);
        txtHoraInicio = (EditText) findViewById(R.id.txtHoraInicio);

        btnFecha = (Button) findViewById(R.id.btnFecha);
        btnHora = (Button) findViewById(R.id.btnHoraInicio);
        btnPublicar = (Button) findViewById(R.id.btnPublicar);

        //LlAMANDO EL TOOLBAR
        final Toolbar toolbar = (Toolbar) findViewById(R.id.MyToolbarLive);
        //  toolbar.setTitleTextColor(Color.parseColor("#00FF00"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Colapsando la barra
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapse_toolbarLive);
        collapsingToolbarLayout.setTitle("Agregar transmisión en vivo");
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
        collapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);

        //Color de la barra
        Context context = this;
        collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(context, R.color.colorPrimary));



        LlenarSpinner();

        fabImgChange.setOnClickListener(this);
        btnHora.setOnClickListener(this);
        btnFecha.setOnClickListener(this);
        btnPublicar.setOnClickListener(this);

        txtFecha.setOnClickListener(this);
        txtHoraInicio.setOnClickListener(this);


    }

    private void mtdAgregarTutoriaLive() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Agregando tutoría...");
        progressDialog.setMessage("La tutoría se esta agregando, por favor espere");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        ObtenerFechaHora();

        //Variables para los datos del profesor

        String materia = spnMateria.getSelectedItem().toString();

        if (download_url == null) {
            download_url = "defaultLive";
        }
        if (thumb_downloadUrl == null) {
            thumb_downloadUrl = "defaultLive";
        }

        String titulo = txtTitulo.getText().toString();
        String descripcion = txtDescripcion.getText().toString();
        String fecha = txtFecha.getText().toString();
        String horainicio = txtHoraInicio.getText().toString();


        keyid = DBRefGetid.child("tutorias").child("institucionales").push().getKey();

        SubirImagen(keyid);

        FirebaseUser user = FAutentic.getCurrentUser();
        //DBReference = FirebaseDatabase.getInstance().getReference().child("tutorias").child("institucionales").child(keyid);

        Calendar calInicial = Calendar.getInstance();

        calInicial.set(ano,mes,dia,hora,minutos,0);

        HashMap<String, Object> TutoMap = new HashMap<>();
        TutoMap.put("materia", materia);
        TutoMap.put("UIDProfesor", user.getUid());
        TutoMap.put("profesor", nombreProfCompleto);
        TutoMap.put("descripcion", descripcion);
        TutoMap.put("timestamp_inicial", String.valueOf(calInicial.getTimeInMillis()));
        TutoMap.put("titulo", titulo);
        TutoMap.put("url_image_portada", download_url);
        TutoMap.put("url_thumb_image_portada", thumb_downloadUrl);
        TutoMap.put("tipo_tuto","Live");
        TutoMap.put("broadcastId","None");
        TutoMap.put("timestamp_pub", String.valueOf(System.currentTimeMillis()));

        fdb.collection("Tutorias_institucionales").document(keyid)
                .set(TutoMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        Intent intent = new Intent(AgregarTutoriaLive.this, Pantalla_Principal.class);
                        AgregarTutoriaLive.this.startActivity(intent);
                        Toast.makeText(AgregarTutoriaLive.this, "Se ha publicado la tutoría", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AgregarTutoriaLive.this, "Ha ocurrido un error al publicar la tutoría", Toast.LENGTH_SHORT).show();
            }
        });
/*
        DBReference.setValue(TutoMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    Intent intent = new Intent(AgregarTutoriaLive.this, Pantalla_Principal.class);
                    AgregarTutoriaLive.this.startActivity(intent);
                    Toast.makeText(AgregarTutoriaLive.this, "Se ha publicado la tutoría", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AgregarTutoriaLive.this, "Ha ocurrido un error al publicar la tutoría", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                }
            }
        });
*/
        progressDialog.dismiss();


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


            if (result!=null) {
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
        }

        if (uri!=null) {
            imgTutoLive.setImageURI(uri);
        }

    }

    private void SubirImagen(final String UIDTuto) {

        if (uri != null) {

            StorageReference filePath = imgStorage.child("img_tutorias").child(UIDTuto + ".jpg");
            final StorageReference thumb_filePath = imgStorage.child("img_tutorias").child("thumbs").child(UIDTuto + ".jpg");

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
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {

                            final StorageReference url_filePath = urlStorage.child("img_tutorias").child(keyid + ".jpg");
                            url_filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    download_url = uri.toString();
                                    Map update_hashmMap=new HashMap();
                                    update_hashmMap.put("url_image_portada",download_url);

                                    /*
                                    DBReference = FirebaseDatabase.getInstance().getReference().child("tutorias").child("institucionales").child(keyid);
                                    DBReference.updateChildren(update_hashmMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                progressDialog.dismiss();
                                            }
                                        }
                                    });
*/

                                    fdb.collection("Tutorias_institucionales").document(UIDTuto)
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

                            //
                            UploadTask uploadTask = thumb_filePath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                    final StorageReference url_thumb_filePath = urlStorage.child("img_tutorias").child("thumbs").child(keyid + ".jpg");
                                    url_thumb_filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {

                                            thumb_downloadUrl = uri.toString();
                                            Map update_hashmMap=new HashMap();
                                            update_hashmMap.put("url_thumb_image_portada",thumb_downloadUrl);

                                            /*
                                            DBReference = FirebaseDatabase.getInstance().getReference().child("tutorias").child("institucionales").child(keyid);
                                            DBReference.updateChildren(update_hashmMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        Toast.makeText(AgregarTutoriaLive.this, "Se ha subido la imagen con exito.", Toast.LENGTH_LONG).show();
                                                        progressDialog.dismiss();
                                                    }
                                                }
                                            });
*/


                                            fdb.collection("Tutorias_institucionales").document(UIDTuto)
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


                                    if (thumb_task.isSuccessful()) {


                                    } else {
                                        Toast.makeText(AgregarTutoriaLive.this, "Error al subir la imagen.", Toast.LENGTH_LONG).show();
                                        progressDialog.dismiss();
                                    }
                                }
                            });

                            //


                        } else {
                            Toast.makeText(AgregarTutoriaLive.this, "Error al subir la imagen.", Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    }
                });

            } catch (Exception e) {

            }

        }
    }

    final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface
                        .BUTTON_POSITIVE:
                    mtdAgregarTutoriaLive();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    dialog.dismiss();
                    break;
            }
        }
    };

    private void LlenarSpinner() {
        try {

            fdb.collection("Materias")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            final List<String> materia = new ArrayList<>();

                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {

                                    materia.add(document.getId());
                                    spnMateria = (Spinner) findViewById(R.id.spnMateriaLive);
                                    ArrayAdapter<String> materiaAdapter = new ArrayAdapter<String>(AgregarTutoriaLive.this, android.R.layout.simple_spinner_item, materia);
                                    materiaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    spnMateria.setAdapter(materiaAdapter);
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
        alertDialog = new AlertDialog.Builder(AgregarTutoriaLive.this).setNegativeButton("Ok", null).create();
        alertDialog.setTitle(Titulo);
        alertDialog.setMessage(Mensaje);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

    }

    public void ObtenerFechaHora(){
        final Calendar calendar = Calendar.getInstance();
        pub_ano = calendar.get(Calendar.YEAR);
        pub_mes = calendar.get(Calendar.MONTH);
        pub_dia = calendar.get(Calendar.DAY_OF_MONTH);
        pub_hora = calendar.get(Calendar.HOUR_OF_DAY);
        pub_minutos = calendar.get(Calendar.MINUTE);

    }

    public void campos_vacios(EditText campo, View view) {
        campo.setError("No puede dejar este campo vacío.");
        view = campo;
    }

    @Override
    public void onClick(View view) {
        if (view == fabImgChange) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Seleccione una imagen"), GALLERY_INTENT);
        }
        if (view == txtHoraInicio || view == btnHora) {
            final Calendar calendar = Calendar.getInstance();
            hora = calendar.get(Calendar.HOUR_OF_DAY);
            minutos = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int hor, int min) {
                    txtHoraInicio.setText(hor + ":" + min);
                    hora=hor;
                    minutos=min;
                }
            }, hora, minutos, false);
            timePickerDialog.show();

        }
        if (view == txtFecha || view == btnFecha) {
            final Calendar calendar = Calendar.getInstance();
            ano = calendar.get(Calendar.YEAR);
            mes = calendar.get(Calendar.MONTH);
            dia = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int an, int me, int di) {
                    txtFecha.setText(di + "/" + (me + 1) + "/" + an);
                    ano=an;
                    mes=me;
                    dia= di;
                }
            }
                    , ano, mes, dia);
            datePickerDialog.show();
        }

        if (view == btnPublicar) {
            View ViewFocus = null;
            if(spnMateria.getSelectedItem()==null){
                Alerta("Elegir materia", "Debe elegir la materia correspondiente a la tutoría");

            } else if (txtTitulo.getText().toString().length() == 0){
                campos_vacios(txtTitulo, ViewFocus);
            } else if (txtDescripcion.getText().toString().length() == 0){
                campos_vacios(txtDescripcion, ViewFocus);
            } else if (txtFecha.getText().toString().length() == 0){
                campos_vacios(txtFecha, ViewFocus);
            } else if (txtHoraInicio.getText().toString().length() == 0){
                campos_vacios(txtHoraInicio, ViewFocus);
            }else {

                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AgregarTutoriaLive.this);
                builder.setMessage("¿Está seguro de que desea publicar esta tutoría?").setPositiveButton("Si", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener)
                        .setCancelable(false);
                builder.show();

            }

        }
    }
}
