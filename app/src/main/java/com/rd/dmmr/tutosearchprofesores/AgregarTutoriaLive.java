package com.rd.dmmr.tutosearchprofesores;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import id.zelory.compressor.Compressor;

public class AgregarTutoriaLive extends AppCompatActivity implements View.OnClickListener {


    //Variables
    private static final int GALLERY_INTENT = 1;
    byte[] thumb_byte;
    String download_url, thumb_downloadUrl;
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

    //Firebase Storage
    private StorageReference imgStorage;
    private StorageReference urlStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_tutoria_live);


        imgStorage = FirebaseStorage.getInstance().getReference();
        urlStorage = FirebaseStorage.getInstance().getReference();

        FAutentic = FirebaseAuth.getInstance();
        FUser = FirebaseAuth.getInstance().getCurrentUser();

        DBReference = FirebaseDatabase.getInstance().getReference();
        DBRefGetid = FirebaseDatabase.getInstance().getReference();
        DBRefGetMateria = FirebaseDatabase.getInstance().getReference().child("materias");

        imgTutoLive = (ImageView) findViewById(R.id.imgTutoLive);

        fabImgChange = (FloatingActionButton) findViewById(R.id.fabImgChange);

        spnMateria = (Spinner) findViewById(R.id.spnMateria);

        txtTitulo = (EditText) findViewById(R.id.txtTitulo);
        txtDescripcion = (EditText) findViewById(R.id.txtDescripcion);
        txtFecha = (EditText) findViewById(R.id.txtFecha);
        txtHoraInicio = (EditText) findViewById(R.id.txtHoraInicio);

        btnFecha = (Button) findViewById(R.id.btnFecha);
        btnHora = (Button) findViewById(R.id.btnHoraInicio);
        btnPublicar = (Button) findViewById(R.id.btnPublicar);

        LlenarSpinner();

        fabImgChange.setOnClickListener(this);
        btnHora.setOnClickListener(this);
        btnFecha.setOnClickListener(this);
        btnPublicar.setOnClickListener(this);

        txtFecha.setOnClickListener(this);
        txtHoraInicio.setOnClickListener(this);
    }

    private void AgregarTutoria() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Cargando imagen...");
        progressDialog.setMessage("Cargando imagen espere mientras se carga la imagen.");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        SharedPreferences pref = getSharedPreferences("ProfPref", Context.MODE_PRIVATE);

        String materia = spnMateria.getSelectedItem().toString();
        String profesor = pref.getString("nombreProf", "");
        Log.i("Probando", "Nada: " + profesor);

        if (download_url == null) {
            download_url = "default";
        }
        if (thumb_downloadUrl == null) {
            thumb_downloadUrl = "default";
        }

        String titulo = txtTitulo.getText().toString();
        String descripcion = txtDescripcion.getText().toString();
        String fecha = txtFecha.getText().toString();
        String horainicio = txtHoraInicio.getText().toString();


        keyid = DBRefGetid.child("tutorias").child("institucionales").push().getKey();
        SubirImagen(keyid);
        FirebaseUser user = FAutentic.getCurrentUser();

        DBReference = FirebaseDatabase.getInstance().getReference().child("tutorias").child("institucionales").child(keyid);

        Log.i("URL", download_url + " Vacio?");
        Log.i("URL", thumb_downloadUrl + " Vacio?");

        HashMap<String, String> TutoMap = new HashMap<>();
        TutoMap.put("Materia", materia);
        TutoMap.put("UIDProfesor", user.getUid());
        TutoMap.put("Profesor", profesor);
        TutoMap.put("Descripción", descripcion);
        TutoMap.put("Fecha", fecha);
        TutoMap.put("Hora inicial", horainicio);
        TutoMap.put("Titulo", titulo);
        TutoMap.put("image", download_url);
        TutoMap.put("thumb_image", thumb_downloadUrl);


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

                }
            }
        });

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

        imgTutoLive.setImageURI(uri);

    }

    private void SubirImagen(String UIDTuto) {

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
                                    Log.i("URL", download_url + " Vacio?");

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

    private void LlenarSpinner() {
        try {
            DBRefGetMateria.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    final List<String> materia = new ArrayList<String>();
                    Iterable<DataSnapshot> dataSnapshotIterator = dataSnapshot.getChildren();
                    Iterator<DataSnapshot> iterator = dataSnapshotIterator.iterator();

                    while (iterator.hasNext()) {
                        final DataSnapshot idmateria = (DataSnapshot) iterator.next();
                        materia.add(idmateria.getKey());
                        Log.i("Materia", idmateria.getKey());
                        spnMateria = (Spinner) findViewById(R.id.spnMateria);
                        ArrayAdapter<String> materiaAdapter = new ArrayAdapter<String>(AgregarTutoriaLive.this, android.R.layout.simple_spinner_item, materia);
                        materiaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spnMateria.setAdapter(materiaAdapter);


                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

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
                }
            }
                    , ano, mes, dia);
            datePickerDialog.show();
        }

        if (view == btnPublicar) {
            AgregarTutoria();
        }
    }
}
