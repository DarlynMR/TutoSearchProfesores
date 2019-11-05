package com.rd.dmmr.tutosearchprofesores;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.HashMap;

public class AgregarTutoria extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth FAutentic;
    private FirebaseUser FUser;
    private StorageReference imgStorage;

    private String urlImgT;

    private static final int GALERRY_PICK=1;

    private Spinner Materia;

    private EditText Titulo, Descripcion, Lugar, Fecha, HoraInicio, HoraFinal;

    private Button btnFecha, btnHoraInicio, btnHoraFinal, CargarImagen, Publicar;

    private DatabaseReference DBReference;
    private DatabaseReference DBRefGetid;
    private ProgressDialog progressDialog;

    private int ano,mes,dia,hora,minutos;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_tutoria);

        Titulo = (EditText) findViewById(R.id.txtTitulo);
        Descripcion = (EditText) findViewById(R.id.txtDescripcion);
        Lugar = (EditText) findViewById(R.id.txtLugar);
        Fecha = (EditText) findViewById(R.id.txtFecha);
        HoraInicio = (EditText) findViewById(R.id.txtHoraInicio);
        HoraFinal = (EditText) findViewById(R.id.txtHoraFinal);

        btnFecha = (Button) findViewById(R.id.btnFecha);
        btnHoraInicio = (Button) findViewById(R.id.btnHoraInicio);
        btnHoraFinal = (Button) findViewById(R.id.btnHoraFinal);


        btnFecha.setOnClickListener(this);
        btnHoraInicio.setOnClickListener(this);
        btnHoraFinal.setOnClickListener(this);
        CargarImagen = (Button) findViewById(R.id.btnCargarImagenTutoria);
        Publicar = (Button) findViewById(R.id.btnPublicar);


        progressDialog = new ProgressDialog(this);

        imgStorage = FirebaseStorage.getInstance().getReference();
        FAutentic = FirebaseAuth.getInstance();
        FUser = FirebaseAuth.getInstance().getCurrentUser();
        DBRefGetid = FirebaseDatabase.getInstance().getReference();

        Publicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AgregarTutoria();
            }
        });
    }

/*
        CargarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent,"Seleccione una imagen"),GALERRY_PICK);
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //try {
            if (requestCode == GALERRY_PICK && resultCode == RESULT_OK) {
                Uri imageurl = data.getData();

                CropImage.activity(imageurl)
                        .setAspectRatio(1, 1)
                        .start(this);

                //Toast.makeText(Detalles_Usuario.this, imageurl,Toast.LENGTH_LONG).show();
            }

            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);

                Uri resultUri = result.getUri();

                //CREANDO EL PROGREES BAR
                progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Cargando imagen...");
                progressDialog.setMessage("Cargando imagen espere mientras se carga la imagen.");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                String current_user_id = FUser.getUid();

                StorageReference filepath = imgStorage.child("tuto_img").child(current_user_id + ".jpg");

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {

                            urlImgT = task.getResult().getDownloadUrl().toString();
                            progressDialog.dismiss();
                            Toast.makeText(AgregarTutoria.this, "Imagen Subida.", Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(AgregarTutoria.this, "Error al subir la imagen.", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        }
       }catch (Exception e){
            Toast.makeText(AgregarTutoria.this,"Error al ver imagen",Toast.LENGTH_LONG).show();

        }
    }
*/
    private void AgregarTutoria(){
        progressDialog.setMessage("Agregando tutoria...");
        progressDialog.show();
        SharedPreferences pref = getSharedPreferences("ProfPref", Context.MODE_PRIVATE);

        String materia= "Fisica";
        String profesor= pref.getString("nombreProf","");
        Log.i("Probando", "Nada: "+profesor);
        String image= "default";
        if(urlImgT==null){
        image= "default";
        }else{
           image= urlImgT;
        }

        String titulo = Titulo.getText().toString();
        String descripcion = Descripcion.getText().toString();
        String lugar = Lugar.getText().toString();
        String fecha = Fecha.getText().toString();
        String horainicio= HoraInicio.getText().toString();
        String horafinal = HoraFinal.getText().toString();


                    String keyid= DBRefGetid.child("tutorias").child("institucionales").push().getKey();
                    FirebaseUser user= FAutentic.getCurrentUser();

                    DBReference= FirebaseDatabase.getInstance().getReference().child("tutorias").child("institucionales").child(keyid);

                    HashMap<String,String> TutoMap=new HashMap<>();
                    TutoMap.put("Materia",materia);
                    TutoMap.put("UIDProfesor",user.getUid());
                    TutoMap.put("Profesor",profesor);
                    TutoMap.put("Descripción",descripcion);
                    TutoMap.put("Lugar",lugar);
                    TutoMap.put("Fecha",fecha);
                    TutoMap.put("Hora inicial",horainicio);
                    TutoMap.put("Hora final",horafinal);
                    TutoMap.put("Titulo",titulo);
                    TutoMap.put("Imagen",image);


                    DBReference.setValue(TutoMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                         public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                progressDialog.dismiss();
                                Toast.makeText(AgregarTutoria.this, "Se ha publicado la tutoría", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(AgregarTutoria.this, "Ha ocurrido un error al publicar la tutoría", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

                    progressDialog.dismiss();






    }


    @Override
    public void onClick(View view) {

        if(view==btnFecha){
            final Calendar calendar= Calendar.getInstance();
            ano=calendar.get(Calendar.YEAR);
            mes=calendar.get(Calendar.MONTH);
            dia=calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog= new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int an, int me, int di) {
                    Fecha.setText(di + "/" + (me + 1) + "/" + an);
                }
            }
                ,ano,mes,dia);
            datePickerDialog.show();
        }

        if(view==btnHoraInicio){
            final Calendar calendar= Calendar.getInstance();
            hora=calendar.get(Calendar.HOUR_OF_DAY);
            minutos=calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog= new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int hor, int min) {
                    HoraInicio.setText(hor+":"+min);
                }
            },hora,minutos,false);
            timePickerDialog.show();

        }
        if(view==btnHoraFinal){
            final Calendar calendar= Calendar.getInstance();
            hora=calendar.get(Calendar.HOUR_OF_DAY);
            mes=calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog= new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int hor, int min) {
                    HoraFinal.setText(hor+":"+min);
                }
            },hora,minutos,false);
            timePickerDialog.show();
        }
    }};