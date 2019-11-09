package com.rd.dmmr.tutosearchprofesores;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
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

public class AgregarTutoriaPresencial extends AppCompatActivity implements View.OnClickListener {

    //Variables Firebase de Usuario y referencia de base de datos
    private FirebaseAuth FAutentic;
    private FirebaseUser FUser;
    private StorageReference imgStorage;
    private StorageReference urlStorage;

    private FirebaseFirestore fdb;

    //Variables
    private String urlImgT;
    private static final int GALERRY_PICK = 1;
    byte[] thumb_byte;
    private int ano, mes, dia, hora, minutos;
    private int pub_ano, pub_mes, pub_dia, pub_hora, pub_minutos;
    private Uri uri = null;
    private String keyid;
    private String download_url, thumb_downloadUrl, nombreProfCompleto;

    //Objetos
    private Spinner spnMateriaPresencial;

    private EditText Titulo, Descripcion, Lugar, Fecha, HoraInicio, HoraFinal;

    private Button btnFecha, btnHoraInicio, btnHoraFinal, btnPublicar;

    private FloatingActionButton fabSelectImagePresencial;

    private ImageView imgTutoPresencial;

    private DatabaseReference DBReference;
    private DatabaseReference DBRefGetid;
    private DatabaseReference UserReference;
    private DatabaseReference DBRefGetMateria;

    private ProgressDialog progressDialog;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_tutoria_presencial);

        Titulo = (EditText) findViewById(R.id.txtTitulo);
        Descripcion = (EditText) findViewById(R.id.txtDescripcion);
        Lugar = (EditText) findViewById(R.id.txtLugar);
        Fecha = (EditText) findViewById(R.id.txtFecha);
        HoraInicio = (EditText) findViewById(R.id.txtHoraInicio);
        HoraFinal = (EditText) findViewById(R.id.txtHoraFinal);

        btnFecha = (Button) findViewById(R.id.btnFecha);
        btnHoraInicio = (Button) findViewById(R.id.btnHoraInicio);
        btnHoraFinal = (Button) findViewById(R.id.btnHoraFinal);

        imgTutoPresencial = (ImageView) findViewById(R.id.imgTutoPresencial);

        spnMateriaPresencial = (Spinner) findViewById(R.id.spnMateriaPresencial);

        fabSelectImagePresencial = (FloatingActionButton) findViewById(R.id.fabImgChangePresencial);

        btnFecha.setOnClickListener(this);
        btnHoraInicio.setOnClickListener(this);
        btnHoraFinal.setOnClickListener(this);
        btnPublicar = (Button) findViewById(R.id.btnPublicar);


        progressDialog = new ProgressDialog(this);

        imgStorage = FirebaseStorage.getInstance().getReference();
        urlStorage = FirebaseStorage.getInstance().getReference();


        FAutentic = FirebaseAuth.getInstance();
        FUser = FirebaseAuth.getInstance().getCurrentUser();
        DBRefGetid = FirebaseDatabase.getInstance().getReference();

        fdb = FirebaseFirestore.getInstance();

        //LlAMANDO EL TOOLBAR
        final Toolbar toolbar = (Toolbar) findViewById(R.id.MyToolbarPresencial);
        //  toolbar.setTitleTextColor(Color.parseColor("#00FF00"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Colapsando la barra
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapse_toolbarPresencial);
        collapsingToolbarLayout.setTitle("Agregar tutoría presencial");
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
        collapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);

        //Color de la barra
        Context context = this;
        collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(context, R.color.colorPrimary));


        DBRefGetMateria = FirebaseDatabase.getInstance().getReference().child("materias");
        UserReference = FirebaseDatabase.getInstance().getReference().child("usuarios").child("profesores").child(FUser.getUid());
        try {

            DocumentReference dc = fdb.collection("Profesores").document(FUser.getUid());

            dc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    DocumentSnapshot docS = task.getResult();
                    if (task.isSuccessful()) {

                        nombreProfCompleto = docS.getString("nombres") + " " + docS.getString("apellidos");
                    }
                    if (task.isCanceled()) {
                        Toast.makeText(AgregarTutoriaPresencial.this, "No se pudo obtener los datos del usuario", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            /*
            UserReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    nombreProfCompleto= dataSnapshot.child("nombres").getValue(String.class)+ " " + dataSnapshot.child("apellidos").getValue(String.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            */
        } catch (Exception e) {
            Toast.makeText(AgregarTutoriaPresencial.this, "Error al obtener los datos del usuario.", Toast.LENGTH_SHORT).show();
        }

        LlenarSpinner();

        btnPublicar.setOnClickListener(this);
        fabSelectImagePresencial.setOnClickListener(this);

    }


    private void mtdAgregarTutoriaPresencial() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Agregando tutoría...");
        progressDialog.setMessage("La tutoría se esta agregando, por favor espere");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        ObtenerFechaHora();

        String materia = spnMateriaPresencial.getSelectedItem().toString();
        if (download_url == null) {
            download_url = "defaultPresencial";
        }
        if (thumb_downloadUrl == null) {
            thumb_downloadUrl = "defaultPresencial";
        }

        String titulo = Titulo.getText().toString();
        String descripcion = Descripcion.getText().toString();
        String lugar = Lugar.getText().toString();
        String fecha = Fecha.getText().toString();
        String horainicio = HoraInicio.getText().toString();
        String horafinal = HoraFinal.getText().toString();


        keyid = DBRefGetid.child("tutorias").child("institucionales").push().getKey();
        SubirImagen(keyid);
        FirebaseUser user = FAutentic.getCurrentUser();

        //DBReference= FirebaseDatabase.getInstance().getReference().child("tutorias").child("institucionales").child(keyid);

        HashMap<String, String> TutoMap = new HashMap<>();
        TutoMap.put("materia", materia);
        TutoMap.put("UIDProfesor", user.getUid());
        TutoMap.put("profesor", nombreProfCompleto);
        TutoMap.put("descripcion", descripcion);
        TutoMap.put("lugar", lugar);
        TutoMap.put("fecha", fecha);
        TutoMap.put("hora_inicial", horainicio);
        TutoMap.put("hora_final", horafinal);
        TutoMap.put("titulo", titulo);
        TutoMap.put("url_image_portada", download_url);
        TutoMap.put("url_thumb_image_portada", thumb_downloadUrl);
        TutoMap.put("tipo_tuto", "Presencial");
        TutoMap.put("fecha_pub", pub_dia + "/" + (pub_mes + 1) + "/" + pub_ano);
        TutoMap.put("hora_pub", pub_hora + ":" + pub_minutos);

        fdb.collection("Tutorias_institucionales").document(keyid)
                .set(TutoMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        Intent intent = new Intent(AgregarTutoriaPresencial.this, Pantalla_Principal.class);
                        AgregarTutoriaPresencial.this.startActivity(intent);
                        Toast.makeText(AgregarTutoriaPresencial.this, "Se ha publicado la tutoría", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AgregarTutoriaPresencial.this, "Ha ocurrido un error al publicar la tutoría", Toast.LENGTH_SHORT).show();
            }
        });
/*

        DBReference.setValue(TutoMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    Intent intent = new Intent(AgregarTutoriaPresencial.this, Pantalla_Principal.class);
                    AgregarTutoriaPresencial.this.startActivity(intent);

                    Toast.makeText(AgregarTutoriaPresencial.this, "Se ha publicado la tutoría", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AgregarTutoriaPresencial.this, "Ha ocurrido un error al publicar la tutoría", Toast.LENGTH_SHORT).show();
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
        if (requestCode == GALERRY_PICK && resultCode == RESULT_OK) {

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

        imgTutoPresencial.setImageURI(uri);

    }

    private void SubirImagen(final String UIDTuto) {

        if (uri != null) {

            StorageReference filePath = imgStorage.child("img_tutorias").child(UIDTuto + ".jpg");
            final StorageReference thumb_filePath = imgStorage.child("img_tutorias").child("thumbs").child(UIDTuto + ".jpg");


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
                                    Map update_hashmMap = new HashMap();
                                    update_hashmMap.put("url_image_portada", download_url);

                                    /*
                                    DBReference = FirebaseDatabase.getInstance().getReference().child("tutorias").child("institucionales").child(keyid);
                                    DBReference.updateChildren(update_hashmMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

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
                                            Map update_hashmMap = new HashMap();
                                            update_hashmMap.put("url_thumb_image_portada", thumb_downloadUrl);

                                            /*
                                            DBReference = FirebaseDatabase.getInstance().getReference().child("tutorias").child("institucionales").child(keyid);
                                            DBReference.updateChildren(update_hashmMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(AgregarTutoriaPresencial.this, "Se ha subido la imagen con exito.", Toast.LENGTH_LONG).show();

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
                                        Toast.makeText(AgregarTutoriaPresencial.this, "Error al subir la imagen.", Toast.LENGTH_LONG).show();

                                    }
                                }
                            });

                            //


                        } else {
                            Toast.makeText(AgregarTutoriaPresencial.this, "Error al subir la imagen.", Toast.LENGTH_LONG).show();

                        }
                    }
                });

            } catch (Exception e) {

            }

        }
    }

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
                                    spnMateriaPresencial = (Spinner) findViewById(R.id.spnMateriaPresencial);
                                    ArrayAdapter<String> materiaAdapter = new ArrayAdapter<String>(AgregarTutoriaPresencial.this, android.R.layout.simple_spinner_item, materia);
                                    materiaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    spnMateriaPresencial.setAdapter(materiaAdapter);
                                }
                            } else {
                                Log.i("ErrorSpinnerMateria", ""+task.getException());
                            }
                        }
                    });

            /*

            DBRefGetMateria.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    final List<String> materia = new ArrayList<String>();
                    Iterable<DataSnapshot> dataSnapshotIterator = dataSnapshot.getChildren();
                    Iterator<DataSnapshot> iterator = dataSnapshotIterator.iterator();

                    while (iterator.hasNext()) {
                        final DataSnapshot idmateria = (DataSnapshot) iterator.next();
                        materia.add(idmateria.getKey());
                        spnMateriaPresencial = (Spinner) findViewById(R.id.spnMateriaPresencial);
                        ArrayAdapter<String> materiaAdapter = new ArrayAdapter<String>(AgregarTutoriaPresencial.this, android.R.layout.simple_spinner_item, materia);
                        materiaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spnMateriaPresencial.setAdapter(materiaAdapter);


                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
*/
        } catch (Exception e) {

        }
    }


    public void ObtenerFechaHora() {
        final Calendar calendar = Calendar.getInstance();
        pub_ano = calendar.get(Calendar.YEAR);
        pub_mes = calendar.get(Calendar.MONTH);
        pub_dia = calendar.get(Calendar.DAY_OF_MONTH);
        pub_hora = calendar.get(Calendar.HOUR_OF_DAY);
        pub_minutos = calendar.get(Calendar.MINUTE);

    }

    @Override
    public void onClick(View view) {


        if (view == btnPublicar) {
            mtdAgregarTutoriaPresencial();
        }
        if (view == btnFecha) {
            final Calendar calendar = Calendar.getInstance();
            ano = calendar.get(Calendar.YEAR);
            mes = calendar.get(Calendar.MONTH);
            dia = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int an, int me, int di) {
                    Fecha.setText(di + "/" + (me + 1) + "/" + an);
                }
            }
                    , ano, mes, dia);
            datePickerDialog.show();
        }

        if (view == btnHoraInicio) {
            final Calendar calendar = Calendar.getInstance();
            hora = calendar.get(Calendar.HOUR_OF_DAY);
            minutos = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int hor, int min) {
                    HoraInicio.setText(hor + ":" + min);
                }
            }, hora, minutos, false);
            timePickerDialog.show();

        }
        if (view == btnHoraFinal) {
            final Calendar calendar = Calendar.getInstance();
            hora = calendar.get(Calendar.HOUR_OF_DAY);
            mes = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int hor, int min) {
                    HoraFinal.setText(hor + ":" + min);
                }
            }, hora, minutos, false);
            timePickerDialog.show();
        }

        if (view == fabSelectImagePresencial) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Seleccione una imagen"), GALERRY_PICK);

        }
    }
};